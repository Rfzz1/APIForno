package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.FornoCompartilhado;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.*;
import com.rafael.monitor_forno.dto.*;
import com.rafael.monitor_forno.exception.*;
import com.rafael.monitor_forno.handler.FornoWebSocketHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FornoService {

    private final FornoRepository fornoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final FornoWebSocketHandler fornoWebSocketHandler;
    private final SessaoRepository sessaoRepository;;
    private final TemperaturaRepository temperaturaRepository;
    private final EventoRepository eventoRepository;
    private final FornoCompartilhadoRepository fornoCompartilhadoRepository;

    public FornoService(FornoRepository fornoRepository, UsuarioRepository usuarioRepository, JwtService jwtService, FornoWebSocketHandler fornoWebSocketHandler, SessaoRepository sessaoRepository, TemperaturaRepository temperaturaRepository, EventoRepository eventoRepository, FornoCompartilhadoRepository fornoCompartilhadoRepository) {
        this.fornoRepository = fornoRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.fornoWebSocketHandler = fornoWebSocketHandler;
        this.sessaoRepository = sessaoRepository;
        this.temperaturaRepository = temperaturaRepository;
        this.eventoRepository = eventoRepository;
        this.fornoCompartilhadoRepository = fornoCompartilhadoRepository;
    }

    private Usuario buscarUsuarioLogado(String email) {
        return  usuarioRepository.findByEmail(email).orElseThrow(
                () -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado " + email
                )
        );
    }

    public boolean verificarAcessoAoForno(Forno forno, Usuario usuario) {

        if (forno.getUsuario() != null && forno.getUsuario().getId().equals(usuario.getId())) {
            return true; // O usuário é o proprietário do forno
        }

        Optional<FornoCompartilhado> fornoCompartilhado = fornoCompartilhadoRepository.findByFornoAndUsuario(forno, usuario);

        if (fornoCompartilhado.isPresent() && fornoCompartilhado.get().getStatus() == com.rafael.monitor_forno.enums.compartilhamento.StatusC.ACEITO) {
            return true; // O usuário tem acesso compartilhado ao forno
        }

        return false;
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
        forno.setAtivo(false);
        forno.setMuted(false);
        forno.setNome(dto.getNome() != null ? dto.getNome() : "Forno sem nome");

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

        int linhasAfetadas = fornoRepository.reivindicarSeNaoReivindicado(dto.getSerialNumber());

        if (linhasAfetadas == 0) {
            throw new CredencialJaCadastradaException(
                    "Forno já reivindicado"
            );
        }

        forno.setReivindicado(true);
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

        return LoginResponseDTO.builder()
                .id(forno.getId())
                .token(jwtService.gerarToken(forno.getSerialNumber(), "FORNO", "FORNO"))
                .build();
    }

    @Transactional
    public void vincularFornoaoUsuario(VincularFornoDTO dto, String email) {
        Usuario usuario = buscarUsuarioLogado(email);

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
        forno.setNome(dto.getNome() != null ? dto.getNome() : forno.getNome());
        forno.setMuted(false);
        fornoRepository.save(forno);
    }

    @Transactional
    public void desvincularFornoDoUsuario(String email, String serialNumber) {
        Usuario usuario =buscarUsuarioLogado(email);

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado " + serialNumber
                        )
                );

        if (forno.getUsuario() == null || !forno.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Você não tem permissão para desvincular este forno");
        }

        temperaturaRepository.deleteAllBySessaoFornoId(forno.getId());
        eventoRepository.deleteAllBySessaoFornoId(forno.getId());
        sessaoRepository.deleteAllByFornoId(forno.getId());

        forno.setUsuario(null);
        forno.setAtivo(false);
        forno.setMuted(false);
        forno.setPinSeguranca(gerarPinSeguranca());
        fornoRepository.save(forno);
    }

    public FornoResponseDTO atualizarForno(FornoAtualizarDTO dto, String email, String serialNumber) {
        Usuario usuario = buscarUsuarioLogado(email);

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado " + serialNumber
                        )
                );

        if (forno.getUsuario() == null) {
            throw new AcessoNegadoException("Você não tem permissão para atualizar este forno");
        }

        if (!forno.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Você não tem permissão para atualizar este forno");
        }

        if (dto.getNome() != null && !dto.getNome().trim().isEmpty()) {
            forno.setNome(dto.getNome());
        }
        fornoRepository.save(forno);

        return toFornoResponseDTO(forno);
    }

    public List<FornoResponseDTO> buscarMeusFornos(String email) {
        Usuario usuario = buscarUsuarioLogado(email);

        List<Forno> fornosProprios = fornoRepository.findByUsuario(usuario);
        List<FornoCompartilhado> fornosCompartilhados = fornoCompartilhadoRepository.findAllByUsuarioAndStatus(usuario, com.rafael.monitor_forno.enums.compartilhamento.StatusC.ACEITO);

        List<Forno> todosFornos = new java.util.ArrayList<>(fornosProprios);

        for (FornoCompartilhado compartilhado : fornosCompartilhados) {
            todosFornos.add(compartilhado.getForno());
        }

        return  todosFornos
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
                .reivindicado(forno.isReivindicado())
                .muted(forno.isMuted())
                .build();
    }

}
