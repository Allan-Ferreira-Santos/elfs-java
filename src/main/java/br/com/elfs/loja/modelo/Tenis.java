package br.com.elfs.loja.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;

import javax.persistence.*;

@Entity
@Table(name = "tenis")
@Inheritance(strategy = InheritanceType.JOINED)
public class Tenis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "tamanho")
    private int tamanho;

    @Column(name = "preco")
    private int preco;

    @ManyToOne
    private Marca marca;

    
    public Tenis(String nome, int tamanho, int preco , Marca marca) {
        this.nome = nome;
        this.tamanho = tamanho;
        this.preco = preco;
        this.marca = marca;
    }
    
    public Tenis() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getPreco() {
        return preco;
    }

    public void setPreco(int preco) {
        this.preco = preco;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }
}
