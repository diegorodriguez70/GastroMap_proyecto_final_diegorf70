package com.proyecto.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.proyecto.beans.Cupon;
import com.proyecto.beans.Reserva;
import com.proyecto.repositories.CuponRepository;
import com.proyecto.repositories.ReservaRepository;
import com.proyecto.services.CuponService;
import com.proyecto.services.ReservaService;

import jakarta.validation.Valid;

@Controller
public class ReservaController {
	
	@Autowired
	ReservaRepository reservaRepository;
	
	@Autowired
	ReservaService reservaService;
	

	@GetMapping("/reservas")
	public ModelAndView getReservas() {

		ModelAndView salida = new ModelAndView("reservas/reservas");
		Iterable<Reserva> reservas = reservaRepository.findAll();

		salida.addObject("reservas", reservas);
		return salida;

	}
	
	@GetMapping("/reservas/{idReserva}")
	public ModelAndView getReservaById(@PathVariable int idReserva) {

		ModelAndView salida = new ModelAndView("reservas/reserva");
		Optional<Reserva> reservaOptional = reservaService.getReservaOptionalById(idReserva);
		salida.addObject("reserva", reservaOptional.get());
		return salida;

	}
	
	@GetMapping("/reservas/delete/{idReserva}")
	public ModelAndView deleteReserva(@PathVariable int idReserva) {

		reservaService.deleteReservaById(idReserva);
		ModelAndView salida = new ModelAndView("redirect:/reservas");

		return salida;

	}
	
	@GetMapping("/reservas/add")
	public ModelAndView addReserva() {

		ModelAndView salida = new ModelAndView("reservas/reservaForm");
		salida.addObject("reserva", new Reserva());
		return salida;

	}
	
	@PostMapping("/reservas/saveReserva")
	public String saveReserva(@Valid @ModelAttribute Reserva reserva, BindingResult result) {

		if (result.hasErrors()) {
			return "reservas/reservaForm";
		}
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

}
