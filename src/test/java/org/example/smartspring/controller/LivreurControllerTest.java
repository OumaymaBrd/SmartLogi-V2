package org.example.smartspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smartspring.dto.colis.UpdateColisDTO;
import org.example.smartspring.dto.livreur.ConsulterColisAffecterDTO;
import org.example.smartspring.entities.Colis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.service.ColisService;
import org.example.smartspring.security.service.JwtService;
import org.example.smartspring.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivreurController.class)
@WithMockUser
class LivreurControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ColisService service;
    @MockBean private JwtService jwtService;
    @MockBean private CustomUserDetailsService customUserDetailsService;

    @Test
    void testGetColisAffectes_found() throws Exception {
        when(service.getColisByLivreurIdAndStatut(anyString(), any())).thenReturn(List.of(new ConsulterColisAffecterDTO()));

        mockMvc.perform(get("/livreur/colis-affecter/123"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateColis() throws Exception {
        UpdateColisDTO dto = new UpdateColisDTO();
        dto.setStatut(StatutColis.LIVRE);

        when(service.updateColis(any(), anyString())).thenReturn(new Colis());

        mockMvc.perform(put("/livreur/updateStatutColis/C123")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}