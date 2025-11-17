package org.example.smartspring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartspring.dto.colis.ColisDetails.*;
import org.example.smartspring.entities.*;
import org.example.smartspring.enums.PrioriteColis;
import org.example.smartspring.enums.StatutColis;
import org.example.smartspring.mapper.ColisDeatilsMapper.*;
import org.example.smartspring.mapper.ColisMapper;
import org.example.smartspring.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ColisDetailsService {

    private final ColisDeatilsRepository colisDeatilsRepository;
    private final LivreurLivreeMapper livreurLivreeMapper;
    private final LivreurCollecteMapper livreurCollecteMapper;
    private final ColisDetailsMapper colisDetailsMapper;
    private final LivreurRepository livreurRepository;
    private final ColisProduitRepository colisProduitRepository;
    private final DestinataireRepository destinataireRepository;
    private final ZoneRepository zoneRepository;
    private final ZoneDeatailsMapper zoneDeatailsMapper;
    private final DestinataireDetailsMapper destinataireDetailsMapper;
    private final ColisDetailsMapper colismapper;
    private final ColisMapper mapper;

    public Page<ColisDetailsDTO> getAll(
            String id,
            PrioriteColis prioriteColis,
            StatutColis statutColis,
            String ville_destination,
            LocalDateTime date_creation,
            Pageable pageable) {

        List<Colis> colisList = colisDeatilsRepository.findAll();

        List<ColisDetailsDTO> dtoList = colisList.stream()
                .filter(colis -> id == null || colis.getId().equals(id))
                .filter(colis -> prioriteColis == null || colis.getPriorite() == prioriteColis)
                .filter(colis -> statutColis == null || colis.getStatut() == statutColis)
                .filter(colis -> ville_destination == null || colis.getVilleDestination().equalsIgnoreCase(ville_destination))
                .filter(colis -> date_creation == null || colis.getDateCreation().toLocalDate().isEqual(date_creation.toLocalDate()))
                .map(colis -> {
                    ColisDetailsDTO dto = ColisDetailsDTO.builder()
                            .id(colis.getId())
                            .numeroColis(colis.getNumeroColis())
                            .statut(colis.getStatut())
                            .priorite(colis.getPriorite())
                            .ville_destination(colis.getVilleDestination())
                            .build();

                    if (colis.getLivreur() != null) {
                        dto.setLivreurCollecte(
                                LivreurCollecteDTO.builder()
                                        .id(colis.getLivreur().getId())
                                        .nom_complet(colis.getLivreur().getNom() + " " + colis.getLivreur().getPrenom())
                                        .build()
                        );
                    }

                    if (colis.getLivreurLivree() != null) {
                        dto.setLivreurLivree(
                                LivreurLivreeDTO.builder()
                                        .id(colis.getLivreurLivree().getId())
                                        .nom_complet(colis.getLivreurLivree().getNom() + " " + colis.getLivreurLivree().getPrenom())
                                        .build()
                        );
                    }

                    List<ColisProduit> produits = colisProduitRepository.findByColis_Id(colis.getId());
                    if (!produits.isEmpty()) {
                        dto.setProduits(produits.stream()
                                .map(cp -> new ProduitDetailsDTO(cp.getProduit().getNom(), cp.getProduit().getPrixUnitaire()))
                                .collect(Collectors.toList()));
                    }

                    if (colis.getDestinataire() != null) {
                        dto.setDestinataire(destinataireDetailsMapper.mapToDto(colis.getDestinataire()));
                    }

                    if (colis.getZone() != null) {
                        dto.setZone(zoneDeatailsMapper.mapToDto(colis.getZone()));
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        int start = (int) Math.min(pageable.getOffset(), dtoList.size());
        int end = (int) Math.min(start + pageable.getPageSize(), dtoList.size());
        List<ColisDetailsDTO> pageContent = dtoList.subList(start, end);

        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }


    public List<ColisDetailsDTO> updateStatutColisLivreur(String livreurId, UpdateStatutLivreurColis updateDto) {

        StatutColis nouveauStatut = updateDto.getStatut();
        List<Colis> colisList = colisDeatilsRepository.findByLivreur_Id(livreurId);

        colisList.forEach(colis -> colis.setStatut(nouveauStatut));

        colisDeatilsRepository.saveAll(colisList);

        return mapper.toDTOList(colisList);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getByIdClientExpediteur() {
        return colisDeatilsRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        colis -> colis.getClientExpediteur().getId(),
                        Collectors.counting()
                ));
    }





}
