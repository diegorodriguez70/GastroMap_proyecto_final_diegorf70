package com.proyecto.beans;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	
	@Lob //para hacer que el string sea mas grande (TIPO TEXT)
	private String carta;
	
	@Lob
	private byte[] cartaPdf;

	
	private String horarios;
	
	private String contacto;


	
	@OneToMany(mappedBy = "restaurante", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Pertenece> pertenece = new ArrayList<>();
	
	@OneToMany(mappedBy = "restaurante", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Reserva> reservas = new ArrayList<>();
	
	@OneToOne
	@JoinColumn(name = "nombre_usuario", referencedColumnName = "nombreUsuario", nullable = true)
	private Usuario usuario;

	
	@Override
	public String toString() {
	    return "Restaurante{" +
	            "idRestaurante=" + idRestaurante +
	            ", nombre='" + nombre + '\'' +
	            '}';
	}



}
