package com.proyecto.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Usuario implements UserDetails{


    private static final long serialVersionUID = 1L;

    @Id
    private String nombreUsuario;

    private String nombre;

    private String contrasenia;

    @ManyToOne
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    @OneToOne(mappedBy = "usuario")
    private Restaurante restaurante;

    // -------------------------------------------------------
    // SPRING SECURITY: construcción de permisos por perfil
    // -------------------------------------------------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> permisos = new ArrayList<>();

        if (perfil.getTipo().equalsIgnoreCase("ADMIN")) {
            permisos.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            permisos.add(new SimpleGrantedAuthority("PERMISO_VER"));
            permisos.add(new SimpleGrantedAuthority("PERMISO_BORRAR"));
        } else if (perfil.getTipo().equalsIgnoreCase("USER")) {
            permisos.add(new SimpleGrantedAuthority("ROLE_USER"));
            permisos.add(new SimpleGrantedAuthority("PERMISO_VER"));
        } else if (perfil.getTipo().equalsIgnoreCase("RESTAURANTE")) {
            permisos.add(new SimpleGrantedAuthority("ROLE_RESTAURANTE"));
            permisos.add(new SimpleGrantedAuthority("PERMISO_VER"));
        }


        return permisos;
    }

    public Restaurante getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(Restaurante restaurante) {
		this.restaurante = restaurante;
	}

	// Métodos obligatorios de UserDetails
    @Override public String getPassword() { return contrasenia; }
    @Override public String getUsername() { return nombreUsuario; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // Getters & setters normales
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }

	
}


