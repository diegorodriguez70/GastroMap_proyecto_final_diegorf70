package com.proyecto.repositories;

import org.springframework.data.repository.CrudRepository;

import com.proyecto.beans.Usuario;


public interface UsuarioRepository extends CrudRepository<Usuario, String>{

	
	Usuario findByNombreUsuario(String nombreUsuario);

}
