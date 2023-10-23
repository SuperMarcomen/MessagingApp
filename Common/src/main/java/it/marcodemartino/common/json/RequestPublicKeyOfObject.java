package it.marcodemartino.common.json;

public class RequestPublicKeyOfObject implements JSONObject {

    private final JSONMethods method = JSONMethods.REQUEST_PUBLIC_KEY_OF;
    private final String requestOf;

    public RequestPublicKeyOfObject(String requestOf) {
        this.requestOf = requestOf;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getRequestOf() {
        return requestOf;
    }
}
