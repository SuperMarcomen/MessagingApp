package it.marcodemartino.common.entities;

import java.security.PublicKey;
import java.util.UUID;

public class User {

    private final String name;
    private final String email;
    private final UUID uuid;
    private final PublicKey publicKey;

    public User(String name, String email, UUID uuid, PublicKey publicKey) {
        this.name = name;
        this.email = email;
        this.uuid = uuid;
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}