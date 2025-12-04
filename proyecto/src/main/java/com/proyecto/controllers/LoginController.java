package com.proyecto.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.proyecto.beans.Usuario;
import com.proyecto.services.CustomUserDetailsService;

@Controller
public class LoginController {


    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/index"})
    public String index(Principal principal) {

        if (principal == null) {
            return "index";
        }

        Usuario usuario = userService.findByNombreUsuario(principal.getName());

        if (usuario == null) {
            return "index";
        }

        String rol = usuario.getPerfil().getTipo().toUpperCase();

        switch (rol) {
            case "ADMIN":
                return "index";  

            case "USER":
            	return "redirect:/usuarios/userLogged"; 

            case "RESTAURANTE":
            	return "redirect:/restaurantes/restauranteLogged";

            default:
                return "index";
        }
    }
    
    @GetMapping("/403")
    public String error403() {
        return "error/403";
    }

}
