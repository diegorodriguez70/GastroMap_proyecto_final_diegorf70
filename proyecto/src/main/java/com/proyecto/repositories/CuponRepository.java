package com.proyecto.repositories;

import org.springframework.data.repository.CrudRepository;

import com.proyecto.beans.Cupon;

public interface CuponRepository extends CrudRepository<Cupon, Integer> {

	
	boolean existsByCodigo(String codigo);

}
