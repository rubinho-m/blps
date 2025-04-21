package com.example.demo.config;

import com.example.demo.jwt.JaasAuthorityGranter;
import com.example.demo.jwt.JwtAccessDeniedHandler;
import com.example.demo.jwt.JwtAuthFilter;
import com.example.demo.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.jwt.JwtLoginModule;
import com.example.demo.jwt.JwtService;
import com.example.demo.model.Authority;
import com.example.demo.repository.UserXmlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtService jwtService;
    private final UserXmlRepository userXmlRepository;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    @Autowired
    public SecurityConfiguration(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                 @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                                 AuthenticationConfiguration authenticationConfiguration,
                                 JwtService jwtService,
                                 UserXmlRepository userXmlRepository) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.resolver = resolver;
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtService = jwtService;
        this.userXmlRepository = userXmlRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(jaasAuthenticationProvider(configuration()))
                .addFilterBefore(new JwtAuthFilter(authenticationManager(authenticationConfiguration), resolver), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/api-docs.yaml").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/configuration/security").permitAll()
                        .requestMatchers("/webjars/**").permitAll()

                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/login").permitAll()

                        .requestMatchers(HttpMethod.GET, "/profiles/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/profiles/{id}").hasAuthority(Authority.BASE.name())
                        .requestMatchers(HttpMethod.DELETE, "/profiles/{id}").hasAuthority(Authority.BASE.name())

                        .requestMatchers("/subscriptions/**").hasAuthority(Authority.BASE.name())

                        .requestMatchers("/photo/**").hasAuthority(Authority.BASE.name())

                        .requestMatchers(HttpMethod.GET, "/news/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/news/**").hasAuthority(Authority.BASE.name())
                        .requestMatchers(HttpMethod.PUT, "/news/**").hasAuthority(Authority.BASE.name())
                        .requestMatchers(HttpMethod.DELETE, "/news/**").hasAuthority(Authority.BASE.name())

                        .requestMatchers("/accounts/**").hasAuthority(Authority.BASE.name())

                        .requestMatchers(HttpMethod.GET, "/languages").permitAll()
                        .requestMatchers(HttpMethod.GET, "/towns").permitAll()

                        .requestMatchers(HttpMethod.POST, "/languages").hasAuthority(Authority.ADD_LANGUAGES.name())
                        .requestMatchers(HttpMethod.POST, "/towns").hasAuthority(Authority.ADD_TOWNS.name())
                        .requestMatchers("/admin/requests/**").hasAuthority(Authority.APPROVE_CHANGES.name())
                        .requestMatchers("/admin/cards").hasAuthority(Authority.VIEW_ANY_CARD.name())
                        .requestMatchers("/admin/subscriptions").hasAuthority(Authority.VIEW_ANY_SUBSCRIPTION.name())
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public InMemoryConfiguration configuration() {
        final AppConfigurationEntry[] configurationEntries = new AppConfigurationEntry[]{
                new AppConfigurationEntry(
                        JwtLoginModule.class.getName(),
                        AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                        Map.of("jwtService", jwtService)
                )
        };
        return new InMemoryConfiguration(Map.of("SPRINGSECURITY", configurationEntries));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    @Qualifier
    public AuthenticationProvider jaasAuthenticationProvider(javax.security.auth.login.Configuration configuration) {
        final DefaultJaasAuthenticationProvider provider = new DefaultJaasAuthenticationProvider();
        provider.setConfiguration(configuration);
        provider.setAuthorityGranters(new AuthorityGranter[]{new JaasAuthorityGranter(jwtService, userXmlRepository)});
        return provider;
    }
}
