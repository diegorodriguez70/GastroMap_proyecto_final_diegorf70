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

	    ModelAndView mv;

	    if (usuario.getPerfil().getTipo().equals("ADMIN")) {
	        mv = new ModelAndView("reservas/reservas");
	        mv.addObject("reservas", reservaRepository.findAll());
	    }
	    else if (usuario.getPerfil().getTipo().equals("USER")) {
	        mv = new ModelAndView("reservas/reservas_user");
	        mv.addObject("reservas",
	                reservaRepository.findByUsuario_NombreUsuario(usuario.getNombreUsuario()));
	    }
	    else if (usuario.getPerfil().getTipo().equals("RESTAURANTE")) {

	        Restaurante restaurante = restauranteRepository
	                .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	        mv = new ModelAndView("reservas/reservas_restaurante");

	        mv.addObject(
	            "reservas",
	            reservaRepository.findByRestaurante_IdRestaurante(
	                restaurante.getIdRestaurante()
	            )
	        );
	    }
	    else {
	        mv = new ModelAndView("reservas/reservas");
	        mv.addObject("reservas", new ArrayList<>());
	    }


	    return mv;
	}
	
	
	//Devolver las reservas para un restaurante en especifico
	@GetMapping("/reservas/rest")
	public ModelAndView reservasRestaurante(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    ModelAndView mv = new ModelAndView("reservas/reservas_restaurante");

	    mv.addObject("reservas",
	            reservaRepository.findByRestaurante_IdRestaurante(
	                    restaurante.getIdRestaurante()
	            )
	    );

	    return mv;
	}


	
	@GetMapping("/reservas/{idReserva}")
	public ModelAndView getReservaById(@PathVariable int idReserva, Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());
	    Optional<Reserva> reservaOptional = reservaService.getReservaOptionalById(idReserva);

	    if (reservaOptional.isEmpty()) {
	        return new ModelAndView("redirect:/reservas");
	    }

	    Reserva reserva = reservaOptional.get();

	    // ✔ Si es USER y la reserva no es suya → prohibido
	    if (usuario.getPerfil().getTipo().equals("USER")
	        && !reserva.getUsuario().getNombreUsuario().equals(usuario.getNombreUsuario())) {

	        return new ModelAndView("redirect:/reservas");
	    }

	    ModelAndView mv = new ModelAndView("reservas/reserva");
	    mv.addObject("reserva", reserva);
	    return mv;
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

	    // Si NO es admin → fuera
	    if (!usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	        return new ModelAndView("redirect:/reservas");
	    }

	    ModelAndView mv = new ModelAndView("reservas/reservaForm");

	    mv.addObject("reserva", new Reserva());
	    mv.addObject("restaurantes", restauranteRepository.findAll());

	    return mv;
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

	    ModelAndView mv = new ModelAndView("reservas/reservaForm_user");
	    mv.addObject("reserva", r);

	    return mv;
	}

	@PostMapping("/reservas/saveReserva")
	public String saveReserva(
	        @Valid @ModelAttribute Reserva reserva,
	        BindingResult result,
	        Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    // ❗ Validación adicional: el restaurante no puede ser -1
	    if (reserva.getRestaurante() != null &&
	        reserva.getRestaurante().getIdRestaurante() == -1) {

	        result.rejectValue("restaurante", "error.restaurante", "Debes seleccionar un restaurante");

	        if (usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	            return "reservas/reservaForm";
	        } else {
	            return "reservas/reservaForm_user";
	        }
	    }

	    // ============================================
	    // ✔ VALIDACIÓN FECHA PASADA
	    // ============================================
	    try {
	        LocalDateTime fecha = LocalDateTime.parse(reserva.getFecha());
	        if (fecha.isBefore(LocalDateTime.now())) {
	            result.rejectValue("fecha", "error.fecha", "No puedes reservar una fecha pasada");
	        }
	    } catch (Exception e) {
	        result.rejectValue("fecha", "error.fecha", "Formato de fecha inválido");
	    }

	    // Si hay errores
	    if (result.hasErrors()) {

	        if (usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	            return "reservas/reservaForm";
	        }

	        if (usuario.getPerfil().getTipo().equalsIgnoreCase("RESTAURANTE")) {
	            return "reservas/reservaForm_rest";
	        }

	        return "reservas/reservaForm_user";
	    }

	    // ============================================
	    // 👇 Protección especial para RESTAURANTE
	    // ============================================
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

	    reserva.setUsuario(usuario);
	    reservaService.saveReserva(reserva);

	    // ============================================
	    // Redirecciones según rol
	    // ============================================
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

	    // ======================================
	    // CASO RESTAURANTE
	    // ======================================
	    if (usuario.getPerfil().getTipo().equals("RESTAURANTE")) {

	        Restaurante restaurante = restauranteRepository
	                .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	        // Evitar editar reservas de OTRO restaurante
	        if (reserva.getRestaurante().getIdRestaurante() != restaurante.getIdRestaurante()) {
	            return new ModelAndView("redirect:/reservas/rest");
	        }

	        ModelAndView mv = new ModelAndView("reservas/reservaForm_rest");
	        mv.addObject("reserva", reserva);
	        return mv;
	    }

	    // ======================================
	    // CASO ADMIN
	    // ======================================
	    ModelAndView mv = new ModelAndView("reservas/reservaForm");
	    mv.addObject("reserva", reserva);
	    mv.addObject("restaurantes", restauranteRepository.findAll());
	    return mv;
	}


	
	
	//para ver las reservas del usuario User:
	@GetMapping("/reservas/user")
	public ModelAndView reservasUsuario(Principal principal) {

	    List<Reserva> reservas = reservaRepository.findByUsuario_NombreUsuario(principal.getName());

	    ModelAndView mv = new ModelAndView("reservas/reservas_user");
	    mv.addObject("reservas", reservas);

	    return mv;
	}
	
	//Para añadir reservas desde el perfil Restaurante solo para el mismo Restaurante
	@GetMapping("/reservas/add/rest")
	public ModelAndView addReservaRestaurante(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    // Seguridad: si NO es un restaurante → fuera
	    if (!usuario.getPerfil().getTipo().equalsIgnoreCase("RESTAURANTE")) {
	        return new ModelAndView("redirect:/reservas");
	    }

	    // Obtener el restaurante asociado a este usuario
	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    // Crear reserva nueva
	    Reserva r = new Reserva();
	    r.setRestaurante(restaurante);

	    ModelAndView mv = new ModelAndView("reservas/reservaForm_rest");
	    mv.addObject("reserva", r);

	    return mv;
	}



}
