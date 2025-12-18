package com.leitner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application Leitner System.
 * Application d'apprentissage basée sur le système de répétition espacée de Leitner.
 */
@SpringBootApplication
public class LeitnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeitnerApplication.class, args);
    }
}
