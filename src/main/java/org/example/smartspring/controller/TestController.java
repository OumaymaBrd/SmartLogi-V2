package org.example.smartspring.controller;

import lombok.RequiredArgsConstructor;
import org.example.smartspring.dto.colis.ColisDetails.ColisDetailsDTO;
import org.example.smartspring.service.ColisDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequestMapping("/testDef")
@RestController
@RequiredArgsConstructor
public class TestController {

    private final ColisDetailsService service;

    @GetMapping
    public ResponseEntity<?> findByClientExpedietur() {

        Map<String,Long>map= service.getByIdClientExpediteur();

        if(map.size()==0){
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("Aucune Data");
        }
        return ResponseEntity.ok(map);


    }
}
