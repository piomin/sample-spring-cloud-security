package pl.piomin.services.gateway.customize;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.VaultCertificateRequest;
import org.springframework.vault.support.VaultCertificateResponse;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Duration;

@Component
@Slf4j
public class GatewayServerCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    private VaultTemplate vaultTemplate;

    public GatewayServerCustomizer(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }

    @SneakyThrows
    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        String keyAlias = "vault";
        CertificateBundle bundle = issueCertificate();
        KeyStore keyStore = bundle.createKeyStore(keyAlias);
        String keyStorePath = saveKeyStoreToFile("server-key.pkcs12", keyStore);

        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setClientAuth(Ssl.ClientAuth.NEED);

        ssl.setKeyStore(keyStorePath);
        ssl.setKeyAlias(keyAlias);
        ssl.setKeyStoreType(keyStore.getType());
        ssl.setKeyPassword("");
        ssl.setKeyStorePassword("123456");

        X509Certificate caCert = bundle.getX509IssuerCertificate();
        log.info("CA-SerialNumber: {}", caCert.getSerialNumber());
        KeyStore trustStore = KeyStore.getInstance("pkcs12");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", caCert);
        String trustStorePath = saveKeyStoreToFile("server-trust.pkcs12", trustStore);

        ssl.setTrustStore(trustStorePath);
        ssl.setTrustStorePassword("123456");
        ssl.setTrustStoreType(trustStore.getType());

        factory.setSsl(ssl);
        factory.setPort(8443);
    }

    private CertificateBundle issueCertificate() throws Exception {
        VaultPkiOperations pkiOperations = vaultTemplate.opsForPki("pki");
        VaultCertificateRequest request = VaultCertificateRequest.builder()
                .ttl(Duration.ofHours(12))
                .commonName("localhost")
                .build();
        VaultCertificateResponse response = pkiOperations.issueCertificate("default", request);
        CertificateBundle certificateBundle = response.getRequiredData();

        log.info("Cert-SerialNumber: {}", certificateBundle.getSerialNumber());
        return certificateBundle;
    }

    String readVaultCA() throws Exception {
        byte[] bytes = vaultTemplate.doWithSession(restOperations -> restOperations
                .getForObject("/pki/ca", byte[].class));
        InputStream inStream = new ByteArrayInputStream(bytes);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(inStream);
        log.info("IssuerDN: {}", certificate.getIssuerDN());
        log.info("SN: {}", certificate.getSerialNumber());
        log.info("Public Key: {}", certificate.getPublicKey());

        KeyStore ks = KeyStore.getInstance("pkcs12");
        ks.load(null);
        ks.setCertificateEntry("vault-ca", certificate);


        File f = new File("vault-trust.jks");
        FileOutputStream fos = new FileOutputStream(f);
        ks.store(fos, "123456".toCharArray());
        fos.close();
        log.info("KeyStore: {}", f.getPath());
        return f.getPath();
    }

    private String saveKeyStoreToFile(String fileName, KeyStore keyStore) {
        File f = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            keyStore.store(fos, "123456".toCharArray());
            log.info("KeyStore: {}", f.getPath());
        } catch (CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Error", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) { }
            }
        }
        return f.getPath();
    }
}
