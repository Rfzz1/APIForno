package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.FornoAuthDTO;
import com.rafael.monitor_forno.dto.LoginResponseDTO;
import com.rafael.monitor_forno.dto.RegistroFornoDTO;
import com.rafael.monitor_forno.dto.RegistroFornoResponseDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.CredencialJaCadastradaException;
import com.rafael.monitor_forno.exception.RecursoEmFormatoInvalido;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

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

    public RegistroFornoResponseDTO registrar(RegistroFornoDTO dto, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        Optional<Forno> fornoExistente = fornoRepository.findBySerialNumber(dto.getSerialNumber());

        if (fornoExistente.isPresent()) {
            throw new CredencialJaCadastradaException(
                    "Forno já vinculado"
            );
        }

        Forno forno = new Forno();
        forno.setSerialNumber(dto.getSerialNumber());
        forno.setUsuario(usuario);
        forno.setNome(dto.getNome());

        forno.setDeviceSecret(
                UUID.randomUUID().toString()
        );

        forno.setAtivo(true);

        fornoRepository.save(forno);

        return toRegistroFornoResponseDTO(forno);
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


        return LoginResponseDTO.builder()
                .id(forno.getId())
                .token(jwtService.gerarToken(forno.getSerialNumber()))
                .build();
    }

    private RegistroFornoResponseDTO toRegistroFornoResponseDTO(Forno forno) {
        return RegistroFornoResponseDTO.builder()
                .serialNumber(forno.getSerialNumber())
                .secret(forno.getDeviceSecret())
                .build();
    }

}
