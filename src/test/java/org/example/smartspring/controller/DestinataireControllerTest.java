package org.example.smartspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smartspring.dto.destinataire.AddDestinataireDTO;
import org.example.smartspring.dto.destinataire.UpdateDestinataireDTO;
import org.example.smartspring.dto.destinataire.DestinataireDTO;
import org.example.smartspring.service.DestinataireService;
import org.example.smartspring.security.service.JwtService; // Import à vérifier
import org.example.smartspring.security.service.CustomUserDetailsService; // Import à vérifier

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DestinataireController.class)
@WithMockUser(authorities = {
        "DESTINATAIRE_CREATE",
        "DESTINATAIRE_READ",
        "DESTINATAIRE_UPDATE",
        "DESTINATAIRE_DELETE"
})
class DestinataireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DestinataireService destinataireService;

    // --- AJOUT DES MOCKS DE SÉCURITÉ POUR DÉBLOQUER JENKINS ---
    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    // ---------------------------------------------------------

    @Autowired
    private ObjectMapper objectMapper;

    // ======================
    // CREATE
    // ======================
    @Test
    void testCreateDestinataire() throws Exception {
        AddDestinataireDTO input = AddDestinataireDTO.builder()
                .nom("Oumayma")
                .prenom("Bramid")
                .adresse("Settat Center")
                .telephone("0612345678")
                .email("test@example.com")
                .ville("Settat")
                .build();

        DestinataireDTO output = DestinataireDTO.builder()
                .nom("Oumayma")
                .prenom("Bramid")
                .email("test@example.com")
                .telephone("0612345678")
                .adresse("Settat Center")
                .ville("Settat")
                .build();

        when(destinataireService.createDestinataire(any()))
                .thenReturn(output);

        mockMvc.perform(post("/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Oumayma"));
    }

    // ======================
    // GET ALL
    // ======================
    @Test
    void testGetAllDestinataires() throws Exception {
        DestinataireDTO dto = DestinataireDTO.builder()
                .nom("Oumayma")
                .prenom("Bramid")
                .email("email@test.com")
                .telephone("0612345678")
                .adresse("Settat")
                .ville("Settat")
                .build();

        Page<DestinataireDTO> page =
                new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(destinataireService.getAllDestinataires(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/destinataires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nom").value("Oumayma"));
    }

    // ======================
    // GET BY ID
    // ======================
    @Test
    void testGetDestinataireById() throws Exception {
        DestinataireDTO dto = DestinataireDTO.builder()
                .nom("Oumayma")
                .prenom("Bramid")
                .email("email@test.com")
                .telephone("0612345678")
                .adresse("Settat")
                .ville("Settat")
                .build();

        when(destinataireService.getDestinataireById("123"))
                .thenReturn(dto);

        mockMvc.perform(get("/destinataires/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Bramid"));
    }

    // ======================
    // UPDATE
    // ======================
    @Test
    void testUpdateDestinataire() throws Exception {
        UpdateDestinataireDTO update = UpdateDestinataireDTO.builder()
                .nom("Ouma")
                .prenom("Bramid")
                .telephone("0612345678")
                .email("email@test.com")
                .adresse("New Adresse")
                .build();

        DestinataireDTO result = DestinataireDTO.builder()
                .nom("Ouma")
                .prenom("Bramid")
                .email("email@test.com")
                .telephone("0612345678")
                .adresse("New Adresse")
                .ville("Settat")
                .build();

        when(destinataireService.updateDestinataire(eq("111"), any()))
                .thenReturn(result);

        mockMvc.perform(put("/destinataires/111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Ouma"));
    }

    // ======================
    // DELETE
    // ======================
    @Test
    void testDeleteDestinataire() throws Exception {
        doNothing().when(destinataireService).deleteDestinataire("222");

        mockMvc.perform(delete("/destinataires/222"))
                .andExpect(status().isNoContent());
    }

    // ======================
    // SEARCH
    // ======================
    @Test
    void testSearchDestinataires() throws Exception {
        DestinataireDTO dto = DestinataireDTO.builder()
                .nom("Oumayma")
                .prenom("Bramid")
                .email("ouma@test.com")
                .telephone("0612345678")
                .adresse("123 Rue Exemple")
                .ville("Settat")
                .build();

        Page<DestinataireDTO> page =
                new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(destinataireService.searchDestinataires(eq("ouma"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/destinataires/search")
                        .param("keyword", "ouma"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nom").value("Oumayma"));
    }


    @Test
    void testGetDestinatairesByVille() throws Exception {
        DestinataireDTO dto = DestinataireDTO.builder()
                .nom("Ouma")
                .prenom("Br")
                .ville("Settat")
                .build();

        when(destinataireService.getDestinatairesByVille("Settat"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/destinataires/ville/Settat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ville").value("Settat"));
    }
}