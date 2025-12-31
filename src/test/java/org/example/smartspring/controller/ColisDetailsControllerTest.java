package org.example.smartspring.controller;

import org.example.smartspring.dto.colis.ColisDetails.*;
import org.example.smartspring.enums.PrioriteColis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.service.ColisDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ColisDetailsControllerTest {

    @Mock
    private ColisDetailsService service;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ColisDetailsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllColisDetails_noData() {
        when(service.getAll(
                any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        ResponseEntity<?> response = controller.getAllColisDetails(
                null, null, null, null, null, 0, 2);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Votre Colis est vide", response.getBody());
    }

    @Test
    void testGetAllColisDetails_withData() {
        ColisDetailsDTO dto = ColisDetailsDTO.builder()
                .id("1")
                .numeroColis("COL123")
                .priorite(PrioriteColis.NORMALE)
                .statut(StatutColis.COLLECTE)
                .ville_destination("Casablanca")
                .livreurCollecte(new LivreurCollecteDTO("L1", "Livreur Collecte"))
                .livreurLivree(new LivreurLivreeDTO("L2", "Livreur Livree"))
                .nom_complet("John Doe")
                .produits(Arrays.asList(
                        new ProduitDetailsDTO("Produit A", null),
                        new ProduitDetailsDTO("Produit B", null)
                ))
                .destinataire(new DestinataireDetailsDTO("Client X", "client@example.com"))
                .zone(new ZoneDeatailsDTO("Zone 1", "Description zone"))
                .build();

        Page<ColisDetailsDTO> pageMock =
                new PageImpl<>(Collections.singletonList(dto));

        when(service.getAll(
                any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(pageMock);

        ResponseEntity<?> response = controller.getAllColisDetails(
                null, null, null, null, null, 0, 2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Page);

        Page<?> resultPage = (Page<?>) response.getBody();
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(dto, resultPage.getContent().get(0));
    }

    @Test
    void testUpdateStatutColisLivreur() {
        String livreurId = "L123";
        UpdateStatutLivreurColis updateDto =
                new UpdateStatutLivreurColis(StatutColis.LIVRE);

        ColisDetailsDTO dto = ColisDetailsDTO.builder()
                .id("C1")
                .build();

        List<ColisDetailsDTO> updatedList =
                Collections.singletonList(dto);

        when(service.updateStatutColisLivreur(eq(livreurId), eq(updateDto)))
                .thenReturn(updatedList);

        ResponseEntity<List<ColisDetailsDTO>> response =
                controller.updateStatutColisLivreur(
                        livreurId,
                        updateDto,
                        authentication
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));

        verify(service, times(1))
                .updateStatutColisLivreur(livreurId, updateDto);
    }
}
