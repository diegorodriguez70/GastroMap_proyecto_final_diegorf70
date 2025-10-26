package com.proyecto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.proyecto.beans.Cupon;
import com.proyecto.beans.Restaurante;
import com.proyecto.repositories.CuponRepository;
import com.proyecto.services.CuponService;

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

}
