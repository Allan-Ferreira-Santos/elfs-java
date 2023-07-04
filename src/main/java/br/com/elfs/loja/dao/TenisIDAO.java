package br.com.elfs.loja.dao;

import java.util.List;

import br.com.elfs.loja.modelo.Tenis;

public interface TenisIDAO {
    void cadastrar(Tenis tenis);
    Tenis buscarPorId(Long id);
    List<Tenis> buscarTodos();
    List<Tenis> buscarPorNome(String nome);
    void atualizar(Tenis tenis);
    void excluir(Tenis tenis);
}
