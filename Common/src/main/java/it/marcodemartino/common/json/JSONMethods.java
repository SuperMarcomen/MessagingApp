package it.marcodemartino.common.json;

public enum JSONMethods {

    REGISTER_EMAIL(RegisterEmailObject.class),
    REGISTRATION_RESULT(RegistrationResultObject.class),
    REQUEST_PUBLIC_KEY(RequestPublicKeyObject.class),
    SEND_PUBLIC_KEY(SendPublicKeyObject.class);

    private final Class<? extends JSONObject> jsonObject;

    JSONMethods(Class<? extends JSONObject> jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Class<?> getJsonObject() {
        return jsonObject;
    }
}
