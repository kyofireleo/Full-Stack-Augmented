package com.example.restservice;

import org.springframework.web.bind.annotation.RestController;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class HousingLocationController {

	private ResourceLoader loader = new DefaultResourceLoader();
	private ObjectMapper mapper = new ObjectMapper();
	private Resource resource = loader.getResource("db.json");;

	@GetMapping("/locations/all/")
	public ResponseEntity<ResponseList> getAllHousingLocations() {
		try(InputStream inputStream = resource.getInputStream()){
			List<HousingLocation> locations = mapper.readValue(inputStream, new TypeReference<List<HousingLocation>>() {});
			return ResponseEntity.ok(new ResponseList(HttpStatus.OK.value(), null, locations.toArray()));
			
		}catch(Exception e){
			e.printStackTrace();
			return ResponseEntity.ok(new ResponseList(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
		}
	}

	@GetMapping("/locations/byId/{id}")
	public ResponseEntity<Response> getAllHousingLocations(@PathVariable String id) {
		try(InputStream inputStream = resource.getInputStream()){
			List<HousingLocation> locations = mapper.readValue(inputStream, new TypeReference<List<HousingLocation>>() {});
			List<HousingLocation> filterdList = locations.stream().filter(location -> (""+location.id()).equals(id)).collect(Collectors.toList());

			if(!filterdList.isEmpty())
				return ResponseEntity.ok(new Response(HttpStatus.OK.value(), null,filterdList.get(0)));
			else
				return ResponseEntity.ok(new Response(HttpStatus.NOT_FOUND.value(), "No se encontraron registros!", null));
		}catch(Exception e){
			e.printStackTrace();
			return ResponseEntity.ok(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
		}
	}

	@PutMapping("/locations/new")
	public ResponseEntity<Response> createNewHousingLocation(@RequestBody HousingLocation location) {
		try(InputStream inputStream = resource.getInputStream()){
			List<HousingLocation> locations = mapper.readValue(inputStream, new TypeReference<List<HousingLocation>>() {});
			locations.add(location);
			
			JsonGenerator g = mapper.createGenerator(new FileOutputStream(resource.getFile()));
			mapper.writerWithDefaultPrettyPrinter().writeValue(g, locations);
			g.close();
			
			return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Elemento guardado correctamente!", location));
			
		}catch(Exception e){
			e.printStackTrace();
			return ResponseEntity.ok(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
		}
	}

	@DeleteMapping("/locations/delete/byId/{id}")
	public ResponseEntity<Response> deleteHousingLocationById(@PathVariable String id){
		try(InputStream inputStream = resource.getInputStream()){
			List<HousingLocation> locations = mapper.readValue(inputStream, new TypeReference<List<HousingLocation>>() {});
			List<HousingLocation> filterdList = locations.stream().filter(location -> (""+location.id()).equals(id)).collect(Collectors.toList());

			if(!filterdList.isEmpty()){
				locations.remove(filterdList.get(0));
				JsonGenerator g = mapper.createGenerator(new FileOutputStream(resource.getFile()));
				mapper.writerWithDefaultPrettyPrinter().writeValue(g, locations);
				g.close();
			
				return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Elemento eliminado correctamente!", null));
			}else{
				return ResponseEntity.ok(new Response(HttpStatus.NOT_FOUND.value(), "No se encontraron registros!", null));
			}
		}catch(Exception e){
			e.printStackTrace();
			return ResponseEntity.ok(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
		}
	}

	@PatchMapping("/locations/update/")
	public ResponseEntity<Response> updateHousingLocation(@RequestBody HousingLocation updatedLocation){
		try(InputStream inputStream = resource.getInputStream()){
			List<HousingLocation> locations = mapper.readValue(inputStream, new TypeReference<List<HousingLocation>>() {});
			List<HousingLocation> filterdList = locations.stream().filter(location -> location.id() == updatedLocation.id()).collect(Collectors.toList());

			if(!filterdList.isEmpty()){
				locations.remove(filterdList.get(0));
				locations.add(updatedLocation);
				JsonGenerator g = mapper.createGenerator(new FileOutputStream(resource.getFile()));
				mapper.writerWithDefaultPrettyPrinter().writeValue(g, locations);
				g.close();
			
				return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Elemento actualizado correctamente!", null));
			}else{
				return ResponseEntity.ok(new Response(HttpStatus.NOT_FOUND.value(), "No se encontraron registros!", null));
			}
		}catch(Exception e){
			e.printStackTrace();
			return ResponseEntity.ok(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
		}
	}
}
