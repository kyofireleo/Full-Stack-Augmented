package com.augmented.developer.backend.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventarios")
public class InventariosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate fecha;
    private String responsable;

    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InventarioProductosEntity> productos = new ArrayList<>();

    public InventariosEntity() {
    }

    public InventariosEntity(Integer id, String fecha, String responsable, List<InventarioProductosEntity> productos) {
        this.id = id;
        this.fecha = LocalDate.parse(fecha);
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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
