package com.proyecto.beans;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idCupon;
	private String tiempo_duracion;
	private BigDecimal descuento;

}
