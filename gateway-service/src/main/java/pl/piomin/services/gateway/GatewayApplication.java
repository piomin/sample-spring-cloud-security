package pl.piomin.services.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class } )
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    KeyResolver authUserKeyResolver() {
        return exchange -> ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal().toString());
    }

    @Bean
    VaultTemplate vaultTemplate() {
        VaultEndpoint e =  new VaultEndpoint();
        e.setScheme("http");
        VaultTemplate template = new VaultTemplate(e, new TokenAuthentication("s.FpDxOxie9aEkvD4qPg5v2VkW"));
        return template;
    }

}
