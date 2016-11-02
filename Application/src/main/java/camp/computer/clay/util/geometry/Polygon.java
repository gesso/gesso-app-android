package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.component.Transform;

public class Polygon<T extends Entity> extends Shape { // <T> {

    protected List<Transform> vertices = new ArrayList<>();

    public Polygon() {
        setup();
    }

//    public Polygon(T entity) {
//        this.entity = entity;
//        setup();
//    }

    public Polygon(Transform position, List<Transform> vertices) {
        super(position);
        this.vertices.addAll(vertices);
        setup();
    }

    public Polygon(List<Transform> vertices) {
        super();
        this.vertices.addAll(vertices);
        setup();
    }

    private void setup() {
        setupGeometry();
    }

    private void setupGeometry() {

    }

    @Override
    public List<Transform> getVertices() {
        return vertices;
    }

}
