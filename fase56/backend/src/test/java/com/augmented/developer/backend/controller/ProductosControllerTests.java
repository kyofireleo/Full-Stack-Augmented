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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductosControllerTests {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void productoCrudFlow() throws Exception {
        var nuevoProducto = new ProductoPayload(999, "Prueba", "Descripcion de prueba", 1.1, 2.2, 3.3, "MarcaX", "123.45");

        ResponseEntity<String> crearResponse = restTemplate.postForEntity(
                baseUrl() + "/producto/nuevo",
                jsonEntity(nuevoProducto),
                String.class);

        assertEquals(201, crearResponse.getStatusCode().value());
        JsonNode crearBody = objectMapper.readTree(crearResponse.getBody());
        JsonNode crearData = crearBody.get("data");
        assertEquals(999, crearData.get("codigo").asInt());
        assertEquals("Prueba", crearData.get("nombre").asText());

        ResponseEntity<String> getByCodeResponse = restTemplate.getForEntity(
                baseUrl() + "/producto/getByCode/999",
                String.class);
        assertEquals(200, getByCodeResponse.getStatusCode().value());
        JsonNode getByCodeBody = objectMapper.readTree(getByCodeResponse.getBody());
        JsonNode getByCodeData = getByCodeBody.get("data");
        assertEquals("Descripcion de prueba", getByCodeData.get("descripcion").asText());

        var filtroProducto = new FiltroProductoPayload(null, "prue", "marc", null);
        ResponseEntity<String> filtroResponse = restTemplate.postForEntity(
                baseUrl() + "/producto/getByFilter",
                jsonEntity(filtroProducto),
                String.class);
        assertEquals(200, filtroResponse.getStatusCode().value());
        JsonNode filtroBody = objectMapper.readTree(filtroResponse.getBody());
        assertEquals(1, filtroBody.get("data").size());
        assertEquals(1, filtroBody.get("pageSize").asInt());

        var productoActualizado = new ProductoPayload(999, "Prueba Actualizada", "Descripcion actualizada", 1.1, 2.2, 3.3, "MarcaX", "123.45");
        ResponseEntity<String> updateResponse = restTemplate.postForEntity(
                baseUrl() + "/producto/guardar",
                jsonEntity(productoActualizado),
                String.class);
        assertEquals(200, updateResponse.getStatusCode().value());
        JsonNode updateBody = objectMapper.readTree(updateResponse.getBody());
        JsonNode updateData = updateBody.get("data");
        assertEquals("Prueba Actualizada", updateData.get("nombre").asText());

        restTemplate.delete(baseUrl() + "/producto/eliminar/999");

        try {
            restTemplate.getForEntity(baseUrl() + "/producto/getByCode/999", String.class);
        }
        catch (HttpClientErrorException ex) {
            assertEquals(404, ex.getStatusCode().value());
        }
    }

    @Test
    void productoGetByCodeNotFound() {
        try {
            restTemplate.getForEntity(baseUrl() + "/producto/getByCode/123456", String.class);
        } catch (HttpClientErrorException ex) {
            assertEquals(404, ex.getStatusCode().value());
        }
    }

    @Test
    void productoGetByFilterNoMatch() throws Exception {
        var filtroProducto = new FiltroProductoPayload(null, "noexiste", "marcaNoExiste", null);
        ResponseEntity<String> filtroResponse = restTemplate.postForEntity(
                baseUrl() + "/producto/getByFilter",
                jsonEntity(filtroProducto),
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

    private static final class FiltroProductoPayload {
        public Integer codigo;
        public String nombre;
        public String marca;
        public String precio;

        FiltroProductoPayload(Integer codigo, String nombre, String marca, String precio) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.marca = marca;
            this.precio = precio;
        }
    }
}
