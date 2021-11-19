package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbCreateCocktail;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CocktailDaoTest extends DaoTestBase {

    @Autowired
    CocktailDao cocktailDao;

    DbCocktail firstCocktail;

    DbCocktail secondCocktail;

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

}
