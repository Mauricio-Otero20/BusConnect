package com.example.busconnect.domine.repositories;

import com.example.busconnect.domine.entities.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {

    Optional<Config> findByKey(String key);

    boolean existsByKey(String key);

    void deleteByKey(String key);
}
