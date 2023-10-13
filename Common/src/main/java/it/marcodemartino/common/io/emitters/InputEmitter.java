package it.marcodemartino.common.io.emitters;

import it.marcodemartino.common.Startable;
import it.marcodemartino.common.io.listeners.InputListener;

public interface InputEmitter extends Startable {

    void registerInputListener(InputListener inputListener);

}
