package com.augmented.developer.backend.model.records;

import java.math.BigDecimal;

public record Producto(int codigo, String nombre, String descripcion, Double altura, Double ancho, Double largo, String marca, BigDecimal precio) {
    public Producto {
        nombre = nombre == null ? "" : nombre;
        descripcion = descripcion == null ? "" : descripcion;
        marca = marca == null ? "" : marca;
    }
}
