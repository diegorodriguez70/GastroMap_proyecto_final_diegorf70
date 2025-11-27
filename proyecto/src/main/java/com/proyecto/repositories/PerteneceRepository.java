package com.proyecto.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.proyecto.beans.Pertenece;
import com.proyecto.beans.PerteneceId;

import jakarta.transaction.Transactional;

public interface PerteneceRepository extends CrudRepository<Pertenece, PerteneceId> {

    boolean existsByIdIdCuponAndIdIdRestaurante(int idCupon, int idRestaurante);
    
    @Transactional
    void deleteByIdIdCuponAndIdIdRestaurante(int idCupon, int idRestaurante);
    
    @Modifying
    @Transactional
    @Query("INSERT INTO Pertenece (id.idRestaurante, id.idCupon) VALUES (:idRest, :idCupon)")
    void saveRelacion(int idRest, int idCupon);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pertenece p WHERE p.id.idRestaurante = :idRestaurante")
    void deleteByIdIdRestaurante(@Param("idRestaurante") int idRestaurante);
    



}
