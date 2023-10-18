package it.marcodemartino.common.application;

import it.marcodemartino.common.io.EventManager;
import it.marcodemartino.common.io.emitters.InputEmitter;
import it.marcodemartino.common.io.emitters.OutputEmitter;

public interface ApplicationIO extends InputEmitter, OutputEmitter {

    EventManager getEventManager();

}
