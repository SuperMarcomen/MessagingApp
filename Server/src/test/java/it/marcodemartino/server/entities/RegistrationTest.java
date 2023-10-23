package it.marcodemartino.server.entities;

import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrationTest {

    @RepeatedTest(value =  10_000)
    void generateCode() {
        int length = 6;
        int verificationCode = new Registration(length).getVerificationCode();
        int digits = String.valueOf(verificationCode).length();
        assertEquals(length, digits);
    }
}