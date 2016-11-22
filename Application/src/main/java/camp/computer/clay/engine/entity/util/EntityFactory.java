package camp.computer.clay.engine.entity.util;

import android.util.Log;

import java.util.List;

import camp.computer.clay.engine.Event;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Geometry;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Notification;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Prototype;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.Style;
import camp.computer.clay.engine.component.Timer;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.EventHandler;
import camp.computer.clay.engine.system.CameraSystem;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.lib.ImageBuilder.Circle;
import camp.computer.clay.lib.ImageBuilder.ImageBuilder;
import camp.computer.clay.lib.ImageBuilder.Rectangle;
import camp.computer.clay.lib.ImageBuilder.Segment;
import camp.computer.clay.lib.ImageBuilder.Text;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.model.player.Player;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.controls.NativeUi;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.Random;

public class EntityFactory {
    /**
     * Adds a <em>virtual</em> {@code HostEntity} that can be configured and later assigned to a physical
     * host.
     */
    public static Entity createHostEntity(final World world) {

        // Create Entity
        final Entity host = new Entity();

        // Add Components
        host.addComponent(new Host()); // Unique to Host
        host.addComponent(new Portable()); // Add Portable Component (so can add Ports)
        host.addComponent(new Transform());
        host.addComponent(new Physics());
        host.addComponent(new Image());
        host.addComponent(new Style());
        host.addComponent(new Boundary());
        host.addComponent(new Visibility());

        // Portable Component (Image Component depends on this)
        final int PORT_COUNT = 12;
        for (int j = 0; j < PORT_COUNT; j++) {
            Entity port = world.createEntity(Port.class);

            Label.setLabel(port, "Port " + (j + 1));
            Port.setIndex(port, j);

            // <HACK>
            // TODO: Set default visibility of Ports some other way?
            port.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);
            // </HACK>

            Portable.addPort(host, port);
        }

        // Load geometry from file into Image Component
        // TODO: Application.getPlatform().openFile(this, "Host.json");
        ImageBuilder imageBuilder = ImageBuilder.open2("Host.json", host);

        // <GEOMETRY_LOADER>
//        for (int i = 0; i < imageBuilder.getShapes().size(); i++) {
//            long eid = Image.addShape(host, imageBuilder.getShapes().get(i));
//            // <HACK>
//            // Set Label
//            Entity shape = world.Manager.get(eid);
//            Label.setLabel(shape, imageBuilder.getShapes().get(i).getLabel());
//            // </HACK>
//        }
        // </GEOMETRY_LOADER>

        // Add relative layout constraints
        for (int i = 0; i < Portable.getPorts(host).size(); i++) {
            Entity port = Portable.getPort(host, i);
            port.addComponent(new RelativeLayoutConstraint());
            port.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(host);
        }


        // Relative Position Port Images
        Portable.getPort(host, 0).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(-19.0, 40.0);
        Portable.getPort(host, 1).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(0, 40.0);
        Portable.getPort(host, 2).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(19.0, 40.0);
        Portable.getPort(host, 3).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(40.0, 19.0);
        Portable.getPort(host, 4).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(40.0, 0.0);
        Portable.getPort(host, 5).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(40.0, -19.0);
        Portable.getPort(host, 6).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(19.0, -40.0);
        Portable.getPort(host, 7).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(0, -40.0);
        Portable.getPort(host, 8).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(-19.0, -40.0);
        Portable.getPort(host, 9).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(-40.0, -19.0);
        Portable.getPort(host, 10).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(-40.0, 0.0);
        Portable.getPort(host, 11).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(-40.0, 19.0);
        for (int i = 0; i < Portable.getPorts(host).size(); i++) {
            Portable.getPort(host, i).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(
                    Portable.getPort(host, i).getComponent(RelativeLayoutConstraint.class).relativeTransform.x * 6.0,
                    Portable.getPort(host, i).getComponent(RelativeLayoutConstraint.class).relativeTransform.y * 6.0
            );
        }
        // Add pin contact points to PortableComponent
        // <HACK>
        Group<Entity> pinContactPoints = host.getComponent(Image.class).getShapes(host);
        for (int i = 0; i < pinContactPoints.size(); i++) {
            Entity pinContactPoint = pinContactPoints.get(i);
            if (Label.getLabel(pinContactPoint).startsWith("Pin")) {
                //Point contactPointShape = (Point) pinContactPoint.getComponent(Geometry.class).shape;
                host.getComponent(Portable.class).headerContactGeometries.add(pinContactPoint);
            }
        }
        // </HACK>

        // <EVENT_HANDLERS>
        world.subscribe(Event.Type.MOVE, new EventHandler<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != host) {
                    return;
                }

