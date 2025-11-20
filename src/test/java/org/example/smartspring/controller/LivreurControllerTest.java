package org.example.smartspring.controller;

import org.example.smartspring.dto.colis.UpdateColisDTO;
import org.example.smartspring.dto.livreur.ConsulterColisAffecterDTO;
import org.example.smartspring.entities.Colis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.mapper.ColisMapper;
import org.example.smartspring.repository.ColisRepository;
import org.example.smartspring.service.ColisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LivreurControllerTest {

    @InjectMocks
    private LivreurController controller;

    @Mock
    private ColisService service;

    @Mock
    private ColisRepository repository;

    @Mock
    private ColisMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetColisAffectes_notFound() {
        String livreurId = "123";
        when(service.getColisByLivreurIdAndStatut(livreurId, null))
                .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.getColisAffectes(livreurId, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isEqualTo("Aucune affectation Colis trouvée");
    }

    @Test
    void testGetColisAffectes_found() {
        String livreurId = "123";
        ConsulterColisAffecterDTO dto = new ConsulterColisAffecterDTO();
        List<ConsulterColisAffecterDTO> liste = List.of(dto);

        when(service.getColisByLivreurIdAndStatut(livreurId, null))
                .thenReturn(liste);

        ResponseEntity<?> response = controller.getColisAffectes(livreurId, null);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(liste);
    }

    @Test
    void testUpdateColis() {
        String colisId = "C123";
        UpdateColisDTO dto = new UpdateColisDTO();
        dto.setStatut(StatutColis.LIVRE);
        dto.setLivreurId("L1");
        dto.setLivreur_id_livree("L2");

        Colis updatedColis = new Colis();
        updatedColis.setStatut(StatutColis.LIVRE);

        when(service.updateColis(dto, colisId)).thenReturn(updatedColis);

        ResponseEntity<String> response = controller.updateColis(colisId, dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).contains("Le colis " + colisId + " a été mis à jour avec succès");
        assertThat(response.getBody()).contains("Statut : LIVRE");
        assertThat(response.getBody()).contains("Livreur collecteur affecté : L1");
        assertThat(response.getBody()).contains("Livreur livré affecté : L2");

        verify(service, times(1)).updateColis(dto, colisId);
    }
}
