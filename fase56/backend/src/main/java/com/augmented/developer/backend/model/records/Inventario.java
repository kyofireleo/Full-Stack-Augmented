package com.augmented.developer.backend.model.records;

import java.util.List;

public record Inventario(int id, String fecha, String responsable, List<ProductosInventario> productos) {
}
