package it.marcodemartino.client;

import it.marcodemartino.client.certificates.CertificateFileReaderWriter;
import it.marcodemartino.client.certificates.CertificateReaderWriter;
import it.marcodemartino.client.commands.jsoncommands.*;
import it.marcodemartino.client.commands.usercommands.*;
import it.marcodemartino.client.errrors.ConsoleErrorManager;
import it.marcodemartino.client.inputs.ConsoleInputEmitter;
import it.marcodemartino.client.services.CertificateService;
import it.marcodemartino.client.services.RequestsKeysService;
import it.marcodemartino.client.socket.SSLSocketClient;
import it.marcodemartino.common.application.Application;
import it.marcodemartino.common.certificates.IdentityCertificate;
import it.marcodemartino.common.commands.JsonCommandManager;
import it.marcodemartino.common.commands.UserCommandManager;
import it.marcodemartino.common.encryption.AsymmetricEncryption;
import it.marcodemartino.common.encryption.RSAEncryption;
import it.marcodemartino.common.json.*;
import it.marcodemartino.common.services.*;
import it.marcodemartino.common.io.emitters.InputEmitter;

import java.nio.file.Paths;

public class MessagingApp {

    public static void main(String[] args) throws Exception {
        Application application = new SSLSocketClient("127.0.0.1", 8443);
        Thread thread = new Thread(application);

        AsymmetricEncryption localEncryption = new RSAEncryption(2048);
        AsymmetricEncryption otherEncryption = new RSAEncryption(2048);

        KeysService keysService = new RequestsKeysService(application.getIO(), localEncryption);

        EncryptionService encryptionService = new EncryptionService(localEncryption, otherEncryption, keysService);
        encryptionService.loadOrGenerateKeys();

        CertificateReaderWriter certificateReaderWriter = new CertificateFileReaderWriter(Paths.get(""));
        CertificateService certificateService = new CertificateService(certificateReaderWriter);

        InputEmitter inputEmitter = new ConsoleInputEmitter();
        UserCommandManager commandManager = new UserCommandManager(new ConsoleErrorManager());
        commandManager.registerUserCommand("register", new RegisterEmail(application.getIO()));
        commandManager.registerUserCommand("verify", new VerifyCommand(application.getIO(), encryptionService, localEncryption));
        commandManager.registerUserCommand("send", new SendMessageCommand(application.getIO(), encryptionService, certificateService));


        JsonCommandManager jsonCommandManager = new JsonCommandManager();
        jsonCommandManager.registerCommand(JSONMethods.SEND_PUBLIC_KEY, new SendPublicKeyCommand(localEncryption, encryptionService));
        jsonCommandManager.registerCommand(JSONMethods.REGISTRATION_RESULT, new RegistrationResultCommand());
        jsonCommandManager.registerCommand(JSONMethods.ENCRYPTED_SIGNED_MESSAGE, new SignedEncryptedMessageCommand(application.getIO().getEventManager(), localEncryption, encryptionService));
        jsonCommandManager.registerCommand(JSONMethods.IDENTITY_CERTIFICATE, new SendIdentityCertificateCommand(certificateService, encryptionService));
        jsonCommandManager.registerCommand(JSONMethods.SEND_PUBLIC_KEY_OF, new ReceivePublicKeyOfCommand(encryptionService.getKeysService(), encryptionService));
        jsonCommandManager.registerCommand(JSONMethods.ENCRYPTED_SIGNED_CERTIFIED_MESSAGE, new SignedEncryptedCertifiedCommand(encryptionService.getLocalAsymmetricEncryption(), application.getIO().getEventManager(), encryptionService));
        jsonCommandManager.registerCommand(JSONMethods.CERTIFIED_MESSAGE, new CertifiedMessageCommand());


        inputEmitter.registerInputListener(commandManager);
        application.getIO().registerInputListener(jsonCommandManager);

        thread.start();
        application.getIO().sendOutput(new RequestPublicKeyObject());
        if (certificateService.doesCertificateExist()) {
            IdentityCertificate identityCertificate = certificateService.getIdentityCertificate();
            application.getIO().sendOutput(new SendIdentityCertificateObject(identityCertificate, encryptionService.signIdentityCertificate(identityCertificate)));
        }
        inputEmitter.start();

    }
}
