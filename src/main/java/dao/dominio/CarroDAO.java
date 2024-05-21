package dao.dominio;

import java.util.List;

import javax.persistence.TypedQuery;

import dao.generico.GenericDAO;
import dominio.Carro;



public class CarroDAO extends GenericDAO<Carro, String> implements ICarroDAO {

	public CarroDAO() {
		super(Carro.class);
	}

	@Override
	public List<Carro> filtrarCarros(String query) {
		TypedQuery<Carro> tpQuery = 
				this.entityManager.createNamedQuery("Carro.findByNome", this.persistenteClass);
		tpQuery.setParameter("nome", "%" + query + "%");
        return tpQuery.getResultList();
	}
}
