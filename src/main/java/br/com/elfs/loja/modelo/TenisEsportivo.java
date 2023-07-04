package br.com.elfs.loja.modelo;

import javax.persistence.*;

@Entity
@Table(name = "tenis_esportivo")
@PrimaryKeyJoinColumn(name = "id")
public class TenisEsportivo extends Tenis {
    @Column(name = "modalidade")
    private String modalidade;

    
    public TenisEsportivo(String nome, int tamanho, int preco, Marca marca, String modalidade) {
        super(nome, tamanho, preco, marca);
        this.modalidade = modalidade;
    }
    
    public TenisEsportivo() {
    }
    
    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }
}
