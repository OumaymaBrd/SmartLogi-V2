package org.example.smartspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smartspring.dto.destinataire.*;
import org.example.smartspring.service.DestinataireService;
import org.example.smartspring.security.service.JwtService;
import org.example.smartspring.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DestinataireController.class)
@WithMockUser(authorities = {"DESTINATAIRE_CREATE", "DESTINATAIRE_READ", "DESTINATAIRE_UPDATE", "DESTINATAIRE_DELETE"})
class DestinataireControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private DestinataireService destinataireService;
    @MockBean private JwtService jwtService;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    @Test
    void testCreateDestinataire() throws Exception {
        AddDestinataireDTO input = AddDestinataireDTO.builder().nom("Oumayma").build();
        DestinataireDTO output = DestinataireDTO.builder().nom("Oumayma").build();
        when(destinataireService.createDestinataire(any())).thenReturn(output);

        mockMvc.perform(post("/destinataires")
                        .with(csrf()) // Correction 403
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetAllDestinataires() throws Exception {
        Page<DestinataireDTO> page = new PageImpl<>(List.of(new DestinataireDTO()));
        when(destinataireService.getAllDestinataires(any())).thenReturn(page);
        mockMvc.perform(get("/destinataires")).andExpect(status().isOk());
    }

    @Test
    void testUpdateDestinataire() throws Exception {
        DestinataireDTO result = new DestinataireDTO();
        when(destinataireService.updateDestinataire(anyString(), any())).thenReturn(result);
        mockMvc.perform(put("/destinataires/111")
                        .with(csrf()) // Correction 403
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateDestinataireDTO())))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteDestinataire() throws Exception {
        doNothing().when(destinataireService).deleteDestinataire(anyString());
        mockMvc.perform(delete("/destinataires/222").with(csrf())) // Correction 403
                .andExpect(status().isNoContent());
    }
}