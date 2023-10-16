package it.marcodemartino.common.json;

public enum JSONMethods {

    REGISTER_EMAIL(RegisterEmailObject.class),
    REGISTRATION_RESULT(RegistrationResultObject.class);

    private final Class<? extends JSONObject> jsonObject;

    JSONMethods(Class<? extends JSONObject> jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Class<?> getJsonObject() {
        return jsonObject;
    }
}
