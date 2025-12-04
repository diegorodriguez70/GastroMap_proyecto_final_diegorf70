package com.proyecto.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.proyecto.services.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http,
                                               CustomUserDetailsService customUserDetailsService) throws Exception {

            http

                .authenticationProvider(authenticationProvider(customUserDetailsService))

                .authorizeHttpRequests(auth -> auth

                        // =======================
                        // RUTAS PÚBLICAS
                        // =======================
                        .requestMatchers("/login", "/addUsuario", "/saveUsuario",
                                         "/css/**", "/js/**", "/images/**").permitAll()

                        // =======================================================
                        // RUTAS DE USUARIO (SOLO ADMIN)
                        // =======================================================
                        .requestMatchers(
                            "/usuarios",
                            "/usuarios/delete/**",
                            "/usuarios/update/**",
                            "/usuarios/update"
                        ).hasRole("ADMIN")

                        // Rutas para que el USER edite su propio perfil
                        .requestMatchers(
                            "/usuarios/userLogged",
                            "/usuarios/editarSelf"
                        ).hasRole("USER")

                        // =======================================================
                        // RUTAS SOLO ADMIN (Restaurantes)
                        // =======================================================
                        .requestMatchers(
                            "/restaurantes/delete/**",
                            "/restaurantes/add",
                            "/restaurantes/saveRestaurante",
                            "/restaurantes/updateRestaurante/**",
                            "/restaurantes/*/addCupon",
                            "/restaurantes/*/cupones/existentes"
                        ).hasRole("ADMIN")

                        // =======================================================
                        // RUTAS SOLO RESTAURANTE
                        // =======================================================
                        .requestMatchers(
                            "/restaurantes/restauranteLogged",
                            "/restaurantes/editSelf"
                        ).hasRole("RESTAURANTE")

                        // =======================================================
                        // RUTAS COMPARTIDAS (Admin + Restaurante)
                        // =======================================================
                        .requestMatchers(
                            "/restaurantes/uploadCarta/**"
                        ).hasAnyRole("ADMIN", "RESTAURANTE")

                        // =======================================================
                        // RUTAS SOLO USER
                        // =======================================================
                        .requestMatchers(
                            "/restaurantes/*/cupones/user",
                            "/reservas/user/**"
                        ).hasRole("USER")

                        // =======================================================
                        // RUTAS DE RESERVAS
                        // =======================================================

                        // Restaurante + Admin
                        .requestMatchers("/reservas/rest").hasAnyRole("ADMIN", "RESTAURANTE")
                        .requestMatchers("/reservas/updateReserva/**").hasAnyRole("ADMIN", "RESTAURANTE")
                        .requestMatchers("/reservas/delete/**").hasAnyRole("ADMIN", "RESTAURANTE")
                        .requestMatchers("/reservas/add/rest").hasAnyRole("ADMIN", "RESTAURANTE")

                        // Solo admin
                        .requestMatchers("/reservas/add").hasRole("ADMIN")

                        // Solo user
                        .requestMatchers("/reservas/user").hasRole("USER")
                        .requestMatchers("/reservas/add/user").hasRole("USER")


                        // =======================================================
                        // RUTAS DE CUPONES
                        // =======================================================

                        // Solo admin
                        .requestMatchers(
                            "/cupones",
                            "/cupones/*",
                            "/cupones/delete/**",
                            "/cupones/add",
                            "/restaurantes/*/cupones/addExistente/**",
                            "/cupones/endAll"
                        ).hasRole("ADMIN")

                        // Restaurante + Admin
                        .requestMatchers(
                            "/cupones/saveCupon",
                            "/cupones/updateCupon/**",
                            "/cupones/rest",
                            "/cupones/rest/delete/**",
                            "/cupones/add/rest",
                            "/cupones/rest/existentes",
                            "/cupones/rest/addExistente/**",
                            "/cupones/rest/deleteAll"
                        ).hasAnyRole("ADMIN", "RESTAURANTE")

                        // =======================
                        // Cualquier otra ruta requiere login
                        // =======================
                        .anyRequest().authenticated()
                )

                // =======================
                // ERROR 403 PERSONALIZADO
                // =======================
                .exceptionHandling(e ->
                        e.accessDeniedHandler((req, res, ex) -> res.sendRedirect("/403"))
                )

                // =======================
                // LOGIN / LOGOUT
                // =======================
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/index", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                .csrf(csrf -> csrf.disable());

            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new PasswordEncoder() {
                @Override
                public String encode(CharSequence rawPassword) {
                    return rawPassword.toString();
                }

                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return rawPassword.toString().equals(encodedPassword);
                }
            };
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(customUserDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder());
            return authProvider;
        }
}
