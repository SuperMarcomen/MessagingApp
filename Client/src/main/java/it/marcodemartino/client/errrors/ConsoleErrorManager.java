package it.marcodemartino.client.errrors;

import it.marcodemartino.common.errors.ErrorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConsoleErrorManager implements ErrorManager {

    private final Logger logger = LogManager.getLogger(ConsoleErrorManager.class);

    @Override
    public void commandNotRecognized(String command) {
        logger.warn("The command {} does not exist", command);
    }

    @Override
    public void wrongCommandFormat() {
        logger.warn("Commands have to start with a /");
    }

    @Override
    public void wrongCommandInputFormat(String correctFormat) {
        logger.warn("That's wrong! This is the correct format: {}", correctFormat);
    }
}
