package org.example.smartspring.service;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.colis.ColisDTO;
import org.example.smartspring.dto.colis.UpdateColisDTO;
import org.example.smartspring.dto.livreur.ConsulterColisAffecterDTO;
import org.example.smartspring.entities.*;
import org.example.smartspring.enums.PrioriteColis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.events.ColisStatusChangeEvent;
import org.example.smartspring.exception.ResourceNotFoundException;
import org.example.smartspring.mapper.ColisMapper;
import org.example.smartspring.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColisService {

    private final ClientExpediteurRepository clientRepo;
    private final ColisRepository colisRepo;
    private final DestinataireRepository destinataireRepo;
    private final ZoneRepository zoneRepo;
    private final ProduitRepository produitRepo;
    private final ColisProduitRepository colisProduitRepo;
    private final ColisMapper mapper;
    private final LivreurRepository livreurRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Colis creerColisPourNouveauClient(ColisDTO dto) {
        ClientExpediteur client = mapper.toClientExpediteur(dto.getExpediteur());
        client = clientRepo.save(client);

        Destinataire destinataire = mapper.toDestinataire(dto.getDestinataire());
        destinataire = destinataireRepo.save(destinataire);

        Zone zone = zoneRepo.findByNom(dto.getZone().getNom())
                .orElseGet(() -> {
                    Zone newZone = mapper.toZone(dto.getZone());
                    return zoneRepo.save(newZone);
                });

        PrioriteColis priorite = (dto.getPriorite() != null && !dto.getPriorite().isEmpty())
                ? PrioriteColis.valueOf(dto.getPriorite().toUpperCase())
                : PrioriteColis.NORMALE;

        Colis colis = Colis.builder()
                .clientExpediteur(client)
                .destinataire(destinataire)
                .zone(zone)
                .numeroColis(genererNumeroColis())
                .numeroSuivi(genererNumeroSuivi())
                .statut(StatutColis.CREE)
                .priorite(priorite)
                .dateCreation(LocalDateTime.now())
                .dateLivraisonReelle(null)
                .livreur(null)
                .villeDestination(dto.getDestinataire().getVille())
                .colisProduits(new ArrayList<>())
                .build();

        colis = colisRepo.save(colis);

        if (dto.getProduits() != null && !dto.getProduits().isEmpty()) {
            for (var produitDTO : dto.getProduits()) {
                Produit produit = produitRepo.findByNom(produitDTO.getNom())
                        .orElseGet(() -> {
                            Produit newProduit = mapper.toProduit(produitDTO);
                            newProduit.setPrixUnitaire(BigDecimal.valueOf(30.00));
                            return produitRepo.save(newProduit);
                        });

                BigDecimal prixTotal = produit.getPrixUnitaire()
                        .multiply(BigDecimal.valueOf(produitDTO.getQuantite()));

                ColisProduit colisProduit = ColisProduit.builder()
                        .colis(colis)
                        .produit(produit)
                        .quantite(produitDTO.getQuantite())
                        .prix(prixTotal)
                        .dateAjout(LocalDateTime.now())
                        .build();

                colis.getColisProduits().add(colisProduit);
            }
        }

        return colisRepo.save(colis);
    }

    @Transactional
    public Colis creerColisPourClientExistant(String clientId, ColisDTO dto) {
        ClientExpediteur client = clientRepo.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        Destinataire destinataire = mapper.toDestinataire(dto.getDestinataire());
        destinataire = destinataireRepo.save(destinataire);

        Zone zone = zoneRepo.findByNom(dto.getZone().getNom())
                .orElseGet(() -> {
                    Zone newZone = mapper.toZone(dto.getZone());
                    return zoneRepo.save(newZone);
                });

        PrioriteColis priorite = (dto.getPriorite() != null && !dto.getPriorite().isEmpty())
                ? PrioriteColis.valueOf(dto.getPriorite().toUpperCase())
                : PrioriteColis.NORMALE;

        Colis colis = Colis.builder()
                .clientExpediteur(client)
                .destinataire(destinataire)
                .zone(zone)
                .numeroColis(genererNumeroColis())
                .numeroSuivi(genererNumeroSuivi())
                .statut(StatutColis.CREE)
                .priorite(priorite)
                .dateCreation(LocalDateTime.now())
                .dateLivraisonReelle(null)
                .livreur(null)
                .villeDestination(dto.getDestinataire().getVille())
                .colisProduits(new ArrayList<>())
                .build();

        colis = colisRepo.save(colis);

        if (dto.getProduits() != null && !dto.getProduits().isEmpty()) {
            for (var produitDTO : dto.getProduits()) {
                Produit produit = produitRepo.findByNom(produitDTO.getNom())
                        .orElseGet(() -> {
                            Produit newProduit = mapper.toProduit(produitDTO);
                            newProduit.setPrixUnitaire(BigDecimal.valueOf(30.00));
                            return produitRepo.save(newProduit);
                        });

                BigDecimal prixTotal = produit.getPrixUnitaire()
                        .multiply(BigDecimal.valueOf(produitDTO.getQuantite()));

                ColisProduit colisProduit = ColisProduit.builder()
                        .colis(colis)
                        .produit(produit)
                        .quantite(produitDTO.getQuantite())
                        .prix(prixTotal)
                        .dateAjout(LocalDateTime.now())
                        .build();

                colis.getColisProduits().add(colisProduit);
            }
        }

        return colisRepo.save(colis);
    }

    @Transactional(readOnly = true)
    public Optional<Colis> getColisById(String id) {
        return colisRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Colis> getColisByClientExpediteur(String clientId) {
        return colisRepo.findByClientExpediteur_Id(clientId);
    }

    @Transactional(readOnly = true)
    public List<Colis> getAllColis() {
        return colisRepo.findAll();
    }

    @Transactional
    public Colis updateColis(String colisId, ColisDTO dto) {
        Colis colis = colisRepo.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis non trouvé"));

        if (dto.getStatut() != null) {
            colis.setStatut(StatutColis.valueOf(dto.getStatut()));
        }

        return colisRepo.save(colis);
    }



    @Transactional
    public void deleteColis(String colisId) {
        Colis colis = colisRepo.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis non trouvé"));
        colisRepo.delete(colis);
    }

    private String genererNumeroColis() {
        return "COLIS_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String genererNumeroSuivi() {
        return "SUIVI_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public List<ConsulterColisAffecterDTO> getColisByLivreurId(String livreurId) {

        List<Colis> colisList = colisRepo.findByLivreur_IdOrLivreurLivree_Id(livreurId, livreurId);

        return colisList.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public Colis updateColis(UpdateColisDTO dto, String colisId) {
        Colis colis = colisRepo.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis non trouvé"));

        if (dto.getStatut() != null) {
            colis.setStatut(dto.getStatut());

            if (dto.getStatut() == StatutColis.LIVRE) {
                colis.setDateLivraisonReelle(LocalDateTime.now());
            }

            eventPublisher.publishEvent(new ColisStatusChangeEvent(colis.getId(), dto.getStatut().name()));
        }

        // Affecter le livreur collecteur
        if (dto.getLivreurId() != null) {
            if (colis.getLivreur() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le livreur collecteur est déjà affecté à ce colis");
            }
            Livreur livreur = livreurRepo.findById(dto.getLivreurId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Aucun livreur trouvé avec l'id : " + dto.getLivreurId()));
            colis.setLivreur(livreur);
        }

        // Affecter le livreur livré
        if (dto.getLivreur_id_livree() != null) {
            if (colis.getLivreurLivree() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le livreur livré est déjà affecté à ce colis");
            }
            Livreur livreurLivree = livreurRepo.findById(dto.getLivreur_id_livree())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Aucun livreur trouvé avec l'id : " + dto.getLivreur_id_livree()));
            colis.setLivreurLivree(livreurLivree);
        }

        return colisRepo.save(colis);
    }

    @Transactional
    public Colis updateStatut(String colisId, StatutColis nouveauStatut) {
        Colis colis = colisRepo.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé"));

        colis.setStatut(nouveauStatut);

        return colisRepo.save(colis);
    }


    public List<ConsulterColisAffecterDTO> getColisByLivreurIdAndStatut(String livreurId, StatutColis statut) {
        return colisRepo.findByLivreur_Id(livreurId)
                .stream()
                .map(mapper::toDto)
                .filter(dto -> statut == null || statut.equals(dto.getStatut()))
                .toList();
    }

    @Transactional
    public Colis modifierStatut(String colisId, StatutColis nouveauStatut) {
        Colis colis = colisRepo.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé"));

        colis.setStatut(nouveauStatut);
        Colis saved = colisRepo.save(colis);

        eventPublisher.publishEvent(new ColisStatusChangeEvent(saved.getId(), saved.getStatut().name()));

        return saved;
    }


}
