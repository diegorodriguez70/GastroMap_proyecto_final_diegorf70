package com.proyecto.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.beans.Perfil;
import com.proyecto.beans.Restaurante;
import com.proyecto.beans.Usuario;
import com.proyecto.repositories.PerfilRepository;
import com.proyecto.repositories.PerteneceRepository;
import com.proyecto.repositories.RestauranteRepository;
import com.proyecto.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class RestauranteService {
	
	@Autowired
	RestauranteRepository restauranteRepository;
	
	@Autowired
	PerfilRepository perfilRepository;

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	PerteneceRepository perteneceRepository;
	
	

	
	public Optional<Restaurante> getRestauranteOptionalById(int idRestaurante){
		
		Optional<Restaurante> restauranteOptional = restauranteRepository.findById(idRestaurante);
		if(restauranteOptional.isPresent()) {
			return restauranteOptional;
		}
		//lanzar una excepcion
		return null;
	}


	@Transactional
	public void deleteRestauranteById(int idRestaurante) {

	    Optional<Restaurante> restauranteOptional = restauranteRepository.findById(idRestaurante);

	    if (restauranteOptional.isEmpty()) {
	        System.out.println("El restaurante no existe");
	        return;
	    }

	    Restaurante restaurante = restauranteOptional.get();

	    // Eliminar relaciones pertenece
	    perteneceRepository.deleteByIdIdRestaurante(idRestaurante);

	    // Eliminar usuario asociado
	    Usuario user = restaurante.getUsuario();
	    if (user != null) {
	    	restaurante.setUsuario(null);
	        usuarioRepository.delete(user);
	    }

	    //  Eliminar restaurante
	    restauranteRepository.delete(restaurante);
	}


	public void saveRestaurante(Restaurante restaurante) {

	    boolean esNuevo = (restaurante.getIdRestaurante() == 0);

	    restauranteRepository.save(restaurante);

	    if (esNuevo) {

	        // genera username (sin espacios, minúsculas)
	        String username = restaurante.getNombre()
	                .toLowerCase()
	                .replace(" ", "_");
	        
	        if (usuarioRepository.existsById(username)) {
	            throw new RuntimeException("Ya existe usuario asociado.");
	        }

	        Usuario nuevo = new Usuario();
	        nuevo.setNombreUsuario(username);
	        nuevo.setNombre(restaurante.getNombre());
	        nuevo.setContrasenia(username);  

	        // perfil restaurante ID = 3
	        Perfil perfilRest = perfilRepository.findById(3).get();
	        nuevo.setPerfil(perfilRest);

	        usuarioRepository.save(nuevo);

	        // vincular restaurante con usuario
	        restaurante.setUsuario(nuevo);
	        restauranteRepository.save(restaurante);
	    }
	}

	

}
