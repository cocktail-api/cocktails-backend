package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IngredientDaoTest extends DaoTestBase {

    @Autowired
    IngredientTypeDao ingredientTypeDao;

    @Autowired
    IngredientDao ingredientDao;

    @Autowired
    UserDao userDao;

    DbIngredientType type;

    DbIngredient ingredient;

    DbIngredient ingredientTwo;

    DbIngredient ingredientThree;

    @Order(5)
    @Test
    void testInitialEmpty() {
        assertEquals(0, ingredientDao.count());
    }

    @Order(10)
    @Test
    void testCreate() {
        type = ingredientTypeDao.create("type");
        ingredient = ingredientDao.create(new DbCreateIngredient(type.id(), "ingredientOne", "descriptionOne"));
        assertNotNull(ingredient.id());
        assertEquals("ingredientOne", ingredient.name());
        assertEquals("descriptionOne", ingredient.description());
    }

    @Order(15)
    @Test
    void testGetById() {
        final var fromDb = ingredientDao.getById(ingredient.id());
        assertEquals(ingredient.id(), fromDb.id());
        assertEquals("ingredientOne", fromDb.name());
        assertEquals("descriptionOne", fromDb.description());
    }

    @Order(20)
    @Test
    void testGetByIdMissing() {
        assertNull(ingredientDao.getById(UUID.randomUUID()));
    }

    @Order(25)
    @Test
    void testCount() {
        assertEquals(1, ingredientDao.count());

        ingredientTwo = ingredientDao.create(new DbCreateIngredient(type.id(), "ingredientTwo", "descriptionTwo"));
        assertNotNull(ingredientTwo);

        assertEquals(2, ingredientDao.count());
    }

    @Order(30)
    @Test
    void testOffsets() {
        assertEquals(2, ingredientDao.getAll(0, 2).size());

        assertEquals(1, ingredientDao.getAll(0, 1).size());
        assertEquals(1, ingredientDao.getAll(1, 1).size());
    }

    @Order(35)
    @Test
    void testDelete() {
        assertEquals(1, ingredientDao.delete(ingredientTwo.id()));
        assertEquals(1, ingredientDao.count());
    }

    @Order(40)
    @Test
    void testDeleteMissing() {
        assertEquals(0, ingredientDao.delete(UUID.randomUUID()));
        assertEquals(1, ingredientDao.count());
    }

    @Order(45)
    @Test
    @Disabled
    void testUseCount() {

    }

    @Order(50)
    @Test
    void testShelfCount() {
        final var firstUser = userDao.create();
        final var secondUser = userDao.create();
        ingredientTwo = ingredientDao.create(new DbCreateIngredient(type.id(),
                "ingredientTwo",
                "descriptionTwo"));
        ingredientThree = ingredientDao.create(new DbCreateIngredient(type.id(),
                "ingredientThree",
                "descriptionThree"));
        assertEquals(0, ingredientDao.shelfCount(ingredient.id()));
        assertEquals(0, ingredientDao.shelfCount(ingredientTwo.id()));
        assertEquals(0, ingredientDao.shelfCount(ingredientThree.id()));

        userDao.addToShelf(firstUser.id(), Stream.of(ingredient, ingredientTwo)
                .map(DbIngredient::id).collect(Collectors.toSet()));
        userDao.addToShelf(secondUser.id(), Stream.of(ingredient)
                .map(DbIngredient::id).collect(Collectors.toSet()));

        assertEquals(2, ingredientDao.shelfCount(ingredient.id()));
        assertEquals(1, ingredientDao.shelfCount(ingredientTwo.id()));
        assertEquals(0, ingredientDao.shelfCount(ingredientThree.id()));
    }

    @Order(55)
    @Test
    @Disabled
    void testFindByCocktail() {
    }

    @Order(60)
    @Test
    void testUpdate() {
        final var replacement = new DbCreateIngredient(type.id(),
                "newName", "newDescription");
        final var newIngredient = ingredientDao.update(ingredient.id(), replacement);
        assertEquals(newIngredient, ingredientDao.getById(ingredient.id()));
        assertEquals("newName", newIngredient.name());
        assertEquals("newDescription", newIngredient.description());
    }

    @Order(65)
    @Test
    void testUpdateMissing() {
        final var replacement = new DbCreateIngredient(type.id(),
                "newName", "newDescription");
        assertNull(ingredientDao.update(UUID.randomUUID(), replacement));
    }
}
