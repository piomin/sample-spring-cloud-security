package pl.piomin.services.account;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SSLConfigServiceBootstrapConfiguration {

	@Autowired
	ConfigClientProperties properties;

	@Bean
	public ConfigServicePropertySourceLocator configServicePropertySourceLocator() throws Exception {
		final char[] password = "123456".toCharArray();
		final TrustStrategy trustStrategy = (X509Certificate[] chain, String authType) -> true;
//		final File keyStoreFile = new File("src/main/resources/account.jks");
		final ClassPathResource resource = new ClassPathResource("discovery.jks");

		SSLContext sslContext = SSLContexts.custom()
				.loadKeyMaterial(resource.getFile(), password, password)
				.loadTrustMaterial(resource.getFile(), password, new TrustSelfSignedStrategy()).build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(sslContext)
				.setSSLHostnameVerifier((s, sslSession) -> true)
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		ConfigServicePropertySourceLocator configServicePropertySourceLocator = new ConfigServicePropertySourceLocator(properties);
		configServicePropertySourceLocator.setRestTemplate(new RestTemplate(requestFactory));
		return configServicePropertySourceLocator;
	}

//	@Bean
//	public RestTemplate template() throws Exception {
//		final char[] password = "123456".toCharArray();
//		final TrustStrategy trustStrategy = (X509Certificate[] chain, String authType) -> true;
//		final File keyStoreFile = new File("src/main/resources/discovery.jks");
//		SSLContext sslContext = SSLContexts.custom()
//				.loadKeyMaterial(keyStoreFile, password, password)
//				.loadTrustMaterial(keyStoreFile, password, new TrustSelfSignedStrategy()).build();
//		CloseableHttpClient httpClient = HttpClients.custom()
//				.setSSLContext(sslContext)
//				.setSSLHostnameVerifier((s, sslSession) -> true)
//				.build();
//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//		return new RestTemplate(requestFactory);
//	}

}
