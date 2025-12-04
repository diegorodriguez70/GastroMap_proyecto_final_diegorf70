package com.proyecto.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.proyecto.beans.Cupon;
import com.proyecto.beans.Reserva;
import com.proyecto.beans.Restaurante;
import com.proyecto.beans.Usuario;
import com.proyecto.repositories.CuponRepository;
import com.proyecto.repositories.ReservaRepository;
import com.proyecto.repositories.RestauranteRepository;
import com.proyecto.services.CuponService;
import com.proyecto.services.CustomUserDetailsService;
import com.proyecto.services.ReservaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class ReservaController {
	
	@Autowired
	ReservaRepository reservaRepository;
	
	
	@Autowired
	RestauranteRepository restauranteRepository;
	
	
	@Autowired
	ReservaService reservaService;
	
	@Autowired
	private CustomUserDetailsService userService;
	
	@GetMapping("/reservas")
	public ModelAndView getReservas(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    ModelAndView salida;

	    if (usuario.getPerfil().getTipo().equals("ADMIN")) {
	    	salida = new ModelAndView("reservas/reservas");
	    	salida.addObject("reservas", reservaRepository.findAll());
	    }
	    else if (usuario.getPerfil().getTipo().equals("USER")) {
	    	salida = new ModelAndView("reservas/reservas_user");
	    	salida.addObject("reservas",
	                reservaRepository.findByUsuario_NombreUsuario(usuario.getNombreUsuario()));
	    }
	    else if (usuario.getPerfil().getTipo().equals("RESTAURANTE")) {

	        Restaurante restaurante = restauranteRepository
	                .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	        salida = new ModelAndView("reservas/reservas_restaurante");

	        salida.addObject(
	            "reservas",
	            reservaRepository.findByRestaurante_IdRestaurante(
	                restaurante.getIdRestaurante()
	            )
	        );
	    }
	    else {
	    	salida = new ModelAndView("reservas/reservas");
	    	salida.addObject("reservas", new ArrayList<>());
	    }


	    return salida;
	}
	
	
	//Devolver las reservas para un restaurante en especifico
	@GetMapping("/reservas/rest")
	public ModelAndView reservasRestaurante(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    ModelAndView salida = new ModelAndView("reservas/reservas_restaurante");

	    salida.addObject("reservas",
	            reservaRepository.findByRestaurante_IdRestaurante(
	                    restaurante.getIdRestaurante()
	            )
	    );

	    return salida;
	}


	
	@GetMapping("/reservas/{idReserva}")
	public ModelAndView getReservaById(@PathVariable int idReserva, Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());
	    Optional<Reserva> reservaOptional = reservaService.getReservaOptionalById(idReserva);

	    if (reservaOptional.isEmpty()) {
	        return new ModelAndView("redirect:/reservas");
	    }

	    Reserva reserva = reservaOptional.get();

	    // Si es USER y la reserva no es suya prohibido
	    if (usuario.getPerfil().getTipo().equals("USER")
	        && !reserva.getUsuario().getNombreUsuario().equals(usuario.getNombreUsuario())) {

	        return new ModelAndView("redirect:/reservas");
	    }

	    ModelAndView salida = new ModelAndView("reservas/reserva");
	    salida.addObject("reserva", reserva);
	    return salida;
	}

	
	@GetMapping("/reservas/delete/{idReserva}")
	public ModelAndView deleteReserva(@PathVariable int idReserva) {

		reservaService.deleteReservaById(idReserva);
		ModelAndView salida = new ModelAndView("redirect:/reservas");

		return salida;

	}
	



	@GetMapping("/reservas/add")
	public ModelAndView addReservaAdmin(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    // Si NO es admin fuera
	    if (!usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	        return new ModelAndView("redirect:/reservas");
	    }

	    ModelAndView salida = new ModelAndView("reservas/reservaForm");

	    salida.addObject("reserva", new Reserva());
	    salida.addObject("restaurantes", restauranteRepository.findAll());

	    return salida;
	}

	@GetMapping("/reservas/add/user")
	public ModelAndView addReservaUser(
	        @RequestParam int idRestaurante,
	        Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Reserva r = new Reserva();
	    Restaurante restaurante = restauranteRepository
	            .findById(idRestaurante)
	            .orElse(null);

	    r.setRestaurante(restaurante);

	    ModelAndView salida = new ModelAndView("reservas/reservaForm_user");
	    salida.addObject("reserva", r);

	    return salida;
	}

	@PostMapping("/reservas/saveReserva")
	public String saveReserva(
	        @Valid @ModelAttribute Reserva reserva,
	        BindingResult result,
	        Principal principal,
	        HttpServletRequest request) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    // Validar restaurante -1
	    if (reserva.getRestaurante() != null &&
	            reserva.getRestaurante().getIdRestaurante() == -1) {

	        result.rejectValue("restaurante", "error.restaurante", "Debes seleccionar un restaurante");

	        if (usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	            request.setAttribute("restaurantes", restauranteRepository.findAll());
	            return "reservas/reservaForm";
	        }
	        return "reservas/reservaForm_user";
	    }

	    // Validación fecha
	    try {
	        LocalDateTime fecha = LocalDateTime.parse(reserva.getFecha());
	        if (fecha.isBefore(LocalDateTime.now())) {
	            result.rejectValue("fecha", "error.fecha", "No puedes reservar una fecha pasada");
	        }
	    } catch (Exception e) {
	        result.rejectValue("fecha", "error.fecha", "Formato de fecha inválido");
	    }

	    if (result.hasErrors()) {

	        if (usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	            request.setAttribute("restaurantes", restauranteRepository.findAll());
	            return "reservas/reservaForm";
	        }

	        if (usuario.getPerfil().getTipo().equalsIgnoreCase("RESTAURANTE")) {
	            Restaurante restaurante = restauranteRepository
	                    .findByUsuario_NombreUsuario(usuario.getNombreUsuario());
	            request.setAttribute("restaurante", restaurante);
	            return "reservas/reservaForm_rest";
	        }

	        if (usuario.getPerfil().getTipo().equalsIgnoreCase("USER")) {
	            Restaurante restaurante = restauranteRepository
	                    .findById(reserva.getRestaurante().getIdRestaurante())
	                    .orElse(null);
	            request.setAttribute("restaurante", restaurante);
	            return "reservas/reservaForm_user";
	        }

	        return "reservas/reservaForm_user";
	    }

	    // Protección para RESTAURANTE
	    if (usuario.getPerfil().getTipo().equalsIgnoreCase("RESTAURANTE")) {

	        Restaurante restaurante = restauranteRepository
	                .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	        reserva.setRestaurante(restaurante);

	    } else {
	        if (reserva.getRestaurante() != null &&
	                reserva.getRestaurante().getIdRestaurante() > 0) {

	            Restaurante rest = restauranteRepository
	                    .findById(reserva.getRestaurante().getIdRestaurante())
	                    .orElse(null);

	            reserva.setRestaurante(rest);
	        }
	    }
 
	    //  MANTENER USUARIO ORIGINAL SI ES EDICIÓN
	
	    if (reserva.getIdReserva() != 0) {

	        Reserva original = reservaRepository.findById(reserva.getIdReserva()).orElse(null);

	        if (original != null) {
	            reserva.setUsuario(original.getUsuario());
	        }

	    } else {
	        reserva.setUsuario(usuario);
	    }

	    reservaService.saveReserva(reserva);

	    // Redirección según rol
	    if (usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	        return "redirect:/reservas";
	    }

	    if (usuario.getPerfil().getTipo().equalsIgnoreCase("RESTAURANTE")) {
	        return "redirect:/reservas/rest";
	    }

	    return "redirect:/reservas/user";
	}






	@GetMapping("/reservas/updateReserva/{idReserva}")
	public ModelAndView updateReserva(
	        @PathVariable int idReserva,
	        Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());
	    Optional<Reserva> optional = reservaRepository.findById(idReserva);

	    if (optional.isEmpty()) {
	        return new ModelAndView("redirect:/reservas");
	    }

	    Reserva reserva = optional.get();

	    
	    // CASO RESTAURANTE
	    if (usuario.getPerfil().getTipo().equals("RESTAURANTE")) {

	        Restaurante restaurante = restauranteRepository
	                .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	        // Evitar editar reservas de OTRO restaurante
	        if (reserva.getRestaurante().getIdRestaurante() != restaurante.getIdRestaurante()) {
	            return new ModelAndView("redirect:/reservas/rest");
	        }

	        ModelAndView salida = new ModelAndView("reservas/reservaForm_rest");
	        salida.addObject("reserva", reserva);
	        return salida;
	    }

	   
	    // CASO ADMIN
	    ModelAndView salida = new ModelAndView("reservas/reservaForm");
	    salida.addObject("reserva", reserva);
	    salida.addObject("restaurantes", restauranteRepository.findAll());
	    return salida;
	}


	
	
	//para ver las reservas del usuario User:
	@GetMapping("/reservas/user")
	public ModelAndView reservasUsuario(Principal principal) {

	    List<Reserva> reservas = reservaRepository.findByUsuario_NombreUsuario(principal.getName());

	    ModelAndView salida = new ModelAndView("reservas/reservas_user");
	    salida.addObject("reservas", reservas);

	    return salida;
	}
	
	//Para añadir reservas desde el perfil Restaurante solo para el mismo Restaurante
	@GetMapping("/reservas/add/rest")
	public ModelAndView addReservaRestaurante(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    // Seguridad: si NO es un restaurante fuera
	    if (!usuario.getPerfil().getTipo().equalsIgnoreCase("RESTAURANTE")) {
	        return new ModelAndView("redirect:/reservas");
	    }

	    // Obtener el restaurante asociado a este usuario
	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    // Crear reserva nueva
	    Reserva r = new Reserva();
	    r.setRestaurante(restaurante);

	    ModelAndView salida = new ModelAndView("reservas/reservaForm_rest");
	    salida.addObject("reserva", r);

	    return salida;
	}



}
