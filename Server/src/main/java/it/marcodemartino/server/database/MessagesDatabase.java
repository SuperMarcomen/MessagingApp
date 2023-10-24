package it.marcodemartino.server.database;

import it.marcodemartino.common.database.SQLLiteDatabase;

public class MessagesDatabase extends SQLLiteDatabase {

    public MessagesDatabase() {
        super("messages");
    }

    @Override
    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS `messages` (
                	`send_to_email` VARCHAR(255) NOT NULL,
                	`message_json` TEXT NOT NULL,
                	PRIMARY KEY (`send_to_email`)
                );""";
        super.createTable(sql);
    }
}
