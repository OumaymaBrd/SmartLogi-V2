package org.example.smartspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smartspring.dto.destinataire.AddDestinataireDTO;
import org.example.smartspring.dto.destinataire.UpdateDestinataireDTO;
import org.example.smartspring.dto.destinataire.DestinataireDTO;
import org.example.smartspring.service.DestinataireService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class DestinataireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DestinataireService destinataireService;

    @Autowired
    private ObjectMapper objectMapper;


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

        Mockito.when(destinataireService.createDestinataire(any()))
                .thenReturn(output);

        mockMvc.perform(post("/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Oumayma"));
    }


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

        Mockito.when(destinataireService.getAllDestinataires(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/destinataires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nom").value("Oumayma"));
    }


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

        Mockito.when(destinataireService.getDestinataireById("123"))
                .thenReturn(dto);

        mockMvc.perform(get("/destinataires/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Bramid"));
    }


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
                .prenom("B")
                .email("email@test.com")
                .telephone("0612345678")
                .adresse("New Adresse")
                .ville("Settat")
                .build();

        Mockito.when(destinataireService.updateDestinataire(eq("111"), any()))
                .thenReturn(result);

        mockMvc.perform(put("/destinataires/111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Ouma"));
    }


    @Test
    void testDeleteDestinataire() throws Exception {
        Mockito.doNothing().when(destinataireService).deleteDestinataire("222");

        mockMvc.perform(delete("/destinataires/222"))
                .andExpect(status().isNoContent());
    }


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

        // Créer une page avec ce DTO
        Page<DestinataireDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        // Mocker le service
        Mockito.when(destinataireService.searchDestinataires(eq("ouma"), any(Pageable.class)))
                .thenReturn(page);

        // Appel de l'endpoint et vérifications
        mockMvc.perform(get("/destinataires/search")
                        .param("keyword", "ouma")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nom").value("Oumayma"))
                .andExpect(jsonPath("$.content[0].prenom").value("Bramid"))
                .andExpect(jsonPath("$.content[0].ville").value("Settat"));
    }



    @Test
    void testGetDestinatairesByVille() throws Exception {
        DestinataireDTO dto = DestinataireDTO.builder()
                .nom("Ouma")
                .prenom("Br")
                .ville("Settat")
                .build();

        Mockito.when(destinataireService.getDestinatairesByVille("Settat"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/destinataires/ville/Settat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ville").value("Settat"));
    }
}
