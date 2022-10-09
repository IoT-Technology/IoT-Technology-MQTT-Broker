package iot.technology.mqtt.server.ssl;

import io.netty.handler.ssl.SslHandler;
import iot.technology.mqtt.server.utils.EncryptionUtil;
import iot.technology.mqtt.server.utils.SslUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author mushuwei
 */
@Slf4j
@Component("mqttSslHandlerProvider")
@ConditionalOnProperty(prefix = "mqtt.ssl", value = "enabled", havingValue = "true", matchIfMissing = false)
public class MqttSslHandlerProvider {

    @Value("${mqtt.ssl.protocol}")
    private String sslProtocol;

    @Bean
    @ConfigurationProperties(prefix = "mqtt.ssl.credentials")
    public SslCredentialsConfig mqttSslCredentials() {
        return new SslCredentialsConfig("MQTT SSL Credentials", false);
    }

    @Autowired
    @Qualifier("mqttSslCredentials")
    private SslCredentialsConfig mqttSslCredentialsConfig;

    private SSLContext sslContext;

    public SslHandler getSslHandler() {
        if (sslContext == null) {
            sslContext = createSslContext();
        }
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(false);
        sslEngine.setWantClientAuth(true);
        sslEngine.setEnabledProtocols(sslEngine.getSupportedProtocols());
        sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
        sslEngine.setEnableSessionCreation(true);
        return new SslHandler(sslEngine);
    }

    private SSLContext createSslContext() {
        try {
            SslCredentials sslCredentials = this.mqttSslCredentialsConfig.getCredentials();
            TrustManagerFactory tmFactory = sslCredentials.createTrustManagerFactory();
            KeyManagerFactory kmf = sslCredentials.createKeyManagerFactory();

            KeyManager[] km = kmf.getKeyManagers();
            TrustManager x509wrapped = getX509TrustManager(tmFactory);
            TrustManager[] tm = {x509wrapped};
            if (StringUtils.isEmpty(sslProtocol)) {
                sslProtocol = "TLS";
            }
            SSLContext sslContext = SSLContext.getInstance(sslProtocol);
            sslContext.init(km, tm, null);
            return sslContext;
        } catch (Exception e) {
            log.error("Unable to set up SSL context. Reason: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get SSL context", e);
        }
    }

    private TrustManager getX509TrustManager(TrustManagerFactory tmf) {
        X509TrustManager x509Tm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                x509Tm = (X509TrustManager) tm;
                break;
            }
        }
        return new MqttX509TrustManager(x509Tm);
    }

    static class MqttX509TrustManager implements X509TrustManager {

        private final X509TrustManager trustManager;

        MqttX509TrustManager(X509TrustManager trustManager) {
            this.trustManager = trustManager;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
           String credentialsBody = null;
           for (X509Certificate cert : chain) {
               try {
                   String strCert = SslUtil.getCertificateString(cert);
                   String sha3Hash = EncryptionUtil.getSha3Hash(strCert);
                   final String[] credentialsBodyHolder = new String[1];
                   CountDownLatch latch = new CountDownLatch(1);
                   //TODO 处理业务逻辑
                   latch.await(10, TimeUnit.SECONDS);
                   if (strCert.equals(credentialsBodyHolder[0])) {
                       credentialsBody = credentialsBodyHolder[0];
                       break;
                   }
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
           if (credentialsBody == null) {
               throw new CertificateException("Invalid Device Certificate");
           }
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            trustManager.checkServerTrusted(chain, authType);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return trustManager.getAcceptedIssuers();
        }
    }
}
