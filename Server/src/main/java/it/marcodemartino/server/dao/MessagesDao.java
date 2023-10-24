package it.marcodemartino.server.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.marcodemartino.common.database.Database;
import it.marcodemartino.common.json.GsonInstance;
import it.marcodemartino.common.json.SignedEncryptedCertifiedObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

public class MessagesDao implements IMessagesDao {

    private final Logger logger = LogManager.getLogger(MessagesDao.class);
    private final Database database;
    private final Map<String, List<SignedEncryptedCertifiedObject>> messages;
    private final Gson gson;

    public MessagesDao(Database database) {
        this.database = database;
        messages = new HashMap<>();
        gson = GsonInstance.get();
    }

    @Override
    public List<SignedEncryptedCertifiedObject> getByEmail(String email) {
        return messages.get(email);
    }

    @Override
    public List<SignedEncryptedCertifiedObject> getAll() {
        String sql = "SELECT * FROM messages;";
        List<SignedEncryptedCertifiedObject> allMessages = new ArrayList<>();

        try (PreparedStatement preparedStatement = database.createPreparedStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            Type listType = new TypeToken<List<SignedEncryptedCertifiedObject>>() {}.getType();
            while (rs.next()) {
                String email = rs.getString("send_to_email");
                String json = rs.getString("message_json");

                List<SignedEncryptedCertifiedObject> messagesList = gson.fromJson(json, listType);
                allMessages.addAll(messagesList);
                messages.put(email, messagesList);
            }
        } catch (SQLException e) {
            logger.error("There was an error while fetching the messages from the database", e);
        }
        return allMessages;
    }

    @Override
    public void deleteAll(String email) {
        messages.remove(email);
        String sql = "DELETE FROM messages WHERE send_to_email = ?;";
        try (PreparedStatement preparedStatement = database.createPreparedStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("There was an error while deleting all the messages of {} from the database", email, e);
        }
    }

    @Override
    public void insert(SignedEncryptedCertifiedObject entity) {
        String sendToEmail = entity.getSendTo();
        if (messages.containsKey(sendToEmail)) {
            messages.get(sendToEmail).add(entity);
        } else {
            messages.put(sendToEmail, List.of(entity));
        }
        String sql = "INSERT OR REPLACE INTO messages (send_to_email, message_json) VALUES (?, ?);";


        try (PreparedStatement preparedStatement = database.createPreparedStatement(sql)) {
            preparedStatement.setString(1, sendToEmail);
            preparedStatement.setString(2, gson.toJson(List.of(entity)));
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("There was an error inserting or updating the message of {} in the database", sendToEmail, e);
        }
    }

    @Override
    public void update(SignedEncryptedCertifiedObject entity) {

    }

    @Override
    public void delete(SignedEncryptedCertifiedObject entity) {

    }
}
