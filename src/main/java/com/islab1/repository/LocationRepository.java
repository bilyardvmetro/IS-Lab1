package com.islab1.repository;

import com.islab1.entities.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LocationRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Location> findById(Integer id) {
        return Optional.ofNullable(em.find(Location.class, id));
    }

    public List<Location> findAll() {
        return em.createQuery("SELECT l FROM Location l", Location.class).getResultList();
    }

    @Transactional
    public void deleteById(Integer id) {
        Location location = em.find(Location.class, id);
        if (location != null) {
            em.remove(location);
        }
    }
}
