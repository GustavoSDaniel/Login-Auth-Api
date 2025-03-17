package de.gustavosdaniel.login_auth_api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import de.gustavosdaniel.login_auth_api.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(Usuario usuario) { // ESSE GENERATETOKEN VAI SER USADO NA CLASSE USUARUIO PELO USUARIO
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); //CHAVE DE ACESSO PARA CRIAR O ALGORITIMO

            String token = JWT.create()
                    .withIssuer("login-auth-api") // QUEM É QUE ESTA EMITINDO O TOKEN NO CASO A API
                    .withSubject(usuario.getEmail()) // VAI SALVAR O EMAIL NESSE TOKEN
                    .withExpiresAt(this.generateExpirationDate()) // TEMPO DE DURAÇÃO DESSE TOKEN GERADO
                    .sign(algorithm); // AONDE VAI GERAR O TOKEN
            return token;
        } catch(JWTCreationException exception) { // CAPTURA ESSA EXCESÃO
            throw new RuntimeException("Erro enquanto estava autenticando");
        }
    }

    public String validandoToken (String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); //CHAVE DE ACESSO PARA CRIAR O ALGORITIMO
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api") // QUEM É QUE ESTA EMITINDO O TOKEN NO CASO A API
                    .build()// MONTA O OBJETO PARA FAZER A VERIFICAÇÃO
                    .verify(token) // VERIFICAR O TOKEN
                    .getSubject();
            // CASO DE ALGUM PROBLEMA NA HORA DE VERIFICAR ELE VAI DA A EXCECAO E VAI RETORNAR NULL
        }catch (JWTVerificationException e){ // CASPO DE ALGUM ERRO NA HORA DE VERIFICAR O TOKEN
            return null; // SE RETORNAR NULL QUER DISER QUE O TOKEN TA ERRADO
        }
    }

    private Instant generateExpirationDate() { // TEMPO DE DURAÇÃO DO TOKEN
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));// O TOKEN VAI SER GERADO NA HORA ELE VAI DURA 1 HORA NP NOSSO HORARIO

    }
}
