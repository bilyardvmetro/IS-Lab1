package com.islab1.services;

import com.islab1.entities.*;
import com.islab1.repository.CoordinatesRepository;
import com.islab1.repository.LocationRepository;
import com.islab1.repository.PersonRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@Stateless
public class PersonService {
    @Inject
    private PersonRepository personRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private CoordinatesRepository coordinatesRepository;

    // CRUD
    @Transactional
    public Person createPerson(Person person) throws IllegalArgumentException {
        if (person.getCoordinates() == null) {
            throw new IllegalArgumentException("Coordinates object cannot be null.");
        } else {
            return personRepository.save(person);
        }
    }

    public Optional<Person> getPersonById(Integer id) {
        return personRepository.findById(id);
    }

    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    @Transactional
    public Person updatePerson(Integer id, Person updatedPerson) throws NotFoundException, IllegalArgumentException {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Person with ID " + id + " not found."));

        existingPerson.setName(updatedPerson.getName());
        existingPerson.setEyeColor(updatedPerson.getEyeColor());
        existingPerson.setHairColor(updatedPerson.getHairColor());
        existingPerson.setWeight(updatedPerson.getWeight());
        existingPerson.setHeight(updatedPerson.getHeight());
        existingPerson.setPassportID(updatedPerson.getPassportID());
        existingPerson.setNationality(updatedPerson.getNationality());

        if (updatedPerson.getLocation() != null) {
            existingPerson.setLocation(updatedPerson.getLocation());
        }
        if (updatedPerson.getCoordinates() != null) {
            existingPerson.setCoordinates(updatedPerson.getCoordinates());
        } else {
            throw new IllegalArgumentException("Coordinates не могут быть null.");
        }

        return personRepository.save(existingPerson);
    }

    @Transactional
    public void deletePersonById(Integer id) {
        personRepository.findById(id).orElseThrow(() -> new NotFoundException("Person with ID " + id + " not found."));
        personRepository.deleteById(id);
    }

    @Transactional
    public void deletePersonWithReassignment(Integer personToAssignId, Integer personToDeleteId) throws NotFoundException {
        Person personToAssign = personRepository.findById(personToAssignId).orElseThrow(() -> new NotFoundException("Person with ID " + personToAssignId + " not found."));
        Person personToDelete = personRepository.findById(personToDeleteId).orElseThrow(() -> new NotFoundException("Person with ID " + personToDeleteId + " not found."));

        // 3. ПЕРЕПРИВЯЗКА COORDINATES
        Coordinates coordinatesToAssign = personToDelete.getCoordinates();
        personToAssign.setCoordinates(coordinatesToAssign);

        // 4. ПЕРЕПРИВЯЗКА LOCATION (если есть)
        if (personToDelete.getLocation() != null) {
            Location locationToAssign = personToDelete.getLocation();
            personToAssign.setLocation(locationToAssign);
        }

        // 5. Сохраняем Person (обновление ссылок)
        personRepository.save(personToAssign);
        personRepository.deleteById(personToDeleteId);
    }

    // Specific
    public Double getAverageHeight() {
        return personRepository.calculateAvgHeight();
    }

    public Long countPeopleByNationality(Country nationality) {
        return personRepository.countByNationality(nationality);
    }

    public List<Person> getPeopleWithWeightLessThan(Integer weight) {
        return personRepository.weightLessThan(weight);
    }

    public Double getPercentageByEyeColor(Color color) {
        return personRepository.calculatePercentageByEyeColor(color);
    }

    public Long countByHairColorAndLocation(Color color,  Double x, Float y, Double z) {
        return personRepository.countByHairColorAndLocation(color, x, y, z);
    }
}
