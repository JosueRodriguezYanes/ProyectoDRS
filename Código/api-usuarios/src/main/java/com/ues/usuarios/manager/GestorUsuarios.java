package com.ues.usuarios.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.ues.usuarios.model.Usuario;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class GestorUsuarios {

    private static final String ARCHIVO = "usuarios.json";

    private final Map<Integer, Usuario> usuarios = new ConcurrentSkipListMap<>();
    private final AtomicInteger siguienteId = new AtomicInteger(1);
    private final ObjectMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    private GestorUsuarios() {
        cargarDesdeArchivo();
    }

    private static class Holder {
        private static final GestorUsuarios INSTANCIA = new GestorUsuarios();
    }

    public static GestorUsuarios getInstance() {
        return Holder.INSTANCIA;
    }

    public Usuario agregar(Usuario usuario) {
        int id = siguienteId.getAndIncrement();
        usuario.setId(id);
        usuarios.put(id, usuario);
        guardarEnArchivo();
        return usuario;
    }

    public List<Usuario> listar() {
        return new ArrayList<>(usuarios.values());
    }

    public Optional<Usuario> obtenerPorId(int id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    public synchronized Optional<Usuario> actualizar(int id, Usuario datos) {
        Usuario existente = usuarios.get(id);
        if (existente == null) {
            return Optional.empty();
        }
        existente.setNombre(datos.getNombre());
        existente.setCorreo(datos.getCorreo());
        guardarEnArchivo();
        return Optional.of(existente);
    }

    public synchronized boolean eliminar(int id) {
        boolean eliminado = usuarios.remove(id) != null;
        if (eliminado) {
            guardarEnArchivo();
        }
        return eliminado;
    }

    // --- Persistencia ---

    private void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return; // Primera ejecución: no hay nada que cargar
        }
        try {
            List<Usuario> lista = mapper.readValue(archivo, new TypeReference<List<Usuario>>() {});
            int maxId = 0;
            for (Usuario u : lista) {
                usuarios.put(u.getId(), u);
                maxId = Math.max(maxId, u.getId());
            }
            siguienteId.set(maxId + 1);
        } catch (JacksonException e) {
            System.err.println("No se pudo leer " + ARCHIVO + ": " + e.getMessage());
        }
    }

    private void guardarEnArchivo() {
        try {
            mapper.writeValue(new File(ARCHIVO), listar());
        } catch (JacksonException e) {
            System.err.println("No se pudo escribir " + ARCHIVO + ": " + e.getMessage());
        }
    }
}