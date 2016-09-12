package camp.computer.clay.model.architecture;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Space} is a simulation of the environmental context.
 */
public class Space extends Construct {

    private System system = new System();

    private List<Actor> actors = new ArrayList<>();

    private List<Base> bases = new ArrayList<>();

    private List<Patch> patches = new ArrayList<>();

    public void addActor(Actor actor) {
        this.actors.add(actor);
    }

    public Actor getActor(int index) {
        return this.actors.get(index);
    }

    public List<Actor> getActors() {
        return this.actors;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public System getSystem() {
        return this.system;
    }

    public void addBase(Base base) {
        this.bases.add(base);
        base.setParent(this);
    }

    public Base getBase(int index) {
        return this.bases.get(index);
    }

    public List<Base> getBases() {
        return this.bases;
    }

    public List<Port> getPorts() {
        List<Port> ports = new ArrayList<>();
        for (int i = 0; i < this.bases.size(); i++) {
            Base base = this.bases.get(i);
            ports.addAll(base.getPorts());
        }
        return ports;
    }

    public List<Path> getPaths() {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < this.bases.size(); i++) {
            Base base = this.bases.get(i);
            for (int j = 0; j < base.getPorts().size(); j++) {
                Port port = base.getPorts().get(j);
                paths.addAll(port.getPaths());
            }
        }
        return paths;
    }

    public void addPatch(Patch patch) {
        this.patches.add(patch);
        patch.setParent(this);
    }

    public Patch getPatch(int index) {
        return this.patches.get(index);
    }

    public List<Patch> getPatches() {
        return this.patches;
    }
}