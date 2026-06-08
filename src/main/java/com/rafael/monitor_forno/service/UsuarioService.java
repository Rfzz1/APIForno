package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.UserRequestDTO;
import com.rafael.monitor_forno.dto.UserResponseDTO;
import com.rafael.monitor_forno.exception.CredenciaisInvalidasException;
import com.rafael.monitor_forno.exception.CredencialJaCadastradaException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

        String senhaHash = passwordEncoder.encode(dto.getSenha());
        usuario.setSenha(senhaHash);
        usuarioRepository.save(usuario);
    }

    public UserResponseDTO atualizarUsuario(UserRequestDTO dto, UUID id) {

        Usuario usuarioExsitente = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado" + id
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

    public void deleteById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado" + id
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

    public String login(String email, String senha) {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        if (!passwordEncoder.matches(
                senha,
                usuario.getSenha()
        )) {

            throw new CredenciaisInvalidasException(
                    "Email ou senha inválidos"
            );
        }

        return jwtService.gerarToken(
                usuario.getEmail()
        );
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

    private UserResponseDTO toUserResponseDTO(Usuario usuario) {
        return UserResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .nascimento(usuario.getNascimento())
                .build();
    }
}
