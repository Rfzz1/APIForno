package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.model.Sessao;
import com.rafael.monitor_forno.database.model.Temperatura;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.database.repository.SessaoRepository;
import com.rafael.monitor_forno.database.repository.TemperaturaRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.TemperaturaDTO;
import com.rafael.monitor_forno.dto.TemperaturaRequestDTO;
import com.rafael.monitor_forno.exception.AcessoNegadoException;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.mappers.TemperaturaMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class    TemperaturaService {

    private static final double THRESHOLD = 2.0;
    private final TemperaturaRepository temperaturaRepository;
    private final TemperaturaMapper temperaturaMapper;
    private final SessaoRepository sessaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FornoRepository fornoRepository;

    public TemperaturaService(TemperaturaRepository temperaturaRepository, TemperaturaMapper temperaturaMapper, SessaoRepository sessaoRepository, UsuarioRepository usuarioRepository, FornoRepository fornoRepository) {
        this.temperaturaRepository = temperaturaRepository;
        this.temperaturaMapper = temperaturaMapper;
        this.sessaoRepository = sessaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fornoRepository = fornoRepository;
    }

    public boolean registrarLeitura(TemperaturaRequestDTO dto, UUID fornoId) {

        Forno forno = fornoRepository.findById(fornoId)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Forno não encontrado: " + dto.getFornoId()
                        )
                );

        Double diferenca = Math.abs(dto.getTemperaturaAtual() - dto.getTemperaturaUltima());

        if (diferenca <= THRESHOLD) {
            return false;
        }

        Sessao sessao = sessaoRepository.findByIdAndForno(dto.getSessaoId(), forno)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Sessao não pertence ao dispositivo " + forno.getNome()
                        )
                );

        Temperatura temperatura = new Temperatura();
        temperatura.setTemperaturaAtual(dto.getTemperaturaAtual());
        temperatura.setTemperaturaUltima(dto.getTemperaturaUltima());
        temperatura.setRegistradoEm(LocalDateTime.now());
        temperatura.setSessao(sessao);
        temperatura.setForno(forno);

        temperaturaRepository.save(temperatura);

        return true;
    }

    public void deleteById(UUID id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        Temperatura temperatura = temperaturaRepository.findByIdAndUsuario(id,  usuario)
                .orElseThrow(
                        () -> new AcessoNegadoException(
                                "Temperatura não pertence ao usuário logado"
                        )
                );
        temperaturaRepository.delete(temperatura);
    }

    public List<TemperaturaDTO> findAllByUsuario(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Usuário não encontrado " + email
                        )
                );

        return temperaturaRepository.findAllByUsuario(usuario)
                .stream()
                .map(temperaturaMapper::toTemperaturaDTO)
                .toList();
    }

    public TemperaturaDTO findByDate(LocalDateTime registradoEm) {
        Temperatura temperatura = temperaturaRepository.findByRegistradoEm(registradoEm)
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException(
                                "Data não encontrada: " + registradoEm
                        )
                );
        return temperaturaMapper.toTemperaturaDTO(temperatura);
    }

}
