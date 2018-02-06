package pl.piomin.services.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

//	@Override
//	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//		oauthServer
//			.checkTokenAccess("isAuthenticated()")
//			.allowFormAuthenticationForClients();
//	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient("piotr.minkowski")
				.secret("123456")
				.scopes("read")
				.authorities("ROLE_CLIENT")
				.authorizedGrantTypes("authorization_code", "refresh_token", "implicit")
				.autoApprove(true)
			.and()
			.withClient("john.smith")
				.secret("123456")
				.scopes("read", "write")
				.authorities("ROLE_ADMIN")
				.authorizedGrantTypes("authorization_code", "refresh_token", "password")
				.autoApprove(true);
	}
	
}
