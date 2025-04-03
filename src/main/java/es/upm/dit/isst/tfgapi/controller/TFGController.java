package es.upm.dit.isst.tfgapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import es.upm.dit.isst.tfgapi.model.Estado;
import es.upm.dit.isst.tfgapi.model.Sesion;
import es.upm.dit.isst.tfgapi.model.TFG;
import es.upm.dit.isst.tfgapi.repository.SesionRepository;
import es.upm.dit.isst.tfgapi.repository.TFGRepository;
import jakarta.transaction.Transactional;  // Para @Transactional

// Y las importaciones de Swagger si se usan:
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;



// Anotaciones...(ver abajo)
@RestController
@RequestMapping("/myApi")
public class TFGController {
    private final TFGRepository tfgRepository;
    private final SesionRepository sesionRepository;
    public static final Logger log =
     LoggerFactory.getLogger(TFGController.class);
    public TFGController(TFGRepository t, SesionRepository s) {
    this.tfgRepository = t;
    this.sesionRepository = s;
}
     // Métodos del controlador... (ver a continuación)

/**
* Maneja tanto el caso en el que se pida el listado de todos TFGs 
* como el caso en el que se piden solo los TFGs de un tutor concreto.
* @param tutor
* @return
*/
//@CrossOrigin(origins = "http://localhost:8080")
@GetMapping("/tfgs")
List<TFG> readAll(@RequestParam(name = "tutor", required = false) String tutor) 
{
// No me habría hecho falta especificar el nombre del parámetro,
// porque coincide con el del método
if (tutor != null && !tutor.isEmpty()) {
    return (List<TFG>) tfgRepository.findByTutor(tutor);
// La lista puede estar vacía, pero eso no es un error
} else {
    return (List<TFG>) tfgRepository.findAll();
    }
}

@PostMapping("/tfgs")
ResponseEntity<TFG> create(@RequestBody TFG newTFG) throws URISyntaxException {
// Devolver código de error si el TFG ya existe
if (tfgRepository.findById(newTFG.getAlumno()).isPresent()) {
    return new ResponseEntity<TFG>(HttpStatus.CONFLICT);
}
// TODO comprobar que el alumno satisface los 
 // criterios económicos (matrícula)
// y académicos (créditos aprobados, etc.)
// TODO comprobar que el tutor es profesor adscrito a la ETSIT
TFG result = tfgRepository.save(newTFG);
// TODO enviar notificación por e-mail al tutor
    return ResponseEntity.created(new URI("/tfgs/" +
    result.getAlumno())).body(result);
}

@GetMapping("/tfgs/{id}")
ResponseEntity<TFG> readOne(@PathVariable String id) {
return tfgRepository.findById(id).map(tfg -> ResponseEntity.ok().body(tfg))
.orElse(new ResponseEntity<TFG>(HttpStatus.NOT_FOUND));
}

@PutMapping("/tfgs/{id}") 
ResponseEntity<TFG> update(@RequestBody TFG newTFG, @PathVariable String id)
{
    return tfgRepository.findById(id).map(tfg -> {
    tfg.setAlumno(newTFG.getAlumno());
    // En realidad nunca debería modificarse, producirá error 500
    tfg.setTutor(newTFG.getTutor());
    tfg.setTitulo(newTFG.getTitulo());
    tfg.setResumen(newTFG.getResumen());
    tfg.setEstado(newTFG.getEstado());
    tfg.setMemoria(newTFG.getMemoria());
    tfg.setCalificacion(newTFG.getCalificacion());
    tfg.setMatriculaHonor(newTFG.getMatriculaHonor());
    tfg.setSesion(newTFG.getSesion());
    tfgRepository.save(tfg);
    return ResponseEntity.ok().body(tfg);
}).orElse(new ResponseEntity<TFG>(HttpStatus.NOT_FOUND));
}

@PatchMapping("/tfgs/{id}")
ResponseEntity<TFG> partialUpdate(@RequestBody TFG newTFG,
 @PathVariable String id) {
return tfgRepository.findById(id).map(tfg -> {
if (newTFG.getAlumno() != null) {
tfg.setAlumno(newTFG.getAlumno()); 
 // En realidad nunca debería modificarse, producirá error 500
}
if (newTFG.getTutor() != null) {
tfg.setTutor(newTFG.getTutor());
}
if (newTFG.getTitulo() != null) {
tfg.setTitulo(newTFG.getTitulo());
}
if (newTFG.getResumen() != null) {
tfg.setResumen(newTFG.getResumen());
}
if (newTFG.getEstado() != null) {
tfg.setEstado(newTFG.getEstado());
}
if (newTFG.getMemoria() != null) {
tfg.setMemoria(newTFG.getMemoria());
}
if (newTFG.getCalificacion() != null) {
tfg.setCalificacion(newTFG.getCalificacion());
}
if (newTFG.getMatriculaHonor() != null) {
tfg.setMatriculaHonor(newTFG.getMatriculaHonor());
}
if (newTFG.getSesion() != null) {
tfg.setSesion(newTFG.getSesion());
}
tfgRepository.save(tfg);
return ResponseEntity.ok().body(tfg);
}).orElse(new ResponseEntity<TFG>(HttpStatus.NOT_FOUND));
}

@DeleteMapping("/tfgs/{id}")
ResponseEntity<TFG> delete(@PathVariable String id) {
tfgRepository.deleteById(id);
return ResponseEntity.ok().body(null);
//return new ResponseEntity<TFG>(HttpStatus.NOT_FOUND);
}

@PutMapping("/tfgs/{id}/estado/{estado}")
@Transactional
public ResponseEntity<?> actualizaEstado(@PathVariable String id,
 @PathVariable Estado estado) {
// Spring ya devuelve un error 400 Bad Request si el estado no es válido
return tfgRepository.findById(id).map(tfg -> {
if (!tfg.getEstado().canTransitionTo(estado)) {
return ResponseEntity.badRequest().body(
 "No se puede pasar del estado "
+ tfg.getEstado() + " a " + estado);
}
/* TODO comprobar que se cumplen otros requisitos 
 para poder cambiar de estado,
 p. ej. no se puede avanzar a solicitada defensa 
 si no se ha subido la memoria,
se tienen que cumplir requisitos académicos 
 para avanzar a solicitada defensa,
no se puede avanzar a programada defensa
 si no se ha asignado sesión,
no se puede avanzar a calificado
 si no se ha llegado la fecha de la sesión de defensa,
etc. */
tfg.setEstado(estado);
// TODO notificar a quien corresponda de los cambios de estado
tfgRepository.save(tfg);
return ResponseEntity.ok().body(tfg); 
}).orElse(ResponseEntity.notFound().build());
}

//... métodos del controlador para el document de la memoria y la sesión

@PutMapping(value = "/tfgs/{id}/memoria", consumes = "application/pdf")
@io.swagger.v3.oas.annotations.Operation(
requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
content = {
@Content(mediaType = "application/pdf", 
schema = @Schema(type = "string", format = "binary")
)}))
public ResponseEntity<?> subeMemoria(@PathVariable String id, 
@RequestBody byte[] fileContent) {
return tfgRepository.findById(id).map(tfg -> {
tfg.setMemoria(fileContent);
tfgRepository.save(tfg);
return ResponseEntity.ok("Documento subido correctamente");
}).orElseThrow( 
 // Similar to orElse(ResponseEntity.notFound().build());
() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
 "TFG no encontrado")
); 
}
@GetMapping(value = "/tfgs/{id}/memoria", produces = "application/pdf")
public ResponseEntity<?> descargaMemmoria(@PathVariable String id) {
TFG tfg = tfgRepository.findById(id).orElseThrow(
() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
 "TFG no encontrado"));
