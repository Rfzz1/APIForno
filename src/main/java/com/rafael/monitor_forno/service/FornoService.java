package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.exception.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FornoService {

    private final FornoRepository fornoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public FornoService(FornoRepository fornoRepository, UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.fornoRepository = fornoRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public FornoResponseDTO fabricar (FabricarFornoDTO dto) {

        Optional<Forno> fornoExistente = fornoRepository.findBySerialNumber(dto.getSerialNumber());

        if (fornoExistente.isPresent()) {
            throw new CredencialJaCadastradaException(
                    "Forno já vinculado"
            );
        }

        Forno forno = new Forno();
        forno.setSerialNumber(dto.getSerialNumber());
        forno.setNome(dto.getNome());
        forno.setPinSeguranca(dto.getPinSeguranca());

        forno.setDeviceSecret(
                UUID.randomUUID().toString()
        );

        fornoRepository.save(forno);

        return toFornoResponseDTO(forno);
    }

    public LoginResponseDTO autenticar(FornoAuthDTO dto) {
        Forno forno = fornoRepository.findBySerialNumber(dto.getSerialNumber())
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado"
                        )
                );

        if (!forno.getDeviceSecret().equals(dto.getSecret())) {
            throw new AcessoNegadoException(
                    "Credenciais inválidas"
            );
        }

        // ALTERAÇÃO AQUI: Passando "FORNO" como tipo
        return LoginResponseDTO.builder()
                .id(forno.getId())
                .token(jwtService.gerarToken(forno.getSerialNumber(), "FORNO", "FORNO"))
                .build();
    }

    public void vincularFornoaoUsuario(VincularFornoDTO dto, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        Forno forno = fornoRepository.findBySerialNumber(dto.getSerialNumber())
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado " + dto.getSerialNumber()
                        )
                );

        if (forno.getUsuario() != null) {
            throw new CredencialJaCadastradaException("Este forno já está vinculado a uma conta");
        }

        if (!forno.getPinSeguranca().equals(dto.getPinSeguranca())) {
            throw new AcessoNegadoException("PIN de segurança inválido para este forno");
        }

        forno.setUsuario(usuario);
        forno.setAtivo(true);
        fornoRepository.save(forno);
    }

    public List<FornoResponseDTO> buscarMeusFornos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );
        return fornoRepository.findByUsuario(usuario)
                .stream()
                .map(this::toFornoResponseDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private FornoResponseDTO toFornoResponseDTO(Forno forno) {
        return FornoResponseDTO.builder()
                .id(forno.getId())
                .serialNumber(forno.getSerialNumber())
                .secret(forno.getDeviceSecret())
                .ativo(forno.isAtivo())
                .nome(forno.getNome())
                .build();
    }

}
