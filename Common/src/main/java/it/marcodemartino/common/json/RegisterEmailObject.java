package it.marcodemartino.common.json;

public class RegisterEmailObject implements JSONObject {

    private final JSONMethods method = JSONMethods.REGISTER_EMAIL;
    private final String email;

    public RegisterEmailObject(String email) {
        this.email = email;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getEmail() {
        return email;
    }
}
