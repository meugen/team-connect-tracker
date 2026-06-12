package com.ua.teamconnect.tracker.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ua.teamconnect.tracker.model.entity.MediaFile;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Integer> {

    Optional<MediaFile> findByUrl(String url);
    
}
