package pl.piomin.services.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        SubjectDnX509PrincipalExtractor principalExtractor =
                new SubjectDnX509PrincipalExtractor();
        principalExtractor.setSubjectDnRegex("CN=(.*?)(?:,|$)");

        return http.csrf().disable()
                .authorizeExchange(exchanges ->
                        exchanges.anyExchange().authenticated())
                .x509()
                    .principalExtractor(principalExtractor)
                .and()
                    .httpBasic().disable().build();
    }

    @Bean
    public MapReactiveUserDetailsService users() {
        UserDetails user1 = User.builder()
                .username("piotrm")
                .password("{noop}1234")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user1);
    }
}
