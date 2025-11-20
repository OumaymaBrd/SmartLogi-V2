package org.example.smartspring.service;

import org.example.smartspring.dto.clientexpediteur.AddClientExpediteurDTO;
import org.example.smartspring.dto.clientexpediteur.ClientExpediteurDTO;
import org.example.smartspring.dto.clientexpediteur.UpdateClientExpediteurDTO;
import org.example.smartspring.entities.ClientExpediteur;
import org.example.smartspring.exception.ResourceNotFoundException;
import org.example.smartspring.mapper.ClientExpediteurMapper;
import org.example.smartspring.repository.ClientExpediteurRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientExpediteurServiceTest {

    @Mock
    private ClientExpediteurRepository repository;

    @Mock
    private ClientExpediteurMapper mapper;


    @InjectMocks
    private ClientExpediteurService service;

    private ClientExpediteur entity;
    private ClientExpediteurDTO dto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        entity = new ClientExpediteur();
        entity.setId("123");
        entity.setNom("Oumaima");
        entity.setPrenom("Badri");
        entity.setEmail("oumaima@test.com");
        entity.setTelephone("0612345678");
        entity.setAdresse("Rabat");

        dto = ClientExpediteurDTO.builder()
                .nom("Oumaima")
                .prenom("Badri")
                .email("oumaima@test.com")
                .telephone("0612345678")
                .adresse("Rabat")
                .ville("Rabat")
                .build();
    }


    @Test
    void testCreateClientExpediteur() {
        AddClientExpediteurDTO addDto = AddClientExpediteurDTO.builder()
                .nom("Oumaima")
                .prenom("Badri")
                .email("test@test.com")
                .telephone("0612345678")
                .adresse("Rabat")
                .build();

        when(mapper.toEntity(addDto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        ClientExpediteurDTO result = service.createClientExpediteur(addDto);

        assertNotNull(result);
        assertEquals("Oumaima", result.getNom());
        verify(repository, times(1)).save(entity);
    }


    @Test
    void testGetAllClientsExpediteurs() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ClientExpediteur> page = new PageImpl<>(List.of(entity));
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toDto(entity)).thenReturn(dto);

        Page<ClientExpediteurDTO> result = service.getAllClientsExpediteurs(pageable);

        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(pageable);
    }


    @Test
    void testGetClientExpediteurById() {
        when(repository.findById("123")).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        ClientExpediteurDTO result = service.getClientExpediteurById("123");

        assertNotNull(result);
        assertEquals("Oumaima", result.getNom());
        verify(repository).findById("123");
    }

    @Test
    void testGetClientExpediteurById_NotFound() {
        when(repository.findById("404")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getClientExpediteurById("404"));
    }


    @Test
    void testUpdateClientExpediteur() {
        UpdateClientExpediteurDTO updateDto = UpdateClientExpediteurDTO.builder()
                .nom("Oumaima UPDATED")
                .prenom("Badri UPDATED")
                .email("new@test.com")
                .telephone("0699999999")
                .adresse("Casablanca")
                .build();

        when(repository.findById("123")).thenReturn(Optional.of(entity));

        doAnswer(invocation -> {
            entity.setNom(updateDto.getNom());
            entity.setPrenom(updateDto.getPrenom());
            entity.setEmail(updateDto.getEmail());
            entity.setTelephone(updateDto.getTelephone());
            entity.setAdresse(updateDto.getAdresse());
            return null;
        }).when(mapper).updateEntityFromDto(updateDto, entity);

        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        ClientExpediteurDTO result = service.updateClientExpediteur("123", updateDto);

        assertNotNull(result);
        verify(repository).save(entity);
    }

    @Test
    void testUpdateClientExpediteur_NotFound() {
        UpdateClientExpediteurDTO updateDto = new UpdateClientExpediteurDTO();
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateClientExpediteur("999", updateDto));
    }


    @Test
    void testDeleteClientExpediteur() {
        when(repository.existsById("123")).thenReturn(true);

        service.deleteClientExpediteur("123");

        verify(repository).deleteById("123");
    }

    @Test
    void testDeleteClientExpediteur_NotFound() {
        when(repository.existsById("999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteClientExpediteur("999"));
    }


    @Test
    void testSearchClientExpediteurs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClientExpediteur> page = new PageImpl<>(List.of(entity));

        when(repository.searchByKeyword("test", pageable)).thenReturn(page);
        when(mapper.toDto(entity)).thenReturn(dto);

        Page<ClientExpediteurDTO> result =
                service.searchClientsExpediteurs("test", pageable);

        assertEquals(1, result.getTotalElements());
        verify(repository).searchByKeyword("test", pageable);
    }
}
