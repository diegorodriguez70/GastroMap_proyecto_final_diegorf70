package com.proyecto.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.beans.Reserva;
import com.proyecto.repositories.ReservaRepository;

@Service
public class ReservaService {
	@Autowired
	ReservaRepository reservaRepository;
	
	public Optional<Reserva> getReservaOptionalById(int idReserva){
		
		Optional<Reserva> reservaOptional = reservaRepository.findById(idReserva);
		if(reservaOptional.isPresent()) {
			return reservaOptional;
		}
		//lanzar una excepcion
		return null;
	}

	public void deleteReservaById(int idReserva) {
		try {
			reservaRepository.deleteById(idReserva);
		}catch(Exception e) {
			System.out.println("no existe la reserva que quieres borrar");
		}
		
	}
	public void saveReserva (Reserva reserva) {
		
		reservaRepository.save(reserva);
	}
	
	/*public void updateCuponById(int idCupon) {
		
		Optional<Cupon> cupon = cuponRepository.findById(idCupon);
		if(cupon.isPresent()) {
			
			
		}
	}*/
}
