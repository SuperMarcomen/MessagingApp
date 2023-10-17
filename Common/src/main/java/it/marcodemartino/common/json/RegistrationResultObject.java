package it.marcodemartino.common.json;

public class RegistrationResultObject implements JSONObject {

    private final JSONMethods method = JSONMethods.REGISTRATION_RESULT;
    private final RegistrationResult registrationResult;

    public RegistrationResultObject(RegistrationResult registrationResult) {
        this.registrationResult = registrationResult;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public RegistrationResult getRegistrationResult() {
        return registrationResult;
    }
}
