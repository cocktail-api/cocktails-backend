package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCreateIngredient;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
class IngredientTypeDaoTest extends DaoTestBase {

    @Autowired
    IngredientTypeDao ingredientTypeDao;

    @Autowired
    IngredientDao ingredientDao;

    UUID createdUuid;

    @Order(1)
    @Test
    void testInitialEmpty() {
        assertEquals(0, ingredientTypeDao.count());
    }

    @Order(2)
    @Test
    void testCreate() {
        final var ingredient = ingredientTypeDao.create("Juice");
        assertNotNull(ingredient.id());
        assertEquals("Juice", ingredient.name());
        createdUuid = ingredient.id();
    }

    @Order(3)
    @Test
    void testGetById() {
        final var ingredient = ingredientTypeDao.getById(createdUuid);

        assertEquals("Juice", ingredient.name());
        assertEquals(createdUuid, ingredient.id());
    }

    @Order(4)
    @Test
    void testGetByIdMissing() {
        assertNull(ingredientTypeDao.getById(UUID.randomUUID()));
    }

    @Order(5)
    @Test
    void testCount() {
        assertEquals(1, ingredientTypeDao.count());

        assertNotNull(ingredientTypeDao.create("Fruit"));

        assertEquals(2, ingredientTypeDao.count());
    }

    @Order(6)
    @Test
    void testOffsets() {
        assertEquals(2, ingredientTypeDao.getAll(0, 2).size());

        assertEquals(1, ingredientTypeDao.getAll(0, 1).size());
        assertEquals(1, ingredientTypeDao.getAll(1, 1).size());
    }

    @Order(7)
    @Test
    void testDelete() {
        assertEquals(1, ingredientTypeDao.delete(createdUuid));

        assertEquals(1, ingredientTypeDao.count());
    }

    @Order(8)
    @Test
    void testDeleteMissing() {
        assertEquals(0, ingredientTypeDao.delete(UUID.randomUUID()));
        assertEquals(1, ingredientTypeDao.count());
    }

    @Order(9)
    @Test
    void testUseCount() {
        final var newType = ingredientTypeDao.create("new");

        final var firstIngredient = new DbCreateIngredient(newType.id(),
                "first", "description");
        final var secondIngredient = new DbCreateIngredient(newType.id(),
                "second", "description");

        ingredientDao.create(firstIngredient);
        ingredientDao.create(secondIngredient);

        assertEquals(0, ingredientTypeDao.usedByCount(createdUuid));
        assertEquals(2, ingredientTypeDao.usedByCount(newType.id()));
    }
}
