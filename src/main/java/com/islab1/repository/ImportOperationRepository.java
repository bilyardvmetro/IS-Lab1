package com.islab1.repository;

import com.islab1.entities.ImportOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ImportOperationRepository {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    /**
     * Сохраняем в отдельной транзакции, чтобы запись истории
     * не откатывалась вместе с неудачным импортом Person'ов.
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public ImportOperation saveNew(ImportOperation op) {
        em.persist(op);
        return op;
    }

    public List<ImportOperation> findAllOrderByStartedDesc() {
        return em.createQuery(
                        "SELECT o FROM ImportOperation o " +
                                "JOIN FETCH o.user " +
                                "ORDER BY o.startedAt DESC",
                        ImportOperation.class)
                .getResultList();
    }

    public List<ImportOperation> findByUserIdOrderByStartedDesc(Long userId) {
        return em.createQuery(
                        "SELECT o FROM ImportOperation o " +
                                "JOIN FETCH o.user " +
                                "WHERE o.user.id = :uid " +
                                "ORDER BY o.startedAt DESC",
                        ImportOperation.class)
                .setParameter("uid", userId)
                .getResultList();
    }
}
