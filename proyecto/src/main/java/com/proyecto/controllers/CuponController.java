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
import com.proyecto.repositories.CuponRepository;
import com.proyecto.services.CuponService;

import jakarta.validation.Valid;

@Controller
public class CuponController {
	
	@Autowired
	CuponRepository cuponRepository;
	
	@Autowired
	CuponService cuponService;
		
	

	@GetMapping("/cupones")
	public ModelAndView getCupones() {

		ModelAndView salida = new ModelAndView("cupones/cupones");
		Iterable<Cupon> cupones = cuponRepository.findAll();

		salida.addObject("cupones", cupones);
		return salida;

	}
	@GetMapping("/cupones/{idCupon}")
	public ModelAndView getCuponById(@PathVariable int idCupon) {

		ModelAndView salida = new ModelAndView("cupones/cupon");
		Optional<Cupon> cuponOptional = cuponService.getCuponOptionalById(idCupon);
		salida.addObject("cupon", cuponOptional.get());
		return salida;

	}
	@GetMapping("/cupones/delete/{idCupon}")
	public ModelAndView deleteCupon(@PathVariable int idCupon) {

		cuponService.deleteCuponById(idCupon);
		ModelAndView salida = new ModelAndView("redirect:/cupones");

		return salida;

	}

	@GetMapping("/cupones/add")
	public ModelAndView addCupon() {

		ModelAndView salida = new ModelAndView("cupones/cuponForm");
		salida.addObject("cupon", new Cupon());
		return salida;

	}

	@PostMapping("/cupones/saveCupon")
	public String saveCupon(@Valid @ModelAttribute Cupon cupon, BindingResult result) {

		if (result.hasErrors()) {
			return "cupones/cuponForm";
		}
		cuponService.saveCupon(cupon);
		return "redirect:/cupones";
	}

	@GetMapping("/cupones/updateCupon/{idCupon}")
	public ModelAndView updateCupon(@PathVariable int idCupon) {
		ModelAndView salida = new ModelAndView("cupones/cuponForm");

		Optional<Cupon> cuponOptional = cuponRepository.findById(idCupon);

		if (cuponOptional.isPresent()) {

			salida.addObject("cupon", cuponOptional.get());
		} else {
			salida.setViewName("redirect:/cupones");
		}

		return salida;
	}
	
}