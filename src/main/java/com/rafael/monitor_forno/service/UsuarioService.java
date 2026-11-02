package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.config.CustomUserDetails;
import com.rafael.monitor_forno.config.utils.UsuarioCreatedEvent;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.MensagemSuporteRepository;
import com.rafael.monitor_forno.database.repository.RefreshTokenRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.enums.Role;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.CredenciaisInvalidasException;
import com.rafael.monitor_forno.exception.CredencialJaCadastradaException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.SecureRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.HtmlUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final MensagemSuporteRepository mensagemSuporteRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthenticationManager authenticationManager;

    @Value("${app.base-url}")
    private String baseUrl;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService,  EmailService emailService, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository, MensagemSuporteRepository mensagemSuporteRepository, ApplicationEventPublisher applicationEventPublisher, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mensagemSuporteRepository = mensagemSuporteRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.authenticationManager = authenticationManager;
    }

    private Usuario buscarUsuarioLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + email
                        )
                );
    }

    @Transactional(rollbackFor = Exception.class)
    public void cadastrarUsuario(UserRequestDTO dto) {

        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new CredencialJaCadastradaException(
                    "Email já cadastrado"
            );
        }

        if (dto.getCpf() != null && usuarioRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new CredencialJaCadastradaException(
                    "CPF já cadastrado"
            );
        }

        if (dto.getCpf() == null || dto.getCpf().trim().isEmpty()) {
            throw new CredenciaisInvalidasException(
                    "CPF é obrigatório"
            );
        }

        String cpfSemMascara = dto.getCpf().replaceAll("\\D", "");
        String emailNormalizado = dto.getEmail().trim().toLowerCase();

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(emailNormalizado);
        usuario.setNascimento(dto.getNascimento());
        usuario.setRole(Role.USER);
        usuario.setCpf(cpfSemMascara);
        usuario.setVersaoUsuario(1L);

        String senhaHash = passwordEncoder.encode(dto.getSenha());
        usuario.setSenha(senhaHash);
        usuarioRepository.save(usuario);

        applicationEventPublisher.publishEvent(new UsuarioCreatedEvent(usuario));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUsuarioCreated(UsuarioCreatedEvent event) {

        Usuario usuario = event.usuario();

        String linkSanitizado = HtmlUtils.htmlEscape(baseUrl);
        String nomeSanitizado = HtmlUtils.htmlEscape(usuario.getNome());
        String emailSanitizado = HtmlUtils.htmlEscape(usuario.getEmail());

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
                        """.formatted(nomeSanitizado, emailSanitizado, linkSanitizado)
        );

    }

    public UserResponseDTO atualizarUsuario(AtualizarMeuPerfilDTO dto, String email) {

        Usuario usuarioExistente = buscarUsuarioLogado(email);

        if (!usuarioExistente.getEmail().equals(dto.getEmail())) {
            if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new CredencialJaCadastradaException("Este e-mail já está sendo usado por outro usuário");
            }
        }

        usuarioExistente.setNome(dto.getNome());
        usuarioExistente.setNascimento(dto.getNascimento());

        return toUserResponseDTO(usuarioRepository.save(usuarioExistente));
    }

    @Transactional
    public void atualizarSenha(NovaSenhaLogadoDTO dto, String email) {
        Usuario usuarioExistente = buscarUsuarioLogado(email);

        if (!passwordEncoder.matches(dto.senhaAtual(), usuarioExistente.getSenha())) {
            throw new AcessoNegadoException(
                    "Senha atual incorreta"
            );
        }

        usuarioExistente.setSenha(passwordEncoder.encode(dto.senhaAtualizada()));
        usuarioExistente.setVersaoUsuario(usuarioExistente.getVersaoUsuario() + 1);
        refreshTokenRepository.deleteByUsuario(usuarioExistente);

        usuarioRepository.save(usuarioExistente);

    }

    public void deleteByEmail(String email) {
        Usuario usuario = buscarUsuarioLogado(email);

        mensagemSuporteRepository.anularRemetentePorUsuarioId(usuario.getId());

        usuarioRepository.delete(usuario);
    }

    public List<UserResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toUserResponseDTO)
                .toList();
    }

    public LoginResponseDTO login(String email, String senha) {

        Authentication authentication = authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(email, senha)
        );

        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role == null) {
            throw new CredenciaisInvalidasException("Usuário sem role definida");
        }

        String emailU = authentication.getName();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long versaoUsuario = userDetails.getVersaoUsuario();
        UUID idUsuario = userDetails.getId();

        // ALTERAÇÃO AQUI: Passando "USUARIO" como tipo
        String token = jwtService.gerarToken(
                emailU,
                "USUARIO", role, versaoUsuario
        );

        String refreshToken = refreshTokenService.cadastrarRefreshToken(emailU).getRefreshToken();

        return LoginResponseDTO.builder()
                .id(idUsuario)
                .token(token)
                .refreshtoken(refreshToken)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void gerarTokenRecuperacao(String email) {

        usuarioRepository.findByEmail(email)
                .ifPresent(usuario -> {

                    String token = UUID.randomUUID().toString();

                    usuario.setTokenRecuperacaoSenha(token);
                    usuario.setExpiracaoToken(Instant.now().plus(30, ChronoUnit.MINUTES));

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

    @Transactional(rollbackFor = Exception.class)
    public void enviarCodigoRedefinirEmail(String emailAtual, String senhaAtual, String novoEmail) {

        Usuario usuario = buscarUsuarioLogado(emailAtual);

        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new AcessoNegadoException(
                    "Email ou senha inválidos"
            );
        }

        if (usuarioRepository.findByEmail(novoEmail).isPresent()) {
            throw new CredencialJaCadastradaException("Este e-mail já está sendo usado por outro usuário");
        }

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        usuario.setCodigoVerificacaoEmail(otp);
        usuario.setExpiracaoCodigoEmail(Instant.now().plus(15, ChronoUnit.MINUTES));
        usuario.setNovoEmailPendente(novoEmail);
        usuario.setTentativasCodigoEmail(0);
        usuarioRepository.save(usuario);

        emailService.enviarEmail(usuario.getNovoEmailPendente(),
                "Código de Verificação para Alteração de E-mail",
                """
                        Olá, %s!<br><br>
                        
                        Recebemos uma solicitação para alterar o e-mail da sua conta para <b>%s<b>.<br><br>
                        
                        Seu código de verificação é:<br><br>
                        
                        <b>%s</b><br><br>
                        
                        Este código expira em 15 minutos.<br><br>
                        
                        Caso você não tenha solicitado esta alteração, ignore este e-mail e altere sua senha.
                        """.formatted(usuario.getNome(), novoEmail, otp)
        );

    }

    @Transactional(rollbackFor = Exception.class, noRollbackFor = {RecursoNaoEncontradoException.class, CredenciaisInvalidasException.class, AcessoNegadoException.class})
    public void verificarCodigoRedefinirEmail(String emailAtual, String codigo) {

        Usuario usuario = buscarUsuarioLogado(emailAtual);

        if (usuario.getExpiracaoCodigoEmail() == null || usuario.getCodigoVerificacaoEmail() == null) {
            throw new CredenciaisInvalidasException("Código expirado ou inválido");
        }

        if (Instant.now().isAfter(usuario.getExpiracaoCodigoEmail())) {
            throw new CredenciaisInvalidasException("Código de verificação expirado");
        }

        if (!Objects.equals(usuario.getCodigoVerificacaoEmail(), codigo)) {

            int totalTentativas = usuario.getTentativasCodigoEmail() + 1;
            usuario.setTentativasCodigoEmail(totalTentativas);

            if (totalTentativas >= 3) {
                usuario.setCodigoVerificacaoEmail(null);
                usuario.setExpiracaoCodigoEmail(null);
                usuario.setNovoEmailPendente(null);
                usuarioRepository.save(usuario);
                throw new AcessoNegadoException("Limite de tentativas excedido!");
            }

            usuarioRepository.save(usuario);
            throw new CredenciaisInvalidasException("Código de verificação inválido");
        }

        String novoEmail = usuario.getNovoEmailPendente();

        usuario.setEmailAnterior(emailAtual);
        usuario.setEmail(novoEmail);
        usuario.setCodigoVerificacaoEmail(null);
        usuario.setExpiracaoCodigoEmail(null);
        usuario.setNovoEmailPendente(null);
        refreshTokenRepository.deleteByUsuario(usuario);
        usuario.setTentativasCodigoEmail(0);

        usuarioRepository.save(usuario);
        String token = jwtService.gerarTokenReversaoEmail(emailAtual, novoEmail);
        String link = baseUrl + "/reverter-email?token=" + token;

        emailService.enviarEmail(usuario.getEmailAnterior(),
                "E-mail alterado com sucesso",
                """
                        Olá, %s!<br><br>
                        
                        O seu e-mail foi alterado para <b>%s<b>.<br><br>
                        
                        Caso você não tenha solicitado esta alteração, clique no link para reverter a ação e altere sua senha.
                        %s
                        """.formatted(usuario.getNome(), novoEmail, link)
        );

    }

    @Transactional(rollbackFor = Exception.class)
    public void reverterEmail(ReversaoEmailDTO dto) {

        Claims claims = jwtService.extrairTodasClaims(dto.getToken());
        String emailAntigo = claims.get("emailAntigo", String.class);
        String tokenTipo = claims.get("tokenTipo", String.class);

        if (!"REVERSAO_EMAIL".equals(tokenTipo)) {
            throw new CredenciaisInvalidasException("Token inválido");
        }

        Usuario usuario = usuarioRepository.findByEmailAnterior(emailAntigo)
                        .orElseThrow(
                                () -> new RecursoNaoEncontradoException(
                                        "Email não encontrado"
                                )
                        );

        if (usuario.getVersaoUsuario() == null) {
            throw new CredenciaisInvalidasException("Token inválido");
        }

        usuario.setEmail(emailAntigo);
        usuario.setEmailAnterior(null);
        usuario.setVersaoUsuario(usuario.getVersaoUsuario() + 1);
        refreshTokenRepository.deleteByUsuario(usuario);
        usuarioRepository.save(usuario);

    }
    @Transactional(rollbackFor = Exception.class)
    public void redefinirSenha(NovaSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacaoSenha(dto.token())
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Token inválido " +dto.token()
                        )
                );

        if (usuario.getExpiracaoToken().isBefore(Instant.now())) {
            throw new CredenciaisInvalidasException("Token expirado");
        }

        String senhaHash = passwordEncoder.encode(dto.novaSenha());
        usuario.setSenha(senhaHash);

        if (usuario.getVersaoUsuario() == null) {
            throw new CredenciaisInvalidasException("Token inválido");
        }

        usuario.setTokenRecuperacaoSenha(null);
        usuario.setExpiracaoToken(null);
        usuario.setVersaoUsuario(usuario.getVersaoUsuario() + 1);
        refreshTokenRepository.deleteByUsuario(usuario);

        usuarioRepository.save(usuario);
    }

    public UserResponseAdminDTO findById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado: " + id
                        )
                );

        return toUserResponseAdminDTO(usuario);
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
        Usuario usuario = buscarUsuarioLogado(email);

        return toUserResponseDTO(usuario);
    }

    private UserResponseAdminDTO toUserResponseAdminDTO(Usuario usuario) {
        return UserResponseAdminDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .emailAnterior(usuario.getEmailAnterior())
                .build();
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
