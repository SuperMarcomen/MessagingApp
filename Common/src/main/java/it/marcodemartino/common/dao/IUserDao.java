package it.marcodemartino.common.dao;

import it.marcodemartino.common.entities.User;

import java.util.UUID;

public interface IUserDao extends Dao<User> {

    User getByUUID(UUID uuid);
    boolean isEmailUsed(String email);

}
