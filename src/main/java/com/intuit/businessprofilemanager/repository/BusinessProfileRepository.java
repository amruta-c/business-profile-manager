package com.intuit.businessprofilemanager.repository;

import com.intuit.businessprofilemanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessProfileRepository extends JpaRepository<ProfileEntity, Long> {
}
