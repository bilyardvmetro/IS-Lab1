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
import java.util.ArrayList;
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

    @Transactional
    public int importPeopleFromJson(List<Person> people) {
        if (people == null || people.isEmpty()) {
            throw new ImportException(List.of("Список импортируемых объектов пуст."));
        }

        List<String> errors = new ArrayList<>();
        List<Person> prepared = new ArrayList<>();

        int index = 0;
        for (Person src : people) {
            index++;
            try {
                prepared.add(prepareForImport(src));
            } catch (IllegalArgumentException e) {
                errors.add("Элемент " + index + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            // откат всей транзакции
            throw new ImportException(errors);
        }

        for (Person person : prepared) {
            personRepository.save(person);
        }

        return prepared.size();
    }

    private Person prepareForImport(Person src) {
        if (src == null) {
            throw new IllegalArgumentException("Объект Person не может быть null.");
        }

        Person person = new Person();
        person.setId(null);                // всегда создаём нового
        person.setCreationDate(null);      // заполнится в @PrePersist

        // name
        String name = src.getName();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Поле name не может быть пустым.");
        }
        person.setName(name);

        // coordinates
        Coordinates srcCoords = src.getCoordinates();
        if (srcCoords == null) {
            throw new IllegalArgumentException("coordinates не может быть null.");
        }
        if (srcCoords.getX() == null) {
            throw new IllegalArgumentException("coordinates.x не может быть null.");
        }
        if (srcCoords.getY() == null) {
            throw new IllegalArgumentException("coordinates.y не может быть null.");
        }
        if (srcCoords.getY() <= -804) {
            throw new IllegalArgumentException("coordinates.y должно быть больше -804.");
        }

        Coordinates coords = new Coordinates();
        coords.setId(0); // новый объект
        coords.setX(srcCoords.getX());
        coords.setY(srcCoords.getY());
        person.setCoordinates(coords);

        // eyeColor / hairColor
        if (src.getEyeColor() == null) {
            throw new IllegalArgumentException("eyeColor не может быть null.");
        }
        if (src.getHairColor() == null) {
            throw new IllegalArgumentException("hairColor не может быть null.");
        }
        person.setEyeColor(src.getEyeColor());
        person.setHairColor(src.getHairColor());

        // location (опционально)
        Location srcLoc = src.getLocation();
        if (srcLoc != null) {
            if (srcLoc.getX() == null) {
                throw new IllegalArgumentException("location.x не может быть null, если location задан.");
            }
            String locName = srcLoc.getName();
            if (locName == null || locName.isBlank()) {
                throw new IllegalArgumentException("location.name не может быть пустым, если location задан.");
            }

            Location loc = new Location();
            loc.setId(0);
            loc.setX(srcLoc.getX());
            loc.setY(srcLoc.getY());
            loc.setZ(srcLoc.getZ());
            loc.setName(locName);
            person.setLocation(loc);
        } else {
            person.setLocation(null);
        }

        // height
        if (src.getHeight() <= 0) {
            throw new IllegalArgumentException("height должно быть > 0.");
        }
        person.setHeight(src.getHeight());

        // weight (может быть null)
        if (src.getWeight() != null && src.getWeight() <= 0) {
            throw new IllegalArgumentException("weight должно быть > 0, если задано.");
        }
        person.setWeight(src.getWeight());

        // nationality
        if (src.getNationality() == null) {
            throw new IllegalArgumentException("nationality не может быть null.");
        }
        person.setNationality(src.getNationality());

        // паспорт как есть (может быть null/пустой)
        person.setPassportID(src.getPassportID());

        return person;
    }

}
