package com.augmented.developer.backend.controller;

import com.augmented.developer.backend.Response;
import com.augmented.developer.backend.ResponseList;
import com.augmented.developer.backend.model.entities.ProductosEntity;
import com.augmented.developer.backend.model.records.FiltroProducto;
import com.augmented.developer.backend.model.records.Producto;
import com.augmented.developer.backend.repository.ProductosRepository;
import com.augmented.developer.backend.repository.ProductosSpecifications;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/producto")
public class ProductosController {

    private final ProductosRepository productosRepository;

    @Value("${app.pagination.size:25}")
    private int defaultPageSize;

    public ProductosController(ProductosRepository productosRepository) {
        this.productosRepository = productosRepository;
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponseList> getAll(@RequestParam(value = "page", defaultValue = "0") int page) {
        Page<ProductosEntity> pageResult = productosRepository.findAll(PageRequest.of(Math.max(0, (page-1)), Math.max(1, defaultPageSize)));
        List<Producto> productos = pageResult.getContent().stream()
                .map(element -> toRecord(element))
                .collect(Collectors.toList());
        ResponseList response = new ResponseList(HttpStatus.OK.value(), null, productos.toArray(), defaultPageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getByCode/{codigo}")
    public ResponseEntity<Response> getByCode(@PathVariable int codigo) {
        return productosRepository.findById(codigo)
                .map(element -> toRecord(element))
                .map(producto -> new Response(HttpStatus.OK.value(), null, producto))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/getByFilter")
    public ResponseEntity<ResponseList> getByFilter(@RequestBody FiltroProducto filtro, @RequestParam(value = "page", defaultValue = "0") int page) {
        Specification<ProductosEntity> spec = ProductosSpecifications.fromFilter(filtro);
        Page<ProductosEntity> pageResult = productosRepository.findAll(spec, PageRequest.of(Math.max(0, (page-1)), Math.max(1, defaultPageSize)));
        List<Producto> productos = pageResult.getContent().stream()
                .map(element -> toRecord(element))
                .collect(Collectors.toList());
        ResponseList response = new ResponseList(HttpStatus.OK.value(), null, productos.toArray(), defaultPageSize);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable int codigo) {
        if (!productosRepository.existsById(codigo)) {
            return ResponseEntity.notFound().build();
        }
        productosRepository.deleteById(codigo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/nuevo")
    public ResponseEntity<Response> nuevo(@RequestBody Producto producto) {
        ProductosEntity entity = toEntity(producto);
        entity.setCodigo(null); // Asegurar que se genere un nuevo código
        ProductosEntity saved = productosRepository.save(entity);
        Response response = new Response(HttpStatus.CREATED.value(), null, toRecord(saved));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/guardar")
    public ResponseEntity<Response> guardar(@RequestBody Producto producto) {
        ProductosEntity entity = productosRepository.findById(producto.codigo())
                .map(existing -> updateEntity(existing, producto))
                .orElse(toEntity(producto));
        ProductosEntity saved = productosRepository.save(entity);
        Response response = new Response(HttpStatus.OK.value(), null, toRecord(saved));
        return ResponseEntity.ok(response);
    }

    private static boolean matchesFiltro(ProductosEntity entity, FiltroProducto filtro) {
        if (filtro == null) {
            return true;
        }
        if (filtro.codigo() != null && !filtro.codigo().equals(entity.getCodigo())) {
            return false;
        }
        if (filtro.nombre() != null && !containsIgnoreCase(entity.getNombre(), filtro.nombre())) {
            return false;
        }
        if (filtro.marca() != null && !containsIgnoreCase(entity.getMarca(), filtro.marca())) {
            return false;
        }
        if (filtro.precio() != null) {
            if (entity.getPrecio() == null || filtro.precio().compareTo(entity.getPrecio()) != 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean containsIgnoreCase(String value, String search) {
        if (value == null || search == null) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));
    }

    private static Producto toRecord(ProductosEntity entity) {
        return new Producto(
                entity.getCodigo(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getAltura(),
                entity.getAncho(),
                entity.getLargo(),
                entity.getMarca(),
                entity.getPrecio());
    }

    private static ProductosEntity toEntity(Producto producto) {
        return new ProductosEntity(
                producto.codigo(),
                producto.nombre(),
                producto.descripcion(),
                producto.altura(),
                producto.ancho(),
                producto.largo(),
                producto.marca(),
                producto.precio());
    }

    private static ProductosEntity updateEntity(ProductosEntity existing, Producto producto) {
        existing.setNombre(producto.nombre());
        existing.setDescripcion(producto.descripcion());
        existing.setAltura(producto.altura());
        existing.setAncho(producto.ancho());
        existing.setLargo(producto.largo());
        existing.setMarca(producto.marca());
        existing.setPrecio(producto.precio());
        return existing;
    }
}
