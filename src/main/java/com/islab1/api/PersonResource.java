package com.islab1.api;

import com.islab1.entities.Color;
import com.islab1.entities.Country;
import com.islab1.entities.Person;
import com.islab1.services.PersonService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON) // Все методы возвращают JSON
@Consumes(MediaType.APPLICATION_JSON) // Все методы принимают JSON
public class PersonResource {
    @Inject
    private PersonService personService;

    // CRUD
    @POST
    @Path("/add")
    public Response create(@Valid Person person) {
        try {
            Person createdPerson = personService.createPerson(person);
            // HTTP 201 Created
            return Response.status(Response.Status.CREATED).entity(createdPerson).build();
        } catch (IllegalArgumentException e) {
            // HTTP 400 Bad Request при ошибке валидации Coordinates
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Person get(@PathParam("id") Integer id) {
        // NotFoundException JAX-RS (HTTP 404) будет брошено, если Optional пуст
        return personService.getPersonById(id)
                .orElseThrow(() -> new NotFoundException("Person с ID " + id + " не найден."));
    }

    @GET
    @Path("/get-all")
    public List<Person> getAll() {
        return personService.getAllPeople();
    }

    @POST
    @Path("update/{id}")
    public Person update(@PathParam("id") Integer id, @Valid Person person) {
        try {
            return personService.updatePerson(id, person);
        } catch (IllegalArgumentException e) {
            // Преобразуем бизнес-исключения в 400
            throw new BadRequestException(e.getMessage());
        }
    }

//    @POST
//    @Path("delete/{id}")
//    public Response delete(@PathParam("id") Integer id) {
//        personService.deletePersonById(id);
//        return Response.noContent().build(); // HTTP 204 No Content
//    }

    @POST
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response deleteAndReassignLocation(
            @FormParam("personToAssignId") Integer personToAssignId,
            @FormParam("personToDeleteId") Integer personToDeleteId) {

        if (personToAssignId == null || personToDeleteId == null) {
            throw new BadRequestException("Требуются оба параметра: personToAssignId и personToDeleteId.");
        }

        personService.deletePersonWithReassignment(personToAssignId, personToDeleteId);

        return Response.ok("Связи успешно перепривязаны.").build();
    }

    // Specific
    @GET
    @Path("/average-height")
    public Double getAverageHeight() {
        return personService.getAverageHeight();
    }

    @GET
    @Path("/count/nationality")
    public Long countByNationality(@QueryParam("country") Country nationality) {
        if (nationality == null) {
            throw new BadRequestException("Параметр 'country' обязателен.");
        }
        return personService.countPeopleByNationality(nationality);
    }

    @GET
    @Path("/weight/less-than/{weight}")
    public List<Person> getPeopleByWeight(@PathParam("weight") Integer weight) {
        if (weight == null || weight <= 0) {
            throw new BadRequestException("Вес должен быть положительным числом.");
        }
        return personService.getPeopleWithWeightLessThan(weight);
    }

    @GET
    @Path("/percentage/eye-color/{color}")
    public Double getPercentageByEyeColor(@PathParam("color") Color color) {
        if (color == null) {
            throw new BadRequestException("Цвет глаз обязателен.");
        }
        return personService.getPercentageByEyeColor(color);
    }

    @GET
    @Path("/count/hair-location")
    public Long countByHairColorAndLocation(
            @QueryParam("color") Color color,
            @QueryParam("x") Double x,
            @QueryParam("y") Float y,   // Используем Float (класс) для возможности null
            @QueryParam("z") Double z) {

        if (color == null) {
            throw new BadRequestException("Параметр color обязателен.");
        }
        return personService.countByHairColorAndLocation(color, x, y, z);
    }
}