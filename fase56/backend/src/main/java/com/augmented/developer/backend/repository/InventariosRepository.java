package com.augmented.developer.backend.repository;

import com.augmented.developer.backend.model.entities.InventariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventariosRepository extends JpaRepository<InventariosEntity, Integer>, JpaSpecificationExecutor<InventariosEntity> {
}
