package it.marcodemartino.client.commands;

import it.marcodemartino.common.encryption.EncryptionService;
import it.marcodemartino.common.commands.UserCommand;
import it.marcodemartino.common.encryption.AsymmetricKeyConstructor;
import it.marcodemartino.common.io.emitters.OutputEmitter;
import it.marcodemartino.common.json.EmailVerificationObject;
import it.marcodemartino.common.json.JSONObject;

public class VerifyCommand implements UserCommand {

    private static final String COMMAND_REGEX = "/verify\\s+[\\w.-]+@[\\w.-]+\\.\\w+\\s+\\d{6}\\s+\\w+";
    private final OutputEmitter outputEmitter;
    private final EncryptionService encryptionService;
    private final AsymmetricKeyConstructor keyConstructor;

    public VerifyCommand(OutputEmitter outputEmitter, EncryptionService encryptionService, AsymmetricKeyConstructor keyConstructor) {
        this.outputEmitter = outputEmitter;
        this.encryptionService = encryptionService;
        this.keyConstructor = keyConstructor;
    }

    @Override
    public void execute(String input) {
        String[] args = input.split(" ");
        String email = args[0];
        int verificationCode = Integer.parseInt(args[1]);
        String username = args[2];
        String publicKey = keyConstructor.publicKeyToString(encryptionService.getLocalAsymmetricEncryption().getPublicKey());
        JSONObject verificationObject = new EmailVerificationObject(email, verificationCode, username, publicKey);
        outputEmitter.sendOutput(encryptionService.encryptMessage(verificationObject));
    }

    @Override
    public boolean isFormatCorrect(String input) {
        return input.matches(COMMAND_REGEX);
    }

    @Override
    public String getCorrectFormat() {
        return "/verify <email> <verification code> <username>";
    }
}
