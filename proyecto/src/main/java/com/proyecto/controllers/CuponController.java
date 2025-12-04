package com.proyecto.controllers;


import java.security.Principal;
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
import com.proyecto.beans.Pertenece;
import com.proyecto.beans.PerteneceId;
import com.proyecto.beans.Restaurante;
import com.proyecto.beans.Usuario;
import com.proyecto.repositories.CuponRepository;
import com.proyecto.repositories.PerteneceRepository;
import com.proyecto.repositories.RestauranteRepository;
import com.proyecto.services.CuponService;
import com.proyecto.services.CustomUserDetailsService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
public class CuponController {
	
	@Autowired
	CuponRepository cuponRepository;
	
	@Autowired
	CuponService cuponService;
		
	
	@Autowired
	RestauranteRepository restauranteRepository;

	@Autowired
	PerteneceRepository perteneceRepository;

	@Autowired
	private CustomUserDetailsService userService;

	

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
	public String saveCupon(
	        @Valid @ModelAttribute Cupon cupon,
	        BindingResult result,
	        Principal principal,
	        @RequestParam(required = false) Integer idRestaurante) {

	    if (result.hasErrors()) {
	        return "cupones/cuponForm";
	    }

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    cuponService.saveCupon(cupon);

	    // Si el usuario es restaurante vincular al restaurante
	    if (usuario.getPerfil().getTipo().equalsIgnoreCase("RESTAURANTE")) {

	        Restaurante restaurante = restauranteRepository
	                .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	        perteneceRepository.saveRelacion(restaurante.getIdRestaurante(), cupon.getIdCupon());

	        return "redirect:/cupones/rest";
	    }

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
	
	
	@GetMapping("/restaurantes/{idRestaurante}/cupones/addExistente/{idCupon}")
	public String addCuponExistenteAdmin(
	        @PathVariable int idRestaurante,
	        @PathVariable int idCupon) {

	    // evitar duplicados
	    boolean existe = perteneceRepository
	            .existsByIdIdCuponAndIdIdRestaurante(idCupon, idRestaurante);

	    if (!existe) {

	        Restaurante restaurante = restauranteRepository.findById(idRestaurante).orElse(null);
	        Cupon cupon = cuponRepository.findById(idCupon).orElse(null);

	        Pertenece p = new Pertenece();
	        p.setId(new PerteneceId(idRestaurante, idCupon));
	        p.setRestaurante(restaurante);
	        p.setCupon(cupon);

	        perteneceRepository.save(p);
	    }

	    return "redirect:/restaurantes/" + idRestaurante + "/cupones";
	}

	
	
	
	// RESTAURANTE: ver sus propios cupones
	@GetMapping("/cupones/rest")
	public ModelAndView cuponesRest(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    List<Cupon> cupones = restaurante.getPertenece()
	            .stream()
	            .map(p -> p.getCupon())
	            .toList();

	    ModelAndView salida = new ModelAndView("restaurantes/cupones_restaurante");
	    salida.addObject("cupones", cupones);
	    salida.addObject("restaurante", restaurante); 

	    return salida;
	}
	
	@Transactional // asegura que la eliminación de la relación entre cupón y restaurantese ejecute dentro de una única transacción. Si ocurre algún error, se revierte, evitando inconsistencias en la tabla intermedia "pertenece".
	@GetMapping("/cupones/rest/delete/{idCupon}")
	public String deleteCuponRest(
	        @PathVariable int idCupon,
	        Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    // eliminar SOLO la relación pertenece
	    perteneceRepository.deleteByIdIdCuponAndIdIdRestaurante(
	            idCupon,
	            restaurante.getIdRestaurante()
	    );

	    return "redirect:/cupones/rest";
	}


	@GetMapping("/cupones/add/rest")
	public ModelAndView addCuponRest(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    ModelAndView salida = new ModelAndView("cupones/cuponForm_restaurante");

	    salida.addObject("cupon", new Cupon());
	    salida.addObject("restaurante", restaurante);

	    return salida;
	}

	@GetMapping("/cupones/rest/existentes")
	public ModelAndView verCuponesNoVinculados(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    // Cupones ya asociados
	    List<Integer> idsAsociados = restaurante.getPertenece()
	            .stream()
	            .map(p -> p.getCupon().getIdCupon())
	            .toList();

	    // Filtrar cupones NO asociados
	    List<Cupon> disponibles = ((List<Cupon>) cuponRepository.findAll())
	            .stream()
	            .filter(c -> !idsAsociados.contains(c.getIdCupon()))
	            .toList();

	    ModelAndView salida = new ModelAndView("cupones/cupones_existentes_restaurante");
	    salida.addObject("cupones", disponibles);
	    salida.addObject("restaurante", restaurante);

	    return salida;
	}
	
	@GetMapping("/cupones/rest/addExistente/{idCupon}")
	public String addCuponExistente(@PathVariable int idCupon, Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    // Evitar duplicados
	    boolean existe = perteneceRepository
	            .existsByIdIdCuponAndIdIdRestaurante(idCupon, restaurante.getIdRestaurante());

	    if (!existe) {
	        perteneceRepository.saveRelacion(restaurante.getIdRestaurante(), idCupon);
	    }

	    return "redirect:/cupones/rest";
	}


	@GetMapping("/cupones/rest/deleteAll")
	public String deleteAllCuponesRest(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    Restaurante restaurante = restauranteRepository
	            .findByUsuario_NombreUsuario(usuario.getNombreUsuario());

	    perteneceRepository.deleteByIdIdRestaurante(restaurante.getIdRestaurante());

	    return "redirect:/cupones/rest";
	}
	
	
	@GetMapping("/cupones/endAll")
	public ModelAndView terminarTodosLosCupones() {

	    List<Cupon> cupones = (List<Cupon>) cuponRepository.findAll();

	    for (Cupon cupon : cupones) {
	        cupon.setTiempoDuracion("Terminado");
	        cupon.setCodigo("-");
	        cupon.setDescuento(null);
	    }

	    cuponRepository.saveAll(cupones);

	    return new ModelAndView("redirect:/usuarios");
	}


}