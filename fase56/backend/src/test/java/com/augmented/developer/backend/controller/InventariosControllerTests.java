package com.augmented.developer.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventariosControllerTests {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void inventarioCrudFlow() throws Exception {
        var fecha = LocalDateTime.now().withNano(0);
        var registroProducto = new ProductoPayload(888, "Producto Inventario", "Descripción", 1.0, 1.0, 1.0, "MarcaInv", "10.00");

        ResponseEntity<String> crearProductoResponse = restTemplate.postForEntity(
                baseUrl() + "/producto/nuevo",
                jsonEntity(registroProducto),
                String.class);
        assertEquals(201, crearProductoResponse.getStatusCode().value());

        var inventarioPayload = new InventarioPayload(
                1,
                fecha.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "Responsable Prueba",
                new ProductoInventarioPayload[]{
                        new ProductoInventarioPayload(888, "Producto Inventario", 25)
                }
        );

        ResponseEntity<String> crearInventarioResponse = restTemplate.postForEntity(
                baseUrl() + "/inventario/nuevo",
                jsonEntity(inventarioPayload),
                String.class);
        assertEquals(201, crearInventarioResponse.getStatusCode().value());
        JsonNode inventarioBody = objectMapper.readTree(crearInventarioResponse.getBody());
        JsonNode inventarioData = inventarioBody.get("data");
        assertEquals(1, inventarioData.get("id").asInt());
        assertEquals(25, inventarioData.get("productos").get(0).get("existenciaActual").asInt());

        ResponseEntity<String> getByIdResponse = restTemplate.getForEntity(
                baseUrl() + "/inventario/getById/1",
                String.class);
        assertEquals(200, getByIdResponse.getStatusCode().value());
        JsonNode getByIdBody = objectMapper.readTree(getByIdResponse.getBody());
        JsonNode getByIdData = getByIdBody.get("data");
        assertEquals("Responsable Prueba", getByIdData.get("responsable").asText());

        var filtro = new FiltroInventarioPayload(null, null, "responsable");
        ResponseEntity<String> filtroResponse = restTemplate.postForEntity(
                baseUrl() + "/inventario/getByFilter",
                jsonEntity(filtro),
                String.class);
        assertEquals(200, filtroResponse.getStatusCode().value());
        JsonNode filtroBody = objectMapper.readTree(filtroResponse.getBody());
        assertEquals(1, filtroBody.get("data").size());
        assertEquals(1, filtroBody.get("pageSize").asInt());

        var inventarioActualizado = new InventarioPayload(
                1,
                fecha.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "Responsable Actualizado",
                new ProductoInventarioPayload[]{
                        new ProductoInventarioPayload(888, "Producto Inventario", 30)
                }
        );

        ResponseEntity<String> guardarResponse = restTemplate.postForEntity(
                baseUrl() + "/inventario/guardar",
                jsonEntity(inventarioActualizado),
                String.class);
        assertEquals(200, guardarResponse.getStatusCode().value());
        JsonNode guardarBody = objectMapper.readTree(guardarResponse.getBody());
        JsonNode guardarData = guardarBody.get("data");
        assertEquals("Responsable Actualizado", guardarData.get("responsable").asText());
        assertEquals(30, guardarData.get("productos").get(0).get("existenciaActual").asInt());
    }

    @Test
    void inventarioGetByIdNotFound() {
        try {
            restTemplate.getForEntity(baseUrl() + "/inventario/getById/999999", String.class);
        } catch (HttpClientErrorException ex) {
            assertEquals(404, ex.getStatusCode().value());
        }
    }

    @Test
    void inventarioGetByFilterNoMatch() throws Exception {
        var filtro = new FiltroInventarioPayload(null, null, "noexiste");
        ResponseEntity<String> filtroResponse = restTemplate.postForEntity(
                baseUrl() + "/inventario/getByFilter",
                jsonEntity(filtro),
                String.class);
        assertEquals(200, filtroResponse.getStatusCode().value());
        JsonNode filtroBody = objectMapper.readTree(filtroResponse.getBody());
        assertEquals(0, filtroBody.get("data").size());
        assertEquals(0, filtroBody.get("pageSize").asInt());
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpEntity<String> jsonEntity(Object body) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
    }

    private static final class ProductoPayload {
        public int codigo;
        public String nombre;
        public String descripcion;
        public Double altura;
        public Double ancho;
        public Double largo;
        public String marca;
        public String precio;

        ProductoPayload(int codigo, String nombre, String descripcion, Double altura, Double ancho, Double largo, String marca, String precio) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.altura = altura;
            this.ancho = ancho;
            this.largo = largo;
            this.marca = marca;
            this.precio = precio;
        }
    }

    private static final class InventarioPayload {
        public int id;
        public String fecha;
        public String responsable;
        public ProductoInventarioPayload[] productos;

        InventarioPayload(int id, String fecha, String responsable, ProductoInventarioPayload[] productos) {
            this.id = id;
            this.fecha = fecha;
            this.responsable = responsable;
            this.productos = productos;
        }
    }

    private static final class ProductoInventarioPayload {
        public int codigo;
        public String nombre;
        public int existenciaActual;

        ProductoInventarioPayload(int codigo, String nombre, int existenciaActual) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.existenciaActual = existenciaActual;
        }
    }

    private static final class FiltroInventarioPayload {
        public Integer id;
        public String fecha;
        public String responsable;

        FiltroInventarioPayload(Integer id, String fecha, String responsable) {
            this.id = id;
            this.fecha = fecha;
            this.responsable = responsable;
        }
    }
}
