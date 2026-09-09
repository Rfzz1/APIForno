    package com.rafael.monitor_forno.controller;

    import com.rafael.monitor_forno.dto.FornoCompartilhadoRequestDTO;
    import com.rafael.monitor_forno.dto.FornoCompartilhadoResponseDTO;
    import com.rafael.monitor_forno.enums.compartilhamento.StatusC;
    import com.rafael.monitor_forno.service.FornoCompartilhadoService;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    import java.util.UUID;

    @RestController
    @RequestMapping("/forno-compartilhado")
    public class FornoCompartilhadoController {

        private final FornoCompartilhadoService fornoCompartilhadoService;

        public FornoCompartilhadoController(FornoCompartilhadoService fornoCompartilhadoService) {
            this.fornoCompartilhadoService = fornoCompartilhadoService;
        }

        @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
        @PostMapping
        public ResponseEntity<Void> enviarConvite (@RequestBody FornoCompartilhadoRequestDTO dto) {

            String emailLoggedUser = SecurityContextHolder.getContext().getAuthentication().getName();

            fornoCompartilhadoService.enviarConvite(dto.getEmail(), dto.getSerialNumber(), emailLoggedUser);
            return ResponseEntity.ok().build();
        }

        @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
        @DeleteMapping
        public ResponseEntity<Void> removerConvite (@RequestBody FornoCompartilhadoRequestDTO dto) {
            fornoCompartilhadoService.excluirConvite(dto.getEmail(), dto.getSerialNumber());
            return ResponseEntity.ok().build();
        }

        @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
        @PutMapping("/aceitar")
        public ResponseEntity<FornoCompartilhadoResponseDTO> aceitarConvite (@RequestBody FornoCompartilhadoRequestDTO dto) {
            return ResponseEntity.status(HttpStatus.CREATED).body(fornoCompartilhadoService.aceitarConvite(dto.getEmail(), dto.getSerialNumber()));
        }

        @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
        @PutMapping("/recusar")
        public ResponseEntity<FornoCompartilhadoResponseDTO> recusarConvite (@RequestBody FornoCompartilhadoRequestDTO dto) {
            return ResponseEntity.status(HttpStatus.CREATED).body(fornoCompartilhadoService.recusarConvite(dto.getEmail(), dto.getSerialNumber()));
        }

        @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
        @GetMapping("/listar")
        public ResponseEntity<?> listarConvites (@RequestParam StatusC status) {

            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            return ResponseEntity.ok(fornoCompartilhadoService.findAllByUsuarioAndStatus(email, status));
        }

    }
