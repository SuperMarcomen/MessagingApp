package it.marcodemartino.common.json;

public class RegisterEmailObject implements JSONObject {

    private final JSONMethods method = JSONMethods.REGISTER_EMAIL;
    private final String username;
    private final String email;

    public RegisterEmailObject(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
