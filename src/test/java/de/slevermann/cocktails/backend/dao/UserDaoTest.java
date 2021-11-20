package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.create.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.DbUser;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDaoTest extends DaoTestBase {

    @Autowired
    UserDao userDao;

    @Autowired
    IngredientTypeDao typeDao;

    @Autowired
    IngredientDao ingredientDao;

    DbUser first;
    DbUser second;

    DbIngredientType type;

    DbIngredient firstIngredient;
    DbIngredient secondIngredient;

    @Order(5)
    @Test
    void testInitialEmpty() {
        assertEquals(0, userDao.count());
    }

    @Order(10)
    @Test
    void testCreate() {
        first = userDao.create();
        assertNotNull(first);
    }

    @Order(15)
    @Test
    void testUpdate() {
        assertNull(first.nick());
        final var updated = userDao.update(first.id(), "hello");
        assertEquals("hello", updated.nick());
        assertEquals(first.created(), updated.created());
        assertTrue(updated.created().isBefore(updated.modified()));
        first = updated;
    }

    @Order(20)
    @Test
    void testCount() {
        assertEquals(1, userDao.count());
        second = userDao.create();
        second = userDao.update(second.id(), "world");
        assertEquals(2, userDao.count());
    }

    @Order(25)
    @Test
    void testOffsets() {
        assertEquals(2, userDao.getAll(0, 2).size());

        assertEquals(1, userDao.getAll(0, 1).size());
        assertEquals(1, userDao.getAll(1, 1).size());
    }

    @Order(30)
    @Test
    void testGetByNick() {
        assertEquals(first, userDao.getByNick("hello"));
    }

    @Order(35)
    @Test
    void testGetByNickMissing() {
        assertNull(userDao.getByNick("does not exist"));
    }

    @Order(40)
    @Test
    void testDelete() {
        assertEquals(1, userDao.delete(second.id()));
        assertEquals(1, userDao.count());
    }

    @Order(45)
    @Test
    void testDeleteMissing() {
        assertEquals(0, userDao.delete(UUID.randomUUID()));
        assertEquals(1, userDao.count());
    }

    @Order(50)
    @Test
    void testAddToShelf() {
        type = typeDao.create("type");
        firstIngredient = ingredientDao.create(new DbCreateIngredient(
                type.id(),
                "first",
                "firstDescription"));
        secondIngredient = ingredientDao.create(new DbCreateIngredient(
                type.id(),
                "second",
                "secondDescription"
        ));
        userDao.addToShelf(first.id(), Stream.of(firstIngredient, secondIngredient)
                .map(DbIngredient::id).collect(Collectors.toSet()));
    }

    @Order(55)
    @Test
    void testAddSingleToShelf() {
        second = userDao.create();
        userDao.addToShelf(second.id(), firstIngredient.id());
    }

    @Order(60)
    @Test
    void testGetShelf() {
        final var firstShelf = userDao.getShelf(first.id());
        assertEquals(2, firstShelf.size());

        final var secondShelf = userDao.getShelf(second.id());
        assertEquals(1, secondShelf.size());
    }

    @Order(65)
    @Test
    void testRemoveFromShelf() {
        userDao.removeFromShelf(first.id(), Stream.of(secondIngredient)
                .map(DbIngredient::id).collect(Collectors.toSet()));
        assertEquals(1, userDao.getShelf(first.id()).size());
    }

    @Order(70)
    @Test
    void testRemoveFromShelfSingle() {
        userDao.removeFromShelf(second.id(), firstIngredient.id());
        assertEquals(0, userDao.getShelf(second.id()).size());
    }

    @Order(75)
    @Test
    void testAddToShelfExisting() {
        userDao.addToShelf(first.id(), firstIngredient.id());
        assertEquals(1, userDao.getShelf(first.id()).size());
    }

    @Order(80)
    @Test
    void createWithNick() {
        final var created = userDao.create("newguy");
        assertEquals("newguy", created.nick());
        assertEquals(created, userDao.getByNick("newguy"));
    }
}
