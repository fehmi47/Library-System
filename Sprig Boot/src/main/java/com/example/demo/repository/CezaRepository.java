package com.example.demo.repository;

import com.example.demo.entity.Ceza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CezaRepository extends JpaRepository<Ceza,Long> {
    List<Ceza> findByEmanet_Uye_Eposta(String eposta);
}
