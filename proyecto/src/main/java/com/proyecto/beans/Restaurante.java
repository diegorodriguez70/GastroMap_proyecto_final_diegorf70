package com.proyecto.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurante {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idRestaurante;
	
	@Size(min = 2, max = 40, message = "No cumple los caracteres del nombre")
	@NotEmpty(message = "No puede estar vacio el nombre")
	private String nombre;
	
	
	private String ubicacion;
	
	private String carta;
	
	private String horarios;
	
	private String contacto;


	
}
