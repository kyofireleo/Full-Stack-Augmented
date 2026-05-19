package com.augmented.developer.backend.repository;

import com.augmented.developer.backend.model.entities.ProductosEntity;
import com.augmented.developer.backend.model.records.FiltroProducto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ProductosSpecifications {

    private ProductosSpecifications() {}

    public static Specification<ProductosEntity> fromFilter(FiltroProducto filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filtro == null) {
                return cb.conjunction();
            }
            if (filtro.codigo() != null) {
                predicates.add(cb.equal(root.get("codigo"), filtro.codigo()));
            }
            if (filtro.nombre() != null) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filtro.nombre().toLowerCase() + "%"));
            }
            if (filtro.marca() != null) {
                predicates.add(cb.like(cb.lower(root.get("marca")), "%" + filtro.marca().toLowerCase() + "%"));
            }
            if (filtro.precio() != null) {
                predicates.add(cb.equal(root.get("precio"), filtro.precio()));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
