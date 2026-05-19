package com.augmented.developer.backend.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventario_productos")
public class InventarioProductosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int codigo;
    private String nombre;
    private int existenciaActual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id")
    private InventariosEntity inventario;

    public InventarioProductosEntity() {
    }

    public InventarioProductosEntity(Integer id, int codigo, String nombre, int existenciaActual, InventariosEntity inventario) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.existenciaActual = existenciaActual;
        this.inventario = inventario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getExistenciaActual() {
        return existenciaActual;
    }

    public void setExistenciaActual(int existenciaActual) {
        this.existenciaActual = existenciaActual;
    }

    public InventariosEntity getInventario() {
        return inventario;
    }

    public void setInventario(InventariosEntity inventario) {
        this.inventario = inventario;
    }
}
