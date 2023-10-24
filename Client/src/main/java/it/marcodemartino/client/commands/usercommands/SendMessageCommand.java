package it.marcodemartino.client.commands.usercommands;

import it.marcodemartino.client.services.CertificateService;
import it.marcodemartino.common.commands.UserCommand;
import it.marcodemartino.common.services.EncryptionService;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.CertifiedMessage;
import it.marcodemartino.common.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class SendMessageCommand implements UserCommand {

    // Matches "/send <email> <message>" where <email> is a valid email
    private static final String COMMAND_REGEX = "/send\\s+[\\w.-]+@[\\w.-]+\\.\\w+\\s+.+";
    private final Logger logger = LogManager.getLogger(SendMessageCommand.class);
    private final OutputEmitter out;
    private final EncryptionService encryptionService;
    private final CertificateService certificateService;

    public SendMessageCommand(OutputEmitter out, EncryptionService encryptionService, CertificateService certificateService) {
        this.out = out;
        this.encryptionService = encryptionService;
        this.certificateService = certificateService;
    }

    @Override
    public void execute(String input) {
        if (certificateService.getIdentityCertificate() == null) {
            logger.warn("You first have to register to send a message!");
            return;
        }

        String[] args = input.split(" ");
        String email = args[0];
        String message = getMessage(args, 1);
        JSONObject jsonObject = new CertifiedMessage(message, certificateService.getIdentityCertificate().getUser());
        CompletableFuture<JSONObject> encryptedMessageFuture = new CompletableFuture<>();
        encryptedMessageFuture.thenAccept(encryptedObject -> {
            if (encryptedObject == null) {
                logger.warn("The user with the email {} was not found", email);
                return;
            }
            out.sendOutput(encryptedObject);
            logger.info("The message was sent");
        });
        encryptionService.encryptSignAndCertifyMessage(jsonObject, email, certificateService.getIdentityCertificate(), encryptedMessageFuture);
    }

    private String getMessage(String[] args, int startingIndex) {
        StringBuilder result = new StringBuilder();

        for (int i = startingIndex; i < args.length; i++) {
            result.append(args[i]);

            if (i < args.length - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    @Override
    public boolean isFormatCorrect(String input) {
        return input.matches(COMMAND_REGEX);
    }

    @Override
    public String getCorrectFormat() {
        return "/send <email> <message>";
    }
}