package com.rafael.monitor_forno.service;

import com.rafael.monitor_forno.config.FornoDetails;
import com.rafael.monitor_forno.database.model.Forno;
import com.rafael.monitor_forno.database.repository.FornoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class FornoDetailsService implements UserDetailsService {

    private final FornoRepository fornoRepository;

    public FornoDetailsService(FornoRepository fornoRepository) {
        this.fornoRepository = fornoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String serialNumber) {
        Forno forno = fornoRepository.findBySerialNumber(serialNumber)
                .orElseThrow(
                        () -> new RuntimeException("Forno não encontrado")
                );

        return new FornoDetails(forno);
    }
}
