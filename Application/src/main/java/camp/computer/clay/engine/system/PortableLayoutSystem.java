package camp.computer.clay.engine.system;

import java.util.List;

import camp.computer.clay.Clay;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.World;

public class PortableLayoutSystem extends System {

    // TODO: Make static methods non-static and call them in the update function or from other systems.

    @Override
    public boolean update(World world) {
        return false;
    }

    /**
     * Creates a new {@code ExtensionEntity} connected to {@hostPort}.
     *
     * @param hostPort
     */
    public static Entity createExtension(Entity hostPort, Transform initialPosition) {

        // TODO: Remove initialPosition... find the position by analyzing the geometry of the HostImage

        //Log.v("ExtensionEntity", "Creating ExtensionEntity from PortEntity");

        //Log.v("IASM", "(1) touch extensionEntity to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // TODO: Prompt to select extensionEntity to use! Then use that profile to create and configure ports for the extensionEntity.

        // Create Extension Entity
        Entity extension = World.createEntity(Extension.class); // HACK: Because Extension is a Component

        // Set the initial position of the Extension
        extension.getComponent(Transform.class).set(initialPosition);

        // Configure Host's Port (i.e., the Path's source Port)
        if (hostPort.getComponent(Port.class).getType() == Port.Type.NONE || hostPort.getComponent(Port.class).getDirection() == Port.Direction.NONE) {
            hostPort.getComponent(Port.class).setType(Port.Type.POWER_REFERENCE); // Set the default type to reference (ground)
            hostPort.getComponent(Port.class).setDirection(Port.Direction.BOTH);
        }

        // Configure Extension's Ports (i.e., the Path's target Port)
        Entity extensionPort = extension.getComponent(Portable.class).getPorts().get(0);
        extensionPort.getComponent(Port.class).setDirection(Port.Direction.INPUT);
        extensionPort.getComponent(Port.class).setType(hostPort.getComponent(Port.class).getType());

        // Create Path from Host to Extension and configure the new Path
        Entity path = World.createEntity(Path.class);
        path.getComponent(Path.class).set(hostPort, extensionPort);

        // Remove focus from other Hosts and their Ports
        Group<Entity> hosts = Entity.Manager.filterWithComponent(Host.class);
        for (int i = 0; i < hosts.size(); i++) {
            Entity host = hosts.get(i);
            host.getComponent(Image.class).setTransparency(0.05f);
            host.getComponent(Portable.class).getPorts().setVisibility(false);
            host.getComponent(Portable.class).getPaths().setVisibility(false);
        }

        // Get all Ports in all Paths from the Host
        Group<Entity> hostPaths = hostPort.getComponent(Port.class).getPaths();
        Group<Entity> hostPorts = new Group<>();
        for (int i = 0; i < hostPaths.size(); i++) {
            Group<Entity> pathPorts = hostPaths.get(i).getComponent(Path.class).getPorts();
            hostPorts.addAll(pathPorts);
        }

        // Show all of Host's Paths and all Ports contained in those Paths
        hostPaths.setVisibility(true);
        hostPorts.setVisibility(true);

        // Update layout
        Entity host = hostPort.getParent(); // HACK
        updateExtensionLayout(host);

        return extension;
    }

    /**
     * Adds and existing {@code ExtensionEntity}.
     *
     * @param profile
     * @param initialPosition
     * @return
     */
    public static Entity restoreExtension(Entity host, Profile profile, Transform initialPosition) {
        // NOTE: Previously called fetchExtension(...)

        // Log.v("IASM", "(1) touch extensionEntity to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // Create the ExtensionEntity
        Entity extension = new Entity();

        // Add Extension Component (for type identification)
        extension.addComponent(new Extension());

        // <HACK>
        // TODO: Remove references to Profile in Portables. Remove Profile altogether!?
        Clay.configureFromProfile(extension, profile);
        // </HACK>

        // Update ExtensionEntity Position
        extension.getComponent(Transform.class).set(initialPosition);

        // Automatically select and connect all Paths to HostEntity
        autoConnectToHost(host, extension);

        // TODO: Start IASM based on automatically configured Paths to HostEntity.

        updateExtensionLayout(host);

        return extension;
    }

