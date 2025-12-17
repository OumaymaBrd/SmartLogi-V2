package org.example.smartspring.controller;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.colis.ColisDTO;
import org.example.smartspring.dto.response.ColisCreationResponseDTO;
import org.example.smartspring.entities.Colis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.exception.ResourceNotFoundException;
import org.example.smartspring.service.ColisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("colis")
@RequiredArgsConstructor
public class ColisController {

    private final ColisService colisService;

    @PostMapping("/nouveau")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColisCreationResponseDTO> creerColisPourNouveauClient(@RequestBody ColisDTO dto) {
        Colis colis = colisService.creerColisPourNouveauClient(dto);

        ColisCreationResponseDTO response = ColisCreationResponseDTO.builder()
                .message("Votre colis créé avec succès")
                .numeroSuivi(colis.getNumeroSuivi())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/existant/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColisCreationResponseDTO> creerColisPourClientExistant(
            @PathVariable String clientId,
            @RequestBody ColisDTO dto,
            Authentication authentication
    ) {
        Colis colis = colisService.creerColisPourClientExistant(clientId, dto);

        ColisCreationResponseDTO response = ColisCreationResponseDTO.builder()
                .message("Votre colis créé avec succès")
                .numeroSuivi(colis.getNumeroSuivi())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{colisId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'LIVREUR', 'MANAGER')")
    public ResponseEntity<Colis> getColisById(
            @PathVariable String colisId,
            Authentication authentication
    ) {
        Colis colis = colisService.getColisById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis non trouvé avec l'ID: " + colisId));
        return ResponseEntity.ok(colis);
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Colis>> getAllColis() {
        List<Colis> colisList = colisService.getAllColis();
        return ResponseEntity.ok(colisList);
    }

    @PutMapping("/{colisId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateColis(
            @PathVariable String colisId,
            @RequestBody ColisDTO dto
    ) {
        Colis updatedColis = colisService.updateColis(colisId, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Update Statut Avec Succes!");
    }

    @DeleteMapping("/{colisId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteColis(@PathVariable String colisId) {
        colisService.deleteColis(colisId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{colisId}/statut")
    @PreAuthorize("hasAnyRole('MANAGER', 'LIVREUR')")
    public ResponseEntity<String> updateStatut(
            @PathVariable String colisId,
            @RequestParam StatutColis statut,
            Authentication authentication
    ) {
        Colis colis = colisService.modifierStatut(colisId, statut);
        return ResponseEntity.ok("Statut mis à jour et e-mails envoyés à l'expéditeur et au destinataire.");
    }
}
