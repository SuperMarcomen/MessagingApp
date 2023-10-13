package it.marcodemartino.common.application;

public interface Application extends Runnable {

    ApplicationIO getIO();
    void stop();

}
