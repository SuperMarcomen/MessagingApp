package it.marcodemartino.common.json;

public class RequestPublicKeyObject implements JSONObject {

    private final JSONMethods method = JSONMethods.REQUEST_PUBLIC_KEY;
    @Override
    public JSONMethods getMethod() {
        return method;
    }
}