                // Show prototype Extension if any are saved and available in the repository
                if (Application.getView().getClay().getConfigurations().size() > 0) {

                    Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.

                    // Update position of prototype Extension
                    // world.portableLayoutSystem.setPathPrototypeSourcePosition(host.getComponent(Transform.class));

                    // Set Event Angle (angle from first Event to current Event)
                    double eventAngle = camp.computer.clay.util.Geometry.getAngle(
                            host.getComponent(Transform.class),
                            event.getPosition()
                    );

                    extensionPrototype.getComponent(Transform.class).set(event.getPosition());
                    extensionPrototype.getComponent(Transform.class).setRotation(eventAngle);

                    // Show the prototype Extension
                    extensionPrototype.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                }
            }
        });

        world.subscribe(Event.Type.UNSELECT, new EventHandler<Entity>() {
            @Override
            public void execute(final Event event) {

                if (event.getTarget() != host) {
                    return;
                }

                final Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);

                // Focus on touched Host
                Portable.getPaths(host).setVisibility(Visible.VISIBLE);
                Portable.getPorts(host).setVisibility(Visible.VISIBLE);

                Log.v("HostPorts", "host.ports.size(): " + Portable.getPorts(host).size());

                // Update transparency
                host.getComponent(Style.class).setTransparency(host, 1.0);

                // Show Ports and Paths of touched Host
                for (int i = 0; i < Portable.getPorts(host).size(); i++) {
                    Entity port = Portable.getPort(host, i);
                    Group<Entity> paths = Port.getPaths(port);

                    for (int j = 0; j < paths.size(); j++) {
                        Entity path = paths.get(j);

                        // Show source and target Ports in Paths
                        Path.getPorts(path).setVisibility(Visible.VISIBLE);

                        // Show Path connection
                        path.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                    }
                }

                // Camera
                world.getSystem(CameraSystem.class).setFocus(camera, host);

                if (Portable.getExtensions(host).size() > 0) {

                    // <HACK>
                    // TODO: Move this into PortableLayoutSystem
                    // TODO: Replace ASAP. This is shit.
                    // TODO: Use "rectangle" or "circular" extension layout algorithms
                    world.getSystem(PortableLayoutSystem.class).setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);
                    // </HACK>
                }

                // TODO: 11/13/2016 Set Title

                // Check if connecting to a Extension
                Entity prototypeExtension = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
                if (prototypeExtension.getComponent(Visibility.class).getVisibile() == Visible.VISIBLE) {

                    Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

                    // Get cached extension configurations (and retrieve additional from Internet store)
                    List<Configuration> configurations = Application.getView().getClay().getConfigurations();

                    if (configurations.size() == 0) {

                        // TODO: Show "default" DIY extension builder (or info about there being no headerExtensions)

                    } else if (configurations.size() > 0) {

                        // NativeUi Player to select an ExtensionEntity from the Store
                        // i.e., NativeUi to select extension to use! Then use that profile to create and configure ports for the extension.
                        Application.getView().getNativeUi().promptSelection(configurations, new NativeUi.OnActionListener<Configuration>() {
                            @Override
                            public void onComplete(Configuration configuration) {

                                // Add Extension from Configuration
                                Entity extension = world.getSystem(PortableLayoutSystem.class).createExtensionFromProfile(host, configuration, event.getPosition());

                                // Camera
//                            camera.getComponent(Camera.class).setFocus(extension);
                                world.getSystem(CameraSystem.class).setFocus(camera, extension);
                            }
                        });

                        // Application.getPlatform().promptTasks();
                    }
                }
            }
        });
        // </EVENT_HANDLERS>

        return host;
    }

    public static Entity createPlayerEntity(final World world) {

        // Create Entity
        Entity player = new Entity();

        // Add Components
        player.addComponent(new Player()); // Unique to Player
        player.addComponent(new Transform());

        return player;
    }

    public static Entity createExtensionEntity(final World world) {

        // Create Entity
        final Entity extension = new Entity();

        // Add Components
        extension.addComponent(new Extension()); // Unique to Extension
        extension.addComponent(new Portable());

        // <PORTABLE_COMPONENT>
        // Create Ports and add them to the Extension
        int defaultPortCount = 1;
        for (int j = 0; j < defaultPortCount; j++) {

            Entity port = world.createEntity(Port.class);

            Port.setIndex(port, j);
            Portable.addPort(extension, port);
        }
        // Add relative layout constraints
        for (int i = 0; i < Portable.getPorts(extension).size(); i++) {
            Entity port = Portable.getPort(extension, i);
            port.addComponent(new RelativeLayoutConstraint());
            port.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(extension);
        }
//        Portable.getPort(extension, 0).getComponent(RelativeLayoutConstraint.class).relativeTransform.set(0, 20.0 * 6.0);

        // Add Components
        extension.addComponent(new Transform());
        extension.addComponent(new Physics());
        extension.addComponent(new Image());
        extension.addComponent(new Style());
        extension.addComponent(new Boundary());
        extension.addComponent(new Visibility());

//        // <LOAD_GEOMETRY_FROM_FILE>
//        ImageBuilder imageBuilder = new ImageBuilder();
//
//        Rectangle rectangle;
//
//        // Create Shapes for Image
//        rectangle = new Rectangle();
//        int randomHeight = Random.generateRandomInteger(125, 200);
//        rectangle.setHeight(randomHeight); // was 200
//        rectangle.setWidth(Random.generateRandomInteger(125, 200)); // was 200
//        rectangle.setLabel("Board");
//        rectangle.setColor(Color.getRandomBoardColor()); // Gray: #f7f7f7, Greens: #ff53BA5D, #32CD32
//        rectangle.setOutlineThickness(0);
//        // TODO: Create BuilderImages with geometry when initializing entity with BuildingImage!
////        extension.getComponent(Image.class).addShape(rectangle);
//        rectangle.isBoundary = true;
//        imageBuilder.addShape(rectangle);
//
//        // Headers
//        rectangle = new Rectangle(50, 14);
//        rectangle.setLabel("Header");
//        rectangle.setPosition(0, randomHeight / 2.0f + 7.0f); // was 0, 107
//        rectangle.setRotation(0);
//        rectangle.setColor("#3b3b3b");
//        rectangle.setOutlineThickness(0);
////        extension.getComponent(Image.class).addShape(rectangle);
//        imageBuilder.addShape(rectangle);
//
//        extension.getComponent(Image.class).setImage(imageBuilder);
//        // </LOAD_GEOMETRY_FROM_FILE>

        // <LOAD_GEOMETRY_FROM_FILE>
//        ImageBuilder imageBuilder = new ImageBuilder();

        Rectangle rectangle;
        long shapeUuid;
        Entity shape;

        // Create Shapes for Image
        rectangle = new Rectangle();
        int randomHeight = Random.generateRandomInteger(125, 200);
        rectangle.setHeight(randomHeight); // was 200
        rectangle.setWidth(Random.generateRandomInteger(125, 200)); // was 200
//        rectangle.setLabel("Board");
        rectangle.setColor(Color.getRandomBoardColor()); // Gray: #f7f7f7, Greens: #ff53BA5D, #32CD32
        rectangle.setOutlineThickness(0);
        // TODO: Create BuilderImages with geometry when initializing entity with BuildingImage!
//        extension.getComponent(Image.class).addShape(rectangle);
//        rectangle.isBoundary = true;
//        imageBuilder.addShape(rectangle);
        shapeUuid = Image.addShape(extension, rectangle);
        shape = world.Manager.get(shapeUuid);
        Label.setLabel(shape, "Board");


        // Headers
        rectangle = new Rectangle(50, 14);
        // rectangle.setLabel("Header");
        // rectangle.setPosition(0, randomHeight / 2.0f + 7.0f); // was 0, 107
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
//        extension.getComponent(Image.class).addShape(rectangle);
//        imageBuilder.addShape(rectangle);
        shapeUuid = Image.addShape(extension, rectangle);
        shape = world.Manager.get(shapeUuid);
        shape.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(0, randomHeight / 2.0f + 7.0f);
        Label.setLabel(shape, "Header");

//        extension.getComponent(Image.class).setImage(imageBuilder);
        // </LOAD_GEOMETRY_FROM_FILE>

        // Load geometry from file into Image Component
        // TODO: Application.getPlatform().openFile(this, "Host.json");

        // <EVENT_HANDLERS>
        world.subscribe(Event.Type.HOLD, new EventHandler<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != extension) {
                    return;
                }

                world.createExtensionProfile(extension);
            }
        });

        world.subscribe(Event.Type.UNSELECT, new EventHandler<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != extension) {
                    return;
                }

                if (world.getSystem(InputSystem.class).previousPrimaryTarget == extension) {

                    boolean openImageEditor = false;

                /*
                // TODO:
                Shape board = extension.getComponent(Image.class).getImage().getShape("Board");
                List<Transform> vertices = board.getVertices();
                Log.v("ExtPos", "ex: " + event.getPosition().x + ", y: " + event.getPosition().y);
                for (int i = 0; i < vertices.size(); i++) {
                    Log.v("ExtPos", "x: " + vertices.get(i).x + ", y: " + vertices.get(i).y);
                    if (Geometry.distance(vertices.get(i), event.getPosition()) < 20) {
                        createImageEditor = true;
                    }
                }
                */

                    // <HACK>
                    if (camp.computer.clay.util.Geometry.distance(event.getPosition(), extension.getComponent(Transform.class)) > 75) {
                        openImageEditor = true;
                    }
                    // </HACK>

                    if (openImageEditor) {
                        Application.getView().getNativeUi().openImageEditor(extension);
                    } else {
                        Application.getView().getNativeUi().openActionEditor(extension);
                    }
                }

                // Focus on selected Host
                Group<Entity> extensionPaths = Portable.getPaths(extension);
                Group<Entity> extensionPorts = Portable.getPorts(extension);
                extensionPaths.setVisibility(Visible.VISIBLE);
                extensionPorts.setVisibility(Visible.VISIBLE);
                extension.getComponent(Style.class).setTransparency(extension, 1.0);

                // Show Ports and Paths for selected Host
                for (int i = 0; i < extensionPorts.size(); i++) {
                    Entity port = extensionPorts.get(i);

                    Group<Entity> paths = Port.getPaths(port);
                    for (int j = 0; j < paths.size(); j++) {
                        Entity path = paths.get(j);

                        // Show Ports
                        Entity sourcePort = Path.getSource(path);
                        Entity targetPort = Path.getTarget(path);
                        sourcePort.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                        targetPort.getComponent(Visibility.class).setVisible(Visible.VISIBLE);


                        // Show Path
                        path.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                    }
                }
                // TODO: Replace above with?: portEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visible.VISIBLE);

                // TODO: 11/13/2016 Set Title

                // Camera
                Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);
                world.getSystem(CameraSystem.class).setFocus(camera, extension);
            }
        });
        // </EVENT_HANDLERS>

        return extension;
    }

    public static Entity createPathEntity(final World world) {
        final Entity path = new Entity();

        // Add Path Component (for type identification)
        path.addComponent(new Path()); // Unique to Path
        path.addComponent(new Transform());
        path.addComponent(new Physics());
        path.addComponent(new Image());
        path.addComponent(new Style());
        path.addComponent(new Boundary());
        path.addComponent(new Visibility());

        // <SETUP_PATH_IMAGE_GEOMETRY>
//        ImageBuilder imageBuilder = new ImageBuilder();

        // Board
        Segment segment = new Segment();
        segment.setOutlineThickness(2.0);
        segment.setLabel("Path");
        segment.setColor("#1f1f1e"); // #f7f7f7
        segment.setOutlineThickness(1);
//        imageBuilder.addShape(segment);
        long pathShapeUuid = Image.addShape(path, segment);

        // <HACK>
        // Set Label
        Entity pathShapeEntity = world.Manager.get(pathShapeUuid);
        pathShapeEntity.getComponent(Label.class).label = "Path";
        // </HACK>

        Circle circle = new Circle();
        circle.setRadius(50.0);
        circle.setLabel("Source Port"); // TODO: Give proper name...
        circle.setColor("#990000"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
//        circle.isBoundary = true;
//        imageBuilder.addShape(circle);
        pathShapeUuid = Image.addShape(path, circle);
        Entity shapeEntity = world.Manager.get(pathShapeUuid);
        // <HACK>
//        shapeEntity.getComponent(RelativeLayoutConstraint.class).relativeTransform.set();
        // </HACK>

        // <HACK>
        // Set Label
        pathShapeEntity = world.Manager.get(pathShapeUuid);
        pathShapeEntity.getComponent(Label.class).label = "Source Port";
        // </HACK>

        circle = new Circle();
        circle.setRadius(50.0);
        circle.setLabel("Target Port"); // TODO: Give proper name...
        circle.setColor("#990000"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
//        circle.isBoundary = true;
//        imageBuilder.addShape(circle);
        pathShapeUuid = Image.addShape(path, circle);

        // <HACK>
        // Set Label
        pathShapeEntity = world.Manager.get(pathShapeUuid);
        pathShapeEntity.getComponent(Label.class).label = "Target Port";
        // </HACK>

        // TODO: 11/5/2016 Add Port circles to the Path? So moving paths around will be easier? Then Port images are always just the same color. They look different because of the Path image. Path can contain single node. Then can be stretched out to include another Port.
        // TODO: 11/5/2016 Create corresponding world state CREATING_PATH, MODIFYING_PATH/MOVING_PATH, etc.

//        path.getComponent(Image.class).setImage(imageBuilder);
        path.getComponent(Image.class).layerIndex = 10;
        // </SETUP_PATH_IMAGE_GEOMETRY>

        // <EVENT_HANDLERS>
        world.subscribe(Event.Type.MOVE, new EventHandler<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != path) {
                    return;
                }

                boolean isSingletonPath = (Path.getTarget(path) == null);
                if (isSingletonPath) {

                    // Singleton Path

                    Log.v("handlePathEvent", "Moving on singleton Path.");

                    Path.setState(path, Component.State.EDITING);

                    Entity pathShape = Image.getShape(path, "Path");
                    Segment pathSegment = (Segment) pathShape.getComponent(Geometry.class).shape;
                    pathSegment.setTarget(event.getPosition());
                    pathShape.getComponent(Visibility.class).visible = Visible.VISIBLE;

                    // Determine if taking "create new Extension" action. This is determined to be true
                    // if at least one Extension is "near enough" to the Event's target position.
                    boolean isCreateExtensionAction = true; // TODO: Convert into Event to send to World?
                    Group<Entity> extensions = world.Manager.getEntities().filterWithComponent(Extension.class);
                    for (int i = 0; i < extensions.size(); i++) {

                        double distanceToExtension = camp.computer.clay.util.Geometry.distance(
                                event.getPosition(),
                                extensions.get(i).getComponent(Transform.class)
                        );

                        if (distanceToExtension < World.NEARBY_EXTENSION_DISTANCE_THRESHOLD) {
                            isCreateExtensionAction = false;
                            break;
                        }

                        // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
                    }

                    // Update position of prototype Path and Extension
                    Entity extensionPrototype = world.Manager.getEntities().filterWithComponents(Prototype.class, Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    if (isCreateExtensionAction) {
                        extensionPrototype.getComponent(Visibility.class).setVisible(Visible.VISIBLE);

                        // Set Event Angle (angle from first Event to current Event)
                        double eventAngle = camp.computer.clay.util.Geometry.getAngle(
                                event.getSecondaryTarget().getComponent(Transform.class), // event.getFirstEvent().getTarget().getComponent(Transform.class),
                                event.getPosition()
                        );
                        // Set prototype Extension transform
                        extensionPrototype.getComponent(Transform.class).set(event.getPosition());
                        extensionPrototype.getComponent(Transform.class).setRotation(eventAngle);
                    } else {
                        extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);
                    }

                    // Ports of nearby Hosts and Extensions
                    Group<Entity> nearbyExtensions = extensions.filterArea(event.getPosition(), World.NEARBY_EXTENSION_RADIUS_THRESHOLD);
                    for (int i = 0; i < nearbyExtensions.size(); i++) {
                        Entity extension = nearbyExtensions.get(i);

                        Group<Entity> nearbyExtensionPorts = Portable.getPorts(extension);

                        // Style
                        nearbyExtensionPorts.setVisibility(Visible.VISIBLE);

                        // Add new Port (if needed)
                        if (!extension.getComponent(Extension.class).isPersistent()) {

                            // Determine if a new Port is required on the custom Extension
                            boolean addNewPort = true;
                            for (int j = 0; j < nearbyExtensionPorts.size(); j++) {
                                Entity existingPort = nearbyExtensionPorts.get(j);
                                if (Port.getType(existingPort) == Port.Type.NONE) {
                                    addNewPort = false;
                                    break;
                                }
                            }
                            Log.v("handlePathEvent", "addNewPort: " + addNewPort);

                            // Add new Port to the Extension (if determined necessary)
                            if (addNewPort) {
                                Entity newPort = world.createEntity(Port.class);

                                // <HACK>
                                newPort.addComponent(new RelativeLayoutConstraint());
                                newPort.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(extension);
//                            newPort.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(0, 25.0 * 6.0);
                                // </HACK>

                                int newPortIndex = nearbyExtensionPorts.size();
                                Port.setIndex(newPort, newPortIndex);
                                Portable.addPort(extension, newPort);
                            }
                        }
//                    }
                    }

                } else if (!isSingletonPath) {

                    // Multi-Port Path (non-singleton)

                    Entity sourcePortShape = Image.getShape(path, "Source Port");
                    if (event.getSecondaryTarget() == sourcePortShape) {
                        Log.v("handlePathEvent", "Touched Source");
                        //sourcePortShape.getComponent(Geometry.class).shape.setPosition(event.getPosition()); // TODO: Change TRANSFORM
                        sourcePortShape.getComponent(Transform.class).set(event.getPosition()); // TODO: Change TRANSFORM
                        // TODO: sourcePortShape.getComponent(Physics.class).targetTransform.set(event.getPosition());

                        Path.setState(path, Component.State.EDITING);
                    }

                    Entity targetPortShape = Image.getShape(path, "Target Port");
                    if (event.getSecondaryTarget() == targetPortShape) {
                        Log.v("handlePathEvent", "Touched Target");
                        targetPortShape.getComponent(Transform.class).set(event.getPosition()); // TODO: Change TRANSFORM
                        // TODO: targetPortShape.getComponent(Physics.class).targetTransform.set(event.getPosition()); // TODO: Change TRANSFORM

                        Path.setState(path, Component.State.EDITING);
                    }

                }
            }
        });

        world.subscribe(Event.Type.UNSELECT, new EventHandler<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != path) {
                    return;
                }

                Entity sourcePortShape = Image.getShape(path, "Source Port"); // path.getComponent(Image.class).getImage().getShape("Source Port");
                Entity targetPortShape = Image.getShape(path, "Target Port"); // path.getComponent(Image.class).getImage().getShape("Target Port");

                Log.v("handlePathEvent", "UNSELECT PATH.");

                if (Path.getTarget(path) != null) {

                    Log.v("handlePathEvent", "NON SINGLETON.");

                    // Full Path (non-singleton Path)

                    if (Path.getState(path) != Component.State.EDITING) {

                        // <PATH>
                        // Set next Path type
                        Path.Type nextType = Path.Type.getNext(Path.getType(path));
                        while ((nextType == Path.Type.NONE) || (nextType == Path.getType(path))) {
                            nextType = Path.Type.getNext(nextType);
                        }
                        Path.setType(path, nextType);
                        // <PATH>

                        // Notification
                        world.createAndConfigureNotification("" + nextType, event.getPosition(), 800);

                    } else if (Path.getState(path) == Component.State.EDITING) {

                        Group<Entity> dropTargetPorts = world.Manager.getEntities().filterWithComponent(Port.class).filterContains(event.getPosition());

                        // Moved the Path to another Port
                        if (dropTargetPorts.size() > 0) {

                            Entity dropTargetPort = dropTargetPorts.get(0); // NOTE: This gets the first port in the list, no matter how many there are or which Ports they are. Maybe not always work...
                            Log.v("TargetAreaPort", "targetAreaPort: " + dropTargetPort);

                            // Remap the Path's Port if the touched Port doesn't already have a Path
                            Group<Entity> targetPaths = Port.getPaths(dropTargetPort);
                            if (targetPaths.size() > 0 && targetPaths.get(0) == path) {

                                // Swap the Path's Ports in the SAME path (swap Ports/flip direction)
                                Log.v("handlePathEvent", "flipping the path");
                                Entity sourcePort = Path.getSource(path);
                                Path.setSource(path, Path.getTarget(path));
                                Path.setTarget(path, sourcePort);

                                // TODO: path.getComponent(Path.class).setDirection();

                                // Notification
                                world.createAndConfigureNotification("flipped path", event.getPosition(), 1000);

                            } else if (targetPaths.size() > 0 && targetPaths.get(0) != path) {

                                // TODO: Make sure both Ports are connected between both a common Host and Extension

                                // Swap ports ACROSS different paths (swap Paths)
                                if (camp.computer.clay.util.Geometry.contains(Boundary.get(sourcePortShape), event.getPosition())) {
                                    // Swapping path A source port shape...
                                    if (dropTargetPort == Path.getSource(targetPaths.get(0))) {
                                        Entity sourcePort = Path.getSource(path);
                                        Path.setSource(path, Path.getSource(targetPaths.get(0))); // Path.getTarget(path));
                                        Path.setSource(targetPaths.get(0), sourcePort);
                                    } else if (dropTargetPort == Path.getTarget(targetPaths.get(0))) {
                                        Entity sourcePort = Path.getSource(path);
                                        Path.setSource(path, Path.getTarget(targetPaths.get(0))); // Path.getTarget(path));
                                        Path.setTarget(targetPaths.get(0), sourcePort);
                                    }
                                } else if (camp.computer.clay.util.Geometry.contains(Boundary.get(targetPortShape), event.getPosition())) {
                                    // Swapping path A target port shape...
                                    if (dropTargetPort == Path.getSource(targetPaths.get(0))) {
                                        Entity targetPath = Path.getTarget(path);
                                        Path.setTarget(path, Path.getSource(targetPaths.get(0))); // Path.getTarget(path));
                                        Path.setSource(targetPaths.get(0), targetPath);
                                    } else if (dropTargetPort == Path.getTarget(targetPaths.get(0))) {
                                        Entity targetPath = Path.getTarget(path);
                                        Path.setTarget(path, Path.getTarget(targetPaths.get(0))); // Path.getTarget(path));
                                        Path.setTarget(targetPaths.get(0), targetPath);
                                    }
                                }

                                // TODO: path.getComponent(Path.class).setDirection();

                                // Notification
                                world.createAndConfigureNotification("swapped paths", event.getPosition(), 1000);

                            } else if (Port.getPaths(dropTargetPort).size() == 0) {

                                // Remap the Path's Ports

                                // Check if source or target in Path was moved, and reassign it
                                Entity sourcePortShape2 = Image.getShape(path, "Source Port"); // path.getComponent(Image.class).getImage().getShape("Source Port");
                                Entity targetPortShape2 = Image.getShape(path, "Target Port"); // path.getComponent(Image.class).getImage().getShape("Target Port");
                                if (camp.computer.clay.util.Geometry.contains(Boundary.get(sourcePortShape2), event.getPosition())) {

                                    // Check if the new Path's Port's would be on the same Portable
                                    if (Path.getTarget(path).getParent() == dropTargetPort.getParent()) {
                                        // Prevent the Path from moving onto the Extension with both Ports
                                        if (!Path.getTarget(path).getParent().hasComponent(Extension.class)) {
                                            Path.setSource(path, dropTargetPort);
                                        }
                                    } else {
                                        Path.setSource(path, dropTargetPort);
                                    }

                                } else if (camp.computer.clay.util.Geometry.contains(Boundary.get(targetPortShape2), event.getPosition())) {

                                    // Check if the new Path's Port's would be on the same Portable
                                    if (Path.getSource(path).getParent() == dropTargetPort.getParent()) {
                                        // Prevent the Path from moving onto the Extension with both Ports
                                        if (!Path.getSource(path).getParent().hasComponent(Extension.class)) {
                                            Path.setTarget(path, dropTargetPort);
                                        }
                                    } else {
                                        Path.setTarget(path, dropTargetPort);
                                    }
                                }

                                // TODO: Configure new Port, clear configuration from old port

                                // Notification
                                world.createAndConfigureNotification("moved path", event.getPosition(), 1000);
                            }
                        }

                        // Moved the Path OFF of Ports (dropped onto the background)
                        else if (dropTargetPorts.size() == 0) {

                            // Remove the Path (and the Extension if the removed Path was the only one)
                            path.isActive = false;
                            world.Manager.remove(path);

//                    Group<Entity> extensionPorts1 = extension.getComponent(Portable.class).getPorts();
//                    extensionPorts1.remove(extensionPort); // Remove from Portable

                            // Notification
                            world.createAndConfigureNotification("removed path", event.getPosition(), 1000);

                            // Reset Ports that were in removed Path
                            Entity sourcePort = Path.getSource(path);
                            Port.setType(sourcePort, Port.Type.NONE);
                            Entity targetPort = Path.getTarget(path);
                            Port.setType(targetPort, Port.Type.NONE);

                            // Update the Path
                            Entity extension = Path.getExtension(path);
                            Group<Entity> extensionPaths = Portable.getPaths(extension);
                            Log.v("handlePathEvent", "paths.size(): " + extensionPaths.size());

                            // Delete Extension if no Paths exist to it
                            if (extensionPaths.size() == 0) {

                                // Deactivate Entities
                                Group<Entity> extensionPorts = Portable.getPorts(extension);
                                for (int i = 0; i < extensionPorts.size(); i++) {
                                    Entity extensionPort = extensionPorts.get(i);
                                    extensionPort.isActive = false;
                                }
                                extension.isActive = false;

                                // Remove Extension's Ports
//                        Group<Entity> extensionPorts = extension.getComponent(Portable.class).getPorts();
//                        for (int i = 0; i < extensionPorts.size(); i++) {
                                while (extensionPorts.size() > 0) {
                                    Entity extensionPort = extensionPorts.get(0);
                                    world.Manager.remove(extensionPort);
                                    extensionPorts.remove(extensionPort); // Remove from Portable
                                }

                                world.Manager.remove(extension);

                                // Notification
                                world.createAndConfigureNotification("removed extension", extension.getComponent(Transform.class), 1000);
                            }

                        }

                    }

                    Path.setState(path, Component.State.NONE);

                } else {

                    // Singleton Path

                    // (Host.Port, ..., World) Action Pattern

                    Group<Entity> targetAreaPorts = world.Manager.getEntities().filterWithComponent(Port.class).filterContains(event.getPosition());

                    Log.v("handlePathEvent", "creating extension");

                    // If prototype Extension is visible, create Extension
//                if (world.getExtensionPrototypeVisibility2() == Visible.VISIBLE) {
                    Entity prototypeExtension = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    if (prototypeExtension.getComponent(Visibility.class).getVisibile() == Visible.VISIBLE) {

                        Log.v("handlePathEvent", "creating extension");

//                    // Hide prototype Path and prototype Extension
//                    world.setPathPrototypeVisibility(Visible.INVISIBLE);
//                    world.setExtensionPrototypeVisibility2(Visible.INVISIBLE);
                        Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                        extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

//                    Entity hostPort = event.getFirstEvent().getTarget();
                        Entity hostPort = Path.getSource(path);

                        Log.v("handlePathEvent", "hostPort: " + hostPort);

                        // Create new custom Extension. Custom Extension can be configured manually.
                        Entity extension = world.getSystem(PortableLayoutSystem.class).createCustomExtension(hostPort, event.getPosition());

                        // Notification
                        world.createAndConfigureNotification("added extension", extension.getComponent(Transform.class), 1000);

                        // Get all Ports in all Paths from the Host
                        Group<Entity> hostPaths = Port.getPaths(hostPort);
                        Group<Entity> hostPorts = new Group<>();
                        for (int i = 0; i < hostPaths.size(); i++) {
                            Group<Entity> pathPorts = Path.getPorts(hostPaths.get(i));
                            hostPorts.addAll(pathPorts);
                        }

                        // Show all of Host's Paths and all Ports contained in those Paths
                        hostPaths.setVisibility(Visible.VISIBLE);
                        hostPorts.setVisibility(Visible.VISIBLE);

                        // Update layout
                        Entity host = hostPort.getParent(); // HACK

                        world.getSystem(PortableLayoutSystem.class).setPortableSeparation(World.HOST_TO_EXTENSION_LONG_DISTANCE);

                        world.getSystem(PortableLayoutSystem.class).updateExtensionLayout(host);
                        // <STYLE_AND_LAYOUT>

                        // Set Camera focus on the Extension
                        // camera.setFocus(extension);
                    } else if (event.isTap()) { // } else if (event.getFirstEvent().getTarget() == event.getTarget()) {

                        // Change Singleton Path Type

                        // <PATH>
                        // Set next Path type
                        Path pathComponent = path.getComponent(Path.class);
                        Path.Type nextType = Path.Type.getNext(Path.getType(path));
                        while ((nextType == Path.Type.NONE) || (nextType == Path.getType(path))) {
                            nextType = Path.Type.getNext(nextType);
                        }
                        Path.setType(path, nextType);
//                Log.v("EventHandlerSystem", "Setting path type to: " + nextType);
                        // <PATH>

                    } else if (targetAreaPorts.size() > 0) { //} else if (event.getFirstEvent().getTarget() != event.getTarget()) {

                        Entity dropTargetEntity = targetAreaPorts.get(0);

                        // Adding Path. Stretches singleton path to a target port.

                        Log.v("handlePathEvent", "creating paaaaatthhh???");

                        // Handle drop on Path (as opposed to drop on a Port). "Merge" the Paths by
                        // removing the Path onto which the target Path was dropped and then update the
                        // target Path's source and target Ports as usual (below).
                        Entity dropTargetPath = dropTargetEntity;
                        if (dropTargetPath.hasComponent(Path.class) && Path.getPorts(dropTargetPath).size() == 1) {
                            Log.v("handlePathEvent", "target is singleton PATH");
                            if (dropTargetPath != path) {
                                Log.v("handlePathEvent", "target is DIFFERENT path");

                                // Combine the Paths into one, deleting one of them!
                                // TODO: Delete path on target
                                // <CLEANUP_ENTITY_DELETE_CODE>
                                dropTargetPath.isActive = false;
                                Path.setState(dropTargetPath, Component.State.EDITING);
                                Entity tempSourcePort = Path.getSource(dropTargetPath);
                                Path.setSource(dropTargetPath, null); // Reset path
                                Path.setTarget(dropTargetPath, null); // Reset path
                                world.Manager.remove(dropTargetPath); // Delete path!
                                // </CLEANUP_ENTITY_DELETE_CODE>

                                // Update the Path from the source Port
                                Entity targetPort = tempSourcePort; // new target is source port from other path
                                Path.setTarget(path, targetPort);
                            }
                        }

                        // Update the Path's target Port
                        if (!dropTargetEntity.hasComponent(Port.class)) { // if (!event.getTarget().hasComponent(Port.class)) {
                            return;
                        }

                        Entity dropTargetPort = dropTargetEntity; // event.getTarget();

                        Path.setTarget(path, dropTargetPort);

                        world.createAndConfigureNotification("added path", event.getPosition(), 1000);

                    }

                    Path.setState(path, Component.State.NONE);
                }
            }
        });
        // </EVENT_HANDLERS>

        return path;
    }

    public static Entity createPortEntity(final World world) {

        final Entity port = new Entity();

        // Add Components
        port.addComponent(new Port()); // Unique to Port
        port.addComponent(new Transform());
        port.addComponent(new Image());
        port.addComponent(new Style());
        port.addComponent(new Physics());
        port.addComponent(new Boundary());
        port.addComponent(new Visibility());
        port.addComponent(new Label());

        // <LOAD_GEOMETRY_FROM_FILE>
//        ImageBuilder imageBuilder = new ImageBuilder();

        // Create Shapes for Image
        Circle circle = new Circle();
        circle.setRadius(50.0);
        circle.setLabel("Port"); // TODO: Give proper name...
        circle.setColor("#ffe7e7e7"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
//        circle.isBoundary = true;
//        imageBuilder.addShape(circle);
        long portShapeUuid = Image.addShape(port, circle);

        // <HACK>
        // Set Label
        Entity portShape = world.Manager.get(portShapeUuid);
        portShape.getComponent(Label.class).label = "Port";
        // </HACK>

//        port.getComponent(Image.class).setImage(imageBuilder);
        // </LOAD_GEOMETRY_FROM_FILE>

        // <EVENT_HANDLERS>
        world.subscribe(Event.Type.UNSELECT, new EventHandler<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != port) {
                    return;
                }

                // (Host.Port, ..., Host.Port) Action Pattern

                if (event.isTap() && event.getTarget() == port) {

                    // (Host.Port A, ..., Host.Port A) Action Pattern
                    // i.e., The action's first and last events address the same Port. Therefore, it must be either a tap or a hold.

                    // Get Port associated with the touched Port
                    Entity sourcePort = event.getFirstEvent().getTarget();

                    // Check if the target Port is contained in any Path.
                    boolean portHasPath = false;
                    Entity firstPort = event.getFirstEvent().getTarget();
                    if (Port.getPaths(firstPort).size() > 0) {
                        portHasPath = true;
                    }

                    // Create new singleton Path, enabling the Port to be connected to other Ports.
                    if (!portHasPath) {
                        Entity singletonPath = world.createEntity(Path.class);
                        Path.setSource(singletonPath, sourcePort);
                        Path.setType(singletonPath, Path.Type.SWITCH);
                    }

                }
            }
        });
        // </EVENT_HANDLERS>

        return port;

    }

    public static Entity createCameraEntity(final World world) {

        final Entity camera = new Entity();

        // Add Path Component (for type identification)
        camera.addComponent(new Camera());

        // Add Transform Component
        camera.addComponent(new Transform());
        camera.addComponent(new Physics());

        // <EVENT_HANDLERS>
        world.subscribe(Event.Type.MOVE, new EventHandler<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != camera) {
                    return;
                }

                //            if (action.isDragging()) {
                // TODO: Make sure there's no inconsistency "information access sequence" between this EventHandlerSystem, InputSystem, and PlatformRenderSurface.onTouch. Should only access info from previously dispatched? event
                world.getSystem(CameraSystem.class).setOffset(camera, event.xOffset, event.yOffset);
                Log.v("CameraEvent", "offset.x: " + event.getOffset().y + ", y: " + event.getOffset().y);
//            }
            }
        });

        world.subscribe(Event.Type.UNSELECT, new EventHandler<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != camera) {
                    return;
                }

                // TODO: 11/13/2016 Set Title

                // Camera
                if (event.isTap()) {
                    world.getSystem(CameraSystem.class).setFocus(camera, null);
                }
            }
        });
        // </EVENT_HANDLERS>

        return camera;
    }

    public static Entity createGeometryEntity(final World world) {

        Entity shape = new Entity();

        // Components
        shape.addComponent(new Geometry()); // Unique to Shape Entity
        shape.addComponent(new Label());
        shape.addComponent(new Transform());
        shape.addComponent(new Physics());
        shape.addComponent(new Style());
        shape.addComponent(new Boundary());
        shape.addComponent(new Visibility());

        shape.addComponent(new RelativeLayoutConstraint());
        //shape.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(extension);

        return shape;
    }

    public static Entity createNotificationEntity(final World world) {

        Entity notification = new Entity();

        // Components
        notification.addComponent(new Notification()); // Unique to Notification Entity
        notification.addComponent(new Transform());
        notification.addComponent(new Image());
        notification.addComponent(new Style());
        notification.addComponent(new Visibility());
        notification.addComponent(new Timer());

        // <HACK>
        notification.getComponent(Timer.class).timeout = RenderSystem.DEFAULT_NOTIFICATION_TIMEOUT;
        // </HACK>

        // Image
//        ImageBuilder imageBuilder = new ImageBuilder();

        Text text = new Text();
        text.setText("DEFAULT_TEXT");
        text.size = RenderSystem.NOTIFICATION_FONT_SIZE;
        text.setColor("#ff000000");
        text.setPosition(0, 0);
        text.font = RenderSystem.NOTIFICATION_FONT;
        Image.addShape(notification, text);

//        imageBuilder.addShape(text);

        // <HACK>
        notification.getComponent(Image.class).layerIndex = 20;
        // </HACK>

//        notification.getComponent(Image.class).setImage(imageBuilder);

        return notification;
    }
}