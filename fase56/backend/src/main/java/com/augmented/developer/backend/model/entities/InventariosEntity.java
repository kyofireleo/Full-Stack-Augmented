package com.augmented.developer.backend.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventarios")
public class InventariosEntity {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha", nullable = false)
    private String fecha;

    @Column(name = "responsable", nullable = false)
    private String responsable;

    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InventarioProductosEntity> productos = new ArrayList<>();

    public InventariosEntity() {
    }

    public InventariosEntity(Integer id, LocalDateTime fecha, String responsable, List<InventarioProductosEntity> productos) {
        this.id = id;
        this.fecha = fecha.format(formatter);
        this.responsable = responsable;
        this.productos = productos != null ? productos : new ArrayList<>();
        this.productos.forEach(producto -> producto.setInventario(this));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return LocalDateTime.parse(fecha, formatter);
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha.format(formatter);
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public List<InventarioProductosEntity> getProductos() {
        return productos;
    }

    public void setProductos(List<InventarioProductosEntity> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
        this.productos.forEach(producto -> producto.setInventario(this));
    }

    public void addProducto(InventarioProductosEntity producto) {
        producto.setInventario(this);
        this.productos.add(producto);
    }

    public void removeProducto(InventarioProductosEntity producto) {
        producto.setInventario(null);
        this.productos.remove(producto);
    }
}
