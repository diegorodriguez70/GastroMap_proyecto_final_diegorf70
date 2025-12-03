package com.proyecto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.proyecto.beans.Perfil;
import com.proyecto.beans.Usuario;
import com.proyecto.repositories.PerfilRepository;
import com.proyecto.repositories.UsuarioRepository;

import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PerfilRepository perfilRepository;
    
    
    @GetMapping("/usuarios")
    public ModelAndView listarUsuarios() {
        ModelAndView mv = new ModelAndView("usuarios/usuarios");
        mv.addObject("usuarios", usuarioRepository.findAll());
        return mv;
    }
    
    
    
    @GetMapping("/addUsuario")
    public ModelAndView formCrearUsuario() {
        ModelAndView modelAndView = new ModelAndView("usuarios/usuarioForm");
        modelAndView.addObject("usuario", new Usuario());
        return modelAndView;
    }
    @PostMapping("/saveUsuario")
    public ModelAndView saveUsuario(
            @Valid @ModelAttribute Usuario usuario,
            BindingResult result) {

        // Validación sencilla
        if (usuarioRepository.existsById(usuario.getNombreUsuario())) {
            result.rejectValue("nombreUsuario", "error.user",
                    "Este nombre de usuario ya existe");
        }

        if (result.hasErrors()) {
            return new ModelAndView("usuarios/usuarioForm");
        }

        // SIEMPRE perfil de tipo USER (2)
        Perfil perfilUser = perfilRepository.findById(2).get();
        usuario.setPerfil(perfilUser);

        usuarioRepository.save(usuario);

        return new ModelAndView("redirect:/login");
    }
    
    
    
    @GetMapping("/usuarios/delete/{nombreUsuario}")
    public ModelAndView borrarUsuario(@PathVariable String nombreUsuario) {

        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(nombreUsuario).orElse(null);

        if (usuario != null) {

            //  NO permitir borrar admins
            if (usuario.getPerfil() != null && 
                "ADMIN".equalsIgnoreCase(usuario.getPerfil().getTipo())) {

             
                return new ModelAndView("redirect:/usuarios?error=NoSePuedeBorrarAdmin");
            }

            usuarioRepository.deleteById(nombreUsuario);
        }

        return new ModelAndView("redirect:/usuarios");
    }

}
