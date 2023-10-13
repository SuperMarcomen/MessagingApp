package it.marcodemartino.common.database;

import java.sql.PreparedStatement;

public interface Database {

    void createDatabase();
    void getConnection();
    PreparedStatement createPreparedStatement(String sql);
    void closeConnection();

}
