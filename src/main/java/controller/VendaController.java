package controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;

import dominio.Carro;
import dominio.CarroQuantidade;
import dominio.Cliente;
import dominio.Venda;
import service.ICarroService;
import service.IClienteService;
import service.IVendaService;


@Named
@ViewScoped
public class VendaController implements Serializable {

	private static final long serialVersionUID = -3508753726177740824L;
	
	private Venda venda;
	
	private Collection<Venda> vendas;
	
	@Inject
	private IVendaService vendaService;
	
	@Inject
	private IClienteService clienteService;
	
	@Inject
	private ICarroService carroService;
	
	private Boolean isUpdate;
	
	private LocalDate dataVenda;
	
	private Integer quantidadeCarros;
	
	private Set<CarroQuantidade> carros;
	
	private Carro carroSelecionado;
	
	private BigDecimal valorTotal; 
	
	@PostConstruct
    public void init() {
		try {
			this.isUpdate = false;
			this.venda = new Venda();
			this.carros = new HashSet<>();
			this.vendas = vendaService.buscarTodos();
			this.valorTotal = BigDecimal.ZERO;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar listar as vendas"));
		}
	}
	
	public void cancel() {
		try {
			this.isUpdate = false;
			this.venda = new Venda();
			this.carros = new HashSet<>();
			this.valorTotal = BigDecimal.ZERO;
			this.dataVenda = null;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar cancelar ação"));
		}
		
    } 
	
	public void edit(Venda venda) {
		try {
			this.isUpdate = true;
			this.venda = this.vendaService.consultarComCollection(venda.getId());
			this.dataVenda = LocalDate.ofInstant(this.venda.getDataVenda(), ZoneId.systemDefault());
			this.carros = this.venda.getCarros();
			this.venda.recalcularValorTotalVenda();
			this.valorTotal = this.venda.getValorTotal();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar editar a venda"));
		}
		
    } 
	
	public void delete(Venda venda) {
		try {
			vendaService.cancelarVenda(venda);
			cancel();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar cancelar a venda"));
		}
		
    } 
	
	public void finalizar(Venda venda) {
		try {
			vendaService.finalizarVenda(venda);
			cancel();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar finalizar a venda"));
		}
		
    } 
	
	public void add() {
		try {
			venda.setDataVenda(dataVenda.atStartOfDay(ZoneId.systemDefault()).toInstant());
			vendaService.cadastrar(venda);
			this.vendas = vendaService.buscarTodos();
			cancel();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar cadastrar a venda"));
		}
    }
	
	public void update() {
    	try {
    		vendaService.alterar(this.venda);
    		this.vendas = vendaService.buscarTodos();
			cancel();
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Venda atualizada com sucesso"));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage("growl", new FacesMessage("Erro ao tentar atualizar a venda"));
		}
        
    }
	
	public void adicionarCarro() {
		Optional<CarroQuantidade> carOp = 
				this.venda.getCarros().stream().filter(carF -> carF.getCarro().getCodigo().equals(this.carroSelecionado.getCodigo())).findFirst();

		if (carOp.isPresent()) {
			CarroQuantidade car = carOp.get();
			car.adicionar(this.quantidadeCarros);
		} else {
			CarroQuantidade car = new CarroQuantidade();
			car.setCarro(carroSelecionado);
			car.adicionar(this.quantidadeCarros);
			car.setVenda(this.venda);
			this.venda.getCarros().add(car);
		}
		this.venda.recalcularValorTotalVenda();
		this.carros = this.venda.getCarros();
		this.valorTotal = this.venda.getValorTotal();
	}
	
	public void removerCarro() {
		Optional<CarroQuantidade> carOp = 
				this.venda.getCarros().stream().filter(carOpL -> carOpL.getCarro().getCodigo().equals(this.carroSelecionado.getCodigo())).findFirst();

		if (carOp.isPresent()) {
			CarroQuantidade car = carOp.get();
			car.remover(this.quantidadeCarros);
			if (car.getQuantidade() == 0 || car.getQuantidade() < 0) {
				this.venda.getCarros().remove(car);
			}
			this.venda.recalcularValorTotalVenda();
			this.carros = this.venda.getCarros();
			this.valorTotal = this.venda.getValorTotal();
		}
		
	}
	
	public void removerCarro(CarroQuantidade carro) {
		
		this.venda.getCarros().remove(carro);
		this.venda.recalcularValorTotalVenda();
		this.carros = this.venda.getCarros();
		this.valorTotal = this.venda.getValorTotal();
	}
	
	public void onRowEdit(RowEditEvent<CarroQuantidade> event) {
		CarroQuantidade car = (CarroQuantidade) event.getObject();
		adicionarOuRemoverCarro(car);
    }

    public void onRowCancel(RowEditEvent<CarroQuantidade> event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void adicionarOuRemoverCarro(CarroQuantidade car) {
    	if (car.getQuantidade() != this.quantidadeCarros) {
    		int quantidade =  this.quantidadeCarros - car.getQuantidade();
    		if (quantidade > 0) {
    			car.adicionar(quantidade);
    		} else {
    			this.carros.remove(car);
    		}
    		this.valorTotal = BigDecimal.ZERO;
    		this.carros.forEach(carL -> {
    			this.valorTotal = this.valorTotal.add(carL.getValorTotal());
    		});
    	}
    }
	
	public List<Cliente> filtrarClientes(String query) {
		return this.clienteService.filtrarClientes(query);
	}
	
	public List<Carro> filtrarCarros(String query) {
		return this.carroService.filtrarCarros(query);
	}
    
    public String voltarTelaInicial() {
		return "/index.xhtml"; 
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}

	public Collection<Venda> getVendas() {
		return vendas;
	}

	public void setVendas(Collection<Venda> vendas) {
		this.vendas = vendas;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public LocalDate getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(LocalDate dataVenda) {
		this.dataVenda = dataVenda;
	}

	

	public IVendaService getVendaService() {
		return vendaService;
	}

	public void setVendaService(IVendaService vendaService) {
		this.vendaService = vendaService;
	}

	public IClienteService getClienteService() {
		return clienteService;
	}

	public void setClienteService(IClienteService clienteService) {
		this.clienteService = clienteService;
	}

	public ICarroService getCarroService() {
		return carroService;
	}

	public void setCarroService(ICarroService carroService) {
		this.carroService = carroService;
	}

	public Integer getQuantidadeCarros() {
		return quantidadeCarros;
	}

	public void setQuantidadeCarros(Integer quantidadeCarros) {
		this.quantidadeCarros = quantidadeCarros;
	}

	public Set<CarroQuantidade> getCarros() {
		return carros;
	}

	public void setCarros(Set<CarroQuantidade> carros) {
		this.carros = carros;
	}

	public Carro getCarroSelecionado() {
		return carroSelecionado;
	}

	public void setCarroSelecionado(Carro carroSelecionado) {
		this.carroSelecionado = carroSelecionado;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	
    

}
