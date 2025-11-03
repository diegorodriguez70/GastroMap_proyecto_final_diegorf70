package com.proyecto.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.proyecto.beans.Restaurante;
import com.proyecto.repositories.RestauranteRepository;
import com.proyecto.services.RestauranteService;

import jakarta.validation.Valid;

@Controller
public class RestauranteController {

	@Autowired
	RestauranteRepository restauranteRepository;

	@Autowired
	RestauranteService restauranteService;
	
	
	@GetMapping("/restaurantes")
	public ModelAndView getRestaurantes() {

		ModelAndView salida = new ModelAndView("restaurantes/restaurantes");
		Iterable<Restaurante> restaurantes = restauranteRepository.findAll();

		salida.addObject("restaurantes", restaurantes);
		return salida;

	}

	@GetMapping("/restaurantes/{idRestaurante}")
	public ModelAndView getRestauranteById(@PathVariable int idRestaurante) {

		ModelAndView salida = new ModelAndView("restaurantes/restaurante");
		Optional<Restaurante> restauranteOptional = restauranteService.getRestauranteOptionalById(idRestaurante);
		salida.addObject("restaurante", restauranteOptional.get());
		return salida;

	}

	@GetMapping("/restaurantes/delete/{idRestaurante}")
	public ModelAndView deleteRestaurante(@PathVariable int idRestaurante) {

		restauranteService.deleteRestauranteById(idRestaurante);
		ModelAndView salida = new ModelAndView("redirect:/restaurantes");

		return salida;

	}

	@GetMapping("/restaurantes/add")
	public ModelAndView addRestaurante() {

		ModelAndView salida = new ModelAndView("restaurantes/restauranteForm");
		salida.addObject("restaurante", new Restaurante());
		return salida;

	}

	@PostMapping("/restaurantes/saveRestaurante")
	public String saveRestaurante(@Valid @ModelAttribute Restaurante restaurante, BindingResult result) {

		if (result.hasErrors()) {
			return "restaurantes/restauranteForm";
		}
		restauranteService.saveRestaurante(restaurante);
		return "redirect:/restaurantes";
	}

	@GetMapping("/restaurantes/updateRestaurante/{idRestaurante}")
	public ModelAndView updateRestaurante(@PathVariable int idRestaurante) {
		ModelAndView salida = new ModelAndView("restaurantes/restauranteForm");

		Optional<Restaurante> restauranteOptional = restauranteRepository.findById(idRestaurante);

		if (restauranteOptional.isPresent()) {

			salida.addObject("restaurante", restauranteOptional.get());
		} else {
			salida.setViewName("redirect:/restaurantes");
		}

		return salida;
	}

}
