package org.example.smartspring.controller;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.colis.ColisDetails.ColisDetailsDTO;
import org.example.smartspring.dto.colis.ColisDetails.UpdateStatutLivreurColis;
import org.example.smartspring.enums.PrioriteColis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.service.ColisDetailsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/detailsColis")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ColisDetailsController {
    private final ColisDetailsService service;

    @GetMapping
    @PreAuthorize("hasAuthority('COLIS_READ_ALL')")
    public ResponseEntity<?> getAllColisDetails(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) PrioriteColis prioriteColis,
            @RequestParam(required = false)StatutColis statutColis,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dateCreation,
            @RequestParam(required = false) String ville_destination,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ColisDetailsDTO> pageResult = service.getAll(
                id,
                prioriteColis,
                statutColis,
                ville_destination,
                dateCreation,
                pageable
        );
        if(pageResult.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Votre Colis est vide");
        }

        return ResponseEntity.ok(pageResult);
    }

    @PutMapping("/livreur/{livreurId}/update-statut")
    @PreAuthorize("hasAuthority('COLIS_UPDATE_STATUS')")
    public ResponseEntity<List<ColisDetailsDTO>> updateStatutColisLivreur(
            @PathVariable String livreurId,
            @RequestBody UpdateStatutLivreurColis updateDto,
            Authentication authentication
    ) {
        List<ColisDetailsDTO> updated = service.updateStatutColisLivreur(livreurId, updateDto);
        return ResponseEntity.ok(updated);
    }
}
