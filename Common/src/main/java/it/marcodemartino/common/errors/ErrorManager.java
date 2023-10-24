package it.marcodemartino.common.errors;

public interface ErrorManager {

    void commandNotRecognized(String command);
    void wrongCommandFormat();
    void wrongCommandInputFormat(String correctFormat);

}
