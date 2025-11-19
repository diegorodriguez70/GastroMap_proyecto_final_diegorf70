//package com.proyecto.beans;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//public class Usuario implements UserDetails{
//
//	private static final long serialVersionUID = 1L;
//	@Id
//	private String nombreUsuario;
//	private String nombre;
//	private String contrasenia;
//	private int rol = 10;
//	
//	
//
//	@OneToMany(mappedBy = "usuario") 
//	private Reserva reserva; //luego la creamos
//
//
//	
//
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//	    List<SimpleGrantedAuthority> permisos = new ArrayList<>();
//	    if (rol == 1) {
//	        permisos.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//	        permisos.add(new SimpleGrantedAuthority("PERMISO_BORRADO"));
//	        permisos.add(new SimpleGrantedAuthority("PERMISO_VER"));
//	    } else if (rol == 10) {
//	        permisos.add(new SimpleGrantedAuthority("ROLE_USER"));
//	        permisos.add(new SimpleGrantedAuthority("PERMISO_VER"));
//	    }
//	    return permisos;
//	}
//	@Override
//	public boolean isAccountNonExpired() {
//	    return true;
//	}
//
//	@Override
//	public boolean isAccountNonLocked() {
//	    return true;
//	}
//
//	@Override
//	public boolean isCredentialsNonExpired() {
//	    return true;
//	}
//
//	@Override
//	public boolean isEnabled() {
//	    return true;
//	}
//	public String getNombreUsuario() {
//		return nombreUsuario;
//	}
//	public void setNombreUsuario(String nombreUsuario) {
//		this.nombreUsuario = nombreUsuario;
//	}
//	public String getNombre() {
//		return nombre;
//	}
//	public void setNombre(String nombre) {
//		this.nombre = nombre;
//	}
//	public String getContrasenia() {
//		return contrasenia;
//	}
//	public void setContrasenia(String contrasenia) {
//		this.contrasenia = contrasenia;
//	}
//	public int getRol() {
//		return rol;
//	}
//	public void setRol(int rol) {
//		this.rol = rol;
//	}
//	public Reserva getReserva() {
//		return reserva;
//	}
//	public void setReserva(Reserva reserva) {
//		this.reserva = reserva;
//	}
//	@Override
//	public String getPassword() {
//		// TODO Auto-generated method stub
//		return contrasenia;
//	}
//	@Override
//	public String getUsername() {
//		// TODO Auto-generated method stub
//		return nombreUsuario;
//	}
//	
//	
//
//
//
