package de.gustavosdaniel.login_auth_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // INDICA QUE É UMA CLASSE DE CONFIGURAÇÃO
@EnableWebSecurity //INDICA QUE É UMA CLASSE QUE CUIDA DA SEGURANÃ DA WEB
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // NÃO FICA SALVO A AUTENTICAÇÃO
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll() // NÃO PRECISA DE AUTENTICAÇÃO PQ QUALQUER PESSOA PODE FAZER LOGIN E SE REGISTRAR
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll() // NÃO PRECISA DE AUTENTICAÇÃO PQ QUALQUER PESSOA PODE FAZER LOGIN E SE REGISTRAR
                        .anyRequest().authenticated() //QUALQUER OUTRA REQUISIÇÃO PRECISA DE AUTENTIÇÃO
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class); // ANTES DE TUDO VAI SER COLOCADO NAS REQUISIÇÕES QUE EU QUERO QUE SEJA IDENTIFICADAS
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // ESSA FUNÇÃO [E CESSESARIA PARA A AUTENTICAÇÃO FUNCIONAR
    }





}
