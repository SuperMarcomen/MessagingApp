package it.marcodemartino.client.commands.jsoncommands;

import it.marcodemartino.common.commands.JsonCommand;
import it.marcodemartino.common.json.CertifiedMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CertifiedMessageCommand extends JsonCommand<CertifiedMessage> {

    private final Logger logger = LogManager.getLogger(CertifiedMessage.class);

    public CertifiedMessageCommand() {
        super(CertifiedMessage.class);
    }

    @Override
    protected void execute(CertifiedMessage certifiedMessage) {
        logger.info("Received a new message:");
        logger.info("{} ({}): {}", certifiedMessage.getFromUser().getName(), certifiedMessage.getFromUser().getEmail(), certifiedMessage.getMessage());
    }
}
