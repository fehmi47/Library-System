package com.example.demo.repository;

import com.example.demo.entity.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KategoriRepository extends JpaRepository<Kategori,Integer> {
}
