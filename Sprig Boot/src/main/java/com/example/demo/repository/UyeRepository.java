package com.example.demo.repository;
import com.example.demo.entity.Uye;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  UyeRepository extends JpaRepository<Uye,Integer> {
//save(uye),findById(id),findAll(),delete(uye) burada tanımlanmıştır Jpa sayesinde
   Optional<Uye> findByEposta(String email);
}
