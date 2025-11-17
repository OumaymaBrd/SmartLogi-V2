package org.example.smartspring.repository;

import org.example.smartspring.dto.colis.ColisDetails.ColisDetailsDTO;
import org.example.smartspring.entities.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColisDeatilsRepository extends JpaRepository<Colis,String> {

   List<Colis> findByLivreur_Id(String id);
   List<Colis> findByClientExpediteur_Id(String id);
}
