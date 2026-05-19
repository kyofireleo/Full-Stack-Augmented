package com.augmented.developer.backend.repository;

import com.augmented.developer.backend.model.entities.ProductosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductosRepository extends JpaRepository<ProductosEntity, Integer>, JpaSpecificationExecutor<ProductosEntity> {
}
