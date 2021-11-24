package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.mapper.IngredientTypeMapper;
import de.slevermann.cocktails.backend.model.mapper.IngredientTypeMapperImpl;
import de.slevermann.cocktails.backend.service.problem.ConflictProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ReferencedEntityProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class IngredientTypeServiceTest {

    private final IngredientTypeDao ingredientTypeDao = Mockito.mock(IngredientTypeDao.class);

    private final IngredientTypeMapper ingredientTypeMapper = new IngredientTypeMapperImpl();

    private final IngredientTypeService ingredientTypeService =
            new IngredientTypeService(ingredientTypeDao, ingredientTypeMapper);

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L, ((long) Integer.MAX_VALUE) + 1})
    void testCount(final long count) {
        when(ingredientTypeDao.count()).thenReturn(count);

        assertEquals(count, ingredientTypeService.count(), "Type count returning incorrect value");
    }

    @Test
    void testTypeList() {
        when(ingredientTypeDao.getAll(0, 2)).thenReturn(List.of(
                new DbIngredientType(randomUUID(), "name"),
                new DbIngredientType(randomUUID(), "name")
        ));
        when(ingredientTypeDao.count()).thenReturn(3L);

        final var types = ingredientTypeService.types(1, 2);
        assertEquals(2, types.getTypes().size());
        assertEquals(3, types.getTotal());
        assertEquals(2, types.getLastPage());
    }

    @Test
    void testTypeListEven() {
        when(ingredientTypeDao.getAll(0, 2)).thenReturn(List.of(
                new DbIngredientType(randomUUID(), "name"),
                new DbIngredientType(randomUUID(), "name")
        ));
        when(ingredientTypeDao.count()).thenReturn(4L);

        final var types = ingredientTypeService.types(1, 2);
        assertEquals(2, types.getTypes().size());
        assertEquals(4, types.getTotal());
        assertEquals(2, types.getLastPage());
    }

    @Test
    void testGetById() {
        when(ingredientTypeDao.getById(any())).thenReturn(new DbIngredientType(randomUUID(), "name"));

        assertDoesNotThrow(() -> ingredientTypeService.get(randomUUID()));
    }

    @Test
    void testGetByIdNotFound() {
        when(ingredientTypeDao.getById(any())).thenReturn(null);

        final var id = randomUUID();
        final var ex = assertThrows(NoSuchResourceProblem.class,
                () -> ingredientTypeService.get(id));
        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(ResourceType.INGREDIENT_TYPE, ex.getResourceType());
    }

    @Test
    void testDelete() {
        when(ingredientTypeDao.usedByCount(any())).thenReturn(0L);
        when(ingredientTypeDao.delete(any())).thenReturn(1);

        assertDoesNotThrow(() -> ingredientTypeService.delete(randomUUID()));
    }

    @Test
    void testDeleteNotFound() {
        when(ingredientTypeDao.usedByCount(any())).thenReturn(0L);
        when(ingredientTypeDao.delete(any())).thenReturn(0);

        final var id = randomUUID();
        final var ex = assertThrows(NoSuchResourceProblem.class,
                () -> ingredientTypeService.delete(id));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(ResourceType.INGREDIENT_TYPE, ex.getResourceType());
    }

    @Test
    void testDeleteUsed() {
        when(ingredientTypeDao.usedByCount(any())).thenReturn(1L);

        final var id = randomUUID();
        final var ex = assertThrows(ReferencedEntityProblem.class,
                () -> ingredientTypeService.delete(id));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(ResourceType.INGREDIENT_TYPE, ex.getResourceType());
    }

    @Test
    void testCreate() {
        final var id = randomUUID();
        final var ingredientType = new IngredientType().id(id).name("name");
        when(ingredientTypeDao.findByName(any())).thenReturn(null);
        when(ingredientTypeDao.create(any())).thenReturn(new DbIngredientType(id, "name"));

        assertEquals(ingredientType, ingredientTypeService.create("name"));
    }

    @Test
    void testCreateExists() {
        final var id = randomUUID();
        final var name = "type";
        when(ingredientTypeDao.findByName(any())).thenReturn(new DbIngredientType(id, name));

        final var ex = assertThrows(ConflictProblem.class,
                () -> ingredientTypeService.create("type"));
        assertEquals("name", ex.getConflictFieldName());
        assertEquals("type", ex.getConflictFieldValue());
        assertEquals(ResourceType.INGREDIENT_TYPE, ex.getResourceType());
        assertEquals(id.toString(), ex.getResourceId());
    }

    @Test
    void testUpdate() {
        final var id = randomUUID();
        final var ingredientType = new IngredientType().id(id).name("name");
        when(ingredientTypeDao.findByNameAndNotId(any(), any())).thenReturn(null);
        when(ingredientTypeDao.update(any(), any())).thenReturn(new DbIngredientType(id, "name"));

        assertEquals(ingredientType, ingredientTypeService.update("name", id));
    }

    @Test
    void testUpdateExists() {
        final var id = randomUUID();
        final var name = "type";
        when(ingredientTypeDao.findByNameAndNotId(any(), any())).thenReturn(new DbIngredientType(id, name));

        final var ex = assertThrows(ConflictProblem.class,
                () -> ingredientTypeService.update(name, id));
        assertEquals("name", ex.getConflictFieldName());
        assertEquals("type", ex.getConflictFieldValue());
        assertEquals(ResourceType.INGREDIENT_TYPE, ex.getResourceType());
        assertEquals(id.toString(), ex.getResourceId());
    }

    @Test
    void testUpdateMissing() {
        final var id = randomUUID();
        final var name = "type";

        when(ingredientTypeDao.findByNameAndNotId(any(), any())).thenReturn(null);
        when(ingredientTypeDao.update(any(), any())).thenReturn(null);

        final var ex = assertThrows(NoSuchResourceProblem.class,
                () -> ingredientTypeService.update(name, id));
        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(ResourceType.INGREDIENT_TYPE, ex.getResourceType());
    }
}
