package com.augmented.developer.backend.repository;

import com.augmented.developer.backend.model.entities.InventariosEntity;
import com.augmented.developer.backend.model.records.FiltroInventario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class InventariosSpecifications {

    private InventariosSpecifications() {}

    public static Specification<InventariosEntity> fromFilter(FiltroInventario filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filtro == null) {
                return cb.conjunction();
            }
            if (filtro.id() != null) {
                predicates.add(cb.equal(root.get("id"), filtro.id()));
            }
            if (filtro.responsable() != null && !filtro.responsable().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("responsable")), "%" + filtro.responsable().toLowerCase() + "%"));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