    // TODO: Make PortableLayoutSystem. Iterate through Hosts and lay out Extensions each PortableLayoutSystem.update().
    private static boolean autoConnectToHost(Entity host, Entity extensionEntity) {

        // Automatically select, connect paths to, and configure the HostEntity's Ports
        for (int i = 0; i < extensionEntity.getComponent(Portable.class).getPorts().size(); i++) {

            // Select an available HostEntity PortEntity
            Entity selectedHostPortEntity = autoSelectNearestAvailableHostPort(host, extensionEntity);

            // Configure HostEntity's PortEntity
            selectedHostPortEntity.getComponent(Port.class).setType(extensionEntity.getComponent(Portable.class).getPorts().get(i).getComponent(Port.class).getType());
            selectedHostPortEntity.getComponent(Port.class).setDirection(extensionEntity.getComponent(Portable.class).getPorts().get(i).getComponent(Port.class).getDirection());

            // Create PathEntity from ExtensionEntity PortEntity to HostEntity PortEntity
            Entity pathEntity = World.createEntity(Path.class);
            pathEntity.getComponent(Path.class).set(selectedHostPortEntity, extensionEntity.getComponent(Portable.class).getPorts().get(i));

            pathEntity.getComponent(Path.class).setType(Path.Type.ELECTRONIC);
        }

        return true;
    }

    private static Entity autoSelectNearestAvailableHostPort(Entity host, Entity extension) {

        // Select an available Port on the Host
        Entity selectedHostPort = null;
        double distanceToSelectedPort = Double.MAX_VALUE;
        for (int j = 0; j < host.getComponent(Portable.class).getPorts().size(); j++) {
            if (host.getComponent(Portable.class).getPorts().get(j).getComponent(Port.class).getType() == Port.Type.NONE) {

                Image hostImage = host.getComponent(Image.class);

//                Entity host = hostImage.getEntity();
                Portable hostPortable = host.getComponent(Portable.class);
                Entity portEntity = hostPortable.getPorts().get(j);

                double distanceToPort = Geometry.distance(
//                        hostPortable.getPortShapes().filterEntity(portEntity).get(0).getPosition(),
                        portEntity.getComponent(Transform.class),
                        extension.getComponent(Image.class).getEntity().getComponent(Transform.class)
                );

                // Check if the port is the nearest
                if (distanceToPort < distanceToSelectedPort) {
                    selectedHostPort = host.getComponent(Portable.class).getPorts().get(j);
                    distanceToSelectedPort = distanceToPort;
                }
            }
        }
        // TODO: selectedHostPortEntity = (PortEntity) getPortShapes().getNearestImage(extensionImage.getPosition()).getEntity();
        return selectedHostPort;
    }

    // TODO: Remove this?
    public static int getHeaderIndex(Entity host, Entity extension) {

        int[] indexCounts = new int[4];
        for (int i = 0; i < indexCounts.length; i++) {
            indexCounts[i] = 0;
        }

        Shape boardShape = host.getComponent(Image.class).getShape("Board");
        List<Transform> hostShapeBoundary = boardShape.getBoundary();

        Group<Entity> extensionPortEntities = extension.getComponent(Portable.class).getPorts();
        for (int j = 0; j < extensionPortEntities.size(); j++) {

            Entity extensionPortEntity = extensionPortEntities.get(j);

            if (extensionPortEntity == null || extensionPortEntity.getComponent(Port.class).getPaths().size() == 0 || extensionPortEntity.getComponent(Port.class).getPaths().get(0) == null) {
                continue;
            }

            Entity hostPortEntity = extensionPortEntity.getComponent(Port.class).getPaths().get(0).getComponent(Path.class).getHostPort(); // HACK b/c using index 0
            Transform hostPortPosition = hostPortEntity.getComponent(Image.class).getShape("Port").getPosition(); // World.getWorld().getShape(hostPortEntity).getPosition();

            double minimumSegmentDistance = Double.MAX_VALUE; // Stores the distance to the nearest segment
            int nearestSegmentIndex = 0; // Stores the index of the nearest segment (on the connected HostEntity)
            for (int i = 0; i < hostShapeBoundary.size() - 1; i++) {

                Transform segmentMidpoint = Geometry.midpoint(hostShapeBoundary.get(i), hostShapeBoundary.get(i + 1));

                double distance = Geometry.distance(hostPortPosition, segmentMidpoint);

                if (distance < minimumSegmentDistance) {
                    minimumSegmentDistance = distance;
                    nearestSegmentIndex = i;
                }
            }

            indexCounts[nearestSegmentIndex]++;
        }

        // Get the segment with the most counts
        int segmentIndex = -1;
        segmentIndex = 0;
        for (int i = 0; i < indexCounts.length; i++) {
            if (indexCounts[i] > indexCounts[segmentIndex]) {
                segmentIndex = i;
            }
        }

        return segmentIndex;
    }

