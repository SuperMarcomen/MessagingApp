package it.marcodemartino.server.dao;

import it.marcodemartino.common.dao.Dao;
import it.marcodemartino.common.dao.UserDao;
import it.marcodemartino.common.database.Database;
import it.marcodemartino.common.database.UserDatabase;
import it.marcodemartino.common.encryption.AsymmetricEncryption;
import it.marcodemartino.common.encryption.RSAEncryption;
import it.marcodemartino.common.entities.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.security.PublicKey;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(OrderAnnotation.class)
class UserDaoTest {

    private static Dao<User> userDao;
    private static PublicKey publicKey;
    private static User testUser;
    @BeforeAll
    static void init() {
        Database database = new UserDatabase();
        database.initDatabase();
        AsymmetricEncryption asymmetricEncryption = new RSAEncryption(2048);
        asymmetricEncryption.generateKeyPair();
        userDao = new UserDao(database, asymmetricEncryption);
        publicKey = asymmetricEncryption.getPublicKey();
        testUser = new User("Marco", "marco@gmail.com", UUID.randomUUID(), publicKey);
    }

    @Test
    void getByUUID() {
        assertNotNull(userDao.getByUUID(testUser.getUuid()));
    }

    @Test
    @Order(1)
    void insert() {
        userDao.insert(testUser);
    }
}