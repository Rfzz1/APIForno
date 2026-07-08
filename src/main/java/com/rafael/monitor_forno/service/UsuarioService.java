package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.enums.Role;
import com.rafael.monitor_forno.exception.CredenciaisInvalidasException;
import com.rafael.monitor_forno.exception.CredencialJaCadastradaException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService,  EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    public void cadastrarUsuario(UserRequestDTO dto) {

        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new CredencialJaCadastradaException(
                    "Email já cadastrado"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setNascimento(dto.getNascimento());
        usuario.setRole(Role.USER);

        String senhaHash = passwordEncoder.encode(dto.getSenha());
        usuario.setSenha(senhaHash);
        usuarioRepository.save(usuario);

        String link = baseUrl;

        emailService.enviarEmail(usuario.getEmail(),
                "Bem vindo(a) ao nosso monitoramento de forno! Seu cadastro foi concluído!",
                """
                        Olá, %s! Estamos empolgados por ter você conosco. Seu cadastro foi realizado com sucesso, sinta-se à vontade para explorar nossos serviços!<br><br>
                        
                        <b>Seus dados de acesso:</b><br><br>
                        
                        E-mail cadastrado: %s<br><br>
                        
                        Acesse o sistema clicando aqui: <a href="%s">Acessar Monitoramento</a><br><br>
                        
                        Se tiver alguma dúvida ou precisar de assistência, não hesite em entrar em contato.<br>
                        Estamos muito felizes em ter você conosco!<br><br>
                        
                        Abraços,<br>
                        Equipe Monitoramento de Forno
                        """.formatted(usuario.getNome(), usuario.getEmail(), link)
        );
    }

    public UserResponseDTO atualizarUsuario(AtualizarMeuPerfilDTO dto, String email) {

        Usuario usuarioExistente = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado" + email
                        )
                );

        if (!usuarioExistente.getEmail().equals(dto.getEmail())) {
            if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new CredencialJaCadastradaException("Este e-mail já está sendo usado por outro usuário");
            }
        }

        usuarioExistente.setNome(dto.getNome());
        usuarioExistente.setNascimento(dto.getNascimento());
        usuarioExistente.setEmail(dto.getEmail());

        return toUserResponseDTO(usuarioRepository.save(usuarioExistente));
    }

    public void atualizarSenha(NovaSenhaLogadoDTO dto, String email) {

        Usuario usuarioExistente = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        if (!passwordEncoder.matches(dto.senhaAtual(), usuarioExistente.getSenha())) {
            throw new RecursoNaoEncontradoException(
                    "Senha atual incorreta"
            );
        }

        usuarioExistente.setSenha(passwordEncoder.encode(dto.senhaAtualizada()));

        usuarioRepository.save(usuarioExistente);

    }

    public void deleteByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );
        usuarioRepository.delete(usuario);
    }

    public List<UserResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toUserResponseDTO)
                .toList();
    }

    public LoginResponseDTO login(String email, String senha) {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException(
                    "Email ou senha inválidos"
            );
        }

        String role = usuario.getRole().toString();

        // ALTERAÇÃO AQUI: Passando "USUARIO" como tipo
        String token = jwtService.gerarToken(
                usuario.getEmail(),
                "USUARIO", role
        );

        return LoginResponseDTO.builder()
                .id(usuario.getId())
                .token(token)
                .build();
    }

    public void gerarTokenRecuperacao(String email) {

        usuarioRepository.findByEmail(email)
                .ifPresent(usuario -> {

                    String token = UUID.randomUUID().toString();

                    usuario.setTokenRecuperacaoSenha(token);
                    usuario.setExpiracaoToken(LocalDateTime.now().plusMinutes(30));

                    usuarioRepository.save(usuario);

                    String link = baseUrl + "/redefinir-senha?token=" + token;

                    emailService.enviarEmail(usuario.getEmail(),
                            "Recuperação de Senha",
                            """
                                    Olá!<br><br>
                                    
                                    Recebemos uma solicitação para redefinição da sua senha.<br><br>
                                    
                                    Clique no link abaixo:<br><br>
                                    
                                    %s<br><br>
                                    
                                    Este link expira em 30 minutos.<br><br>
                                    
                                    Caso você não tenha solicitado esta alteração, ignore este e-mail.
                                    """.formatted(link)
                    );
                });
    }

    public void redefinirSenha(NovaSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacaoSenha(dto.token())
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Token inválido " +dto.token()
                        )
                );

        if (usuario.getExpiracaoToken().isBefore(LocalDateTime.now())) {
            throw new CredenciaisInvalidasException("Token expirado");
        }

        String senhaHash = passwordEncoder.encode(dto.novaSenha());
        usuario.setSenha(senhaHash);

        usuario.setTokenRecuperacaoSenha(null);
        usuario.setExpiracaoToken(null);

        usuarioRepository.save(usuario);
    }

    public UserResponseDTO findById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + id
                        )
                );

        return toUserResponseDTO(usuario);
    }

    public void deleteById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + id
                        )
                );
        usuarioRepository.delete(usuario);
    }

    public UserResponseDTO promoverUsuario(UUID id) {

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + id
                        )
                );

        usuarioExistente.setRole(Role.ADMIN);

        return toUserResponseDTO(usuarioRepository.save(usuarioExistente));

    }

    public UserResponseDTO rebaixarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + id
                        )
                );
        usuario.setRole(Role.USER);
        return toUserResponseDTO(usuarioRepository.save(usuario));
    }

    public UserResponseDTO atualizarUsuarioAdmin(UUID id, AdminAtualizarUsuarioDTO dto) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + id
                        )
                );

        if (!usuarioExistente.getEmail().equals(dto.getEmail())) {
            if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new CredencialJaCadastradaException("Este e-mail já está sendo usado por outro usuário");
            }
        }

        usuarioExistente.setNome(dto.getNome());
        usuarioExistente.setEmail(dto.getEmail());
        usuarioExistente.setNascimento(dto.getNascimento());

        return toUserResponseDTO(usuarioRepository.save(usuarioExistente));
    }

    public UserResponseDTO meuPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );

        return toUserResponseDTO(usuario);
    }

    private UserResponseDTO toUserResponseDTO(Usuario usuario) {
        return UserResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .nascimento(usuario.getNascimento())
                .build();
    }
}
