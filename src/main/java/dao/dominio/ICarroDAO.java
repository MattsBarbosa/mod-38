package dao.dominio;

import java.util.List;

import dao.generico.IGenericDAO;
import dominio.Carro;



public interface ICarroDAO extends IGenericDAO<Carro, String>{

	List<Carro> filtrarCarros(String query);

}
