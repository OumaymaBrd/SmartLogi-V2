package org.example.smartspring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartspring.dto.clientexpediteur.AddClientExpediteurDTO;
import org.example.smartspring.dto.clientexpediteur.ClientExpediteurDTO;
import org.example.smartspring.dto.clientexpediteur.UpdateClientExpediteurDTO;
import org.example.smartspring.entities.ClientExpediteur;
import org.example.smartspring.exception.ResourceNotFoundException;
import org.example.smartspring.mapper.ClientExpediteurMapper;
import org.example.smartspring.repository.ClientExpediteurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientExpediteurService {

    private final ClientExpediteurRepository repository;
    private final ClientExpediteurMapper mapper;

    public ClientExpediteurDTO createClientExpediteur(AddClientExpediteurDTO dto) {
        log.debug("Creating new client expediteur: {}", dto.getEmail());
        ClientExpediteur entity = mapper.toEntity(dto);
        ClientExpediteur saved = repository.save(entity);
        return mapper.toDto(saved);
    }


    @Transactional(readOnly = true)
    public Page<ClientExpediteurDTO> getAllClientsExpediteurs(Pageable pageable) {
        log.debug("Fetching all clients expediteurs with pagination");
        return repository.findAll(pageable).map(mapper::toDto);
    }


    @Transactional(readOnly = true)
    public ClientExpediteurDTO getClientExpediteurById(String id) {
        log.debug("Fetching client expediteur by id: {}", id);
        ClientExpediteur entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur not found with id: " + id));
        return mapper.toDto(entity);
    }

    public ClientExpediteurDTO updateClientExpediteur(String id, UpdateClientExpediteurDTO dto) {
        log.debug("Updating client expediteur with id: {}", id);
        ClientExpediteur entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur not found with id: " + id));

        mapper.updateEntityFromDto(dto, entity);
        ClientExpediteur updated = repository.save(entity);
        return mapper.toDto(updated);
    }

    public void deleteClientExpediteur(String id) {
        log.debug("Deleting client expediteur with id: {}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ClientExpediteur not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)

    public Page<ClientExpediteurDTO> searchClientsExpediteurs(String keyword, Pageable pageable) {
        log.debug("Searching clients expediteurs with keyword: {}", keyword);
        return repository.searchByKeyword(keyword, pageable).map(mapper::toDto);
    }
}
