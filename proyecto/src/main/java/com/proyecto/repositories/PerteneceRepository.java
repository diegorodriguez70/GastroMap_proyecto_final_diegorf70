package com.proyecto.repositories;

import org.springframework.data.repository.CrudRepository;

import com.proyecto.beans.Pertenece;
import com.proyecto.beans.PerteneceId;

public interface PerteneceRepository extends CrudRepository<Pertenece, PerteneceId> {

    boolean existsByIdIdCuponAndIdIdRestaurante(int idCupon, int idRestaurante);
}
