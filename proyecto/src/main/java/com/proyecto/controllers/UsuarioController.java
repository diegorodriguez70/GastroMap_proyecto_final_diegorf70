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
import org.springframework.web.servlet.ModelAndView;

import com.proyecto.beans.Perfil;
import com.proyecto.beans.Usuario;
import com.proyecto.beans.Restaurante;

import com.proyecto.repositories.PerfilRepository;
import com.proyecto.repositories.RestauranteRepository;
import com.proyecto.repositories.UsuarioRepository;

import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PerfilRepository perfilRepository;
    
    @Autowired
    RestauranteRepository restauranteRepository;
    
    
    
    
    @GetMapping("/usuarios")
    public ModelAndView listarUsuarios() {
        ModelAndView salida = new ModelAndView("usuarios/usuarios");
        salida.addObject("usuarios", usuarioRepository.findAll());
        return salida;
    }
    
    
    
    @GetMapping("/addUsuario")
    public ModelAndView formCrearUsuario() {
        ModelAndView salida = new ModelAndView("usuarios/usuarioForm");
        salida.addObject("usuario", new Usuario());
        return salida;
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

    


    @GetMapping("/usuarios/update/{nombreUsuario}")
    public ModelAndView updateUsuario(@PathVariable String nombreUsuario) {

        ModelAndView salida = new ModelAndView("usuarios/usuarioEditForm");

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(nombreUsuario);

        if (usuarioOptional.isEmpty()) {
         
            return new ModelAndView("redirect:/usuarios?error=UsuarioNoEncontrado");
        }

        salida.addObject("usuario", usuarioOptional.get());

        return salida;
    }

    @PostMapping("/usuarios/update")
    public ModelAndView saveUpdatedUsuario(
            @Valid @ModelAttribute Usuario usuario,
            BindingResult result) {

        if (result.hasErrors()) {
            return new ModelAndView("usuarios/usuarioEditForm");
        }

        Optional<Usuario> optionalOriginal = usuarioRepository.findById(usuario.getNombreUsuario());

        if (optionalOriginal.isEmpty()) {
            return new ModelAndView("redirect:/usuarios?error=UsuarioNoEncontrado");
        }

        Usuario original = optionalOriginal.get();

       
        usuario.setPerfil(original.getPerfil());

        usuarioRepository.save(usuario);

        return new ModelAndView("redirect:/usuarios");
    }
    
    
    
    
    @GetMapping("/usuarios/userLogged")
    public ModelAndView userLogged(Principal principal) {

        ModelAndView salida = new ModelAndView("index_user");

        String username = principal.getName();
        Usuario usuario = usuarioRepository.findById(username).orElse(null);

        if (usuario == null) {
            return new ModelAndView("redirect:/index?error=UsuarioNoEncontrado");
        }

        salida.addObject("usuario", usuario);
        
        List<Restaurante> restaurantes = (List<Restaurante>) restauranteRepository.findAll();
        salida.addObject("restaurantes", restaurantes);

        return salida;
    }

    
    @PostMapping("/usuarios/editarSelf")
    public String editarSelf(
            @ModelAttribute Usuario usuarioForm,
            Principal principal) {

        Usuario original = usuarioRepository.findById(usuarioForm.getNombreUsuario()).orElse(null);

        if (original == null) {
            return "redirect:/usuarios/userLogged?error=NoEncontrado";
        }

        // Seguridad: solo puede editarse a sí mismo
        if (!original.getNombreUsuario().equals(principal.getName())) {
            return "redirect:/usuarios/userLogged?error=AccesoDenegado";
        }

        // Campos editables
        original.setNombre(usuarioForm.getNombre());
        original.setContrasenia(usuarioForm.getContrasenia());

        usuarioRepository.save(original);

        return "redirect:/usuarios/userLogged?success=Actualizado";
    }



    
}
