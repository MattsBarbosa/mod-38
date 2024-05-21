package controller;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dominio.Carro;
import service.ICarroService;



@Named
@ViewScoped
public class CarroController implements Serializable{

	private static final long serialVersionUID = -2659624917967343145L;	

	private Carro carro;
	
	private Collection<Carro> carros;
	
	@Inject
	private ICarroService carroService;
	
	private Boolean isUpdate;
	
	@PostConstruct
    public void init() {
		try {
			this.isUpdate = false;
			this.carro = new Carro();
			this.carros = carroService.buscarTodos();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar listar os carros"));
		}
	}
	public void cancel() {
		try {
			this.isUpdate = false;
			this.carro = new Carro();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar cancelar ação"));
		}
		
    } 
	
	public void edit(Carro carro) {
		try {
			this.isUpdate = true;
			this.carro = carro;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar editar o carro"));
		}
		
    } 
	
	public void delete(Carro carro) {
		try {
			carroService.excluir(carro);
			carros.remove(carro);
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar excluir o produto"));
		}
		
    } 
	
	public void add() {
		try {
			carroService.cadastrar(carro);
			this.carros = carroService.buscarTodos();
			this.carro = new Carro();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar criar o Carro"));
		}
		
        
    }

    public void update() {
    	try {
    		carroService.alterar(this.carro);
			cancel();
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Carro Atualiado com sucesso"));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar atualizar o Carro"));
		}
        
    }
    
    public String voltarTelaInicial() {
		return "/index.xhtml"; 
	}

	public Carro getCarro() {
		return carro;
	}

	public void setCarro(Carro carro) {
		this.carro = carro;
	}

	public Collection<Carro> getCarros() {
		return carros;
	}

	public void setCarros(Collection<Carro> carros) {
		this.carros = carros;
	}

	public ICarroService getCarroService() {
		return carroService;
	}

	public void setCarroService(ICarroService carroService) {
		this.carroService = carroService;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	
}
