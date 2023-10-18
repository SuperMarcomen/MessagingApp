package it.marcodemartino.server.services;

import com.google.gson.Gson;
import it.marcodemartino.common.certificates.IdentityCertificate;
import it.marcodemartino.common.encryption.AsymmetricEncryption;
import it.marcodemartino.common.entities.User;
import it.marcodemartino.common.json.GsonInstance;

public class CertificatesService {

    private final AsymmetricEncryption asymmetricEncryption;
    private final Gson gson;

    public CertificatesService(AsymmetricEncryption asymmetricEncryption) {
        this.asymmetricEncryption = asymmetricEncryption;
        gson = GsonInstance.get();
    }

    public IdentityCertificate generateCertificate(User user) {
        String userJson = gson.toJson(user);
        byte[][] signedJson = asymmetricEncryption.signFromString(userJson);
        return new IdentityCertificate(user, signedJson);
    }
}
