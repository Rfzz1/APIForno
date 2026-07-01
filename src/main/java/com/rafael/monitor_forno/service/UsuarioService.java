package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.enums.Role;
import com.rafael.monitor_forno.exception.CredenciaisInvalidasException;
import com.rafael.monitor_forno.exception.CredencialJaCadastradaException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
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

        String link = "https://monitoramentoforno.com.br";

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

    public UserResponseDTO atualizarUsuario(UserRequestDTO dto, String email) {

        Usuario usuarioExsitente = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado" + email
                        )
                );

        usuarioExsitente.setNome(dto.getNome());
        usuarioExsitente.setNascimento(dto.getNascimento());
        usuarioExsitente.setEmail(dto.getEmail());

        if (dto.getSenha() != null &&
                !dto.getSenha().isBlank()) {

            usuarioExsitente.setSenha(
                    passwordEncoder.encode(dto.getSenha())
            );
        }

        return toUserResponseDTO(usuarioRepository.save(usuarioExsitente));
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

        usuarioExistente.setSenha(dto.senhaAtualizada());

        usuarioRepository.save(usuarioExistente);

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

                    String link = "https://monitoramentoforno.com.br/redefinir-senha?token=" + token;

                    emailService.enviarEmail(usuario.getEmail(),
                            "Recuperação de Senha",
                            """
                                    Olá!
                                    
                                    Recebemos uma solicitação para redefinição da sua senha.
                                    
                                    Clique no link abaixo:
                                    
                                    %s
                                    
                                    Este link expira em 30 minutos.
                                    
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

        usuario.setSenha(
                passwordEncoder.encode(dto.novaSenha())
        );

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
                .fotoBase64(usuario.getFotoBase64())
                .build();
    }
}
