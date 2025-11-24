package com.proyecto.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.beans.Restaurante;
import com.proyecto.repositories.RestauranteRepository;

import jakarta.transaction.Transactional;

@Service
public class RestauranteService {
	
	@Autowired
	RestauranteRepository restauranteRepository;
	
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
	    try {

	        restauranteRepository.desvincularUsuario(idRestaurante);

	        restauranteRepository.deleteById(idRestaurante);

	    } catch (Exception e) {
	        System.out.println("Error al borrar restaurante: " + e.getMessage());
	    }
	}


	public void saveRestaurante (Restaurante restaurante) {
		
		restauranteRepository.save(restaurante);
	}
	
	/*public void updateRestauranteById(int idRestaurante) {
		
		Optional<Restaurante> restaurante = restauranteRepository.findById(idRestaurante);
		if(restaurante.isPresent()) {
			
			
		}
	}*/
}
