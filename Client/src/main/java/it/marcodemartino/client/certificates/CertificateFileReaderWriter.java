package it.marcodemartino.client.certificates;

import com.google.gson.Gson;
import it.marcodemartino.common.certificates.IdentityCertificate;
import it.marcodemartino.common.json.GsonInstance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;

public class CertificateFileReaderWriter implements CertificateReaderWriter {

    private final Gson gson;
    private static final String CERTIFICATE_FILE_NAME = "identity_certificate.pem";

    public CertificateFileReaderWriter() {
        gson = GsonInstance.get();
    }

    @Override
    public IdentityCertificate readCertificate(Path path) {
        String base64Json = tryReadCertificate(path);
        byte[] jsonBytes = Base64.getDecoder().decode(base64Json.getBytes(StandardCharsets.UTF_8));
        String json = new String(jsonBytes, StandardCharsets.UTF_8);
        return gson.fromJson(json, IdentityCertificate.class);
    }

    private String tryReadCertificate(Path path) {
        try {
             return Files.readString(Paths.get(path.toAbsolutePath().toString(), CERTIFICATE_FILE_NAME));
        } catch (IOException e) {
        }
        return "";
    }

    @Override
    public void writeCertificate(IdentityCertificate identityCertificate, Path path) {
        String json = gson.toJson(identityCertificate);
        String base64JSon = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        tryWriteCertificate(path, base64JSon);
    }

    private void tryWriteCertificate(Path path, String base64JSon) {
        try {
            Files.writeString(Paths.get(path.toAbsolutePath().toString(), CERTIFICATE_FILE_NAME), base64JSon);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
