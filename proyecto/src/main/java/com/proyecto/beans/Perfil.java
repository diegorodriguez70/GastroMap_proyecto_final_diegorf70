package com.proyecto.beans;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Perfil {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPerfil;

    private String tipo;   // ADMIN o USER

    @OneToMany(mappedBy = "perfil", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Usuario> usuarios = new ArrayList<>();


}
