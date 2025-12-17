package org.example.smartspring.controller;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.historique.AjouterCommentaireDTO;
import org.example.smartspring.entities.HistoriqueLivraison;
import org.example.smartspring.service.HistoriqueLivraisonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/historique")
@RequiredArgsConstructor
public class HistoriqueLivraisonController {

    private final HistoriqueLivraisonService service;

    @PutMapping("/colis/{colisId}/commentaire")
    @PreAuthorize("hasAnyRole('CLIENT', 'LIVREUR', 'MANAGER')")
    public ResponseEntity<?> updateCommentaire(
            @PathVariable String colisId,
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        String commentaire = body.get("commentaire");

        HistoriqueLivraison updated = service.updateDernierCommentaire(colisId, commentaire);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Merci Pour Votre Commentaire");
    }
}
