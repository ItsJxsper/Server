package de.itsjxsper.server.database;

import de.itsjxsper.server.Main;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Location;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {

    public final Main main;

    @Getter
    private Connection connection;;

    public DatabaseManager(Main main) {
        this.main = main;
        connect();
        createTable();
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String URL = "jdbc:sqlite:" + this.main.getDataFolder() + "/sqlite.db";
            this.connection = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            this.main.getLogger().severe("Could not connect to database :" + e.getMessage());
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
                    "UUID VARCHAR(36) PRIMARY KEY," +
                    "coordinates TEXT," +
                    "FOREIGN KEY (UUID) REFERENCES player(UUID)" +
                    ")");

            // Create a Back-Coordinates table with UUID as a foreign key

            this.main.getLogger().info("Database tables created successfully");
        }
    }

    @SneakyThrows
    public void disconnect() {
        if (isConnected()) {
            connection.close();
           this.main.getLogger().info("Disconnected from database");
        }
    }

    public boolean isConnected() {
        if (connection == null) {
            connect();
        }
        return connection != null;
    }

    @SneakyThrows
    public boolean addPlayer(UUID playerUUID, String playerName) {
        if (!isConnected()) return false;

        String sql = "REPLACE INTO player (UUID, name) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, playerName);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            this.main.getLogger().severe("Could not add player: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds or replaces a back coordinate entry for a player
     * @param playerUUID The UUID of the player
     * @param coordinates The coordinates as a string
     * @return true if successful, false otherwise
     */
    @SneakyThrows
    private boolean addBackCoordinate(UUID playerUUID, String coordinates) {
        if (!isConnected()) return false;

        String sql = "REPLACE INTO backCoordinates (UUID, coordinates) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, coordinates);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            this.main.getLogger().severe("Could not add back coordinate: " + e.getMessage());
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
    public boolean addBackCoordinate(UUID playerUUID, Location location) {
        String coordinates = BackCoordinateEntry.fromLocation(location);
        if (coordinates == null) {
            this.main.getLogger().severe("Invalid location provided");
            return false;
        }
        return addBackCoordinate(playerUUID, coordinates);
    }

    /**
     * Removes the back coordinate entry for a player
     * @param playerUUID The UUID of the player
     * @return true if successful, false otherwise
     */
    @SneakyThrows
    public boolean removeBackCoordinate(UUID playerUUID) {
        if (!isConnected()) return false;

        String sql = "DELETE FROM backCoordinates WHERE UUID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            this.main.getLogger().severe("Could not remove back coordinate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves the back coordinate entry for a player
     * @param playerUUID The UUID of the player
     * @return The back coordinate entry, or null if not found
     */
    @SneakyThrows
    public BackCoordinateEntry getBackCoordinate(UUID playerUUID) {
        if (!isConnected()) return null;

        String sql = "SELECT coordinates FROM backCoordinates WHERE UUID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String coordinate = resultSet.getString("coordinates");
                return new BackCoordinateEntry(coordinate);
            }

            return null;
        } catch (SQLException e) {
            this.main.getLogger().severe("Could not retrieve back coordinate: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the back coordinate entry for a player as a Bukkit Location
     * @param playerUUID The UUID of the player
     * @return The Location object, or null if not found or if the format is invalid
     */
    @SneakyThrows
    public org.bukkit.Location getBackLocation(UUID playerUUID) {
        BackCoordinateEntry entry = getBackCoordinate(playerUUID);
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
            this.main.getLogger().severe("Could not check if player has back coordinates: " + e.getMessage());
            return false;
        }
    }
}
