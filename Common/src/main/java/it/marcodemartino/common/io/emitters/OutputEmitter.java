package it.marcodemartino.common.io.emitters;

import it.marcodemartino.common.Startable;
import it.marcodemartino.common.json.JSONObject;

public interface OutputEmitter extends Startable {

    void sendOutput(JSONObject object);

}
