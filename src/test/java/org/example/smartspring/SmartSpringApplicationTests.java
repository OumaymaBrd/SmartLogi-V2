package org.example.smartspring;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Désactivé car nécessite une base de données réelle pour charger le contexte complet")
class SmartSpringApplicationTests {
    @Test
    void contextLoads() { }
}