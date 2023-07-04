package br.com.elfs.loja.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import br.com.elfs.loja.modelo.TenisEsportivo;

public class TenisEsportivoDAO  implements TenisEsportivoIDAO{

    private EntityManager em;

    public TenisEsportivoDAO(EntityManager em) {
        this.em = em;
    }

    public void cadastrar(TenisEsportivo tenisEsportivo) {
        em.persist(tenisEsportivo);
    }

    public TenisEsportivo buscarPorId(Long id) {
        return em.find(TenisEsportivo.class, id);
    }

    public List<TenisEsportivo> buscarTodos() {
        String jpql = "SELECT t FROM TenisEsportivo t";
        TypedQuery<TenisEsportivo> query = em.createQuery(jpql, TenisEsportivo.class);
        return query.getResultList();
    }

    public List<TenisEsportivo> buscarPorNome(String nome) {
        String jpql = "SELECT t FROM TenisEsportivo t WHERE t.nome = :nome";
        TypedQuery<TenisEsportivo> query = em.createQuery(jpql, TenisEsportivo.class);
        query.setParameter("nome", nome);
        return query.getResultList();
    }

    public void atualizar(TenisEsportivo tenisEsportivo) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(tenisEsportivo);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void excluir(TenisEsportivo tenisEsportivo) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.remove(tenisEsportivo);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
