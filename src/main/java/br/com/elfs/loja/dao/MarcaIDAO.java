package br.com.elfs.loja.dao;

import java.util.List;
import br.com.elfs.loja.modelo.Marca;

public interface MarcaIDAO {
    void cadastrar(Marca marca);

    Marca buscarPorId(Long id);

    Marca buscarPorNome(String nome);

    List<Marca> buscarTodos();

    void atualizar(Marca marca);

    void remover(Marca marca);
}
