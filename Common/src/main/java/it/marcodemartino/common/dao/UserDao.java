package it.marcodemartino.common.dao;

import it.marcodemartino.common.database.Database;
import it.marcodemartino.common.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

public class UserDao implements IUserDao {

    private final Logger logger = LogManager.getLogger(UserDao.class);
    private final Database database;
    private final Map<UUID, User> userMap;
    private final Map<String, UUID> mailUUIDMap;

    public UserDao(Database database) {
        this.database = database;
        userMap = new HashMap<>();
        mailUUIDMap = new HashMap<>();
        for (User user : getAll()) {
            userMap.put(user.getUuid(), user);
            mailUUIDMap.put(user.getEmail(), user.getUuid());
        }
    }

    @Override
    public User getByUUID(UUID uuid) {
        return userMap.get(uuid);
    }

    @Override
    public User getByEmail(String email) {
        return getByUUID(mailUUIDMap.get(email));
    }

    @Override
    public boolean isEmailUsed(String email) {
        for (User user : userMap.values()) {
            if (user.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }


    @Override
    public List<User> getAll() {
        if (!userMap.isEmpty()) return new ArrayList<>(userMap.values());

        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (PreparedStatement preparedStatement = database.createPreparedStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                User user = reconstructUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            logger.error("There was an error while fetching the users from the database", e);
        }
        return users;
    }

    private User reconstructUser(ResultSet rs) throws SQLException {
        String uuidString = rs.getString("user_id");
        UUID uuid = UUID.fromString(uuidString);
        String username = rs.getString("username");
        String email = rs.getString("email");
        String publicKeyString = rs.getString("rsa_public_key");
        return new User(username, email, uuid, publicKeyString);
    }

    @Override
    public void insert(User user) {
        userMap.put(user.getUuid(), user);
        mailUUIDMap.put(user.getEmail(), user.getUuid());
        String sql = "INSERT INTO users (user_id, username, email, rsa_public_key) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = database.createPreparedStatement(sql)) {
            preparedStatement.setString(1, user.getUuid().toString());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPublicKey());
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("There was an error inserting the user {} in the database", user.getUuid().toString(), e);
        }
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(User user) {

    }
}
