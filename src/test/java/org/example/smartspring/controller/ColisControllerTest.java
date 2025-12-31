package org.example.smartspring.controller;

import org.example.smartspring.dto.colis.ColisDTO;
import org.example.smartspring.dto.response.ColisCreationResponseDTO;
import org.example.smartspring.entities.Colis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.exception.ResourceNotFoundException;
import org.example.smartspring.service.ColisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ColisControllerTest {

    @InjectMocks
    private ColisController controller;

    @Mock
    private ColisService colisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreerColisPourNouveauClient() {
        ColisDTO dto = new ColisDTO();
        Colis colis = new Colis();
        colis.setNumeroSuivi("SUIVI123");

        when(colisService.creerColisPourNouveauClient(dto)).thenReturn(colis);

        ResponseEntity<ColisCreationResponseDTO> response = controller.creerColisPourNouveauClient(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getNumeroSuivi()).isEqualTo("SUIVI123");
        assertThat(response.getBody().getMessage()).isEqualTo("Votre colis créé avec succès");

        verify(colisService, times(1)).creerColisPourNouveauClient(dto);
    }

    @Test
    void testCreerColisPourClientExistant() {
        String clientId = "CL123";
        ColisDTO dto = new ColisDTO();
        Colis colis = new Colis();
        colis.setNumeroSuivi("SUIVI456");

        when(colisService.creerColisPourClientExistant(clientId, dto)).thenReturn(colis);

        ResponseEntity<ColisCreationResponseDTO> response = controller.creerColisPourClientExistant(clientId, dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getNumeroSuivi()).isEqualTo("SUIVI456");

        verify(colisService, times(1)).creerColisPourClientExistant(clientId, dto);
    }

    @Test
    void testGetColisById_found() {
        String colisId = "C123";
        Colis colis = new Colis();
        colis.setId(colisId);

        when(colisService.getColisById(colisId)).thenReturn(Optional.of(colis));

        ResponseEntity<Colis> response = controller.getColisById(colisId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(colisId);
    }

    @Test
    void testGetColisById_notFound() {
        String colisId = "C999";
        when(colisService.getColisById(colisId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controller.getColisById(colisId));
    }

    @Test
    void testGetAllColis() {
        Colis colis = new Colis();
        List<Colis> list = List.of(colis);

        when(colisService.getAllColis()).thenReturn(list);

        ResponseEntity<List<Colis>> response = controller.getAllColis();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(colis);
    }



    @Test
    void testDeleteColis() {
        String colisId = "C123";

        doNothing().when(colisService).deleteColis(colisId);

        ResponseEntity<Void> response = controller.deleteColis(colisId);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(colisService, times(1)).deleteColis(colisId);
    }


}
