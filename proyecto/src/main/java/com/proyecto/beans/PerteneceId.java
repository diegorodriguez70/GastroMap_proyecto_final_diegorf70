package com.proyecto.beans;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PerteneceId implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idRestaurante;
    private int idCupon;


    public PerteneceId() {
        // Obligatorio para JPA
    }

    public PerteneceId(int idRestaurante, int idCupon) {
        this.idRestaurante = idRestaurante;
        this.idCupon = idCupon;
    }


	public int getIdRestaurante() {
		return idRestaurante;
	}

	public void setIdRestaurante(int idRestaurante) {
		this.idRestaurante = idRestaurante;
	}

	public int getIdCupon() {
		return idCupon;
	}

	public void setIdCupon(int idCupon) {
		this.idCupon = idCupon;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PerteneceId that = (PerteneceId) o;

        return idRestaurante == that.idRestaurante && idCupon == that.idCupon;
    }

    @Override
    public int hashCode() {
        int result = idRestaurante;
        result = 31 * result + idCupon;
        return result;
    }
}