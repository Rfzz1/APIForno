package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.database.model.FotoPerfil;
import com.rafael.monitor_forno.database.model.Usuario;
import com.rafael.monitor_forno.database.repository.FotoPerfilRepository;
import com.rafael.monitor_forno.database.repository.UsuarioRepository;
import com.rafael.monitor_forno.dto.FotoPerfilRequestDTO;
import com.rafael.monitor_forno.dto.FotoPerfilResponseDTO;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FotoPerfilService {

    private final FotoPerfilRepository fotoPerfilRepository;
    private final UsuarioRepository usuarioRepository;

    public FotoPerfilService(FotoPerfilRepository fotoPerfilRepository, UsuarioRepository usuarioRepository) {
        this.fotoPerfilRepository = fotoPerfilRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public FotoPerfilResponseDTO setProfileImage(FotoPerfilRequestDTO dto, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o email: " + email));

        if (fotoPerfilRepository.findByUsuario(usuario).isPresent()) {
            throw new IllegalArgumentException("O usuário já possui uma foto de perfil. Use a rota de atualização.");
        }

        FotoPerfil fotoPerfil = new FotoPerfil();
        fotoPerfil.setFotoBase64(dto.getFotoBase64());
        fotoPerfil.setUsuario(usuario);
        return toFotoPerfilResponseDTO(fotoPerfilRepository.save(fotoPerfil));
    }

    public void deleteProfileImage(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o email: " + email));

        FotoPerfil fotoPerfil = fotoPerfilRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto de perfil não encontrada para o usuário: " + email));

        fotoPerfilRepository.delete(fotoPerfil);
    }

    public FotoPerfilResponseDTO changeProfileImage(FotoPerfilRequestDTO dto, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o email: " + email));

        FotoPerfil fotoPerfil = fotoPerfilRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto de perfil não encontrada para o usuário: " + email));

        fotoPerfil.setFotoBase64(dto.getFotoBase64());
        return toFotoPerfilResponseDTO(fotoPerfilRepository.save(fotoPerfil));
    }

    public FotoPerfilResponseDTO getFotoPerfilByUsuario(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o email: " + email));

        FotoPerfil fotoPerfil = fotoPerfilRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto de perfil não encontrada para o usuário: " + email));

        return toFotoPerfilResponseDTO(fotoPerfil);
    }

    public FotoPerfilResponseDTO findById(UUID id) {
        FotoPerfil fotoPerfil = fotoPerfilRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Foto de perfil não encontrada com o ID: " + id));
        return toFotoPerfilResponseDTO(fotoPerfil);
    }

    private FotoPerfilResponseDTO toFotoPerfilResponseDTO(FotoPerfil fotoPerfil) {
        return FotoPerfilResponseDTO.builder()
                .id(fotoPerfil.getId())
                .fotoBase64(fotoPerfil.getFotoBase64())
                .build();
    }

}
