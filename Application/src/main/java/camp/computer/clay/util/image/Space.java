package camp.computer.clay.util.image;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.entity.Actor;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.engine.entity.Extension;
import camp.computer.clay.model.Group;
import camp.computer.clay.engine.entity.Host;
import camp.computer.clay.model.Model;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Camera;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.space.image.ExtensionImage;
import camp.computer.clay.space.image.HostImage;
import camp.computer.clay.space.image.PathImage;
import camp.computer.clay.space.image.PortableImage;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.image.util.ShapeGroup;

// TODO: DO NOT extend Image. Remove this class!
public class Space extends Image<Model> {

    public static double PIXEL_PER_MILLIMETER = 6.0;

    protected Visibility extensionPrototypeVisibility = Visibility.INVISIBLE;
    protected Transform extensionPrototypePosition = new Transform();

    protected Visibility pathPrototypeVisibility = Visibility.INVISIBLE;
    protected Transform pathPrototypeSourcePosition = new Transform(0, 0);
    protected Transform pathPrototypeDestinationCoordinate = new Transform(0, 0);

    private List<Actor> actors = new LinkedList<>();

    public Space(Model model) {
        super(model);
        setup();
    }

    private void setup() {
        setupActions();
    }

    private void setupActions() {
        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();
                Camera camera = event.getActor().getCamera();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                } else if (event.getType() == Event.Type.MOVE) {

                    Log.v("Space_Action", "MOVE");

                    if (action.isDragging()) {
                        Log.v("Space_Action", "DRAG");
                        camera.setOffset(action.getOffset());
                    }

                } else if (event.getType() == Event.Type.UNSELECT) {

//                    // Previous Action targeted also this Extension
//                    if (action.getPrevious() != null && action.getPrevious().getFirstEvent().getTargetImage().getEntity() == getEntity()) {
//
//                        if (action.isTap()) {
//
//                            Log.v("Space_Action", "UNSELECT 2");
//
//                            // Title
//                            setTitleText("Project");
//                            setTitleVisibility(Visibility.VISIBLE);
//                        }
//
//                    } else {

                    // NOT a repeat tap on this Image

                    if (action.isTap()) {
                        Log.v("Space_Action", "UNSELECT 1");

                        // Title
                        setTitleVisibility(Visibility.INVISIBLE);

                        // Camera
                        camera.setFocus(getSpace());
                    }
//                    }
                }
            }
        });
    }

    public Model getModel() {
        return getEntity();
    }

    // TODO: Replace with Entity.Manager
