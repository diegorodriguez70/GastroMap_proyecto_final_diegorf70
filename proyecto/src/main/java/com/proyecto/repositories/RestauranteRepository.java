package com.proyecto.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.proyecto.beans.Restaurante;

import jakarta.transaction.Transactional;

public interface RestauranteRepository extends CrudRepository<Restaurante, Integer>{

	@Modifying
	@Transactional
	@Query("UPDATE Restaurante r SET r.usuario = null WHERE r.idRestaurante = :id")
	void desvincularUsuario(@Param("id") int idRestaurante);

}
