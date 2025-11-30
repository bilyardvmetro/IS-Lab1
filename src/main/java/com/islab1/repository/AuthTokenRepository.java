package com.islab1.repository;

import com.islab1.entities.AuthToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class AuthTokenRepository {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public AuthToken save(AuthToken token) {
        if (token.getId() == null) {
            em.persist(token);
            return token;
        } else {
            return em.merge(token);
        }
    }

    public Optional<AuthToken> findByToken(String tokenValue) {
        try {
            AuthToken token = em.createQuery("SELECT t FROM AuthToken t JOIN FETCH t.user WHERE t.token = :token", AuthToken.class)
                    .setParameter("token", tokenValue)
                    .getSingleResult();
            return Optional.of(token);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void delete(AuthToken token) {
        AuthToken managed = em.contains(token) ? token : em.merge(token);
        em.remove(managed);
    }

    @Transactional
    public void deleteExpired() {
        em.createQuery("DELETE FROM AuthToken t WHERE t.expiresAt < CURRENT_TIMESTAMP")
                .executeUpdate();
    }
}
