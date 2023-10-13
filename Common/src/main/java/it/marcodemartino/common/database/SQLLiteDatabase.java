package it.marcodemartino.common.database;

import java.sql.*;

public class SQLLiteDatabase implements Database {

    @Override
    public void createDatabase() {
        getConnection();
    }

    @Override
    public void getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PreparedStatement createPreparedStatement(String sql) {
        return null;
    }

    @Override
    public void closeConnection() {

    }
}
