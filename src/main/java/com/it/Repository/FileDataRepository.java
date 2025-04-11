package com.it.Repository;

import com.it.Entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileDataRepository extends JpaRepository<FileData, Integer> {

    // find by name
    Optional<FileData> findByName(String filename);
}