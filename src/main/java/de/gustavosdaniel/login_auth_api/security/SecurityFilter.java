package de.gustavosdaniel.login_auth_api.security;

import de.gustavosdaniel.login_auth_api.model.Usuario;
import de.gustavosdaniel.login_auth_api.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter { // OncePerRequestFilter E UM FILTRO QUE EXECUTA CADA REQUEST QUE CHEGA NA API ANTES DE CHEGAR NO CONTROLLER

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository usuarioRepository; // VERIFICA O USUARIO SE O TOKEN ESTA VALIDO

    @Override // METODO QUE É O FILTRO
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        var login = tokenService.validandoToken(token); //  AQUI ELE PEGA O TOKEN E VERIFICA SE NÃO É NULO

        if(login != null){
            Usuario usuario = usuarioRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("Usuario Não encontrado")); // VAI BUSCAR O USUARIO LA NO BANCO DE DADOS
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); // CRIA UMA COLEÇÃO DE ROLE
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities);// CRIA UM OBJETO DE USUARIO PARA AUTENTICAÇÃO
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){  // AQUI ELE VAI PEGAR A REQUESTI E VAI VER SE PODE AUTORIZAR A ENTRADA DO TOKEN
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

}
