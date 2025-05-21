package de.itsjxsper.server.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record BackCoordinateEntry(String coordinates) {

    /**
     * Converts this entry's coordinate string to a Bukkit Location
     * Format: "world,x,y,z" or "world,x,y,z,yaw,pitch"
     * @return The Location object, or null if the format is invalid
     */
    public Location toLocation() {
        try {
            String[] parts = coordinates.split(",");
            if (parts.length < 4) {
                return null;
            }

            World world = Bukkit.getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            if (parts.length >= 6) {
                float yaw = Float.parseFloat(parts[4]);
                float pitch = Float.parseFloat(parts[5]);
                return new Location(world, x, y, z, yaw, pitch);
            }

            return new Location(world, x, y, z);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a string representation of a Location object for storage
     * @param location The Location to convert
     * @return A string in the format "world,x,y,z,yaw,pitch"
     */
    public static String fromLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        return String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }
}
