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

        // Si NO hay usuario logueado → mostrar index normal
        if (principal == null) {
            return "index";
        }

        Usuario usuario = userService.findByNombreUsuario(principal.getName());

        if (usuario == null) {
            return "index";
        }

        // Selección de index según el perfil
        String rol = usuario.getPerfil().getTipo().toUpperCase();

        switch (rol) {
            case "ADMIN":
                return "index";  // index normal

            case "USER":
                return "index_user";  // el que acabas de crear

            case "RESTAURANTE":
                return "index_restaurante"; // cuando lo hagamos

            default:
                return "index";
        }
    }
}
