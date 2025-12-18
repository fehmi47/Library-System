package com.example.demo.repository;

import com.example.demo.entity.Kitap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitapRepository extends JpaRepository<Kitap,Integer> {
    List<Kitap> findByAdContainingIgnoreCase(String name);
}
