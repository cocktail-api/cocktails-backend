package de.slevermann.cocktails.backend.dao;

import de.slevermann.cocktails.backend.model.db.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.DbUser;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Rollback(false)
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
    DbIngredient thirdIngredient;

    @Order(1)
    @Test
    void testInitialEmpty() {
        assertEquals(0, userDao.count());
    }

    @Order(2)
    @Test
    void testCreate() {
        first = userDao.create();
        assertNotNull(first);
    }

    @Order(3)
    @Test
    void testSetNick() {
        assertNull(first.nick());
        first = userDao.updateNick(first.id(), "hello");
        assertEquals("hello", first.nick());
    }

    @Order(4)
    @Test
    void testCount() {
        assertEquals(1, userDao.count());
        second = userDao.create();
        second = userDao.updateNick(second.id(), "world");
        assertEquals(2, userDao.count());
    }

    @Order(5)
    @Test
    void testGetByNick() {
        assertEquals(first, userDao.getByNick("hello"));
    }

    @Order(6)
    @Test
    void testGetByNickMissing() {
        assertNull(userDao.getByNick("does not exist"));
    }

    @Order(7)
    @Test
    void testDelete() {
        assertEquals(1, userDao.delete(second.id()));
        assertEquals(1, userDao.count());
    }

    @Order(8)
    @Test
    void testDeleteMissing() {
        assertEquals(0, userDao.delete(UUID.randomUUID()));
        assertEquals(1, userDao.count());
    }

    @Order(9)
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

    @Order(10)
    @Test
    void testAddSingleToShelf() {
        second = userDao.create();
        userDao.addToShelf(second.id(), firstIngredient.id());
    }

    @Order(11)
    @Test
    void testGetShelf() {
        final var firstShelf = userDao.getShelf(first.id());
        assertEquals(2, firstShelf.size());

        final var secondShelf = userDao.getShelf(second.id());
        assertEquals(1, secondShelf.size());
    }

    @Order(12)
    @Test
    void testRemoveFromShelf() {
        userDao.removeFromShelf(first.id(), Stream.of(secondIngredient)
                .map(DbIngredient::id).collect(Collectors.toSet()));
        assertEquals(1, userDao.getShelf(first.id()).size());
    }

    @Order(13)
    @Test
    void testRemoveFromShelfSingle() {
        userDao.removeFromShelf(second.id(), firstIngredient.id());
        assertEquals(0, userDao.getShelf(second.id()).size());
    }

    @Order(14)
    @Test
    void testAddToShelfExisting() {
        userDao.addToShelf(first.id(), firstIngredient.id());
        assertEquals(1, userDao.getShelf(first.id()).size());
    }

}
