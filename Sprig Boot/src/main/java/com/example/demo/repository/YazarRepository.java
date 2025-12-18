package com.example.demo.repository;

import com.example.demo.entity.Yazar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YazarRepository extends JpaRepository<Yazar,Integer> {

}
