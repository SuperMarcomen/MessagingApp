package it.marcodemartino.server.dao;

import it.marcodemartino.common.dao.Dao;
import it.marcodemartino.common.json.SignedEncryptedCertifiedObject;

import java.util.List;

public interface IMessagesDao extends Dao<SignedEncryptedCertifiedObject> {

    List<SignedEncryptedCertifiedObject> getByEmail(String email);
    void deleteAll(String email);

}
