package iot.technology.mqtt.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

@Slf4j
public class SslUtil {

    private SslUtil() {

    }

    public static String getCertificateString(Certificate cert)
            throws CertificateEncodingException {
        return EncryptionUtil.certTrimNewLines(Base64Utils.encodeToString(cert.getEncoded()));

    }
}
