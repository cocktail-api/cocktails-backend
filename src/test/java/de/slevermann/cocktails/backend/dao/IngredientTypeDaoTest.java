package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.create.DbCreateIngredient;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class IngredientTypeDaoTest extends DaoTestBase {

    @Autowired
    IngredientTypeDao ingredientTypeDao;

    @Autowired
    IngredientDao ingredientDao;

    UUID createdUuid;

    DbIngredientType newType;

    @Order(5)
    @Test
    void testInitialEmpty() {
        assertEquals(0, ingredientTypeDao.count());
    }

    @Order(10)
    @Test
    void testCreate() {
        final var ingredient = ingredientTypeDao.create("Juice");
        assertNotNull(ingredient.id());
        assertEquals("Juice", ingredient.name());
        createdUuid = ingredient.id();
    }

    @Order(11)
    @Test
    void testCreateNameExists() {
        final var ex = assertThrows(UnableToExecuteStatementException.class, () -> ingredientTypeDao.create("Juice"));
        if (ex.getCause() instanceof PSQLException psqlException) {
            assertEquals(psqlException.getSQLState(), PSQLState.UNIQUE_VIOLATION.getState());
        } else {
            fail();
        }
    }

    @Order(12)
    @Test
    void testCreateNameExistsIgnoreCase() {
        final var ex = assertThrows(UnableToExecuteStatementException.class, () -> ingredientTypeDao.create("jUICE"));
        if (ex.getCause() instanceof PSQLException psqlException) {
            assertEquals(psqlException.getSQLState(), PSQLState.UNIQUE_VIOLATION.getState());
        } else {
            fail();
        }
    }

    @Order(15)
    @Test
    void testGetById() {
        final var ingredient = ingredientTypeDao.getById(createdUuid);

        assertEquals("Juice", ingredient.name());
        assertEquals(createdUuid, ingredient.id());
    }

    @Order(20)
    @Test
    void testGetByIdMissing() {
        assertNull(ingredientTypeDao.getById(UUID.randomUUID()));
    }

    @Order(25)
    @Test
    void testCount() {
        assertEquals(1, ingredientTypeDao.count());

        assertNotNull(ingredientTypeDao.create("Fruit"));

        assertEquals(2, ingredientTypeDao.count());
    }

    @Order(30)
    @Test
    void testOffsets() {
        assertEquals(2, ingredientTypeDao.getAll(0, 2).size());

        assertEquals(1, ingredientTypeDao.getAll(0, 1).size());
        assertEquals(1, ingredientTypeDao.getAll(1, 1).size());
    }

    @Order(35)
    @Test
    void testDelete() {
        assertEquals(1, ingredientTypeDao.delete(createdUuid));

        assertEquals(1, ingredientTypeDao.count());
    }

    @Order(40)
    @Test
    void testDeleteMissing() {
        assertEquals(0, ingredientTypeDao.delete(UUID.randomUUID()));
        assertEquals(1, ingredientTypeDao.count());
    }

    @Order(45)
    @Test
    void testUseCount() {
        newType = ingredientTypeDao.create("new");

        final var firstIngredient = new DbCreateIngredient(newType.id(),
                "first", "description");
        final var secondIngredient = new DbCreateIngredient(newType.id(),
                "second", "description");

        ingredientDao.create(firstIngredient);
        ingredientDao.create(secondIngredient);

        assertEquals(0, ingredientTypeDao.usedByCount(createdUuid));
        assertEquals(2, ingredientTypeDao.usedByCount(newType.id()));
    }

    @Order(46)
    @Test
    void testDeleteReferenced() {
        final var ex = assertThrows(UnableToExecuteStatementException.class,
                () -> ingredientTypeDao.delete(newType.id()));
        if (ex.getCause() instanceof PSQLException psqlException) {
            final var state = PSQLState.FOREIGN_KEY_VIOLATION.getState();
            assertEquals(state, psqlException.getSQLState());
        } else {
            fail();
        }
    }

    @Order(50)
    @Test
    void testUpdate() {
        final var type = ingredientTypeDao.create("newType");
        assertEquals("newType", type.name());

        final var updated = ingredientTypeDao.update(type.id(), "newName");
        assertEquals("newName", updated.name());
        assertEquals(updated, ingredientTypeDao.getById(type.id()));
        assertEquals(type.created(), updated.created());
        assertTrue(updated.created().isBefore(updated.modified()));
    }

    @Order(55)
    @Test
    void testFindByName() {
        final var type = ingredientTypeDao.create("funnyName");

        assertNotNull(ingredientTypeDao.findByName("funnyName"));
        assertNotNull(ingredientTypeDao.findByName("FUnnYnamE"));
    }

    @Order(60)
    @Test
    void testFindByNameAndNotId() {
        final var type = ingredientTypeDao.create("newFunnyType");

        assertNotNull(ingredientTypeDao.findByNameAndNotId("newFunnyType", UUID.randomUUID()));
        assertNotNull(ingredientTypeDao.findByNameAndNotId("newFunnyType", UUID.randomUUID()));

        assertNull(ingredientTypeDao.findByNameAndNotId("newFunnyType", type.id()));
        assertNull(ingredientTypeDao.findByNameAndNotId("newFunnyType", type.id()));
    }

    @Order(65)
    @Test
    void testSearch() {
        final var firstType = ingredientTypeDao.create("Strong alcohol");
        final var secondType = ingredientTypeDao.create("Soft alcohol");

        final var results = ingredientTypeDao.search("alcohol", 0, 10);

        assertEquals(2, results.size());

        // "Soft alcohol" is more similar to "alcohol" than "Strong alcohol" on account of having fewer extra letters
        assertEquals(firstType, results.get(1));
        assertEquals(secondType, results.get(0));

        final var secondSearch = ingredientTypeDao.search("strong", 0, 10);

        assertEquals(1, secondSearch.size());
        assertEquals(firstType, secondSearch.get(0));
    }
}
