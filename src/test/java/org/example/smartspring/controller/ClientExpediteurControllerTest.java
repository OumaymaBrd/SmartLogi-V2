package org.example.smartspring.controller;

import org.example.smartspring.dto.clientexpediteur.*;
import org.example.smartspring.service.ClientExpediteurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ClientExpediteurControllerTest {

    @InjectMocks
    private ClientExpediteurController controller;

    @Mock
    private ClientExpediteurService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private AddClientExpediteurDTO createValidAddDTO() {
        return AddClientExpediteurDTO.builder()
                .nom("Bramid")
                .prenom("Oumayma")
                .email("oumayma@test.com")
                .adresse("123 Rue de Test")
                .telephone("0612345678")
                .build();
    }

    private UpdateClientExpediteurDTO createValidUpdateDTO() {
        return UpdateClientExpediteurDTO.builder()
                .nom("BramidUpdated")
                .prenom("OumaymaUpdated")
                .email("updated@test.com")
                .adresse("456 Rue Updated")
                .telephone("0698765432")
                .build();
    }

    private ClientExpediteurDTO createClientExpediteurDTO() {
        return ClientExpediteurDTO.builder()
                .nom("Bramid")
                .prenom("Oumayma")
                .email("oumayma@test.com")
                .adresse("123 Rue de Test")
                .telephone("0612345678")
                .ville("Casablanca")
                .build();
    }

    @Test
    void testCreateClientExpediteur() {
        AddClientExpediteurDTO dto = createValidAddDTO();
        ClientExpediteurDTO responseDto = createClientExpediteurDTO();

        when(service.createClientExpediteur(dto)).thenReturn(responseDto);

        ResponseEntity<ClientExpediteurDTO> response = controller.createClientExpediteur(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getNom()).isEqualTo("Bramid");

        verify(service, times(1)).createClientExpediteur(dto);
    }

    @Test
    void testGetAllClientsExpediteurs_nonEmpty() {
        ClientExpediteurDTO dto = createClientExpediteurDTO();
        Page<ClientExpediteurDTO> page = new PageImpl<>(List.of(dto));

        when(service.getAllClientsExpediteurs(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClientExpediteurDTO>> response = controller.getAllClientsExpediteurs(Pageable.unpaged());

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    void testGetAllClientsExpediteurs_empty() {
        Page<ClientExpediteurDTO> page = Page.empty();

        when(service.getAllClientsExpediteurs(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClientExpediteurDTO>> response = controller.getAllClientsExpediteurs(Pageable.unpaged());

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    void testGetClientExpediteurById() {
        String id = "CL123";
        ClientExpediteurDTO dto = createClientExpediteurDTO();

        when(service.getClientExpediteurById(id)).thenReturn(dto);

        ResponseEntity<ClientExpediteurDTO> response = controller.getClientExpediteurById(id);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getNom()).isEqualTo("Bramid");
    }

    @Test
    void testUpdateClientExpediteur() {
        String id = "CL123";
        UpdateClientExpediteurDTO dto = createValidUpdateDTO();
        ClientExpediteurDTO responseDto = createClientExpediteurDTO();
        responseDto.setNom("BramidUpdated");

        when(service.updateClientExpediteur(id, dto)).thenReturn(responseDto);

        ResponseEntity<ClientExpediteurDTO> response = controller.updateClientExpediteur(id, dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getNom()).isEqualTo("BramidUpdated");

        verify(service, times(1)).updateClientExpediteur(id, dto);
    }

    @Test
    void testDeleteClientExpediteur() {
        String id = "CL123";

        doNothing().when(service).deleteClientExpediteur(id);

        ResponseEntity<?> response = controller.deleteClientExpediteur(id);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("Suppression Avec Succes");

        verify(service, times(1)).deleteClientExpediteur(id);
    }

    @Test
    void testSearchClientsExpediteurs() {
        String keyword = "Bramid";
        ClientExpediteurDTO dto = createClientExpediteurDTO();
        Page<ClientExpediteurDTO> page = new PageImpl<>(List.of(dto));

        when(service.searchClientsExpediteurs(eq(keyword), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ClientExpediteurDTO>> response = controller.searchClientsExpediteurs(keyword, Pageable.unpaged());

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getContent()).hasSize(1);

        verify(service, times(1)).searchClientsExpediteurs(eq(keyword), any(Pageable.class));
    }
}
