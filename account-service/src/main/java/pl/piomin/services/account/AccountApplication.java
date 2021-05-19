package pl.piomin.services.account;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClientImpl.EurekaJerseyClientBuilder;

import org.springframework.core.io.ClassPathResource;
import pl.piomin.services.account.model.Account;
import pl.piomin.services.account.repository.AccountRepository;

import javax.net.ssl.SSLContext;

@SpringBootApplication
@EnableDiscoveryClient
public class AccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountApplication.class, args);
	}

	@Bean
	AccountRepository repository() {
		AccountRepository repository = new AccountRepository();
		repository.add(new Account("1234567890", 50000, 1L));
		repository.add(new Account("1234567891", 50000, 1L));
		repository.add(new Account("1234567892", 0, 1L));
		repository.add(new Account("1234567893", 50000, 2L));
		repository.add(new Account("1234567894", 0, 2L));
		repository.add(new Account("1234567895", 50000, 2L));
		repository.add(new Account("1234567896", 0, 3L));
		repository.add(new Account("1234567897", 50000, 3L));
		repository.add(new Account("1234567898", 50000, 3L));
		return repository;
	}
	
	@Bean
	public DiscoveryClient.DiscoveryClientOptionalArgs discoveryClientOptionalArgs() throws Exception {
		DiscoveryClient.DiscoveryClientOptionalArgs args = new DiscoveryClient.DiscoveryClientOptionalArgs();
		final char[] password = "123456".toCharArray();
		final ClassPathResource resource = new ClassPathResource("account.jks");

		SSLContext sslContext = SSLContexts.custom()
				.loadKeyMaterial(resource.getFile(), password, password)
				.loadTrustMaterial(resource.getFile(), password, new TrustSelfSignedStrategy()).build();

		EurekaJerseyClientBuilder builder = new EurekaJerseyClientBuilder();
		builder.withClientName("account-client");
		builder.withMaxTotalConnections(10);
		builder.withMaxConnectionsPerHost(10);
		builder.withCustomSSL(sslContext);
		args.setEurekaJerseyClient(builder.build());
		return args;
	}
	
}
