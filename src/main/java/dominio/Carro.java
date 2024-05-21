package dominio;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "TB_CARRO")
@NamedQuery(name = "Carro.findByModelo", query = "SELECT c FROM Carro c WHERE c.modelo LIKE :modelo")
public class Carro implements Persistente {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="carro_seq")
	@SequenceGenerator(name="carro_seq", sequenceName="sq_carro", initialValue = 1, allocationSize = 1)
	private Long id;

	@Column(name = "codigo", nullable = false, unique = true)
	private String codigo;
	
	@Column(name = "pais_fabricacao", nullable=false)
	private String paisFabricacao;
	
	@Column(name = "modelo", nullable = false)
	private String modelo;
	
	@Column(name = "ano", nullable = false)
	private Integer ano;
	
	@Column(name = "valor", nullable = false)
	private BigDecimal valor;

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getPaisFabricacao() {
		return paisFabricacao;
	}

	public void setPaisFabricacao(String paisFabricacao) {
		this.paisFabricacao = paisFabricacao;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
