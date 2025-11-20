package org.example.smartspring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LivreurService {

//    private final LivreurRepository repository;
//    private final ZoneRepository zoneRepository;
//    private final LivreurMapper mapper;
//
//    public LivreurDTO createLivreur(AddLivreurDTO dto) {
//        log.debug("Creating new livreur: {} {}", dto.getNom(), dto.getPrenom());
//
//        Zone zone = zoneRepository.findById(dto.getZoneId())
//                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + dto.getZoneId()));
//
//        Livreur entity = mapper.toEntity(dto);
//        entity.setZone(zone);
//
//        Livreur saved = repository.save(entity);
//        return mapper.toDto(saved);
//    }
//
//    @Transactional(readOnly = true)
//
//    public List<LivreurDTO> getAllLivreurs() {
//        log.debug("Fetching all livreurs with pagination");
//        return repository.findAll()
//                        .stream()
//                        .map(mapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//
//    @Transactional(readOnly = true)
//    public LivreurDTO getLivreurById(String id) {
//        log.debug("Fetching livreur by id: {}", id);
//        Livreur entity = repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Livreur not found with id: " + id));
//        return mapper.toDto(entity);
//    }
//
//    public LivreurDTO updateLivreur(String id, UpdateLivreurDTO dto) {
//        log.debug("Updating livreur with id: {}", id);
//        Livreur entity = repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Livreur not found with id: " + id));
//
//        if (dto.getZoneId() != null) {
//            Zone zone = zoneRepository.findById(dto.getZoneId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + dto.getZoneId()));
//            entity.setZone(zone);
//        }
//
//        mapper.updateEntityFromDto(dto, entity);
//        Livreur updated = repository.save(entity);
//        return mapper.toDto(updated);
//    }
//
//    public void deleteLivreur(String id) {
//        log.debug("Deleting livreur with id: {}", id);
//        if (!repository.existsById(id)) {
//            throw new ResourceNotFoundException("Livreur not found with id: " + id);
//        }
//        repository.deleteById(id);
//    }
//
//    @Transactional(readOnly = true)
//    public Page<LivreurDTO> searchLivreurs(String keyword, Pageable pageable) {
//        log.debug("Searching livreurs with keyword: {}", keyword);
//        return repository.searchByKeyword(keyword, pageable).map(mapper::toDto);
//    }
//
//    @Transactional(readOnly = true)
//    public List<LivreurDTO> getLivreursByZone(String zoneId) {
//        log.debug("Fetching livreurs by zone: {}", zoneId);
//        return repository.findByZoneId(zoneId).stream()
//                .map(mapper::toDto)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public Map<String, Object> getLivreurStatistics(String livreurId) {
//        log.debug("Fetching statistics for livreur: {}", livreurId);
//        if (!repository.existsById(livreurId)) {
//            throw new ResourceNotFoundException("Livreur not found with id: " + livreurId);
//        }
//
//        Long nombreColis = repository.countColisByLivreur(livreurId);
//        Double poidsTotal = repository.sumPoidsByLivreur(livreurId);
//
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("livreurId", livreurId);
//        stats.put("nombreColis", nombreColis);
//        stats.put("poidsTotal", poidsTotal != null ? poidsTotal : 0.0);
//
//        return stats;
//    }
//
//    @Transactional(readOnly = true)
//    public Map<String, Object> getLivreurStats(String livreurId) {
//        return getLivreurStatistics(livreurId);
//    }
}
