package de.itsjxsper.server.database;

import de.itsjxsper.server.Main;
import de.itsjxsper.server.utlis.ConfigUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.*;

public class DatabaseManager {

    @Getter
    private Connection connection;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String URL = "jdbc:sqlite:sqlite.db";
            this.connection = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            Main.getInstance().getLogger().severe("Could not connect to database :" + e.getMessage());
        }
    }

    @SneakyThrows
    public void createTable() {
        if (isConnected()) {
            Statement statement = connection.createStatement();

            // Create a player table with UUID as a primary key
            statement.execute("CREATE TABLE IF NOT EXISTS player (" +
                    "UUID VARCHAR(36) PRIMARY KEY," +
                    "name VARCHAR(16)," +
                    "last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS backCoordinates (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "UUID VARCHAR(36)," +
                    "coordinates TEXT," +
                    "FOREIGN KEY (UUID) REFERENCES player(UUID)" +
                    ")");

            // Create a Back-Coordinates table with UUID as a foreign key

            Main.getInstance().getLogger().info("Database tables created successfully");
        }
    }

    @SneakyThrows
    public void disconnect() {
        if (isConnected()) {
            connection.close();
            Main.getInstance().getLogger().info("Disconnected from database");
        }
    }

    public boolean isConnected() {
        if (connection == null) {
            ConfigUtil.getString("message.database.not-connected");
            connect();
        }
        return connection != null;
    }

    /**
     * Adds a back coordinate entry for a player
     * @param playerUUID The UUID of the player
     * @param coordinates The coordinates as a string
     * @return true if successful, false otherwise
     */
    @SneakyThrows
    public boolean addBackCoordinate(UUID playerUUID, String coordinates) {
        if (!isConnected()) return false;

        String sql = "INSERT INTO backCoordinates (UUID, coordinates) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, coordinates);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Could not add back coordinate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a back coordinate entry for a player using a Bukkit Location
     * @param playerUUID The UUID of the player
     * @param location The Minecraft location
     * @return true if successful, false otherwise
     */
    @SneakyThrows
    public boolean addBackCoordinate(UUID playerUUID, org.bukkit.Location location) {
        String coordinates = BackCoordinateEntry.fromLocation(location);
        if (coordinates == null) {
            Main.getInstance().getLogger().severe("Invalid location provided");
            return false;
        }
        return addBackCoordinate(playerUUID, coordinates);
    }

    /**
     * Removes a back coordinate entry for a player
     * @param playerUUID The UUID of the player
     * @param id The ID of the coordinate entry to remove
     * @return true if successful, false otherwise
     */
    @SneakyThrows
    public boolean removeBackCoordinate(UUID playerUUID, int id) {
        if (!isConnected()) return false;

        String sql = "DELETE FROM backCoordinates WHERE UUID = ? AND id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Could not remove back coordinate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all back coordinate entries for a player
     * @param playerUUID The UUID of the player
     * @return A list of coordinate entries
     */
    @SneakyThrows
    public List<BackCoordinateEntry> getBackCoordinates(UUID playerUUID) {
        if (!isConnected()) return new ArrayList<>();

        List<BackCoordinateEntry> coordinates = new ArrayList<>();
        String sql = "SELECT id, coordinates FROM backCoordinates WHERE UUID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String coordinate = resultSet.getString("coordinates");
                coordinates.add(new BackCoordinateEntry(id, coordinate));
            }

            return coordinates;
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Could not retrieve back coordinates: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves coordinate entries for a player as Bukkit Locations all back
     * @param playerUUID The UUID of the player
     * @return A map of entry IDs to Location objects (null values for invalid locations are excluded)
     */
    @SneakyThrows
    public Map<Integer, org.bukkit.Location> getBackLocations(UUID playerUUID) {
        List<BackCoordinateEntry> entries = getBackCoordinates(playerUUID);
        Map<Integer, org.bukkit.Location> locations = new HashMap<>();

        for (BackCoordinateEntry entry : entries) {
            org.bukkit.Location location = entry.toLocation();
            if (location != null) {
                locations.put(entry.id(), location);
            }
        }

        return locations;
    }

    /**
     * Retrieves a specific back coordinate entry by its ID
     * @param playerUUID The UUID of the player
     * @param id The ID of the coordinate entry
     * @return The back coordinate entry, or null if not found
     */
    @SneakyThrows
    public BackCoordinateEntry getBackCoordinate(UUID playerUUID, int id) {
        if (!isConnected()) return null;

        String sql = "SELECT coordinates FROM backCoordinates WHERE UUID = ? AND id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setInt(2, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String coordinate = resultSet.getString("coordinates");
                return new BackCoordinateEntry(id, coordinate);
            }

            return null;
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Could not retrieve back coordinate: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a specific back coordinate entry by its ID as a Bukkit Location
     * @param playerUUID The UUID of the player
     * @param id The ID of the coordinate entry
     * @return The Location object, or null if not found or if the format is invalid
     */
    @SneakyThrows
    public org.bukkit.Location getBackLocation(UUID playerUUID, int id) {
        BackCoordinateEntry entry = getBackCoordinate(playerUUID, id);
        if (entry == null) {
            return null;
        }
        return entry.toLocation();
    }

    /**
     * Checks if a player has any back coordinates
     * @param playerUUID The UUID of the player
     * @return true if the player has at least one back coordinate, false otherwise
     */
    @SneakyThrows
    public boolean hasBackCoordinates(UUID playerUUID) {
        if (!isConnected()) return false;

        String sql = "SELECT COUNT(*) FROM backCoordinates WHERE UUID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

            return false;
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Could not check if player has back coordinates: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing back coordinate entry
     * @param playerUUID The UUID of the player
     * @param id The ID of the coordinate entry to update
     * @param newCoordinates The new coordinates
     * @return true if successful, false otherwise
     */
    @SneakyThrows
    public boolean updateBackCoordinate(UUID playerUUID, int id, String newCoordinates) {
        if (!isConnected()) return false;

        String sql = "UPDATE backCoordinates SET coordinates = ? WHERE UUID = ? AND id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newCoordinates);
            statement.setString(2, playerUUID.toString());
            statement.setInt(3, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Could not update back coordinate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing back coordinate entry using a Bukkit Location
     * @param playerUUID The UUID of the player
     * @param id The ID of the coordinate entry to update
     * @param newLocation The new Minecraft location
     * @return true if successful, false otherwise
     */
    @SneakyThrows
    public boolean updateBackCoordinate(UUID playerUUID, int id, org.bukkit.Location newLocation) {
        String coordinates = BackCoordinateEntry.fromLocation(newLocation);
        if (coordinates == null) {
            Main.getInstance().getLogger().severe("Invalid location provided");
            return false;
        }
        return updateBackCoordinate(playerUUID, id, coordinates);
    }

    /**
     * Removes all back coordinate entries for a player
     * @param playerUUID The UUID of the player
     * @return The number of entries removed
     */
    @SneakyThrows
    public int removeAllBackCoordinates(UUID playerUUID) {
        if (!isConnected()) return 0;

        String sql = "DELETE FROM backCoordinates WHERE UUID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            return statement.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Could not remove all back coordinates: " + e.getMessage());
            return 0;
        }
    }
}