if (tfg.getMemoria() == null) {
return ResponseEntity.notFound().build();
}
return ResponseEntity.ok()
.header(HttpHeaders.CONTENT_DISPOSITION, 
 "attachment; filename=\"tfg_document_" + id + ".pdf" + "\"")
.body(new ByteArrayResource(tfg.getMemoria()));
}
@PostMapping("/sesiones")
ResponseEntity<Sesion> createSesion(@RequestBody Sesion newSesion) 
 throws URISyntaxException
 {
// No deberíamos recibir ID en el body, pero tampoco lo comprobaremos
// No consideramos aquí el caso en el que recibamos TFGs, 
 // sino que se asignan después
Sesion result = sesionRepository.save(newSesion);
return ResponseEntity.created(new URI("/sesiones/"
 + result.getId())).body(result);
}
@PostMapping("/sesiones/{id}/tfgs")
ResponseEntity<?> asignaTFG(@PathVariable Long id,
 @RequestBody String alumno) 
 {
return sesionRepository.findById(id).map(sesion -> {
TFG tfg = tfgRepository.findById(alumno).orElseThrow(
() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
"TFG no encontrado"));
tfg.setSesion(sesion);
tfgRepository.save(tfg);
return ResponseEntity.ok().body(tfg);
}).orElse(ResponseEntity.notFound().build());
}
}
    