package it.marcodemartino.server.services;

import it.marcodemartino.common.io.emitters.OutputEmitter;

import java.util.*;

public class MessagingService {

    private final Map<String, OutputEmitter> onlineClients;

    public MessagingService() {
        onlineClients = new HashMap<>();
    }

    public void addClient(String email, OutputEmitter out) {
        onlineClients.put(email, out);
    }

    public OutputEmitter getClient(String email) {
        return onlineClients.get(email);
    }
}
