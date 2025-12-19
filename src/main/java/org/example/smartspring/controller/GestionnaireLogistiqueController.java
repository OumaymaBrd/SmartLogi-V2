package org.example.smartspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartspring.dto.colis.UpdateColisDTO;
import org.example.smartspring.dto.gestionnairelogistique.AddGestionnaireLogistqueDTO;
import org.example.smartspring.dto.gestionnairelogistique.GestionnaireLogistiqueDTO;
import org.example.smartspring.entities.Colis;
import org.example.smartspring.service.ColisService;
import org.example.smartspring.service.GestionnaireLogistiqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/gestionnairelogistique")
public class GestionnaireLogistiqueController {

    private final GestionnaireLogistiqueService service;
    private final ColisService colisService;

    @PostMapping
    @PreAuthorize("hasAuthority('GESTIONNAIRE_CREATE')")
    public ResponseEntity<?> create(
            @Valid @RequestBody AddGestionnaireLogistqueDTO dto) {

        service.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Création Gestionnaire Logistique Avec Succès");
    }

    @PutMapping("/affecter-livreur")
    @PreAuthorize("hasAuthority('LIVREUR_ASSIGN')")
    public ResponseEntity<?> affecterLivreur(
            @RequestParam String numero_colis,
            @RequestParam String idGestionnaire,
            @RequestParam(required = false, name = "livreur_id") String livreurId,
            @RequestParam(required = false, name = "livreur_id_livree") String livreurIdLivree
    ) {

        if (livreurId == null && livreurIdLivree == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Vous devez fournir soit livreur_id soit livreur_id_livree");
        }

        String message = service.affecterLivreur(numero_colis, idGestionnaire, livreurId, livreurIdLivree);

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PutMapping("/updateStatutColis/{colisId}")
    @PreAuthorize("hasAuthority('COLIS_UPDATE_STATUS')")
    public ResponseEntity<String> updateColis(@PathVariable String colisId,
                                              @RequestBody UpdateColisDTO dto) {
        Colis updatedColis = colisService.updateColis(dto, colisId);

        StringBuilder message = new StringBuilder("Le colis " + colisId + " a été mis à jour avec succès");

        if (dto.getStatut() != null) {
            message.append(" | Statut : ").append(updatedColis.getStatut());
        }

        if (dto.getLivreurId() != null) {
            message.append(" | Livreur collecteur affecté : ").append(dto.getLivreurId());
        }

        if (dto.getLivreur_id_livree() != null) {
            message.append(" | Livreur livré affecté : ").append(dto.getLivreur_id_livree());
        }

        return ResponseEntity.status(HttpStatus.OK).body(message.toString());
    }
}