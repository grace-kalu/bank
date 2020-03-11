package com.dev.bank.controller;

import com.dev.bank.model.Demo;
import com.dev.bank.repository.DemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class DemoController {
    @Autowired
    private DemoRepository demoRepository;
    @GetMapping("/demo")
    public List<Demo> getAllDemos(){
        return demoRepository.findAll();
    }
    @GetMapping("/demo/again")
    public ResponseEntity<?> getAllDemosAgain(){
        return ResponseEntity.ok().body( demoRepository.findAll());
    }
    @PostMapping("/demo")
    public ResponseEntity<?> createADemo(@RequestBody Demo d){
        return ResponseEntity.status(HttpStatus.OK).body(demoRepository.save(d));
    }
}
