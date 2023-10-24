package it.marcodemartino.server.services;

import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.SignedEncryptedCertifiedObject;
import it.marcodemartino.server.dao.IMessagesDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

public class MessagingService {

    private final Logger logger = LogManager.getLogger(MessagingService.class);
    private final Map<String, OutputEmitter> onlineClients;
    private final IMessagesDao messagesDao;

    public MessagingService(IMessagesDao messagesDao) {
        this.messagesDao = messagesDao;
        onlineClients = new HashMap<>();
    }

    public void addClient(String email, OutputEmitter out) {
        onlineClients.put(email, out);
        List<SignedEncryptedCertifiedObject> allMessages = messagesDao.getByEmail(email);
        if (allMessages == null || allMessages.isEmpty()) return;
        for (SignedEncryptedCertifiedObject message : allMessages) {
            logger.info("Sending a message to {} that he received while he was offline", message.getSendTo());
            out.sendOutput(message);
        }
        logger.info("Deleting the stored messages of {}", email);
        messagesDao.deleteAll(email);
    }

    public void storeMessage(SignedEncryptedCertifiedObject object) {
        messagesDao.insert(object);
    }

    public void disconnectClient(OutputEmitter out) {
        for (Entry<String, OutputEmitter> entry : onlineClients.entrySet()) {
            if (entry.getValue() == out) {
                onlineClients.remove(entry.getKey());
                return;
            }
        }
    }

    public OutputEmitter getClient(String email) {
        return onlineClients.get(email);
    }
}
