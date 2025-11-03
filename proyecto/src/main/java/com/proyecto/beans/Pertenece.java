package com.proyecto.beans;

import jakarta.persistence.*;

@Entity
@Table(name = "pertenece")
public class Pertenece {

    @EmbeddedId
    private PerteneceId id; 


	@ManyToOne
    @MapsId("idCupon")  
    @JoinColumn(name = "id_cupon", referencedColumnName = "idCupon", insertable = false, updatable = false)
    private Cupon cupon;

    @ManyToOne
    @MapsId("idRestaurante")  
    @JoinColumn(name = "id_restaurante", referencedColumnName = "idRestaurante", insertable = false, updatable = false)
    private Restaurante restaurante;


    public Cupon getCupon() {
		return cupon;
	}

	public void setCupon(Cupon cupon) {
		this.cupon = cupon;
	}

	public PerteneceId getId() {
		return id;
	}

	public void setId(PerteneceId id) {
		this.id = id;
	}

	public Restaurante getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(Restaurante restaurante) {
		this.restaurante = restaurante;
	}

 
}
