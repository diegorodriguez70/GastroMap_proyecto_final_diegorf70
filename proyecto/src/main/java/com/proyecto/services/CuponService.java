package com.proyecto.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.beans.Cupon;

import com.proyecto.repositories.CuponRepository;

@Service
public class CuponService {
	@Autowired
	CuponRepository cuponRepository;
	
	private String generarCodigo() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
	
	
	
	public Optional<Cupon> getCuponOptionalById(int idCupon){
		
		Optional<Cupon> cuponOptional = cuponRepository.findById(idCupon);
		if(cuponOptional.isPresent()) {
			return cuponOptional;
		}
		//lanzar una excepcion
		return null;
	}

	public void deleteCuponById(int idCupon) {
		try {
			cuponRepository.deleteById(idCupon);
		}catch(Exception e) {
			System.out.println("no existe el cupon que quieres borrar");
		}
		
	}
	
	
	
	public void saveCupon(Cupon cupon) {

        if (cupon.getCodigo() == null || cupon.getCodigo().isBlank()) {

            String codigo;

            do {
                codigo = generarCodigo();
            } while (cuponRepository.existsByCodigo(codigo));

            cupon.setCodigo(codigo);
        }

        cuponRepository.save(cupon);
    }


}