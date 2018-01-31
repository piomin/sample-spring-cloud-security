package pl.piomin.services.account;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClientImpl.EurekaJerseyClientBuilder;

import pl.piomin.services.account.model.Account;
import pl.piomin.services.account.repository.AccountRepository;

@SpringBootApplication
@EnableDiscoveryClient
public class AccountApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AccountApplication.class).web(true).run(args);
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
	public DiscoveryClient.DiscoveryClientOptionalArgs discoveryClientOptionalArgs() throws NoSuchAlgorithmException {
		DiscoveryClient.DiscoveryClientOptionalArgs args = new DiscoveryClient.DiscoveryClientOptionalArgs();
//		SSLContext sslContext = SSLContext.getDefault();
		EurekaJerseyClientBuilder builder = new EurekaJerseyClientBuilder();
		builder.withCustomSSL(null);
		builder.withClientName("account-client");
		builder.withTrustStoreFile("src/main/resources/account.jks", "123456");
		builder.withMaxTotalConnections(10);
		builder.withMaxConnectionsPerHost(10);
		args.setEurekaJerseyClient(builder.build());
		return args;
	}
	
}
