package com.islab1.repository;

import com.islab1.entities.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class CoordinatesRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Coordinates> findById(Integer id) {
        return Optional.ofNullable(em.find(Coordinates.class, id));
    }

    @Transactional
    public void deleteById(Integer id) {
        Coordinates coordinates = em.find(Coordinates.class, id);
        if (coordinates != null) {
            em.remove(coordinates);
        }
    }
}
