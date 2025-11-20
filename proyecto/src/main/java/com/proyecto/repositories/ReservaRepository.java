package com.proyecto.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.proyecto.beans.Reserva;

public interface ReservaRepository extends CrudRepository<Reserva, Integer>{

	List<Reserva> findByUsuario_NombreUsuario(String nombreUsuario);

}
