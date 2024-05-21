package service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import dao.dominio.ICarroDAO;
import dominio.Carro;
import service.generico.GenericService;


@Stateless
public class CarroService extends GenericService<Carro, String> implements ICarroService {
	
	private ICarroDAO carroDao;

	@Inject
	public CarroService(ICarroDAO carroDao) {
		super(carroDao);
		this.carroDao = carroDao;
	}

	@Override
	public List<Carro> filtrarCarros(String query) {
		return carroDao.filtrarCarros(query);
	}

}
