package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.create.DbCreateCocktail;
import de.slevermann.cocktails.backend.model.db.create.DbCreateCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateInstruction;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.DbInstruction;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.slevermann.cocktails.backend.model.db.DbUnit.grams;
import static de.slevermann.cocktails.backend.model.db.DbUnit.milliliters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    long firstid;

    DbCocktail secondCocktail;

    DbIngredientType type;

    DbIngredient firstIngredient;

    DbIngredient secondIngredient;

    DbIngredient thirdIngredient;

    DbInstruction firstInstruction;

    DbInstruction secondInstruction;

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
        assertEquals(firstCocktail.created(), updated.created());
        assertTrue(updated.created().isBefore(updated.modified()));
        firstCocktail = updated;
    }

    @Order(40)
    @Test
    void testDelete() {
        jdbi.useHandle(h -> firstid = h.createQuery("select id from cocktail where uuid = :uuid")
                .bind("uuid", firstCocktail.id())
                .mapTo(Long.class).findFirst().get()
        );
        assertEquals(1, cocktailDao.delete(firstCocktail.id()));
        assertNull(cocktailDao.getById(firstCocktail.id()));
        assertEquals(1, cocktailDao.count());
    }

    @Order(41)
    @Test
    void testRelationsDeleted() {
        jdbi.useHandle(h -> {
            final var ingredientCount = h.createQuery(
                    "select count (*) from cocktail_ingredient where cocktail = :id"
            ).bind("id", firstid).mapTo(Long.class).findFirst().get();
            assertEquals(0, ingredientCount);

            final var instructionCount = h.createQuery(
                    "select count(*) from instruction where cocktail = :id"
            ).bind("id", firstid).mapTo(Long.class).findFirst().get();
            assertEquals(0, instructionCount);
        });
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
                new DbCreateCocktailIngredient(firstIngredient, 1.0d, grams, false, false),
                new DbCreateCocktailIngredient(secondIngredient, 20d, milliliters, false, false)
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
                new DbCreateCocktailIngredient(secondIngredient, 20d, milliliters, false, false));

        final var ingredients = cocktailDao.getIngredients(secondCocktail.id());
        assertEquals(1, ingredients.size());
        assertEquals(secondIngredient.id(), ingredients.get(0).id());
    }

    @Order(60)
    @Test
    void testRemoveIngredients() {
        cocktailDao.addIngredient(firstCocktail.id(),
                new DbCreateCocktailIngredient(thirdIngredient, 20d, milliliters, false, false));

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

    @Order(70)
    @Test
    void testAddExistingIngredient() {
        var ingredients = cocktailDao.getIngredients(firstCocktail.id());
        assertEquals(1, ingredients.size());
        assertEquals(thirdIngredient.id(), ingredients.get(0).id());
        assertEquals(20d, ingredients.get(0).amount());
        assertEquals(milliliters, ingredients.get(0).unit());
        assertFalse(ingredients.get(0).garnish());
        assertFalse(ingredients.get(0).optional());

        cocktailDao.addIngredient(firstCocktail.id(),
                new DbCreateCocktailIngredient(thirdIngredient, 40d, grams, true, true));

        final var newIngredients = cocktailDao.getIngredients(firstCocktail.id());
        assertEquals(1, newIngredients.size());
        assertEquals(thirdIngredient.id(), newIngredients.get(0).id());
        assertEquals(40d, newIngredients.get(0).amount());
        assertEquals(grams, newIngredients.get(0).unit());
        assertTrue(newIngredients.get(0).garnish());
        assertTrue(newIngredients.get(0).optional());
        assertEquals(ingredients.get(0).created(), newIngredients.get(0).created());
        assertTrue(ingredients.get(0).modified().isBefore(newIngredients.get(0).modified()));
    }

    @Order(75)
    @Test
    void testAddInstructions() {
        final var instructions = cocktailDao.addInstructions(firstCocktail.id(), Set.of(
                new DbCreateInstruction("do the thing", 100),
                new DbCreateInstruction("do another thing", 200)
        ));
        assertEquals(2, instructions.size());
        for (final var i : instructions) {
            if (i.number() == 100) firstInstruction = i;
            else secondInstruction = i;
        }
    }

    @Order(80)
    @Test
    void testGetInstructions() {
        assertEquals(2, cocktailDao.getInstructions(firstCocktail.id()).size());
    }

    @Order(85)
    @Test
    void testClearInstructions() {
        assertEquals(2, cocktailDao.clearInstructions(firstCocktail.id()));
        assertEquals(0, cocktailDao.getInstructions(firstCocktail.id()).size());
    }

    /*
     * Probability of generating 1000 random integers which are monotonically increasing
     * 10 times in a row is vanishingly small.
     */
    @Order(90)
    @RepeatedTest(10)
    void testGetInstructionsOrder() {
        final var numbers = new HashSet<Integer>();
        final var random = new Random();
        // Generate into a set for deduplication
        for (int i = 0; i < 1000; i++) {
            numbers.add(random.nextInt(Integer.MAX_VALUE));
        }
        // Shuffle the generated numbers around, because hashcode order == numeric order
        final var numberList = new ArrayList<>(numbers.stream().toList());
        Collections.shuffle(numberList, random);
        final var instructions = numberList.stream().map(i ->
                new DbCreateInstruction(String.format("instruction number %d", i), i)).collect(Collectors.toSet());
        cocktailDao.addInstructions(secondCocktail.id(), instructions);

        final var dbInstructions = cocktailDao.getInstructions(secondCocktail.id());
        assertEquals(numbers.size(), instructions.size());

        DbInstruction prev = null;
        for (final var instruction : dbInstructions) {
            if (prev != null) {
                assertTrue(prev.number() < instruction.number());
            }
            prev = instruction;
        }
        assertEquals(numbers.size(), cocktailDao.clearInstructions(secondCocktail.id()));
    }
}
