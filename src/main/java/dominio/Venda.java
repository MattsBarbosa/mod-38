package dominio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;



@Entity
@Table(name = "TB_VENDA")
public class Venda implements Persistente {
	
	public enum Status {
		INICIADA, CONCLUIDA, CANCELADA;

		public static Status getByName(String value) {
			for (Status status : Status.values()) {
	            if (status.name().equals(value)) {
	                return status;
	            }
	        }
			return null;
		}
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="venda_seq")
	@SequenceGenerator(name="venda_seq", sequenceName="sq_venda", initialValue = 1, allocationSize = 1)
	private Long id;

	@Column(name = "CODIGO", nullable = false, unique = true)
	private String codigo;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente_fk", 
		foreignKey = @ForeignKey(name = "fk_venda_cliente"), 
		referencedColumnName = "id", nullable = false
	)
	private Cliente cliente;
	
	/*
	 * OBS: Não é uma boa prática utiliar FetchType.EAGER pois ele sempre irá trazer todos os objetos da collection
	 * mesmo sem precisar utilizar. Fazer um método específico para buscar tudo e utilizar quando precisar
	 * 
	 * @see IVendaJpaDAO consultarComCollection
	 */
	@OneToMany(mappedBy = "venda", cascade = CascadeType.ALL/*, fetch = FetchType.EAGER*/, orphanRemoval = true)
	private Set<CarroQuantidade> carros;
	
	@Column(name = "VALOR_TOTAL", nullable = false)
	private BigDecimal valorTotal;
	
	@Column(name = "DATA_VENDA", nullable = false)
	private Instant dataVenda;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_VENDA", nullable = false)
	private Status status;
	
	public Venda() {
		carros = new HashSet<>();
	}
	
	public void recalcularValorTotalVenda() {
		//validarStatus();
		BigDecimal valorTotal = BigDecimal.ZERO;
		for (CarroQuantidade car : this.carros) {
			valorTotal = valorTotal.add(car.getValorTotal());
		}
		this.valorTotal = valorTotal;
	}
	
	public void adicionarCarro(Carro carro, Integer quantidade) {
		validarStatus();
		Optional<CarroQuantidade> op = 
				carros.stream().filter(filter -> filter.getCarro().getCodigo().equals(carro.getCodigo())).findAny();
		if (op.isPresent()) {
			CarroQuantidade carroQtd = op.get();
			carroQtd.adicionar(quantidade);
		} else {
			// Criar fabrica para criar ProdutoQuantidade
			CarroQuantidade car = new CarroQuantidade();
			car.setVenda(this);
			car.setCarro(carro);
			car.adicionar(quantidade);
			carros.add(car);
		}
		recalcularValorTotalVenda();
	}
	
	private void validarStatus() {
		if (this.status == Status.CONCLUIDA) {
			throw new UnsupportedOperationException("IMPOSSÍVEL ALTERAR, VENDA FINALIZADA");
		}
	}
	
	public void removerCarro(Carro carro, Integer quantidade) {
		validarStatus();
		Optional<CarroQuantidade> op = 
				carros.stream().filter(filter -> filter.getCarro().getCodigo().equals(carro.getCodigo())).findAny();
		
		if (op.isPresent()) {
			CarroQuantidade carroQtd = op.get();
			if (carroQtd.getQuantidade()>quantidade) {
				carroQtd.remover(quantidade);
				recalcularValorTotalVenda();
			} else {
				carros.remove(op.get());
				recalcularValorTotalVenda();
			}
		}
	}
	
	public void removerTodosProdutos() {
		validarStatus();
		carros.clear();
		valorTotal = BigDecimal.ZERO;
	}
	
	public Integer getQuantidadeTotalCarros() {
		// Soma a quantidade getQuantidade() de todos os objetos ProdutoQuantidade
		int result = carros.stream()
		  .reduce(0, (partialCountResult, prod) -> partialCountResult + prod.getQuantidade(), Integer::sum);
		return result;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Set<CarroQuantidade> getCarros() {
		return carros;
	}

	public void setCarros(Set<CarroQuantidade> carros) {
		this.carros = carros;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Instant getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(Instant dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
		
}
	
