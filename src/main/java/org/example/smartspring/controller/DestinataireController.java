package org.example.smartspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.destinataire.AddDestinataireDTO;
import org.example.smartspring.dto.destinataire.UpdateDestinataireDTO;
import org.example.smartspring.dto.destinataire.DestinataireDTO;
import org.example.smartspring.service.DestinataireService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/destinataires")
@RequiredArgsConstructor
public class DestinataireController {

    private final DestinataireService destinataireService;

    @PostMapping
    @PreAuthorize("hasAuthority('DESTINATAIRE_CREATE')")
    public ResponseEntity<DestinataireDTO> createDestinataire(@Valid @RequestBody AddDestinataireDTO dto) {
        return new ResponseEntity<>(destinataireService.createDestinataire(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DESTINATAIRE_READ')")
    public ResponseEntity<Page<DestinataireDTO>> getAllDestinataires(Pageable pageable) {
        return ResponseEntity.ok(destinataireService.getAllDestinataires(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DESTINATAIRE_READ')")
    public ResponseEntity<DestinataireDTO> getDestinataireById(@PathVariable String id) {
        return ResponseEntity.ok(destinataireService.getDestinataireById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DESTINATAIRE_UPDATE')")
    public ResponseEntity<DestinataireDTO> updateDestinataire(@PathVariable String id, @Valid @RequestBody UpdateDestinataireDTO dto) {
        return ResponseEntity.ok(destinataireService.updateDestinataire(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DESTINATAIRE_DELETE')")
    public ResponseEntity<Void> deleteDestinataire(@PathVariable String id) {
        destinataireService.deleteDestinataire(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('DESTINATAIRE_READ')")
    public ResponseEntity<Page<DestinataireDTO>> searchDestinataires(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(destinataireService.searchDestinataires(keyword, pageable));
    }

    @GetMapping("/ville/{ville}")
    @PreAuthorize("hasAuthority('DESTINATAIRE_READ')")
    public ResponseEntity<List<DestinataireDTO>> getDestinatairesByVille(@PathVariable String ville) {
        return ResponseEntity.ok(destinataireService.getDestinatairesByVille(ville));
    }
}