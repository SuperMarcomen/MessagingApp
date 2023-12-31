package it.marcodemartino.common.encryption;

import java.security.KeyPair;

public interface AsymmetricKeyReader {

    KeyPair readKeyPair(String publicName, String privateName);

}
