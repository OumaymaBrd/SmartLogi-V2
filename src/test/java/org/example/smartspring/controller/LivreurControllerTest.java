package org.example.smartspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smartspring.dto.colis.UpdateColisDTO;
import org.example.smartspring.dto.livreur.ConsulterColisAffecterDTO;
import org.example.smartspring.entities.Colis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.mapper.ColisMapper;
import org.example.smartspring.repository.ColisRepository;
import org.example.smartspring.service.ColisService;
import org.example.smartspring.security.service.JwtService;
import org.example.smartspring.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivreurController.class)
@WithMockUser // Simule un utilisateur authentifié pour passer la sécurité
class LivreurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ColisService service;

    @MockBean
    private ColisRepository repository;

    @MockBean
    private ColisMapper mapper;

    // --- MOCKS INDISPENSABLES POUR DÉBLOQUER JENKINS ---
    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    // ---------------------------------------------------

    @Test
    void testGetColisAffectes_notFound() throws Exception {
        String livreurId = "123";
        when(service.getColisByLivreurIdAndStatut(eq(livreurId), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/livreur/colis-affecter/" + livreurId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Aucune affectation Colis trouvée"));
    }

    @Test
    void testGetColisAffectes_found() throws Exception {
        String livreurId = "123";
        ConsulterColisAffecterDTO dto = new ConsulterColisAffecterDTO();
        List<ConsulterColisAffecterDTO> liste = List.of(dto);

        when(service.getColisByLivreurIdAndStatut(eq(livreurId), any()))
                .thenReturn(liste);

        mockMvc.perform(get("/livreur/colis-affecter/" + livreurId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateColis() throws Exception {
        String colisId = "C123";
        UpdateColisDTO dto = new UpdateColisDTO();
        dto.setStatut(StatutColis.LIVRE);
        dto.setLivreurId("L1");
        dto.setLivreur_id_livree("L2");

        Colis updatedColis = new Colis();
        updatedColis.setStatut(StatutColis.LIVRE);

        when(service.updateColis(any(UpdateColisDTO.class), eq(colisId))).thenReturn(updatedColis);

        mockMvc.perform(put("/livreur/updateStatutColis/" + colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Le colis " + colisId + " a été mis à jour avec succès")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Statut : LIVRE")));

        verify(service, times(1)).updateColis(any(UpdateColisDTO.class), eq(colisId));
    }
}