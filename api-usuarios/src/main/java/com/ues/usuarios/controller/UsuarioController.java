package com.ues.usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ues.usuarios.manager.GestorUsuarios;
import com.ues.usuarios.model.Usuario;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    // Siempre la misma instancia, obtenida a través del Singleton
    private final GestorUsuarios gestor = GestorUsuarios.getInstance();

    @PostMapping
    public ResponseEntity<Usuario> agregar(@RequestBody Usuario usuario) {
        Usuario creado = gestor.agregar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<Usuario> listar() {
        return gestor.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable int id) {
        return gestor.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping ("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable int id, @RequestBody Usuario datos) {
        return gestor.actualizar(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return gestor.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
