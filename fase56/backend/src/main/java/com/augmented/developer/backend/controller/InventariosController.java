package com.augmented.developer.backend.controller;

import com.augmented.developer.backend.Response;
import com.augmented.developer.backend.ResponseList;
import com.augmented.developer.backend.model.entities.InventarioProductosEntity;
import com.augmented.developer.backend.model.entities.InventariosEntity;
import com.augmented.developer.backend.model.records.FiltroInventario;
import com.augmented.developer.backend.model.records.Inventario;
import com.augmented.developer.backend.model.records.ProductosInventario;
import com.augmented.developer.backend.repository.InventariosRepository;
import com.augmented.developer.backend.repository.InventariosSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventario")
public class InventariosController {

    private final InventariosRepository inventariosRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${app.pagination.size:25}")
    private int defaultPageSize;

    public InventariosController(InventariosRepository inventariosRepository) {
        this.inventariosRepository = inventariosRepository;
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponseList> getAll(@RequestParam(value = "page", defaultValue = "0") int page) {
        Page<InventariosEntity> pageResult = inventariosRepository.findAll(PageRequest.of(Math.max(0, page), Math.max(1, defaultPageSize)));
        List<Inventario> inventarios = pageResult.getContent().stream()
                .map(element -> toRecord(element))
                .collect(Collectors.toList());
        ResponseList response = new ResponseList(HttpStatus.OK.value(), null, inventarios.toArray(), pageResult.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Response> getById(@PathVariable int id) {
        return inventariosRepository.findById(id)
                .map(InventariosController::toRecord)
                .map(inventario -> new Response(HttpStatus.OK.value(), null, inventario))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/getByFilter")
    public ResponseEntity<ResponseList> getByFilter(@RequestBody FiltroInventario filtro, @RequestParam(value = "page", defaultValue = "0") int page) {
        Specification<InventariosEntity> spec = InventariosSpecifications.fromFilter(filtro);
        Page<InventariosEntity> pageResult = inventariosRepository.findAll(spec, PageRequest.of(Math.max(0, page), Math.max(1, defaultPageSize)));
        List<Inventario> inventarios = pageResult.getContent().stream()
                .map(element -> toRecord(element))
                .collect(Collectors.toList());
        ResponseList response = new ResponseList(HttpStatus.OK.value(), null, inventarios.toArray(), pageResult.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/nuevo")
    public ResponseEntity<Response> nuevo(@RequestBody Inventario inventario) {
        if (inventario == null) {
            return ResponseEntity.badRequest().build();
        }
        if (inventariosRepository.existsById(inventario.id())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        InventariosEntity entity = toEntity(inventario);
        entity.setId(null); // Asegura que se genere un nuevo ID
        InventariosEntity saved = inventariosRepository.save(entity);
        Response response = new Response(HttpStatus.CREATED.value(), null, toRecord(saved));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/guardar/{id}")
    public ResponseEntity<Response> guardar(@PathVariable int id, @RequestBody Inventario inventario) {
        if (inventario == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        InventariosEntity entity = inventariosRepository.findById(id)
                .map(existing -> updateEntity(existing, inventario))
                .orElse(toEntity(inventario));
        InventariosEntity saved = inventariosRepository.save(entity);
        Response response = new Response(HttpStatus.OK.value(), null, toRecord(saved));
        return ResponseEntity.ok(response);
    }

    private static Inventario toRecord(InventariosEntity entity) {
        List<ProductosInventario> productos = entity.getProductos().stream()
                .map(producto -> new ProductosInventario(producto.getCodigo(), producto.getNombre(), producto.getExistenciaActual()))
                .collect(Collectors.toList());
        return new Inventario(entity.getId(), entity.getFecha().toString(), entity.getResponsable(), productos);
    }

    private static InventariosEntity toEntity(Inventario inventario) {
        List<InventarioProductosEntity> productos = inventario.productos().stream()
                .map(producto -> new InventarioProductosEntity(null, producto.codigo(), producto.nombre(), producto.existenciaActual(), null))
                .collect(Collectors.toList());
        return new InventariosEntity(inventario.id(), (inventario.fecha() != null ? LocalDateTime.parse(inventario.fecha(), formatter) : LocalDateTime.now()), inventario.responsable(), productos);
    }

    private static InventariosEntity updateEntity(InventariosEntity existing, Inventario inventario) {
        if(inventario.fecha() != null) {
            existing.setFecha(LocalDateTime.parse(inventario.fecha(), formatter));
        }else{
            existing.setFecha(LocalDateTime.now());
        }
        existing.setResponsable(inventario.responsable());
        existing.getProductos().clear();
        inventario.productos().stream()
                .map(producto -> new InventarioProductosEntity(null, producto.codigo(), producto.nombre(), producto.existenciaActual(), existing))
                .forEach(existing::addProducto);
        return existing;
    }
}
