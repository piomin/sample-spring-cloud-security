package pl.piomin.services.auth;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
//@EnableResourceServer
//@EnableAuthorizationServer
public class AuthApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(AuthApplication.class).web(true).run(args);
	}
	
}
