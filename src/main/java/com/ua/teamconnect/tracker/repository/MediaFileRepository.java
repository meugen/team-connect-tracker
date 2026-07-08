package com.ua.teamconnect.tracker.repository;

import com.ua.teamconnect.tracker.model.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Integer> {

    Optional<MediaFile> findByUrl(String url);
    
}
