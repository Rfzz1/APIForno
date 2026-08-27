package com.rafael.monitor_forno.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.rafael.monitor_forno.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class FireBaseConfig {

    FileInputStream fis;

    @Value("${caminho}")
    private String path;

    @PostConstruct
    public void inicializarFirebase() throws IOException {
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new RecursoNaoEncontradoException(e.getMessage());
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(fis);
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();

        FirebaseApp.initializeApp(options);
    }

}
