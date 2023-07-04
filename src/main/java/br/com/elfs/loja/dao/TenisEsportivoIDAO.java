package br.com.elfs.loja.dao;

import java.util.List;

import br.com.elfs.loja.modelo.TenisEsportivo;

public interface TenisEsportivoIDAO {
    void cadastrar(TenisEsportivo tenisEsportivo);
    TenisEsportivo buscarPorId(Long id);
    List<TenisEsportivo> buscarTodos();
    List<TenisEsportivo> buscarPorNome(String nome);
    void atualizar(TenisEsportivo tenisEsportivo);
    void excluir(TenisEsportivo tenisEsportivo);
}
