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
import org.example.smartspring.service.EmailService;
import org.example.smartspring.service.LivreurService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/livreurs")
@RequiredArgsConstructor
public class LivreurController {

    private final ColisService service;
    private final ColisRepository repository;
    private final ColisMapper mapper;

    @GetMapping("/colis/{id}")
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<?> getColisAffectes(
            @PathVariable String id,
            @RequestParam(required = false) StatutColis statut,
            Authentication authentication
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
    @PreAuthorize("hasRole('LIVREUR')")
    public ResponseEntity<String> updateColis(
            @PathVariable String colisId,
            @RequestBody UpdateColisDTO dto,
            Authentication authentication
    ) {
        Colis updatedColis = service.updateColis(dto, colisId);

        String message = "Le colis " + colisId + " a été mis à jour avec succès";

        if (dto.getStatut() != null) {
            message += " | Statut : " + updatedColis.getStatut();
        }

        if (dto.getLivreurId() != null) {
            message += " | Livreur collecteur affecté : " + dto.getLivreurId();
        }

        if (dto.getLivreur_id_livree() != null) {
            message += " | Livreur livré affecté : " + dto.getLivreur_id_livree();
        }

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
