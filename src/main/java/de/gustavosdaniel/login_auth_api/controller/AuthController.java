package de.gustavosdaniel.login_auth_api.controller;

import de.gustavosdaniel.login_auth_api.dto.LoginRequestDTO;
import de.gustavosdaniel.login_auth_api.dto.RegisterRequestDTO;
import de.gustavosdaniel.login_auth_api.dto.ResponseDTO;
import de.gustavosdaniel.login_auth_api.model.Usuario;
import de.gustavosdaniel.login_auth_api.repository.UsuarioRepository;
import de.gustavosdaniel.login_auth_api.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<> login(@RequestBody LoginRequestDTO body ) {
        Usuario usuario = usuarioRepository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        if (passwordEncoder.matches(usuario.getSenha(), body.senha())){// VAI COMPARAR SE A SENHA QUE EU RECEBI NO BODY [E A MESMA QUE EU TERNHO SALVO NO BANCO DE DADOS
            String token = this.tokenService.generateToken(usuario);
            return ResponseEntity.ok(new ResponseDTO(usuario.getNome(), token));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<> register(@RequestBody RegisterRequestDTO body ) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(body.email());
        if (usuario.isEmpty()) { //SE NÃO TIVER USUARIO
            Usuario novoSsuario = new Usuario();
            novoSsuario.setSenha(passwordEncoder.encode(body.senha())); // PARA JA SALVAR A SENHA CRIPTOGRAFADA
            novoSsuario.setEmail(body.email());
            novoSsuario.setNome(body.nome());
            this.usuarioRepository.save(novoSsuario);
                String token = this.tokenService.generateToken(novoSsuario);
                return ResponseEntity.ok(new ResponseDTO(novoSsuario.getNome(), token));
            }
        return ResponseEntity.badRequest().build();

    }
}
