package service;

import java.util.List;

import dominio.Cliente;
import exceptions.DAOException;
import service.generico.IGenericService;



public interface IClienteService extends IGenericService<Cliente, Long> {

	Cliente buscarPorCPF(Long cpf) throws DAOException;

	List<Cliente> filtrarClientes(String query);

}