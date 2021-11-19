package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.DbCreateCocktail;
import de.slevermann.cocktails.backend.model.db.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CocktailDaoTest extends DaoTestBase {

    @Autowired
    CocktailDao cocktailDao;

    @Autowired
    IngredientTypeDao ingredientTypeDao;

    @Autowired
    IngredientDao ingredientDao;

    DbCocktail firstCocktail;

    DbCocktail secondCocktail;

    DbIngredientType type;

    DbIngredient firstIngredient;

    DbIngredient secondIngredient;

    DbIngredient thirdIngredient;

    @Order(5)
    @Test
    void testInitialEmpty() {
        assertEquals(0, cocktailDao.count());
    }

    @Order(10)
    @Test
    void testCreate() {
        firstCocktail = cocktailDao.create(new DbCreateCocktail("firstName", "firstDescription"));
        assertNotNull(firstCocktail.id());
        assertEquals("firstName", firstCocktail.name());
        assertEquals("firstDescription", firstCocktail.description());
    }

    @Order(15)
    @Test
    void testGetById() {
        assertEquals(firstCocktail, cocktailDao.getById(firstCocktail.id()));
    }

    @Order(20)
    @Test
    void testGetByIdMissing() {
        assertNull(cocktailDao.getById(UUID.randomUUID()));
    }

    @Order(25)
    @Test
    void testCount() {
        assertEquals(1, cocktailDao.count());
        secondCocktail = cocktailDao.create(new DbCreateCocktail("secondName", "secondDescription"));
        assertEquals(2, cocktailDao.count());
    }

    @Order(30)
    @Test
    void testOffsets() {
        assertEquals(2, cocktailDao.getAll(0, 2).size());

        assertEquals(1, cocktailDao.getAll(0, 1).size());
        assertEquals(1, cocktailDao.getAll(1, 1).size());
    }

    @Order(35)
    @Test
    void testUpdate() {
        final var updated = cocktailDao.update(firstCocktail.id(),
                new DbCreateCocktail("newName", "newDescription"));
        assertEquals(firstCocktail.id(), updated.id());
        assertEquals("newName", updated.name());
        assertEquals("newDescription", updated.description());
        assertEquals(updated, cocktailDao.getById(firstCocktail.id()));
        firstCocktail = updated;
    }

    @Order(40)
    @Test
    void testDelete() {
        assertEquals(1, cocktailDao.delete(firstCocktail.id()));
        assertNull(cocktailDao.getById(firstCocktail.id()));
        assertEquals(1, cocktailDao.count());
    }

    @Order(45)
    @Test
    void testDeleteMissing() {
        assertEquals(0, cocktailDao.delete(UUID.randomUUID()));
        assertEquals(1, cocktailDao.count());
    }

    @Order(50)
    @Test
    void testAddIngredients() {
        type = ingredientTypeDao.create("type");
        firstIngredient = ingredientDao.create(new DbCreateIngredient(type.id(),
                "first", "firstDescription"));
        secondIngredient = ingredientDao.create(new DbCreateIngredient(type.id(),
                "second", "secondDescription"));
        thirdIngredient = ingredientDao.create(new DbCreateIngredient(type.id(),
                "third", "thirdDescription"));
        firstCocktail = cocktailDao.create(new DbCreateCocktail("cocktail", "description"));

        cocktailDao.addIngredients(firstCocktail.id(), Set.of(
                new DbCocktailIngredient(firstIngredient, 1.0d, DbUnit.grams, false, false),
                new DbCocktailIngredient(secondIngredient, 20d, DbUnit.milliliters, false, false)
        ));

        final var ingredients = cocktailDao.getIngredients(firstCocktail.id());
        assertEquals(2, ingredients.size());
        assertTrue(ingredients.get(0).id().equals(firstIngredient.id()) ||
                ingredients.get(1).id().equals(firstIngredient.id()));
        assertTrue(ingredients.get(0).id().equals(secondIngredient.id()) ||
                ingredients.get(1).id().equals(secondIngredient.id()));
    }

    @Order(55)
    @Test
    void testAddSingleIngredient() {
        cocktailDao.addIngredient(secondCocktail.id(),
                new DbCocktailIngredient(secondIngredient, 20d, DbUnit.milliliters, false, false));

        final var ingredients = cocktailDao.getIngredients(secondCocktail.id());
        assertEquals(1, ingredients.size());
        assertEquals(secondIngredient.id(), ingredients.get(0).id());
    }

    @Order(60)
    @Test
    void testRemoveIngredients() {
        cocktailDao.addIngredient(firstCocktail.id(),
                new DbCocktailIngredient(thirdIngredient, 20d, DbUnit.milliliters, false, false));

        cocktailDao.removeIngredients(firstCocktail.id(), Set.of(firstIngredient.id(), secondIngredient.id()));

        final var ingredients = cocktailDao.getIngredients(firstCocktail.id());
        assertEquals(1, ingredients.size());
        assertEquals(thirdIngredient.id(), ingredients.get(0).id());
    }

    @Order(65)
    @Test
    void testRemoveSingleIngredient() {
        cocktailDao.removeIngredient(secondCocktail.id(), secondIngredient.id());
        assertEquals(0, cocktailDao.getIngredients(secondCocktail.id()).size());
    }

}
