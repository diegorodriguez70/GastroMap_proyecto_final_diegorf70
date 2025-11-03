package com.proyecto.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	private String tiempoDuracion;
	private BigDecimal descuento;
	
	@OneToMany(mappedBy = "cupon", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Pertenece> pertenece = new ArrayList<>();
}
