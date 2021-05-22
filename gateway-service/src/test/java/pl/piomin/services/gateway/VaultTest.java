package pl.piomin.services.gateway;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.CertificateBundle;
import org.springframework.vault.support.VaultCertificateRequest;
import org.springframework.vault.support.VaultCertificateResponse;

import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Duration;

public class VaultTest {

    TestRestTemplate restTemplate = new TestRestTemplate(builder());

    RestTemplateBuilder builder() {
        try {
            char[] password = "".toCharArray();

            CertificateBundle bundle = issueCertFromVault("user1");
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(bundle.createKeyStore("vault"), password)
                    .loadTrustMaterial(createTrustStore(bundle), (x509, s) -> true).build();

            HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
            return new RestTemplateBuilder()
                    .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    void comm() {
        RestTemplateBuilder b = new RestTemplateBuilder();
        ResponseEntity<String> res = restTemplate.getForEntity("https://localhost:8443/account/1", String.class);
        HttpStatus h = res.getStatusCode();
    }

    private CertificateBundle issueCertFromVault(String user) {
        VaultPkiOperations pkiOperations = vaultTemplate().opsForPki("pki");

        VaultCertificateRequest request = VaultCertificateRequest.builder()
                .withAltName("localhost")
                .ttl(Duration.ofHours(3))
                .commonName("piotrm")
                .build();

        VaultCertificateResponse response = pkiOperations.issueCertificate("default", request);
        CertificateBundle certificateBundle = response.getRequiredData();
        return certificateBundle;
    }

    private KeyStore createTrustStore(CertificateBundle bundle) {
        X509Certificate caCert = bundle.getX509IssuerCertificate();
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", caCert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trustStore;
    }

    private VaultTemplate vaultTemplate() {
        VaultEndpoint e =  new VaultEndpoint();
        e.setScheme("http");
        VaultTemplate template = new VaultTemplate(e, new TokenAuthentication("s.FpDxOxie9aEkvD4qPg5v2VkW"));
        return template;
    }
}
