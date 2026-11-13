package com.rafael.monitor_forno.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import com.rafael.monitor_forno.dto.EventoRequestDTO;
import com.rafael.monitor_forno.dto.SessaoAtualizarDTO;
import com.rafael.monitor_forno.dto.TelemetriaRequestDTO;
import com.rafael.monitor_forno.dto.TemperaturaRequestDTO;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import com.rafael.monitor_forno.service.EventoService;
import com.rafael.monitor_forno.service.SessaoService;
import com.rafael.monitor_forno.service.TelemetriaService;
import com.rafael.monitor_forno.service.TemperaturaService;
import com.rafael.monitor_forno.websocket.FornoSessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Slf4j
@Component
public class FornoWebSocketHandler extends TextWebSocketHandler {

    private final FornoSessionRegistry fornoSessionRegistry;
    private final TelemetriaService telemetriaService;
    private final EventoService eventoService;
    private final FornoRepository fornoRepository;
    private final TemperaturaService temperaturaService;
    private final SessaoService sessaoService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader leitorTelemetria;
    private final ObjectReader leitorEvento;
    private final ObjectReader leitorTemperatura;
    private final ObjectReader leitorSessao;

    public FornoWebSocketHandler(FornoSessionRegistry fornoSessionRegistry, TelemetriaService telemetriaService, EventoService eventoService, FornoRepository fornoRepository, TemperaturaService temperaturaService, SessaoService sessaoService) {
        this.fornoSessionRegistry = fornoSessionRegistry;
        this.telemetriaService = telemetriaService;
        this.eventoService = eventoService;
        this.fornoRepository = fornoRepository;
        this.temperaturaService = temperaturaService;
        this.sessaoService = sessaoService;

        this.leitorTelemetria = mapper.readerFor(TelemetriaRequestDTO.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.leitorEvento = mapper.readerFor(EventoRequestDTO.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.leitorTemperatura = mapper.readerFor(TemperaturaRequestDTO.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.leitorSessao = mapper.readerFor(SessaoAtualizarDTO.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    protected void handleTextMessage(WebSocketSession sessao, TextMessage mensagem) {

        UUID fornoId = (UUID) sessao.getAttributes().get("fornoId");
        if (fornoId == null) {
            log.warn("[WS] mensagem de sessão sem serialNumber, ignorada");
            return;
        }

        try {
            JsonNode raiz = mapper.readTree(mensagem.getPayload());
            String tipoMensagem = raiz.path("msg").asText("");

            switch (tipoMensagem) {
                case "TELEMETRIA" -> telemetriaService.registrar(leitorTelemetria.readValue(raiz), fornoId);
                case "EVENTO"     -> eventoService.registrarEvento(leitorEvento.readValue(raiz), fornoId);
                // Mesma regra do POST /v1/temperaturas (que continua existindo para o plano B HTTP)
                case "TEMPERATURA" -> {
                    TemperaturaRequestDTO dto = leitorTemperatura.readValue(raiz);
                    dto.setFornoId(fornoId);
                    temperaturaService.registrarLeitura(dto, fornoId);
                }
                // Mesma regra do PUT /v1/sessoes/{id}/atualizar
                case "SESSAO" -> sessaoService.atualizarSessao(
                        UUID.fromString(raiz.path("sessaoId").asText()),
                        fornoId,
                        leitorSessao.readValue(raiz));
                default           -> log.warn("[WS] tipo de mensagem desconhecido do forno {}: '{}'", fornoId, tipoMensagem);
            }
        } catch (Exception e) {
            // Uma mensagem ruim não pode derrubar a conexão do forno
            log.warn("[WS] falha ao processar mensagem do forno {}: {}", fornoId, e.getMessage());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession sessao) throws Exception {

        String serialNumber = (String) sessao.getAttributes().get("serialNumber");

        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Forno não encontrado"));

        sessao.getAttributes().put("fornoId", forno.getId());

        WebSocketSession sessaoSegura = new ConcurrentWebSocketSessionDecorator(
                sessao,
                2000,
                1024 * 5
        );

        fornoSessionRegistry.registrar(serialNumber, sessaoSegura);
        sessaoSegura.sendMessage(new TextMessage("Conexão estabelecida com sucesso. Forno: " + serialNumber));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession sessao, CloseStatus status) {

        String serialNumber =  (String) sessao.getAttributes().get("serialNumber");
        if (serialNumber != null) {
            fornoSessionRegistry.remover(serialNumber, sessao);
        }
        log.info("[WS] Forno Desconectado: {} - {}", serialNumber, status);


    }

}