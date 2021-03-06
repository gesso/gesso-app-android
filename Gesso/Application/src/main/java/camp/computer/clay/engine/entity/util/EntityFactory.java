package camp.computer.clay.engine.entity.util;

import android.util.Log;

import java.util.List;

import camp.computer.clay.engine.Engine;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Notification;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Player;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Prototype;
import camp.computer.clay.engine.component.Scriptable;
import camp.computer.clay.engine.component.Structure;
import camp.computer.clay.engine.component.Style;
import camp.computer.clay.engine.component.Timer;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.TransformConstraint;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.Workspace;
import camp.computer.clay.engine.component.util.FilterStrategy;
import camp.computer.clay.engine.component.util.Signal;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.event.EventResponse;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.system.CameraSystem;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.lib.Geometry.Circle;
import camp.computer.clay.lib.Geometry.ModelBuilder;
import camp.computer.clay.lib.Geometry.Rectangle;
import camp.computer.clay.lib.Geometry.Segment;
import camp.computer.clay.lib.Geometry.Shape;
import camp.computer.clay.lib.Geometry.Text;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.controls.Widgets;
import camp.computer.clay.structure.configuration.Configuration;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.Geometry;

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
        host.addComponent(new Model());
        host.addComponent(new Style());
        host.addComponent(new Boundary());
        host.addComponent(new Visibility());

        // <GEOMETRY_LOADER>
        // Load geometry from file into ModelBuilder Component
        // TODO: Application.getPlatform().openFile(this, "Model_Host_Clay-v7.1.0-beta.json");
        ModelBuilder modelBuilder = ModelBuilder.openFile("Model_Host_Clay-v7.1.0-beta.json");
        long modelAssetUid = Engine.getInstance().world.cache.add(modelBuilder); // TODO: Replace with AssetManager?
        ModelBuilder modelAsset = (ModelBuilder) Engine.getInstance().world.cache.get(modelAssetUid);
        Log.v("CACHE", "cached model asset shape count: " + modelAsset.getShapes().size());
        modelBuilder.getModelComponent(host);
        host.getComponent(Model.class).assetUid = modelAssetUid;
        // </GEOMETRY_LOADER>

        // Portable Component (ModelBuilder Component depends on this)
        final int PORT_COUNT = 12;
        for (int j = 0; j < PORT_COUNT; j++) {
            Entity port = world.createEntity(Port.class);

            Label.setLabel(port, "Port " + (j + 1));
            Port.setIndex(port, j);

            // <HACK>
            // TODO: Set default visibility of Ports some other way?
            port.getComponent(Visibility.class).visible = Visible.INVISIBLE;

            // <REFACTOR>
            // TODO: Move to common location (in System update based on state of Entity/components) and make function.
            List<Entity> primitives = port.getComponent(Model.class).primitives;
            for (int i = 0; i < primitives.size(); i++) {
                primitives.get(i).getComponent(Visibility.class).visible = Visible.INVISIBLE;
            }
            // </REFACTOR>
            // </HACK>

            Portable.addPort(host, port);
        }

        // <DELETE>
        for (int i = 0; i < modelBuilder.getShapes().size(); i++) {
            Shape shape = modelBuilder.getShapes().get(i);
            Log.v("SHAPES", "" + shape.getTag());
        }

        for (int i = 0; i < modelBuilder.getShapes().size(); i++) {
            Shape shape = modelBuilder.getShapes().get(i);
            if (shape.isBoundary) {
                Log.v("SHAPES", "boundary: " + shape.getTag());
            }
        }
        // </DELETE>

        // Add relative layout constraints
        for (int i = 0; i < Portable.getPorts(host).size(); i++) {
            Entity port = Portable.getPort(host, i);
            port.addComponent(new TransformConstraint());
            port.getComponent(TransformConstraint.class).setReferenceEntity(host);
        }

        // Relative Position Port Images
        Portable.getPort(host, 0).getComponent(TransformConstraint.class).relativeTransform.set(-19.0, 40.0);
        Portable.getPort(host, 1).getComponent(TransformConstraint.class).relativeTransform.set(0, 40.0);
        Portable.getPort(host, 2).getComponent(TransformConstraint.class).relativeTransform.set(19.0, 40.0);
        Portable.getPort(host, 3).getComponent(TransformConstraint.class).relativeTransform.set(40.0, 19.0);
        Portable.getPort(host, 4).getComponent(TransformConstraint.class).relativeTransform.set(40.0, 0.0);
        Portable.getPort(host, 5).getComponent(TransformConstraint.class).relativeTransform.set(40.0, -19.0);
        Portable.getPort(host, 6).getComponent(TransformConstraint.class).relativeTransform.set(19.0, -40.0);
        Portable.getPort(host, 7).getComponent(TransformConstraint.class).relativeTransform.set(0, -40.0);
        Portable.getPort(host, 8).getComponent(TransformConstraint.class).relativeTransform.set(-19.0, -40.0);
        Portable.getPort(host, 9).getComponent(TransformConstraint.class).relativeTransform.set(-40.0, -19.0);
        Portable.getPort(host, 10).getComponent(TransformConstraint.class).relativeTransform.set(-40.0, 0.0);
        Portable.getPort(host, 11).getComponent(TransformConstraint.class).relativeTransform.set(-40.0, 19.0);
        for (int i = 0; i < Portable.getPorts(host).size(); i++) {
            Portable.getPort(host, i).getComponent(TransformConstraint.class).relativeTransform.set(
                    Portable.getPort(host, i).getComponent(TransformConstraint.class).relativeTransform.x * 6.0,
                    Portable.getPort(host, i).getComponent(TransformConstraint.class).relativeTransform.y * 6.0
            );
        }

        // <EVENT_HANDLERS>
        world.eventManager.registerResponse("MOVE", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != host) {
                    return;
                }

                // Show prototype Extension if any are saved and available in the repository
                if (world.cache.getObjects(Configuration.class).size() > 0) {

                    Entity prototypeExtension = world.entityManager.get().filterWithComponent(Label.class).filterWithLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.

                    // Update position of prototype Extension
                    // world.portableLayoutSystem.setPathPrototypeSourcePosition(host.getComponent(Transform.class));

                    // Set Event Angle (angle from first Event to current Event)
                    double eventAngle = camp.computer.clay.util.Geometry.getAngle(
                            host.getComponent(Transform.class),
                            event.getPosition()
                    );

                    prototypeExtension.getComponent(Transform.class).set(event.getPosition());
                    prototypeExtension.getComponent(Transform.class).setRotation(eventAngle);

                    // Show the prototype Extension
                    prototypeExtension.getComponent(Visibility.class).visible = Visible.VISIBLE;

                    // <REFACTOR>
                    // TODO: Move to common location (in System?) and make function.
                    List<Entity> primitives = prototypeExtension.getComponent(Model.class).primitives;
                    for (int i = 0; i < primitives.size(); i++) {
                        primitives.get(i).getComponent(Visibility.class).visible = Visible.VISIBLE;
                    }
                    // </REFACTOR>
                }
            }
        });

        world.eventManager.registerResponse("UNSELECT", new EventResponse<Entity>() {
            @Override
            public void execute(final Event event) {

                if (event.getTarget() != host) {
                    return;
                }

                final Entity camera = world.entityManager.get().filterWithComponent(Camera.class).get(0);

                // Focus on touched Host
                Portable.getPaths(host).setVisibility(Visible.VISIBLE);
                Portable.getPorts(host).setVisibility(Visible.VISIBLE);

                // <REFACTOR>
                for (int i = 0; i < Portable.getPorts(host).size(); i++) {
                    Entity port = Portable.getPort(host, i);
                    // TODO: Move to common location (in System?) and make function.
                    // TODO: (Response to previous TODO) Yes, in system, update based on state of Entity/components.
                    List<Entity> primitives = port.getComponent(Model.class).primitives;
                    for (int k = 0; k < primitives.size(); k++) {
                        primitives.get(k).getComponent(Visibility.class).visible = Visible.VISIBLE;
                    }
                }
                // </REFACTOR>

                Log.v("HostPorts", "host.ports.size(): " + Portable.getPorts(host).size());

                // TODO: Update transparency
                // e.g., host.getComponent(Style.class).setTransparency(host, 1.0);

                // Show Ports and Paths of touched Host
                for (int i = 0; i < Portable.getPorts(host).size(); i++) {
                    Entity port = Portable.getPort(host, i);
                    Group<Entity> paths = Port.getPaths(port);

                    // Set Path Visibility
                    for (int j = 0; j < paths.size(); j++) {
                        Entity path = paths.get(j);

                        // Show source and target Ports in Paths
                        Path.getPorts(path).setVisibility(Visible.VISIBLE);

                        // Show Path connection
                        path.getComponent(Visibility.class).visible = Visible.VISIBLE;
                    }
                }

                if (Portable.getExtensions(host).size() > 0) {

                    // <HACK>
                    // TODO: Move this into LayoutSystem
                    // TODO: Replace ASAP. This is shit.
                    // TODO: Use "rectangle" or "circular" extension layout algorithms
//                    world.getSystem(LayoutSystem.class).setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);
                    // </HACK>
                }

                // TODO: 11/13/2016 Set Title

                // Check if connecting to a Extension
                Entity prototypeExtension = world.entityManager.get().filterWithComponent(Label.class).filterWithLabel("prototypeExtension").get(0);
                if (prototypeExtension.getComponent(Visibility.class).visible == Visible.VISIBLE) {

                    // Entity extensionPrototype = world.entityManager.get().filterWithComponent(Label.class).filterWithLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    prototypeExtension.getComponent(Visibility.class).visible = Visible.INVISIBLE;

                    // <REFACTOR>
                    // TODO: Move to common location (in System?) and make function.
                    List<Entity> primitives = prototypeExtension.getComponent(Model.class).primitives;
                    for (int i = 0; i < primitives.size(); i++) {
                        primitives.get(i).getComponent(Visibility.class).visible = Visible.INVISIBLE;
                    }
                    // </REFACTOR>

                    // Get cached extension configurations (and retrieve additional from Internet2 store)
                    List<Configuration> configurations = world.cache.getObjects(Configuration.class);

                    if (configurations.size() == 0) {

                        // TODO: Show "default" DIY extension builder (or info about there being no headerExtensions)

                    } else if (configurations.size() > 0) {

                        // Widgets Player to select an ExtensionEntity from the Store
                        // i.e., Widgets to select extension to use! Then use that profile to create and configure ports for the extension.
                        Application.getInstance().getWidgets().openInteractiveAssembler(configurations, new Widgets.OnActionListener<Configuration>() {
                            @Override
                            public void onComplete(Configuration configuration) {

                                // Add Extension from Configuration
                                Entity extension = createExtensionFromProfile(host, configuration, event.getPosition());

                                // Camera
//                            camera.getComponent(Camera.class).setFocus(extension);
                                world.getSystem(CameraSystem.class).setFocus(camera, extension);
                            }
                        });

                        // Application.getPlatform().openInteractiveAssemblyTaskOverview();
                    }
                }

//                // Camera
//                world.getSystem(CameraSystem.class).setFocus(camera, host);
            }
        });
        // </EVENT_HANDLERS>

        return host;
    }

    public static Entity createWorkspaceEntity(final World world) {

        // Create Entity
        final Entity workspace = new Entity();

        // Add Components
        workspace.addComponent(new Workspace()); // Unique to Workspace

        // <EVENT_HANDLERS>
        world.eventManager.registerResponse("UNSELECT", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

//                if (event.getTargetPort() != workspace) {
//                    return;
//                }

                if (event.getTarget().hasComponent(Camera.class)) {

                    // <HACK>
                    Group<Entity> ports = null;
                    Group<Entity> pathsAndPorts = null;

                    if (ports == null) {
                        ports = world.entityManager.subscribe(
                                new FilterStrategy(Group.Filters.filterWithComponent, Port.class)
                        );
                    }
                    if (pathsAndPorts == null) {
                        pathsAndPorts = world.entityManager.subscribe(
                                new FilterStrategy(Group.Filters.filterWithComponent, Path.class, Port.class)
                        );
                    }
                    // </HACK>

                    // <MOVE_TO_WORLD_EVENT_HANDLER>
                    // Hide Portables' Ports.
//                pathAndPortEntities.setVisibility(Visible.INVISIBLE);
                    ports.setVisibility(Visible.INVISIBLE);

                    // <REFACTOR>
                    for (int i = 0; i < ports.size(); i++) {
                        Entity port = ports.get(i);
                        // TODO: Move to common location (in System?) and make function.
                        // TODO: (Response to previous TODO) Yes, in system, update based on state of Entity/components.
                        List<Entity> primitives = port.getComponent(Model.class).primitives;
                        for (int k = 0; k < primitives.size(); k++) {
                            primitives.get(k).getComponent(Visibility.class).visible = Visible.INVISIBLE;
                        }
                    }
                    // </REFACTOR>

                    Group<Model> pathAndPortModels = pathsAndPorts.getModels();
                    for (int i = 0; i < pathAndPortModels.size(); i++) {
                        pathAndPortModels.get(i).assetIndex = 0;
                    }


                    // Update distance between Hosts and Extensions
//                    world.getSystem(LayoutSystem.class).setPortableSeparation(World.HOST_TO_EXTENSION_SHORT_DISTANCE);
                    // </MOVE_TO_WORLD_EVENT_HANDLER>
                }
            }
        });
        // </EVENT_HANDLERS>

        return workspace;
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
        extension.addComponent(new Transform());
        extension.addComponent(new Physics());
        extension.addComponent(new Model());
        extension.addComponent(new Style());
        extension.addComponent(new Boundary());
        extension.addComponent(new Visibility());

        // <REFACTOR>
        extension.addComponent(new Scriptable());
        // </REFACTOR>

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
            port.addComponent(new TransformConstraint());
            port.getComponent(TransformConstraint.class).setReferenceEntity(extension);
        }

        // <LOAD_GEOMETRY_FROM_FILE>
        ModelBuilder modelBuilder = new ModelBuilder();

        Rectangle rectangle;

        // Create Shapes for ModelBuilder
        int BOARD_HEIGHT = 200; // Random.generateRandomInteger(50, 200); // was 125, 200
        rectangle = new Rectangle();
        rectangle.setHeight(200); // was 200
        rectangle.setWidth(200); // was 125, 200 // (Random.generateRandomInteger(50, 200)
        rectangle.setTag("Board");
        rectangle.setColor(Color.getRandomBoardColor()); // Gray: #f7f7f7, Greens: #ff53BA5D, #32CD32
        rectangle.setOutlineThickness(0);
        rectangle.isBoundary = true;

        modelBuilder.addShape(rectangle);

        // Set TransformConstraint for relative positioning and add Shape entity to ModelBuilder component.
        Entity primitive = Model.createPrimitiveFromShape(rectangle);
        primitive.getComponent(TransformConstraint.class).setReferenceEntity(extension);
        extension.getComponent(Model.class).primitives.add(primitive);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setTag("Header");
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        modelBuilder.addShape(rectangle);

        // Set TransformConstraint for relative positioning and add Shape entity to ModelBuilder component.
        primitive = Model.createPrimitiveFromShape(rectangle);
        primitive.getComponent(TransformConstraint.class).setReferenceEntity(extension);
        primitive.getComponent(TransformConstraint.class).relativeTransform.set(0, BOARD_HEIGHT / 2.0f + 7.0f);
        extension.getComponent(Model.class).primitives.add(primitive);
        // </LOAD_GEOMETRY_FROM_FILE>

        // Load geometry from file into ModelBuilder Component
        // TODO: Application.getPlatform().openFile(this, "Model_Host_Clay-v7.1.0-beta.json");

        // <EVENT_HANDLERS>
        world.eventManager.registerResponse("HOLD", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != extension) {
                    return;
                }

                world.createExtensionConfiguration(extension);
            }
        });

        world.eventManager.registerResponse("UNSELECT", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != extension) {
                    return;
                }

                if (world.getSystem(InputSystem.class).previousPrimaryTarget == extension) {

                    boolean openImageEditor = false;

                /*
                // TODO:
                Shape board = extension.getComponent(ModelBuilder.class).getModelComponent().getPrimitive("Board");
                List<Transform> vertices = board.getVertices();
                Log.v("ExtPos", "ex: " + event.getPosition().x + ", y: " + event.getPosition().y);
                for (int i = 0; i < vertices.size(); i++) {
                    Log.v("ExtPos", "x: " + vertices.get(i).x + ", y: " + vertices.get(i).y);
                    if (Primitive.distance(vertices.get(i), event.getPosition()) < 20) {
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
                        Application.getInstance().getWidgets().openImageEditor(extension);
                    } else {
                        Application.getInstance().getWidgets().openActionEditor(extension);
                    }
                }

                // Focus on selected Host
                Group<Entity> extensionPaths = Portable.getPaths(extension);
                Group<Entity> extensionPorts = Portable.getPorts(extension);
                extensionPaths.setVisibility(Visible.VISIBLE);
                extensionPorts.setVisibility(Visible.VISIBLE);

                // TODO: Set transparency
                // e.g., extension.getComponent(Style.class).setTransparency(extension, 1.0);

                // Show Ports and Paths for selected Host
                for (int i = 0; i < extensionPorts.size(); i++) {
                    Entity port = extensionPorts.get(i);

                    Group<Entity> paths = Port.getPaths(port);
                    for (int j = 0; j < paths.size(); j++) {
                        Entity path = paths.get(j);

                        // Show Ports
                        Entity sourcePort = Path.getSourcePort(path);
                        Entity targetPort = Path.getTargetPort(path);
                        sourcePort.getComponent(Visibility.class).visible = Visible.VISIBLE;
                        targetPort.getComponent(Visibility.class).visible = Visible.VISIBLE;


                        // Show Path
                        path.getComponent(Visibility.class).visible = Visible.VISIBLE;
                    }
                }
                // TODO: Replace above with?: portEntity.getComponent(Portable.class).getPorts().getModels().setVisibility(Visible.VISIBLE);

                // TODO: 11/13/2016 Set Title

                // Camera
//                Entity camera = world.entityManager.get().filterWithComponent(Camera.class).get(0);
//                world.getSystem(CameraSystem.class).setFocus(camera, extension);
            }
        });
        // </EVENT_HANDLERS>

        return extension;
    }


    /**
     * Creates a new {@code ExtensionEntity} connected to {@hostPort}.
     *
     * @param hostPort
     */
    public static Entity createCustomExtensionEntity(Entity hostPort, Transform initialPosition) {
        // TODO: Remove initialPosition. Should be able to figure out the positioning since have the initial port (and thus a side of the board where the most ports are connected).

        // IASM Message:
        // (1) touch extensionEntity to select from store, or
        // (2) drag signal to base, or
        // (3) touch elsewhere to cancel

        // TODO: Widgets to select Extension from repository then copy that Extension configuration!
        // TODO: (...) Then use that profile to create and configure Ports for the Extension.

        // Create Extension Entity
        Entity extension = World.getInstance().createEntity(Extension.class); // HACK: Because Extension is a Component

        // Set the initial position of the Extension
        extension.getComponent(Transform.class).set(initialPosition); // TODO: Set Physics.targetPosition instead? Probs!

        // Configure Host's Port (i.e., the Path's source Port)
        if (Port.getType(hostPort) == Signal.Type.NONE || Port.getDirection(hostPort) == Signal.Direction.NONE) {
            Port.setType(hostPort, Signal.Type.POWER_REFERENCE); // Set the default type to reference (ground)
            Port.setDirection(hostPort, Signal.Direction.BOTH);
        }

        // Configure Extension's Ports (i.e., the Path's target Port)
        Entity extensionPort = Portable.getPorts(extension).get(0);

        // Create Path from Host to Extension and configure the new Path
        // TODO: Create the Path and then apply it. It should automatically configure the
        // TODO: (...) Extension's Ports (so the previous segment of code can be removed and
        // TODO: (...) automated!). The idea here is that a Path can be created given two Ports,
        // TODO: (...) then a System will automatically configure the Ports based on the newly-
        // TODO: (...) existing Path's Port dependencies.
        if (!Port.hasPath(hostPort)) {
            Entity path = World.getInstance().createEntity(Path.class);
            Path.set(path, hostPort, extensionPort);
        } else {
            Entity path = Port.getPaths(hostPort).get(0);
            Path.set(path, hostPort, extensionPort);
            Path.setTarget(path, extensionPort);
        }

        return extension;
    }

    /**
     * Adds and existing {@code ExtensionEntity}.
     *
     * @param configuration
     * @param initialPosition
     * @return
     */
    public static Entity createExtensionFromProfile(Entity host, Configuration configuration, Transform initialPosition) {
        // NOTE: Previously called fetchExtension(...)

        // Log.v("IASM", "(1) touch extensionEntity to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // Create the Extension
        Entity extension = World.getInstance().createEntity(Extension.class);

        // <HACK>
        // TODO: Remove references to Configuration in Portables. Remove Configuration altogether!?
        World.getInstance().configureExtensionFromConfiguration(extension, configuration);
        // </HACK>

        Log.v("Configuration", "extension from profile # ports: " + Portable.getPorts(extension).size());

        // Update ExtensionEntity Position
        extension.getComponent(Transform.class).set(initialPosition);

        // Automatically select and connect all Paths to HostEntity
//        World.getInstance().getSystem(LayoutSystem.class).autoConnectToHost(host, extension);

        // TODO: Start IASM based on automatically configured Paths to HostEntity.

//        World.getInstance().getSystem(LayoutSystem.class).updateExtensionLayout(host);

        return extension;
    }
    // </REFACTOR>

    public static Entity createPathEntity(final World world) {
        final Entity path = new Entity();

        // Add Path Component (for type identification)
        path.addComponent(new Path()); // Unique to Path
        path.addComponent(new Transform());
        path.addComponent(new Physics());
        path.addComponent(new Model());
        path.addComponent(new Style());
        path.addComponent(new Boundary());
        path.addComponent(new Visibility());

        // <SETUP_PATH_IMAGE_GEOMETRY>
        // TODO: DO NOT POPULATE GEOMETRY HERE. GENERATE IT IN THE PATH SYSTEM.

        // Board
        Segment segment = new Segment();
        segment.setOutlineThickness(2.0);
        segment.setTag("Path");
        segment.setColor("#1f1f1e"); // #f7f7f7
        segment.setOutlineThickness(1);
//        imageBuilder.addPrimitive(segment);

        // Set TransformConstraint for relative positioning and add Shape entity to ModelBuilder component.
        Entity primitive = Model.createPrimitiveFromShape(segment);
        primitive.getComponent(TransformConstraint.class).setReferenceEntity(path);
        path.getComponent(Model.class).primitives.add(primitive);

        // <HACK>
        // Set Label
//        pathShapeEntity.getComponent(Label.class).label = "Path";
        // </HACK>

        Circle circle = new Circle();
        circle.getPosition().z = 50;
        circle.setRadius(50.0);
        circle.setTag("Source Port"); // TODO: Give proper name...
        circle.setColor("#990000"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
        circle.isBoundary = true;
//        imageBuilder.addPrimitive(circle);

        // Set TransformConstraint for relative positioning and add Shape entity to ModelBuilder component.
        primitive = Model.createPrimitiveFromShape(circle);
        primitive.getComponent(TransformConstraint.class).setReferenceEntity(path);
        path.getComponent(Model.class).primitives.add(primitive);

//        Entity shapeEntity = world.entityManager.get(pathShapeUuid);
        // <HACK>
//        shapeEntity.getComponent(TransformConstraint.class).relativeTransform.set();
        // </HACK>

        // <HACK>
        // Set Label
//        pathShapeEntity.getComponent(Label.class).label = "Source Port";
        // </HACK>

        circle = new Circle();
        circle.getPosition().z = 50;
        circle.setRadius(50.0);
        circle.setTag("Target Port"); // TODO: Give proper name...
        circle.setColor("#990000"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
        circle.isBoundary = true;
//        imageBuilder.addPrimitive(circle);

        // Set TransformConstraint for relative positioning and add Shape entity to ModelBuilder component.
        primitive = Model.createPrimitiveFromShape(circle);
        primitive.getComponent(TransformConstraint.class).setReferenceEntity(path);
        path.getComponent(Model.class).primitives.add(primitive);

        // <HACK>
        // Set Label
//        pathShapeEntity.getComponent(Label.class).label = "Target Port";
        // </HACK>

        // TODO: 11/5/2016 Add Port circles to the Path? So moving paths around will be easier? Then Port images are always just the same color. They look different because of the Path image. Path can contain single node. Then can be stretched out to include another Port.
        // TODO: 11/5/2016 Create corresponding world state CREATING_PATH, MODIFYING_PATH/MOVING_PATH, etc.

//        path.getComponent(ModelBuilder.class).setImage(imageBuilder);
        path.getComponent(Transform.class).z = 10;
        // </SETUP_PATH_IMAGE_GEOMETRY>

        // <EVENT_HANDLERS>
        world.eventManager.registerResponse("MOVE", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != path) {
                    return;
                }

                if (event.getDistance() < Event.MINIMUM_MOVE_DISTANCE) {
                    return;
                }

                boolean isSingletonPath = (Path.getTargetPort(path) == null);
                if (isSingletonPath) {

                    // Singleton Path

                    Log.v("handlePathEvent", "Moving on singleton Path.");

                    Path.setState(path, Component.State.EDIT);

                    Entity pathShape = path.getComponent(Model.class).primitives.filterWithLabel("Path").get(0);
                    Segment pathSegment = (Segment) pathShape.getComponent(Primitive.class).shape;
                    pathSegment.setTarget(event.getPosition());
                    pathShape.getComponent(Visibility.class).visible = Visible.VISIBLE;

                    // Determine if taking "create new Extension" action. This is determined to be true
                    // if at least one Extension is "near enough" to the Event's target position.
                    boolean isCreateExtensionAction = true; // TODO: Convert into Event to send to World?
                    Group<Entity> extensions = world.entityManager.get().filterWithComponent(Extension.class);
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
                    Entity prototypeExtension = world.entityManager.get().filterWithComponents(Prototype.class, Label.class).filterWithLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    if (isCreateExtensionAction) {
                        prototypeExtension.getComponent(Visibility.class).visible = Visible.VISIBLE;

                        // <REFACTOR>
                        // TODO: Move to common location (in System?) and make function.
                        List<Entity> primitives = prototypeExtension.getComponent(Model.class).primitives;
                        for (int i = 0; i < primitives.size(); i++) {
                            primitives.get(i).getComponent(Visibility.class).visible = Visible.VISIBLE;
                        }
                        // </REFACTOR>

                        // Set Event Angle (angle from first Event to current Event)
                        double eventAngle = camp.computer.clay.util.Geometry.getAngle(
                                event.getSecondaryTarget().getComponent(Transform.class), // event.getFirstEvent().getTargetPort().getComponent(Transform.class),
                                event.getPosition()
                        );
                        // Set prototype Extension transform
                        prototypeExtension.getComponent(Transform.class).set(event.getPosition());
                        prototypeExtension.getComponent(Transform.class).setRotation(eventAngle);
                    } else {
                        prototypeExtension.getComponent(Visibility.class).visible = Visible.INVISIBLE;

                        // <REFACTOR>
                        // TODO: Move to common location (in System?) and make function.
                        List<Entity> primitives = prototypeExtension.getComponent(Model.class).primitives;
                        for (int i = 0; i < primitives.size(); i++) {
                            primitives.get(i).getComponent(Visibility.class).visible = Visible.INVISIBLE;
                        }
                        // </REFACTOR>
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
                                if (Port.getType(existingPort) == Signal.Type.NONE) {
                                    addNewPort = false;
                                    break;
                                }
                            }
                            Log.v("handlePathEvent", "addNewPort: " + addNewPort);

                            // Add new Port to the Extension (if determined necessary)
                            if (addNewPort) {
                                Entity newPort = world.createEntity(Port.class);

                                // <HACK>
                                newPort.addComponent(new TransformConstraint());
                                newPort.getComponent(TransformConstraint.class).setReferenceEntity(extension);
//                            newPort.getComponent(TransformConstraint.class).relativeTransform.set(0, 25.0 * 6.0);
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

                    Entity sourcePortShape = path.getComponent(Model.class).primitives.filterWithLabel("Source Port").get(0);
                    if (event.getSecondaryTarget() == sourcePortShape) {
                        Log.v("handlePathEvent", "Touched Source");
                        //sourcePortShape.getComponent(Primitive.class).shape.setPosition(event.getPosition()); // TODO: Change TRANSFORM
                        sourcePortShape.getComponent(Transform.class).set(event.getPosition()); // TODO: Change TRANSFORM
                        // TODO: sourcePortShape.getComponent(Physics.class).targetTransform.set(event.getPosition());

                        Path.setState(path, Component.State.EDIT);
                    }

                    Entity targetPortShape = path.getComponent(Model.class).primitives.filterWithLabel("Target Port").get(0);
                    if (event.getSecondaryTarget() == targetPortShape) {
                        Log.v("handlePathEvent", "Touched Target");
                        targetPortShape.getComponent(Transform.class).set(event.getPosition()); // TODO: Change TRANSFORM
                        // TODO: targetPortShape.getComponent(Physics.class).targetTransform.set(event.getPosition()); // TODO: Change TRANSFORM

                        Path.setState(path, Component.State.EDIT);
                    }

                }
            }
        });

        world.eventManager.registerResponse("UNSELECT", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != path) {
                    return;
                }

                Entity sourcePortShape = path.getComponent(Model.class).primitives.filterWithLabel("Source Port").get(0);
                Entity targetPortShape = path.getComponent(Model.class).primitives.filterWithLabel("Target Port").get(0);

                Log.v("handlePathEvent", "UNSELECT PATH.");

                if (Path.getTargetPort(path) != null) {

                    Log.v("handlePathEvent", "NON SINGLETON.");

                    // Full Path (non-singleton Path)

                    if (Path.getState(path) != Component.State.EDIT) {

                        // <PATH>
                        // Set next Path type
                        Signal.Type nextType = Signal.Type.next(Path.getType(path));
                        while ((nextType == Signal.Type.NONE) || (nextType == Path.getType(path))) {
                            nextType = Signal.Type.next(nextType);
                        }
                        Path.setType(path, nextType);
                        // <PATH>

                        // Notification
                        world.createAndConfigureNotification("" + nextType, event.getPosition(), 800);

                    } else if (Path.getState(path) == Component.State.EDIT) {

                        Group<Entity> dropTargetPorts = world.entityManager.get().filterWithComponent(Port.class).filterContains(event.getPosition());

                        // Moved the Path to another Port
                        if (dropTargetPorts.size() > 0) {

                            Entity dropTargetPort = dropTargetPorts.get(0); // NOTE: This gets the first port in the list, no matter how many there are or which Ports they are. Maybe not always work...
                            Log.v("TargetAreaPort", "targetAreaPort: " + dropTargetPort);

                            // Remap the Path's Port if the touched Port doesn't already have a Path
                            Group<Entity> targetPaths = Port.getPaths(dropTargetPort);
                            if (targetPaths.size() > 0 && targetPaths.get(0) == path) {

                                // Swap the Path's Ports in the SAME path (swap Ports/flip direction)
                                Log.v("handlePathEvent", "flipping the path");
                                Entity sourcePort = Path.getSourcePort(path);
                                Path.setSource(path, Path.getTargetPort(path));
                                Path.setTarget(path, sourcePort);

                                // TODO: path.getComponent(Path.class).setDirection();

                                // Notification
                                world.createAndConfigureNotification("flipped path", event.getPosition(), 1000);

                            } else if (targetPaths.size() > 0 && targetPaths.get(0) != path) {

                                // TODO: Make sure both Ports are connected between both a common Host and Extension

                                // Swap ports ACROSS different paths (swap Paths)
                                if (camp.computer.clay.util.Geometry.contains(Boundary.get(sourcePortShape), event.getPosition())) {
                                    // Swapping path A source port shape...
                                    if (dropTargetPort == Path.getSourcePort(targetPaths.get(0))) {
                                        Entity sourcePort = Path.getSourcePort(path);
                                        Path.setSource(path, Path.getSourcePort(targetPaths.get(0))); // Path.getTargetPort(path));
                                        Path.setSource(targetPaths.get(0), sourcePort);
                                    } else if (dropTargetPort == Path.getTargetPort(targetPaths.get(0))) {
                                        Entity sourcePort = Path.getSourcePort(path);
                                        Path.setSource(path, Path.getTargetPort(targetPaths.get(0))); // Path.getTargetPort(path));
                                        Path.setTarget(targetPaths.get(0), sourcePort);
                                    }
                                } else if (camp.computer.clay.util.Geometry.contains(Boundary.get(targetPortShape), event.getPosition())) {
                                    // Swapping path A target port shape...
                                    if (dropTargetPort == Path.getSourcePort(targetPaths.get(0))) {
                                        Entity targetPath = Path.getTargetPort(path);
                                        Path.setTarget(path, Path.getSourcePort(targetPaths.get(0))); // Path.getTargetPort(path));
                                        Path.setSource(targetPaths.get(0), targetPath);
                                    } else if (dropTargetPort == Path.getTargetPort(targetPaths.get(0))) {
                                        Entity targetPath = Path.getTargetPort(path);
                                        Path.setTarget(path, Path.getTargetPort(targetPaths.get(0))); // Path.getTargetPort(path));
                                        Path.setTarget(targetPaths.get(0), targetPath);
                                    }
                                }

                                // TODO: path.getComponent(Path.class).setDirection();

                                // Notification
                                world.createAndConfigureNotification("swapped paths", event.getPosition(), 1000);

                            } else if (Port.getPaths(dropTargetPort).size() == 0) {

                                // Remap the Path's Ports

                                // Check if source or target in Path was moved, and reassign it
                                Entity sourcePortShape2 = path.getComponent(Model.class).primitives.filterWithLabel("Source Port").get(0);
                                Entity targetPortShape2 = path.getComponent(Model.class).primitives.filterWithLabel("Target Port").get(0);
                                if (camp.computer.clay.util.Geometry.contains(Boundary.get(sourcePortShape2), event.getPosition())) {

                                    // Check if the new Path's Port's would be on the same Portable
                                    if (Path.getTargetPort(path).getComponent(Structure.class).parentEntity == dropTargetPort.getComponent(Structure.class).parentEntity) {
                                        // Prevent the Path from moving onto the Extension with both Ports
                                        if (!Path.getTargetPort(path).getComponent(Structure.class).parentEntity.hasComponent(Extension.class)) {
                                            Path.setSource(path, dropTargetPort);
                                        }
                                    } else {
                                        Path.setSource(path, dropTargetPort);
                                    }

                                } else if (Geometry.contains(Boundary.get(targetPortShape2), event.getPosition())) {

                                    // Check if the new Path's Port's would be on the same Portable
                                    if (Path.getSourcePort(path).getComponent(Structure.class).parentEntity == dropTargetPort.getComponent(Structure.class).parentEntity) {
                                        // Prevent the Path from moving onto the Extension with both Ports
                                        if (!Path.getSourcePort(path).getComponent(Structure.class).parentEntity.hasComponent(Extension.class)) {
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
                            world.entityManager.remove(path);

                            // Notification
                            world.createAndConfigureNotification("removed path", event.getPosition(), 1000);

                            // Reset Ports that were in removed Path
                            Entity sourcePort = Path.getSourcePort(path);
                            Port.setType(sourcePort, Signal.Type.NONE);
                            Entity targetPort = Path.getTargetPort(path);
                            Port.setType(targetPort, Signal.Type.NONE);

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
                                while (extensionPorts.size() > 0) {
                                    Entity extensionPort = extensionPorts.get(0);
                                    world.entityManager.remove(extensionPort);
                                    extensionPorts.remove(extensionPort); // Remove from Portable
                                }

                                world.entityManager.remove(extension);

                                // Notification
                                world.createAndConfigureNotification("removed extension", extension.getComponent(Transform.class), 1000);
                            }

                        }

                    }

                    Path.setState(path, Component.State.VIEW);

                } else {

                    // Singleton Path

                    // (Host.Port, ..., World) Action Pattern

                    Group<Entity> targetAreaPorts = world.entityManager.get().filterWithComponent(Port.class).filterContains(event.getPosition());

                    Log.v("handlePathEvent", "creating extension");

                    // If prototype Extension is visible, create Extension
//                if (world.getExtensionPrototypeVisibility2() == Visible.VISIBLE) {
                    Entity prototypeExtension = world.entityManager.get().filterWithComponent(Label.class).filterWithLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    if (prototypeExtension.getComponent(Visibility.class).visible == Visible.VISIBLE) {

                        Log.v("handlePathEvent", "creating extension");

//                    // Hide prototype Path and prototype Extension
//                    world.setPathPrototypeVisibility(Visible.INVISIBLE);
//                    world.setExtensionPrototypeVisibility2(Visible.INVISIBLE);
//                        Entity extensionPrototype = world.entityManager.get().filterWithComponent(Label.class).filterWithLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                        prototypeExtension.getComponent(Visibility.class).visible = Visible.INVISIBLE;

                        // <REFACTOR>
                        // TODO: Move to common location (in System?) and make function.
                        List<Entity> primitives = prototypeExtension.getComponent(Model.class).primitives;
                        for (int i = 0; i < primitives.size(); i++) {
                            primitives.get(i).getComponent(Visibility.class).visible = Visible.INVISIBLE;
                        }
                        // </REFACTOR>

//                    Entity hostPort = event.getFirstEvent().getTargetPort();
                        Entity hostPort = Path.getSourcePort(path);

                        Log.v("handlePathEvent", "hostPort: " + hostPort);

                        // Create new custom Extension. Custom Extension can be configured manually.
                        Entity extension = createCustomExtensionEntity(hostPort, event.getPosition());

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
//                        Entity host = hostPort.getParent(); // HACK
//                        world.getSystem(LayoutSystem.class).setPortableSeparation(World.HOST_TO_EXTENSION_LONG_DISTANCE);

//                        world.getSystem(LayoutSystem.class).updateExtensionLayout(host);
                        // <STYLE_AND_LAYOUT>

                        // Set Camera focus on the Extension
                        // camera.setFocus(extension);
                    } else if (event.isTap()) { // } else if (event.getFirstEvent().getTargetPort() == event.getTargetPort()) {

                        // Change Singleton Path Type

                        // <PATH>
                        // Set next Path type
                        Path pathComponent = path.getComponent(Path.class);
                        Signal.Type nextType = Signal.Type.next(Path.getType(path));
                        while ((nextType == Signal.Type.NONE) || (nextType == Path.getType(path))) {
                            nextType = Signal.Type.next(nextType);
                        }
                        Path.setType(path, nextType);
//                Log.v("EventHandlerSystem", "Setting path type to: " + nextType);

                        // Notification
                        world.createAndConfigureNotification("" + nextType, event.getPosition(), 800);
                        // <PATH>

                    } else if (targetAreaPorts.size() > 0) { //} else if (event.getFirstEvent().getTargetPort() != event.getTargetPort()) {

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
                                Path.setState(dropTargetPath, Component.State.EDIT);
                                Entity tempSourcePort = Path.getSourcePort(dropTargetPath);
                                Path.setSource(dropTargetPath, null); // Reset path
                                Path.setTarget(dropTargetPath, null); // Reset path
                                world.entityManager.remove(dropTargetPath); // Delete path!
                                // </CLEANUP_ENTITY_DELETE_CODE>

                                // Update the Path from the source Port
                                Entity targetPort = tempSourcePort; // new target is source port from other path
                                Path.setTarget(path, targetPort);
                            }
                        }

                        // Update the Path's target Port
                        if (!dropTargetEntity.hasComponent(Port.class)) { // if (!event.getTargetPort().hasComponent(Port.class)) {
                            return;
                        }

                        Entity dropTargetPort = dropTargetEntity; // event.getTargetPort();

                        Path.setTarget(path, dropTargetPort);

                        world.createAndConfigureNotification("added path", event.getPosition(), 1000);

                    }

                    Path.setState(path, Component.State.VIEW);
                }
            }
        });
        // </EVENT_HANDLERS>

        path.isActive = true;

        return path;
    }

    public static Entity createPortEntity(final World world) {

        final Entity port = new Entity();

        // Add Components
        port.addComponent(new Port()); // Unique to Port
        port.addComponent(new Transform());
        port.addComponent(new Structure());
        port.addComponent(new Model());
        port.addComponent(new Style());
        port.addComponent(new Physics());
        port.addComponent(new Boundary());
        port.addComponent(new Visibility());
        port.addComponent(new Label());

        // <LOAD_GEOMETRY_FROM_FILE>
//        ModelBuilder imageBuilder = new ModelBuilder();

        // Create Shapes for ModelBuilder
        Circle circle = new Circle();
        circle.setRadius(50.0);
        circle.setTag("Port"); // TODO: Give proper name...
        circle.setColor("#ffe7e7e7"); // Gray: #f7f7f7, Greens: #32CD32
        circle.setOutlineThickness(0);
        circle.isBoundary = true;
//        imageBuilder.addPrimitive(circle);

        // Set TransformConstraint for relative positioning and add Shape entity to ModelBuilder component.
        Entity primitive = Model.createPrimitiveFromShape(circle);
        primitive.getComponent(TransformConstraint.class).setReferenceEntity(port);
        port.getComponent(Model.class).primitives.add(primitive);

//        // <HACK>
//        // Set Label
//        portShape.getComponent(Label.class).label = "Port";
//        // </HACK>

//        port.getComponent(ModelBuilder.class).setImage(imageBuilder);
        // </LOAD_GEOMETRY_FROM_FILE>

        // <EVENT_HANDLERS>
        world.eventManager.registerResponse("UNSELECT", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != port) {
                    return;
                }

                // (Host.Port, ..., Host.Port) Action Pattern

                if (event.isTap() && event.getTarget() == port) {

                    // (Host.Port A, ..., Host.Port A) Action Pattern
                    // i.e., The action's first and last eventManager address the same Port. Therefore, it must be either a tap or a hold.

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
                        Path.setType(singletonPath, Signal.Type.SWITCH);
                    }

                }
            }
        });
        // </EVENT_HANDLERS>

        return port;

    }

    public static Entity createCameraEntity(final World world) {

        final Entity camera = new Entity();

        // Components
        camera.addComponent(new Camera()); // Unique to Camera
        camera.addComponent(new Transform());
        camera.addComponent(new Physics());

        // <EVENT_HANDLERS>
        world.eventManager.registerResponse("MOVE", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() != camera) {
                    return;
                }

                Camera cameraComponent = camera.getComponent(Camera.class);
                Physics physicsComponent = camera.getComponent(Physics.class);

                // TODO: Make sure there's no inconsistency "information access sequence" between this EventHandlerSystem, InputSystem, and PlatformRenderSurface.onTouch. Should only access info from previously dispatched? event
                //world.getSystem(CameraSystem.class).setOffset(camera, event.xOffset, event.yOffset);
                cameraComponent.mode = Camera.Mode.FREE;

                physicsComponent.targetTransform.set(
                        physicsComponent.targetTransform.x + event.xOffset,
                        physicsComponent.targetTransform.y + event.yOffset
                );
            }
        });

        world.eventManager.registerResponse("UNSELECT", new EventResponse<Entity>() {
            @Override
            public void execute(Event event) {

                if (event.getTarget() == camera) {
                    Camera cameraComponent = camera.getComponent(Camera.class);

                    // Camera
                    if (event.isTap()) {
                        cameraComponent.focus = null;
                        cameraComponent.mode = Camera.Mode.FOCUS;
                    }

                } else if (event.getTarget().hasComponent(Host.class)) {

//                    world.getSystem(CameraSystem.class).setFocus(camera, event.getTargetPort());
                    Camera cameraComponent = camera.getComponent(Camera.class);
                    cameraComponent.focus = event.getTarget();
                    cameraComponent.mode = Camera.Mode.FOCUS;

                } else if (event.getTarget().hasComponent(Extension.class)) {

//                    world.getSystem(CameraSystem.class).setFocus(camera, event.getTargetPort());
                    // TODO: Create CameraEvent.SetFocus(target)
                    Camera cameraComponent = camera.getComponent(Camera.class);
                    cameraComponent.focus = event.getTarget();
                    cameraComponent.mode = Camera.Mode.FOCUS;

                }
            }
        });
        // </EVENT_HANDLERS>

        return camera;
    }

    public static Entity createPrimitiveEntity(final World world) {

        Entity shape = new Entity();

        // Components
        shape.addComponent(new Label());
        shape.addComponent(new Primitive()); // Unique to Shape Entity
        shape.addComponent(new Transform());
        shape.addComponent(new Structure());
        shape.addComponent(new Physics());
        shape.addComponent(new Style());
        shape.addComponent(new Boundary());
        shape.addComponent(new Visibility());

        shape.addComponent(new TransformConstraint());
        //shape.getComponent(TransformConstraint.class).setReferenceEntity(extension);

        return shape;
    }

    public static Entity createNotificationEntity(final World world) {

        Entity notification = new Entity();

        // Components
        notification.addComponent(new Notification()); // Unique to Notification Entity
        notification.addComponent(new Transform());
        notification.addComponent(new Model());
        notification.addComponent(new Style());
        notification.addComponent(new Visibility());
        notification.addComponent(new Timer());

        // <HACK>
        notification.getComponent(Timer.class).timeout = World.DEFAULT_NOTIFICATION_TIMEOUT;
        // </HACK>

        // ModelBuilder
//        ModelBuilder imageBuilder = new ModelBuilder();

        Text text = new Text();
        text.setText("DEFAULT_TEXT");
        text.size = World.NOTIFICATION_FONT_SIZE;
        text.setColor("#ff000000");
        text.setPosition(0, 0);
        text.font = World.NOTIFICATION_FONT;

        // Set TransformConstraint for relative positioning and add Shape entity to ModelBuilder component.
        Entity primitive = Model.createPrimitiveFromShape(text);
        primitive.getComponent(TransformConstraint.class).setReferenceEntity(notification);
        notification.getComponent(Model.class).primitives.add(primitive);

//        imageBuilder.addPrimitive(text);

        // <HACK>
        notification.getComponent(Transform.class).z = 20;
        // </HACK>

//        notification.getComponent(ModelBuilder.class).setImage(imageBuilder);

        return notification;
    }
}
