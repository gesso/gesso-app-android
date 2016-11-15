package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.ImageBuilder.Shape;

public class Image extends Component {

    // <TODO>
    // TODO: Delete this and create separate decoupled code to populate ImageComponent from Image file.
//    public ImageBuilder image = null;
    // </TODO>

    //public Group<ShapeComponent> shapes;
    //public Group<Long> shapes;
    public List<Long> shapes;

    public Image() {
        super();
        setup();
    }

    private void setup() {
//        shapes = new Group<>();
        shapes = new ArrayList<>();
    }

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    public int layerIndex = DEFAULT_LAYER_INDEX;
    // </LAYER>

//    public void setImage(ImageBuilder imageBuilder) {
//        this.image = imageBuilder;
//    }
//
//    public ImageBuilder getImage() {
//        return this.image;
//    }

    public static long addShape(Entity entity, Shape shape) {

        // Create Shape entity and assign shape to it
        Entity shapeEntity = World.getWorld().createEntity(ShapeComponent.class);
        shapeEntity.getComponent(ShapeComponent.class).shape = shape;

        shapeEntity.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(entity);

        shapeEntity.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(shape.getPosition());
//        shapeEntity.getComponent(RelativeLayoutConstraint.class).relativeTransform.setRotation(shape.getRotation());

//        shapeEntity.getComponent(Transform.class).rotation = shape.getRotation();

        // Add Shape entity to Image component
        entity.getComponent(Image.class).shapes.add(shapeEntity.getUuid());

        return shapeEntity.getUuid();
    }

    public static Entity getShape(Entity entity, String label) {
        List<Long> shapeUuids = entity.getComponent(Image.class).shapes;
        for (int i = 0; i < shapeUuids.size(); i++) {
            Entity shape = World.getWorld().Manager.get(shapeUuids.get(i));
            if (Label.getLabel(shape).equals(label)) {
                return shape;
            }
        }
        return null;
    }

    public static Entity getShape(Entity entity, Shape shape) {
        List<Long> shapeUuids = entity.getComponent(Image.class).shapes;
        for (int i = 0; i < shapeUuids.size(); i++) {
            Entity shapeEntity = World.getWorld().Manager.get(shapeUuids.get(i));
            if (shapeEntity.getComponent(ShapeComponent.class).shape == shape) {
                return shapeEntity;
            }
        }
        return null;
    }

    public static Group<Entity> getShapes(Entity entity) {
        List<Long> shapeUuids = entity.getComponent(Image.class).shapes;
        Group<Entity> shapes = new Group<>();
        for (int i = 0; i < shapeUuids.size(); i++) {
            Entity shape = World.getWorld().Manager.get(shapeUuids.get(i));
            shapes.add(shape);
        }
        return shapes;
    }
}
