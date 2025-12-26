package com.example.demo.repository;

import com.example.demo.entity.Emanet;
import com.example.demo.entity.Kitap;
import com.example.demo.entity.Uye;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmanetRepository extends JpaRepository<Emanet,Integer> {
    List<Emanet> findAllByUye(Uye uye);

    boolean existsByUyeAndKitapAndGercekTeslimTarihiIsNull(Uye uye, Kitap kitap);
}

