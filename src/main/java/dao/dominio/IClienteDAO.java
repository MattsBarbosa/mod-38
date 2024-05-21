package dao.dominio;

import java.util.List;

import dao.generico.IGenericDAO;
import dominio.Cliente;



public interface IClienteDAO extends IGenericDAO<Cliente, Long>{

	List<Cliente> filtrarClientes(String query);

}
