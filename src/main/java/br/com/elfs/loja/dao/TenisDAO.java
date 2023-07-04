package br.com.elfs.loja.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import br.com.elfs.loja.modelo.Tenis;

public class TenisDAO implements TenisIDAO{

    private EntityManager em;

    public TenisDAO(EntityManager em) {
        this.em = em;
    }

    public void cadastrar(Tenis tenis) {
        em.persist(tenis);
    }

    public Tenis buscarPorId(Long id) {
        return em.find(Tenis.class, id);
    }


    public List<Tenis> buscarTodos() {
        String jpql = "SELECT t FROM Tenis t";
        TypedQuery<Tenis> query = em.createQuery(jpql, Tenis.class);
        return query.getResultList();
    }


    public List<Tenis> buscarPorNome(String nome) {
        String jpql = "SELECT t FROM Tenis t WHERE t.nome = :nome";
        TypedQuery<Tenis> query = em.createQuery(jpql, Tenis.class);
        query.setParameter("nome", nome);
        return query.getResultList();
    }


    public void atualizar(Tenis tenis) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(tenis);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }


    public void excluir(Tenis tenis) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.remove(tenis);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