    public static void setExtensionDistance(Entity host, double distance) {
        host.getComponent(Host.class).distanceToExtensions = distance;
        updateExtensionLayout(host);
    }

    public static void updateExtensionLayout(Entity host) {

        // Get Extensions connected to the Host
        Group<Entity> extensions = host.getComponent(Portable.class).getExtensions();

        Host hostComponent = host.getComponent(Host.class);

        // Reset current layout in preparation for updating it in the presently-running updateImage step.
        for (int i = 0; i < hostComponent.headerExtensions.size(); i++) {
            hostComponent.headerExtensions.get(i).clear();
        }

        // Assign the Extensions connected to this HostEntity to the most-strongly-connected Header.
        // This can be thought of as the "high level layout" of ExtensionEntity relative to the HostEntity.
        for (int i = 0; i < extensions.size(); i++) {
            updateExtensionHeaderIndex(host, extensions.get(i));
        }

        // Update each Extension's placement, relative to the connected Host.
        for (int headerIndex = 0; headerIndex < hostComponent.headerExtensions.size(); headerIndex++) {
            for (int extensionIndex = 0; extensionIndex < hostComponent.headerExtensions.get(headerIndex).size(); extensionIndex++) {

                Entity extension = hostComponent.headerExtensions.get(headerIndex).get(extensionIndex);

                final double extensionSeparationDistance = 25.0;
                double extensionWidth = 200;
                int extensionCount = hostComponent.headerExtensions.get(headerIndex).size();
                double offset = extensionIndex * 250 - (((extensionCount - 1) * (extensionWidth + extensionSeparationDistance)) / 2.0);

                // Update the Extension's position
                if (headerIndex == 0) {
                    extension.getComponent(Transform.class).set(
                            0 + offset,
                            -hostComponent.distanceToExtensions,
                            host.getComponent(Transform.class)
                    );
                } else if (headerIndex == 1) {
                    extension.getComponent(Transform.class).set(
                            hostComponent.distanceToExtensions,
                            0 + offset,
                            host.getComponent(Transform.class)
                    );
                } else if (headerIndex == 2) {
                    extension.getComponent(Transform.class).set(
                            0 + offset,
                            hostComponent.distanceToExtensions,
                            host.getComponent(Transform.class)
                    );
                } else if (headerIndex == 3) {
                    extension.getComponent(Transform.class).set(
                            -hostComponent.distanceToExtensions,
                            0 + offset,
                            host.getComponent(Transform.class)
                    );
                }

                // Update the Extension's rotation.
                double hostRotation = host.getComponent(Transform.class).getRotation();
                if (headerIndex == 0) {
                    extension.getComponent(Transform.class).setRotation(hostRotation + 0);
                } else if (headerIndex == 1) {
                    extension.getComponent(Transform.class).setRotation(hostRotation + 90);
                } else if (headerIndex == 2) {
                    extension.getComponent(Transform.class).setRotation(hostRotation + 180);
                } else if (headerIndex == 3) {
                    extension.getComponent(Transform.class).setRotation(hostRotation + 270);
                }

                // Invalidate Image Component so its geometry (i.e., shapes) will be updated.
                extension.getComponent(Image.class).invalidate();
            }
        }
    }

    public static void updateExtensionHeaderIndex(Entity host, Entity extension) {
        if (extension.getComponent(Image.class) == null || extension.getComponent(Portable.class).getHosts().size() == 0) {
            return;
        }
        int segmentIndex = getHeaderIndex(host, extension);
        host.getComponent(Host.class).headerExtensions.get(segmentIndex).add(extension);
    }
}