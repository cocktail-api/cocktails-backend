package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import de.slevermann.cocktails.backend.model.db.create.DbCreateCocktail;
import de.slevermann.cocktails.backend.model.db.create.DbCreateCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateIngredient;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class IngredientDaoTest extends DaoTestBase {

    @Autowired
    IngredientTypeDao ingredientTypeDao;

    @Autowired
    IngredientDao ingredientDao;

    @Autowired
    UserDao userDao;

    @Autowired
    CocktailDao cocktailDao;

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
        assertNull(ingredientDao.getById(randomUUID()));
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
        assertEquals(0, ingredientDao.delete(randomUUID()));
        assertEquals(1, ingredientDao.count());
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

    @Order(51)
    @Test
    void testDeleteShelfIngredient() {
        final var ex = assertThrows(UnableToExecuteStatementException.class,
                () -> ingredientDao.delete(ingredient.id()));
        if (ex.getCause() instanceof PSQLException psqlException) {
            final var state = PSQLState.FOREIGN_KEY_VIOLATION.getState();
            assertEquals(state, psqlException.getSQLState());
        } else {
            fail();
        }
    }

    @Order(55)
    @Test
    void testUseCount() {
        final var firstCocktail = cocktailDao.create(
                new DbCreateCocktail("first", "description")
        );
        final var secondCocktail = cocktailDao.create(
                new DbCreateCocktail("second", "description")
        );

        assertEquals(0, ingredientDao.usedByCount(ingredient.id()));
        assertEquals(0, ingredientDao.usedByCount(ingredientTwo.id()));
        assertEquals(0, ingredientDao.usedByCount(ingredientThree.id()));

        cocktailDao.addIngredients(firstCocktail.id(), List.of(
                new DbCreateCocktailIngredient(ingredient, 20d, DbUnit.milliliters,
                        false, false),
                new DbCreateCocktailIngredient(ingredientTwo, 30d, DbUnit.grams,
                        true, true)
        ));
        cocktailDao.addIngredients(secondCocktail.id(), List.of(
                new DbCreateCocktailIngredient(ingredientThree, 20d, DbUnit.milliliters,
                        false, false)
        ));

        assertEquals(1, ingredientDao.usedByCount(ingredient.id()));
        assertEquals(1, ingredientDao.usedByCount(ingredientTwo.id()));
        assertEquals(1, ingredientDao.usedByCount(ingredientThree.id()));
    }

    @Order(56)
    @Test
    void testDeleteUsed() {
        final var ex = assertThrows(UnableToExecuteStatementException.class,
                () -> ingredientDao.delete(ingredientThree.id()));
        if (ex.getCause() instanceof PSQLException psqlException) {
            final var state = PSQLState.FOREIGN_KEY_VIOLATION.getState();
            assertEquals(state, psqlException.getSQLState());
        } else {
            fail();
        }
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
        assertEquals(ingredient.created(), newIngredient.created());
        assertTrue(newIngredient.created().isBefore(newIngredient.modified()));
    }

    @Order(65)
    @Test
    void testUpdateMissing() {
        final var replacement = new DbCreateIngredient(type.id(),
                "newName", "newDescription");
        assertNull(ingredientDao.update(randomUUID(), replacement));
    }

    @Order(70)
    @Test
    void testFindByType() {
        assertEquals(2, ingredientDao.findByType(type.id(), 0, 2).size());

        assertEquals(1, ingredientDao.findByType(type.id(), 0, 1).size());
        assertEquals(1, ingredientDao.findByType(type.id(), 1, 1).size());
    }

    @Order(75)
    @Test
    void testFindIngredients() {
        assertEquals(2, ingredientDao.findIngredients(Set.of(
                ingredient.id(), ingredientTwo.id()
        )).size());
        assertEquals(2, ingredientDao.findIngredients(Set.of(
                ingredient.id(), ingredientTwo.id(), randomUUID()
        )).size());
        assertEquals(0, ingredientDao.findIngredients(Set.of(
                randomUUID(), randomUUID()
        )).size());
    }

    @Order(80)
    @Test
    void testCountByType() {
        final var type = ingredientTypeDao.create("someNewAmazingType");
        assertEquals(0, ingredientDao.countByType(type.id()));

        ingredientDao.create(new DbCreateIngredient(type.id(), "someGreatName", "description"));
        assertEquals(1, ingredientDao.countByType(type.id()));

        ingredientDao.create(new DbCreateIngredient(type.id(), "another", "foo"));
        assertEquals(2, ingredientDao.countByType(type.id()));
    }
}
