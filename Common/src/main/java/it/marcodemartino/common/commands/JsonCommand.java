package it.marcodemartino.common.commands;

import com.google.gson.Gson;
import it.marcodemartino.common.json.GsonInstance;

public abstract class JsonCommand<T> implements Command {

    private final Gson gson;
    private final Class<T> typeClass;

    public JsonCommand(Class<T> typeClass) {
        this.gson = GsonInstance.get();
        this.typeClass = typeClass;
    }

    @Override
    public void execute(String input) {
        execute(gson.fromJson(input, typeClass));
    }

    protected abstract void execute(T t);
}
