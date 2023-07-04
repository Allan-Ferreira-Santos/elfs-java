package br.com.elfs.loja.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import br.com.elfs.loja.modelo.Marca;

public class MarcaDAO implements MarcaIDAO{

    private EntityManager em;

    public MarcaDAO(EntityManager em) {
        this.em = em;
    }

    public void cadastrar(Marca marca) {
        this.em.persist(marca);
    }

    public Marca buscarPorId(Long id) {
        return em.find(Marca.class, id);
    }

    public Marca buscarPorNome(String nome) {
        String jpql = "SELECT m FROM Marca m WHERE m.nome = :nome";
        TypedQuery<Marca> query = em.createQuery(jpql, Marca.class);
        query.setParameter("nome", nome);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Marca> buscarTodos() {
        String jpql = "SELECT m FROM Marca m";
        TypedQuery<Marca> query = em.createQuery(jpql, Marca.class);
        return query.getResultList();
    }

    public void atualizar(Marca marca) {
        em.getTransaction().begin();
        em.merge(marca);
        em.getTransaction().commit();
    }

    public void remover(Marca marca) {
        em.getTransaction().begin();
        marca = em.merge(marca);
        em.remove(marca);
        em.getTransaction().commit();
    }
}
