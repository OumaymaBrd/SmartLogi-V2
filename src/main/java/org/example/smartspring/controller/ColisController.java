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
@RequestMapping("/colis")
@RequiredArgsConstructor
public class ColisController {

    private final ColisService colisService;

    @PostMapping("/nouveau")
    @PreAuthorize("hasAuthority('COLIS_CREATE')") // Autorise Admin et Manager
    public ResponseEntity<ColisCreationResponseDTO> creerColisPourNouveauClient(@RequestBody ColisDTO dto) {
        Colis colis = colisService.creerColisPourNouveauClient(dto);

        ColisCreationResponseDTO response = ColisCreationResponseDTO.builder()
                .message("Votre colis créé avec succès")
                .numeroSuivi(colis.getNumeroSuivi())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/existant/{clientId}")
    @PreAuthorize("hasAuthority('COLIS_CREATE')") // Autorise Admin et Manager
    public ResponseEntity<ColisCreationResponseDTO> creerColisPourClientExistant(
            @PathVariable String clientId,
            @RequestBody ColisDTO dto
    ) {
        Colis colis = colisService.creerColisPourClientExistant(clientId, dto);

        ColisCreationResponseDTO response = ColisCreationResponseDTO.builder()
                .message("Votre colis créé avec succès")
                .numeroSuivi(colis.getNumeroSuivi())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{colisId}")
    @PreAuthorize("hasAnyAuthority('COLIS_READ_ALL', 'COLIS_READ_OWN', 'COLIS_READ_ASSIGNED')")
    public ResponseEntity<Colis> getColisById(
            @PathVariable String colisId
    ) {
        Colis colis = colisService.getColisById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis non trouvé avec l'ID: " + colisId));
        return ResponseEntity.ok(colis);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COLIS_READ_ALL')") // Réservé Admin / Manager
    public ResponseEntity<List<Colis>> getAllColis() {
        List<Colis> colisList = colisService.getAllColis();
        return ResponseEntity.ok(colisList);
    }

    @PutMapping("/{colisId}")
    @PreAuthorize("hasAuthority('COLIS_UPDATE')")
    public ResponseEntity<?> updateColis(
            @PathVariable String colisId,
            @RequestBody ColisDTO dto
    ) {
        colisService.updateColis(colisId, dto);
        return ResponseEntity.ok("Colis mis à jour avec succès");
    }

    @DeleteMapping("/{colisId}")
    @PreAuthorize("hasAuthority('COLIS_DELETE')")
    public ResponseEntity<Void> deleteColis(@PathVariable String colisId) {
        colisService.deleteColis(colisId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{colisId}/statut")
    @PreAuthorize("hasAuthority('COLIS_UPDATE_STATUS')") // Partagé entre Manager et Livreur
    public ResponseEntity<String> updateStatut(
            @PathVariable String colisId,
            @RequestParam StatutColis statut
    ) {
        colisService.modifierStatut(colisId, statut);
        return ResponseEntity.ok("Statut mis à jour avec succès.");
    }
}