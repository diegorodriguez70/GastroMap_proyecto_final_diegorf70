package com.proyecto.controllers;

import java.security.Principal;
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
	
//	@GetMapping("/reservas")
//	public ModelAndView getReservas() {
//
//		ModelAndView salida = new ModelAndView("reservas/reservas");
//		Iterable<Reserva> reservas = reservaRepository.findAll();
//
//		salida.addObject("reservas", reservas);
//		return salida;
//
//	}
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
	    else {
	        // PERFIL RESTAURANTE (más adelante)
	        mv = new ModelAndView("reservas/reservas_user");
	        mv.addObject("reservas", new ArrayList<>());
	    }

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
	
//	@GetMapping("/reservas/add")
//	public ModelAndView addReserva() {
//
//		ModelAndView salida = new ModelAndView("reservas/reservaForm");
//		salida.addObject("reserva", new Reserva());
//		return salida;
//
//	}
	
	@GetMapping("/reservas/add")
	public ModelAndView addReserva(@RequestParam int idRestaurante, Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());
	    ModelAndView mv;

	    // PREPARAMOS LA RESERVA
	    Reserva r = new Reserva();
	    Restaurante restaurante = restauranteRepository.findById(idRestaurante).orElse(null);
	    r.setRestaurante(restaurante);

	    // ✔ ADMIN → formulario completo
	    if (usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	        mv = new ModelAndView("reservas/reservaForm");
	        mv.addObject("restaurantes", restauranteRepository.findAll()); // ADMIN sí necesita la lista
	    }

	    // ✔ USER → formulario simplificado
	    else {
	        mv = new ModelAndView("reservas/reservaForm_user");
	        // nada más, el restaurante ya viene fijado y no se selecciona
	    }

	    mv.addObject("reserva", r);
	    return mv;
	}



	
//	@PostMapping("/reservas/saveReserva")
//	public String saveReserva(@Valid @ModelAttribute Reserva reserva, BindingResult result) {
//
//		if (result.hasErrors()) {
//			return "reservas/reservaForm";
//		}
//		reservaService.saveReserva(reserva);
//		return "redirect:/reservas";
//	}
	
	@PostMapping("/reservas/saveReserva")
	public String saveReserva(@Valid @ModelAttribute Reserva reserva,
	                          BindingResult result, Principal principal) {

	    if (result.hasErrors()) {
	        return "reservas/reservaForm";
	    }

	    // Obtener usuario logueado
	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    reserva.setUsuario(usuario);

	    reservaService.saveReserva(reserva);

	    return "redirect:/reservas";
	}
	
	
	@GetMapping("/reservas/updateReserva/{idReserva}")
	public ModelAndView updateReserva(@PathVariable int idReserva) {
		ModelAndView salida = new ModelAndView("reservas/reservaForm");

		Optional<Reserva> reservaOptional = reservaRepository.findById(idReserva);

		if (reservaOptional.isPresent()) {

			salida.addObject("reserva", reservaOptional.get());
		} else {
			salida.setViewName("redirect:/reservas");
		}

		return salida;
	}
	
	
	//para ver las reservas del usuario User:
	@GetMapping("/reservas/user")
	public ModelAndView reservasUsuario(Principal principal) {

	    List<Reserva> reservas = reservaRepository.findByUsuario_NombreUsuario(principal.getName());

	    ModelAndView mv = new ModelAndView("reservas/reservas_user");
	    mv.addObject("reservas", reservas);

	    return mv;
	}


}
