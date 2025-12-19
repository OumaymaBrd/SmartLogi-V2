package org.example.smartspring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.clientexpediteur.AddClientExpediteurDTO;
import org.example.smartspring.dto.clientexpediteur.UpdateClientExpediteurDTO;
import org.example.smartspring.dto.clientexpediteur.ClientExpediteurDTO;
import org.example.smartspring.service.ClientExpediteurService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients-expediteurs")
@RequiredArgsConstructor
public class ClientExpediteurController {

    private final ClientExpediteurService clientExpediteurService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    public ResponseEntity<ClientExpediteurDTO> createClientExpediteur(@Valid @RequestBody AddClientExpediteurDTO dto) {
        return new ResponseEntity<>(clientExpediteurService.createClientExpediteur(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT_READ')")
    public ResponseEntity<?> getAllClientsExpediteurs(Pageable pageable) {
        Page<ClientExpediteurDTO> page = clientExpediteurService.getAllClientsExpediteurs(pageable);

        if (page.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Aucun Client Trouvé");
        }
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_READ')")
    public ResponseEntity<ClientExpediteurDTO> getClientExpediteurById(@PathVariable String id) {
        return ResponseEntity.ok(clientExpediteurService.getClientExpediteurById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_UPDATE')")
    public ResponseEntity<ClientExpediteurDTO> updateClientExpediteur(
            @PathVariable String id,
            @Valid @RequestBody UpdateClientExpediteurDTO dto
    ) {
        return ResponseEntity.ok(clientExpediteurService.updateClientExpediteur(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_DELETE')")
    public ResponseEntity<?> deleteClientExpediteur(@PathVariable String id) {
        clientExpediteurService.deleteClientExpediteur(id);
        return ResponseEntity.ok("Suppression Avec Succès");
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('CLIENT_READ')")
    public ResponseEntity<Page<ClientExpediteurDTO>> searchClientsExpediteurs(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(clientExpediteurService.searchClientsExpediteurs(keyword, pageable));
    }
}