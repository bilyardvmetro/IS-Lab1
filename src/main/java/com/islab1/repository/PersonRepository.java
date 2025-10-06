package com.islab1.repository;

import com.islab1.entities.Color;
import com.islab1.entities.Country;
import com.islab1.entities.Person;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonRepository {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    // CRUD
    public Person save(Person person) {
        if (person.getId() == null) {
            em.persist(person);
            em.flush();
            return person;
        } else {
            return em.merge(person);
        }
    }

    public Optional<Person> findById(Integer id) {
        String jpql = "SELECT p FROM Person p LEFT JOIN FETCH p.coordinates c LEFT JOIN FETCH p.location l WHERE p.id = :id";

        try {
            return Optional.of(em.createQuery(jpql, Person.class).setParameter("id", id).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Person> findAll() {
        String jpql = "SELECT p FROM Person p LEFT JOIN FETCH p.coordinates c LEFT JOIN FETCH p.location l";
        return em.createQuery(jpql, Person.class).getResultList();
    }

    public void deleteById(Integer id) {
        Person person = em.find(Person.class, id);
        if (person != null) {
            em.remove(person);
        }
    }

    // Specific
    public Double calculateAvgHeight() {
        return em.createQuery("SELECT AVG(p.height) FROM Person p", Double.class).getSingleResult();
    }

    public Long countByNationality(Country nationality) {
        return em.createQuery("SELECT COUNT(p) FROM Person p WHERE p.nationality = :nationality", Long.class)
                .setParameter("nationality", nationality)
                .getSingleResult();
    }

    public List<Person> weightLessThan(Integer weight) {
        return em.createQuery("SELECT p FROM Person p WHERE p.weight < :weight", Person.class)
                .setParameter("weight", weight)
                .getResultList();
    }

    public Double calculatePercentageByEyeColor(Color color) {
        Long countByEyeColor = em.createQuery("SELECT COUNT(p) FROM Person p WHERE p.eyeColor = :color", Long.class)
                .setParameter("color", color).getSingleResult();

        Long totalCount = em.createQuery("SELECT COUNT(p) FROM Person p", Long.class)
                .getSingleResult();

        if (totalCount == 0) {
            return 0.0;
        }
        return ((double) countByEyeColor / (double) totalCount) * 100.0;
    }

    public Long countByHairColorAndLocation(Color color, Double x, Float y, Double z) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Person p LEFT JOIN p.location l WHERE p.hairColor = :color");

        // Если хотя бы одна координата X, Y, или Z передана, мы должны присоединиться к Location.
        if (x != null || y != null || z != null) {

            if (x != null) {
                jpql.append(" AND l.x <= :x");
            }
            if (y != null) {
                jpql.append(" AND l.y <= :y");
            }
            if (z != null) {
                jpql.append(" AND l.z <= :z");
            }
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class).setParameter("color", color);

        if (x != null) {
            query.setParameter("x", x);
        }
        if (y != null) {
            query.setParameter("y", y);
        }
        if (z != null) {
            query.setParameter("z", z);
        }

        return query.getSingleResult();
    }
}
