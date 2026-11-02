    package com.rafael.monitor_forno.controller;

    import com.rafael.monitor_forno.dto.FornoCompartilhadoRequestDTO;
    import com.rafael.monitor_forno.dto.FornoCompartilhadoResponseDTO;
    import com.rafael.monitor_forno.enums.compartilhamento.StatusC;
    import com.rafael.monitor_forno.service.FornoCompartilhadoService;
    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/forno-compartilhado")
    public class FornoCompartilhadoController {

        private final FornoCompartilhadoService fornoCompartilhadoService;

        public FornoCompartilhadoController(FornoCompartilhadoService fornoCompartilhadoService) {
            this.fornoCompartilhadoService = fornoCompartilhadoService;
        }

        @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
        @PostMapping
        public ResponseEntity<Void> enviarConvite (@Valid @RequestBody FornoCompartilhadoRequestDTO dto, Authentication authentication) {

            fornoCompartilhadoService.enviarConvite(dto.getEmail(), dto.getFornoId(), authentication.getName());
            return ResponseEntity.ok().build();
        }

        @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
        @DeleteMapping
        public ResponseEntity<Void> removerConvite (@Valid @RequestBody FornoCompartilhadoRequestDTO dto, Authentication authentication) {
            fornoCompartilhadoService.excluirConvite(dto.getEmail(), authentication.getName(), dto.getFornoId());
            return ResponseEntity.ok().build();
        }

        @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
        @PutMapping("/aceitar")
        public ResponseEntity<FornoCompartilhadoResponseDTO> aceitarConvite (@Valid @RequestBody FornoCompartilhadoRequestDTO dto, Authentication authentication) {

            return ResponseEntity.status(HttpStatus.OK).body(fornoCompartilhadoService.aceitarConvite(authentication.getName(), dto.getFornoId()));
        }

        @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
        @PutMapping("/recusar")
        public ResponseEntity<FornoCompartilhadoResponseDTO> recusarConvite (@Valid @RequestBody FornoCompartilhadoRequestDTO dto, Authentication authentication) {

            return ResponseEntity.status(HttpStatus.OK).body(fornoCompartilhadoService.recusarConvite(authentication.getName(), dto.getFornoId()));
        }

        @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
        @GetMapping("/listar")
        public ResponseEntity<?> listarConvites (@RequestParam StatusC status, Authentication authentication) {

            return ResponseEntity.ok(fornoCompartilhadoService.findAllByUsuarioAndStatus(authentication.getName(), status));
        }

    }
