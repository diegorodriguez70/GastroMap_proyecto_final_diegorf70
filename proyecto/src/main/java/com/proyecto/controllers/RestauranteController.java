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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.proyecto.beans.Cupon;
import com.proyecto.beans.Pertenece;
import com.proyecto.beans.PerteneceId;
import com.proyecto.beans.Restaurante;
import com.proyecto.beans.Usuario;
import com.proyecto.repositories.CuponRepository;
import com.proyecto.repositories.PerteneceRepository;
import com.proyecto.repositories.RestauranteRepository;
import com.proyecto.services.CustomUserDetailsService;
import com.proyecto.services.RestauranteService;

import jakarta.validation.Valid;

@Controller
public class RestauranteController {

	@Autowired
	RestauranteRepository restauranteRepository;
	
	@Autowired
	CuponRepository cuponRepository;

	@Autowired
	PerteneceRepository perteneceRepository;

	
	@Autowired
	RestauranteService restauranteService;
	
	@Autowired
	private CustomUserDetailsService userService;
	

	@GetMapping("/restaurantes")
	public ModelAndView getRestaurantes(Principal principal) {

	    Usuario usuario = userService.findByNombreUsuario(principal.getName());

	    ModelAndView mv;

	    if (usuario.getPerfil().getTipo().equalsIgnoreCase("ADMIN")) {
	        mv = new ModelAndView("restaurantes/restaurantes");
	    } 
	    else if (usuario.getPerfil().getTipo().equalsIgnoreCase("USER")) {
	        mv = new ModelAndView("restaurantes/restaurantes_user");
	    } 
	    else {
	        mv = new ModelAndView("restaurantes/restaurantes");
	    }

	    mv.addObject("restaurantes", restauranteRepository.findAll());
	    return mv;
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
	
	@GetMapping("/restaurantes/{idRestaurante}/cupones")
	public ModelAndView verCuponesRestaurante(@PathVariable int idRestaurante) {

	    ModelAndView salida = new ModelAndView("restaurantes/cupones_restaurante_admin");

	    Optional<Restaurante> restauranteOptional = restauranteRepository.findById(idRestaurante);

	    if (restauranteOptional.isPresent()) {
	        Restaurante restaurante = restauranteOptional.get();
	        salida.addObject("restaurante", restaurante);

	        // Lista de cupones asociados
	        List<Cupon> cupones = restaurante.getPertenece()
	                                         .stream()//como un bucle para procesar
	                                         .map(p -> p.getCupon())
	                                         .toList();

	        salida.addObject("cupones", cupones);
	    } else {
	        salida.setViewName("redirect:/restaurantes");
	    }

	    return salida;
	}
	
	@GetMapping("/restaurantes/{idRestaurante}/addCupon")
	public ModelAndView showAddCuponForm(@PathVariable int idRestaurante) {

	    ModelAndView mav = new ModelAndView("restaurantes/addCupon");

	    // Restaurante seleccionado
	    Restaurante restaurante = restauranteRepository.findById(idRestaurante).orElse(null);
	    mav.addObject("restaurante", restaurante);

	    // Todos los cupones
	    Iterable<Cupon> cupones = cuponRepository.findAll();
	    mav.addObject("cupones", cupones);

	    return mav;
	}
	
	@PostMapping("/restaurantes/{idRestaurante}/addCupon")
	public String addCuponToRestaurante(@PathVariable int idRestaurante,
	                                    @ModelAttribute("idCupon") int idCupon) {

	    // 1 Comprobar si ya existe la relación
	    boolean existe = perteneceRepository
	            .existsByIdIdCuponAndIdIdRestaurante(idCupon, idRestaurante);

	    if (!existe) {

	        // 2 Cargar objetos completos
	        Restaurante restaurante = restauranteRepository.findById(idRestaurante).orElse(null);
	        Cupon cupon = cuponRepository.findById(idCupon).orElse(null);

	        if (restaurante == null || cupon == null) {
	            return "redirect:/restaurantes"; // por si acaso
	        }

	        // 3 Crear la relación
	        PerteneceId id = new PerteneceId(idRestaurante, idCupon);

	        Pertenece p = new Pertenece();
	        p.setId(id);
	        p.setRestaurante(restaurante);  
	        p.setCupon(cupon);              

	        perteneceRepository.save(p);
	    }

	    return "redirect:/restaurantes/" + idRestaurante + "/cupones";
	}
	
	// ============ ADMIN: mostrar cupones NO vinculados ============
	@GetMapping("/restaurantes/{idRestaurante}/cupones/existentes")
	public ModelAndView verCuponesNoVinculadosAdmin(@PathVariable int idRestaurante) {

	    Restaurante restaurante = restauranteRepository.findById(idRestaurante).orElse(null);

	    if (restaurante == null) {
	        return new ModelAndView("redirect:/restaurantes");
	    }

	    List<Cupon> yaVinculados = restaurante.getPertenece()
	            .stream()
	            .map(p -> p.getCupon())
	            .toList();

	    List<Cupon> disponibles = ((List<Cupon>) cuponRepository.findAll())
	            .stream()
	            .filter(c -> !yaVinculados.contains(c))
	            .toList();

	    ModelAndView mv = new ModelAndView("restaurantes/cupones_existentes_admin");
	    mv.addObject("cupones", disponibles);
	    mv.addObject("restaurante", restaurante);

	    return mv;
	}

	// ============ USER: ver cupones de un restaurante ============
	@GetMapping("/restaurantes/{idRestaurante}/cupones/user")
	public ModelAndView verCuponesRestauranteUser(@PathVariable int idRestaurante) {

	    Restaurante restaurante = restauranteRepository.findById(idRestaurante).orElse(null);

	    if (restaurante == null) {
	        return new ModelAndView("redirect:/restaurantes");
	    }

	    List<Cupon> cupones = restaurante.getPertenece()
	            .stream()
	            .map(p -> p.getCupon())
	            .toList();

	    ModelAndView mv = new ModelAndView("restaurantes/cupones_restaurante_user");
	    mv.addObject("cupones", cupones);
	    mv.addObject("restaurante", restaurante);

	    return mv;
	}





}
