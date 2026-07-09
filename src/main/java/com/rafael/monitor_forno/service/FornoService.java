package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.exception.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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

    public FornoResponseDTO preRegistro (PreRegistroFornoDTO dto) {

        Optional<Forno> fornoExistente = fornoRepository.findBySerialNumber(dto.getSerialNumber());

        if (fornoExistente.isPresent()) {
            throw new CredencialJaCadastradaException(
                    "Forno já vinculado"
            );
        }

        Forno forno = new Forno();
        forno.setSerialNumber(dto.getSerialNumber());
        forno.setPinSeguranca(gerarPinSeguranca());
        forno.setReivindicado(false);
        forno.setNome(dto.getNome());

        forno.setDeviceSecret(
                UUID.randomUUID().toString()
        );

        fornoRepository.save(forno);

        return toFornoResponseDTO(forno);
    }

    public AutoProvisionamentoResponseDTO autoProvisionar(AutoProvisionamentoDTO dto) {
        Forno forno = fornoRepository.findBySerialNumber(dto.getSerialNumber())
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado " + dto.getSerialNumber()
                        )
                );

        if(forno.isReivindicado()) {
            throw new CredencialJaCadastradaException(
                    "Forno já reivindicado"
            );
        }

        forno.setReivindicado(true);
        fornoRepository.save(forno);

        return toAutoProvisionamentoResponseDTO(forno);
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

        if (!forno.isReivindicado()) {
            throw new AcessoNegadoException(
                    "Forno ainda não provisionado, acesso negado!"
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

        if (!forno.isReivindicado()) {
            throw new AcessoNegadoException("Este forno ainda não foi inicializado pelo sistema.");
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

    private String gerarPinSeguranca() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@!*$#";
        SecureRandom random = new SecureRandom();
        StringBuilder pin = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            int index = random.nextInt(caracteres.length());
            pin.append(caracteres.charAt(index));
        }

        return pin.toString();
    }

    private AutoProvisionamentoResponseDTO toAutoProvisionamentoResponseDTO(Forno forno) {
        return AutoProvisionamentoResponseDTO.builder()
                .secret(forno.getDeviceSecret())
                .build();
    }

    private FornoResponseDTO toFornoResponseDTO(Forno forno) {
        return FornoResponseDTO.builder()
                .id(forno.getId())
                .serialNumber(forno.getSerialNumber())
                .ativo(forno.isAtivo())
                .nome(forno.getNome())
                .pinSeguranca(forno.getPinSeguranca())
                .reivindicado(forno.isReivindicado())
                .build();
    }

}
