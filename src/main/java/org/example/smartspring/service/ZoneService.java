package org.example.smartspring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ZoneService {

//    private final ZoneRepository repository;
//    private final ZoneMapper mapper;
//
//    public ZoneDTO createZone(AddZoneDTO dto) {
//        log.debug("Creating new zone: {}", dto.getNom());
//        Zone entity = mapper.toEntity(dto);
//        Zone saved = repository.save(entity);
//        return mapper.toDto(saved);
//    }
//
//    @Transactional(readOnly = true)
//    public Page<ZoneDTO> getAllZones(Pageable pageable) {
//        log.debug("Fetching all zones with pagination");
//        return repository.findAll(pageable).map(mapper::toDto);
//    }
//
//    @Transactional(readOnly = true)
//    public ZoneDTO getZoneById(String id) {
//        log.debug("Fetching zone by id: {}", id);
//        Zone entity = repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));
//        return mapper.toDto(entity);
//    }
//
//    public ZoneDTO updateZone(String id, UpdateZoneDTO dto) {
//        log.debug("Updating zone with id: {}", id);
//        Zone entity = repository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + id));
//
//        mapper.updateEntityFromDto(dto, entity);
//        Zone updated = repository.save(entity);
//        return mapper.toDto(updated);
//    }
//
//    public void deleteZone(String id) {
//        log.debug("Deleting zone with id: {}", id);
//        if (!repository.existsById(id)) {
//            throw new ResourceNotFoundException("Zone not found with id: " + id);
//        }
//        repository.deleteById(id);
//    }
//
//    @Transactional(readOnly = true)
//    public Page<ZoneDTO> searchZones(String keyword, Pageable pageable) {
//        log.debug("Searching zones with keyword: {}", keyword);
//        return repository.searchByKeyword(keyword, pageable).map(mapper::toDto);
//    }
//
//    @Transactional(readOnly = true)
//    public Map<String, Object> getZoneStatistics(String zoneId) {
//        log.debug("Fetching statistics for zone: {}", zoneId);
//        if (!repository.existsById(zoneId)) {
//            throw new ResourceNotFoundException("Zone not found with id: " + zoneId);
//        }
//
//        Long nombreColis = repository.countColisByZone(zoneId);
//        Double poidsTotal = repository.sumPoidsByZone(zoneId);
//
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("zoneId", zoneId);
//        stats.put("nombreColis", nombreColis);
//        stats.put("poidsTotal", poidsTotal != null ? poidsTotal : 0.0);
//
//        return stats;
//    }
//
//    @Transactional(readOnly = true)
//    public Map<String, Object> getZoneStats(String zoneId) {
//        return getZoneStatistics(zoneId);
//    }
}
