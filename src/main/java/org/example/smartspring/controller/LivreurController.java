package org.example.smartspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.colis.UpdateColisDTO;
import org.example.smartspring.dto.livreur.*;
import org.example.smartspring.entities.Colis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.mapper.ColisMapper;
import org.example.smartspring.repository.ColisRepository;
import org.example.smartspring.service.ColisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livreurs")
@RequiredArgsConstructor
public class LivreurController {

    private final ColisService service;

    @GetMapping("/colis/{id}")
    @PreAuthorize("hasAuthority('COLIS_READ_ASSIGNED')")
    public ResponseEntity<?> getColisAffectes(
            @PathVariable String id,
            @RequestParam(required = false) StatutColis statut
    ) {
        List<ConsulterColisAffecterDTO> liste = service.getColisByLivreurIdAndStatut(id, statut);

        if (liste.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Aucune affectation Colis trouvée");
        }

        return ResponseEntity.ok(liste);
    }

    @PutMapping("/updateStatutColis/{colisId}")
    @PreAuthorize("hasAuthority('COLIS_UPDATE_STATUS')")
    public ResponseEntity<String> updateColis(
            @PathVariable String colisId,
            @RequestBody UpdateColisDTO dto
    ) {
        Colis updatedColis = service.updateColis(dto, colisId);

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