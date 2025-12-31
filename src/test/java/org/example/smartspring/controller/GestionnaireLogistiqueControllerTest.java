package org.example.smartspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smartspring.dto.gestionnairelogistique.AddGestionnaireLogistqueDTO;
import org.example.smartspring.dto.gestionnairelogistique.GestionnaireLogistiqueDTO;
import org.example.smartspring.dto.colis.UpdateColisDTO;
import org.example.smartspring.entities.Colis;
import org.example.smartspring.service.ColisService;
import org.example.smartspring.service.GestionnaireLogistiqueService;
import org.example.smartspring.security.service.JwtService; // Import à vérifier selon votre package
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GestionnaireLogistiqueController.class)
class GestionnaireLogistiqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GestionnaireLogistiqueService gestionnaireService;

    @MockBean
    private ColisService colisService;

    // Mocks indispensables pour Spring Security / JWT
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private AddGestionnaireLogistqueDTO dto;
    private GestionnaireLogistiqueDTO gDto;
    private UpdateColisDTO updateColisDTO;
    private Colis colis;

    @BeforeEach
    void setUp() {
        dto = AddGestionnaireLogistqueDTO.builder()
                .nom("Oumaima")
                .prenom("B")
                .email("oumaima@example.com")
                .telephone("0600000000")
                .build();

        gDto = GestionnaireLogistiqueDTO.builder()
                .id("1")
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .telephone(dto.getTelephone())
                .build();

        updateColisDTO = new UpdateColisDTO();
        updateColisDTO.setStatut(null);
        updateColisDTO.setLivreurId("livreur1");
        updateColisDTO.setLivreur_id_livree(null);

        colis = new Colis();
        colis.setStatut(null);
        colis.setId("C123");
    }

    @Test
    void testCreate_ShouldReturnCreated() throws Exception {
        Mockito.when(gestionnaireService.create(any(AddGestionnaireLogistqueDTO.class)))
                .thenReturn(gDto);

        mockMvc.perform(post("/gestionnairelogistique")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    // Adapté pour ignorer les problèmes de sécurité si nécessaire
                });
    }

    @Test
    void testAffecterLivreur_ShouldReturnBadRequest_WhenNoLivreurGiven() throws Exception {
        mockMvc.perform(put("/gestionnairelogistique/affecter-livreur")
                        .param("numero_colis", "C123")
                        .param("idGestionnaire", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateColis_withLivreurOnly_ShouldReturnOK() throws Exception {
        Mockito.when(colisService.updateColis(any(UpdateColisDTO.class), eq("C123")))
                .thenReturn(colis);

        mockMvc.perform(put("/gestionnairelogistique/updateStatutColis/C123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateColisDTO)))
                .andExpect(status().isOk());
    }
}