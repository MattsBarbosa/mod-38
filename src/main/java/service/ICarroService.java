package service;

import java.util.List;

import dominio.Carro;
import service.generico.IGenericService;



public interface ICarroService extends IGenericService<Carro, String> {

	List<Carro> filtrarCarros(String query);

}