//    private Group<Entity> entities = new Group<>();

    public Group<Entity> getEntities() {
        return this.getModel().getEntities();
    }

    public <T extends Entity> void addEntity(T entity) {

        if (!getEntities().contains(entity)) {
            getEntities().add(entity);
        }

        if (entity instanceof Host) {

            Host host = (Host) entity;

            // Create Host Image
            HostImage hostImage = new HostImage(host);
            hostImage.setSpace(this);

            // Assign Image to Entity
            host.setComponent(hostImage);

            // Create Port Shapes for each of the PhoneHost's Ports
            for (int i = 0; i < host.getPorts().size(); i++) {
                Port port = host.getPorts().get(i);
                addEntity(port);
            }

            // Add Host Image to Space
//            addImage(hostImage);

            // Position the Image
            adjustLayout();

        } else if (entity instanceof Extension) {

            Extension extension = (Extension) entity;

            // Create Extension Image
            ExtensionImage extensionImage = new ExtensionImage(extension);
            extensionImage.setSpace(this);

            // Assign Image to Entity
            extension.setComponent(extensionImage);

            // Create Port Shapes for each of the Extension's Ports
            for (int i = 0; i < extension.getPorts().size(); i++) {
                Port port = extension.getPorts().get(i);
                addEntity(port);
            }

            // Add Extension Image to Space
//            addImage(extensionImage);

        } else if (entity instanceof Port) {

//            Port port = (Port) entity;

        } else if (entity instanceof Path) {

            Path path = (Path) entity;

            // Create Path Image
            PathImage pathImage = new PathImage(path);
            pathImage.setSpace(this);

            // Assign Image to Entity
            path.setComponent(pathImage);
        }
    }

    public void addActor(Actor actor) {
        if (!this.actors.contains(actor)) {
            this.actors.add(actor);
        }
    }

    /**
     * Sorts {@code Image}s by layer.
     */
    @Override
    public void updateLayers() {

        Group<Image> images = getEntities().getImages();

        for (int i = 0; i < images.size() - 1; i++) {
            for (int j = i + 1; j < images.size(); j++) {
                // Check for out-of-order pairs, and swap them
                if (images.get(i).layerIndex > images.get(j).layerIndex) {
                    Image image = images.get(i);
                    images.set(i, images.get(j));
                    images.set(j, image);
                }
            }
        }

        /*
        // TODO: Sort using this after making Group implement List
        Collections.sort(Database.arrayList, new Comparator<MyObject>() {
            @Override
            public int compare(MyObject o1, MyObject o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        */
    }

    @Override
    public Space getSpace() {
        return this;
    }

    // TODO: Use base class's addImage() so Shapes are added to super.shapes. Then add an index instead of layers?

    /**
     * Automatically determines and assigns a valid position for all {@code Host} {@code Image}s.
     */
    private void adjustLayout() {

        Group<Image> hostImages = getEntities().filterType2(Host.class).getImages();

        // Set position on grid layout
        if (hostImages.size() == 1) {
            hostImages.get(0).getEntity().getPosition().set(0, 0);
        } else if (hostImages.size() == 2) {
            hostImages.get(0).getEntity().getPosition().set(-300, 0);
            hostImages.get(1).getEntity().getPosition().set(300, 0);
        } else if (hostImages.size() == 5) {
            hostImages.get(0).getEntity().getPosition().set(-300, -600);
            hostImages.get(0).getEntity().getPosition().setRotation(0);
            hostImages.get(1).getEntity().getPosition().set(300, -600);
            hostImages.get(1).getEntity().getPosition().setRotation(20);
            hostImages.get(2).getEntity().getPosition().set(-300, 0);
            hostImages.get(2).getEntity().getPosition().setRotation(40);
            hostImages.get(3).getEntity().getPosition().set(300, 0);
            hostImages.get(3).getEntity().getPosition().setRotation(60);
            hostImages.get(4).getEntity().getPosition().set(-300, 600);
            hostImages.get(4).getEntity().getPosition().setRotation(80);
        }

        // TODO: Set position on "scatter" layout

        // Set rotation
        // image.setRotation(Probability.getRandomGenerator().nextInt(360));
    }

    public Model getEntity() {
        return this.entity;
    }

    // TODO: Remove this! First don't extend Image on Shape (this class)? Make TouchableComponent?
    public ShapeGroup getShapes() {
//        return getImages().getShapes();
        ShapeGroup shapes = new ShapeGroup();
        Group<Image> images = getEntities().getImages();
        for (int i = 0; i < images.size(); i++) {
            shapes.addAll(images.get(i).getShapes());
        }
        return shapes;
    }

    // TODO: Refactor to be cleaner and leverage other classes...
    public <T extends Entity> ShapeGroup getShapes(Class<? extends Entity>... entityTypes) {
        ShapeGroup shapeGroup = new ShapeGroup();
        Group<Image> imageList = getEntities().getImages();

        for (int i = 0; i < imageList.size(); i++) {
            shapeGroup.addAll(imageList.get(i).getShapes(entityTypes));
        }

        return shapeGroup.filterType(entityTypes);
    }

    public Shape getShape(Entity entity) {
        Group<Image> images = getEntities().getImages();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            Shape shape = image.getShape(entity);
            if (shape != null) {
                return shape;
            }
        }
        return null;
    }

    @Override
    public void update() {

        // Update Actors
        for (int i = 0; i < actors.size(); i++) {
            this.actors.get(i).update();
        }

        // Update Images
        for (int i = 0; i < getEntities().size(); i++) {
            if (getEntities().get(i).hasComponent(Image.class)) {
                Image image = getEntities().get(i).getComponent(Image.class);

                // Update bounding box of Image
                // TODO:

                // Update the Image
                image.update();
            }
        }

        // Update Camera(s)
        getEntity().getActor(0).getCamera().update();


//        // Sandbox
//        Line line = new Line();
//        line.setRotation(45);
//        for (int i = -100; i <= 100; i += 10) {
//            Transform linePoint = line.getPoint(i);
//        }
    }

    @Override
    public void draw(Display display) {

        display.canvas.save();

        // Draw Portables
        for (int i = 0; i < getEntities().size(); i++) {
            if (getEntities().get(i).hasComponent(Image.class)) {
                if (!(getEntities().get(i).getComponent(Image.class) instanceof ExtensionImage)) {
                    getEntities().get(i).getComponent(Image.class).draw(display);
                }
            }
        }

        // Draw Extensions
        for (int i = 0; i < getEntities().size(); i++) {
            if (getEntities().get(i).hasComponent(Image.class)) {
                if (getEntities().get(i).getComponent(Image.class) instanceof ExtensionImage) {
                    getEntities().get(i).getComponent(Image.class).draw(display);
                }
            }
        }

        // Draw any prototype Paths and Extensions
        drawPathPrototype(display);
        drawExtensionPrototype(display);

        display.canvas.restore();

//        getEntity().getActor(0).getCamera().setFocus(this);
    }



    // <EXTENSION_PROTOTYPE>
    private void drawExtensionPrototype(Display display) {
        if (extensionPrototypeVisibility == Visibility.VISIBLE) {

            Paint paint = display.paint;

            double pathRotationAngle = Geometry.getAngle(pathPrototypeSourcePosition, extensionPrototypePosition);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#fff7f7f7"));

            display.drawRectangle(extensionPrototypePosition, pathRotationAngle + 180, 200, 200);
        }
    }

    // TODO: Make this into a shape and put this on a separate layerIndex!
    public void drawPathPrototype(Display display) {
        if (pathPrototypeVisibility == Visibility.VISIBLE) {

            Paint paint = display.paint;

            double triangleWidth = 20;
            double triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
            double triangleSpacing = 35;

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
//            paint.setColor(this.getUniqueColor());

            double pathRotationAngle = Geometry.getAngle(
                    pathPrototypeSourcePosition,
                    pathPrototypeDestinationCoordinate
            );

            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(
                    pathPrototypeSourcePosition,
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(
                    pathPrototypeDestinationCoordinate,
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            paint.setColor(Color.parseColor("#efefef"));
            display.drawTrianglePath(pathStartCoordinate, pathStopCoordinate, triangleWidth, triangleHeight);

            // Color
            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.parseColor("#efefef"));
            double shapeRadius = 40.0;
            display.drawCircle(pathPrototypeDestinationCoordinate, shapeRadius, 0.0f);
        }
    }

    public void setPathPrototypeVisibility(Visibility visibility) {
        pathPrototypeVisibility = visibility;
    }

    public Visibility getPathPrototypeVisibility() {
        return pathPrototypeVisibility;
    }

    public void setPathPrototypeSourcePosition(Transform position) {
        this.pathPrototypeSourcePosition.set(position);
    }

    public void setPathPrototypeDestinationPosition(Transform position) {
        this.pathPrototypeDestinationCoordinate.set(position);
    }

    public void setExtensionPrototypePosition(Transform position) {
        this.extensionPrototypePosition.set(position);
    }

    public void setExtensionPrototypeVisibility(Visibility visibility) {
        extensionPrototypeVisibility = visibility;
    }

    public Visibility getExtensionPrototypeVisibility() {
        return extensionPrototypeVisibility;
    }
    // </EXTENSION_PROTOTYPE>


    public void setPortableSeparation(double distance) {
        // <HACK>
        // TODO: Replace ASAP. This is shit.
        Group<Image> extensionImages = getEntities().filterType2(Extension.class).getImages();
        for (int i = 0; i < extensionImages.size(); i++) {
            ExtensionImage extensionImage = (ExtensionImage) extensionImages.get(i);

            if (extensionImage.getExtension().getHosts().size() > 0) {
                Host host = extensionImage.getExtension().getHosts().get(0);
                HostImage hostImage = (HostImage) host.getComponent(Image.class);
                hostImage.setExtensionDistance(distance);
            }
        }
        // </HACK>
    }



    public void hideAllPorts() {
        // TODO: getEntities().filterType2(Port.class).getShapes().setVisibility(Visibility.INVISIBLE);

        Group<Image> portableImages = getEntities().filterType2(Host.class, Extension.class).getImages();
//        ImageGroup portableImages = getImages(Host.class, Extension.class);
        for (int i = 0; i < portableImages.size(); i++) {
            PortableImage portableImage = (PortableImage) portableImages.get(i);
            portableImage.getPortShapes().setVisibility(Visibility.INVISIBLE);
            portableImage.setPathVisibility(Visibility.INVISIBLE);
//            portableImage.setDockVisibility(Visibility.VISIBLE);
            portableImage.setTransparency(1.0);
        }
    }



    // <TITLE>
    // TODO: Allow user to setAbsolute and change a goal. Track it in relation to the actions taken and things built.
    protected Visibility titleVisibility = Visibility.INVISIBLE;
    protected String titleText = "Project";

    public void setTitleText(String text) {
        this.titleText = text;
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setTitleVisibility(Visibility visibility) {
        if (titleVisibility == Visibility.INVISIBLE && visibility == Visibility.VISIBLE) {
//            Application.getView().openTitleEditor(getTitleText());
            this.titleVisibility = visibility;
        } else if (titleVisibility == Visibility.VISIBLE && visibility == Visibility.VISIBLE) {
//            Application.getView().setTitleEditor(getTitleText());
        } else if (titleVisibility == Visibility.VISIBLE && visibility == Visibility.INVISIBLE) {
//            Application.getView().closeTitleEditor();
            this.titleVisibility = visibility;
        }
    }

    public Visibility getTitleVisibility() {
        return this.titleVisibility;
    }
    // </TITLE>
}
