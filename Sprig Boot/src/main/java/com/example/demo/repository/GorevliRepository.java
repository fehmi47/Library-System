package com.example.demo.repository;

import com.example.demo.entity.Gorevli;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GorevliRepository extends JpaRepository<Gorevli,Long> {
    Optional<Gorevli> findByEposta(String email);
}
