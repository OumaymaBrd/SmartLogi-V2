package org.example.smartspring;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartSpringApplication {
    public static void main(String[] args) {
        // Cette configuration ne plante pas si le .env est absent (cas Docker)
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Récupération avec priorité : .env > Variable Système
        String gmailUser = dotenv.get("GMAIL_USERNAME") != null ?
                dotenv.get("GMAIL_USERNAME") : System.getenv("GMAIL_USERNAME");
        String gmailPass = dotenv.get("GMAIL_APP_PASSWORD") != null ?
                dotenv.get("GMAIL_APP_PASSWORD") : System.getenv("GMAIL_APP_PASSWORD");

        if (gmailUser != null) System.setProperty("GMAIL_USERNAME", gmailUser);
        if (gmailPass != null) System.setProperty("GMAIL_APP_PASSWORD", gmailPass);

        SpringApplication.run(SmartSpringApplication.class, args);
    }
}