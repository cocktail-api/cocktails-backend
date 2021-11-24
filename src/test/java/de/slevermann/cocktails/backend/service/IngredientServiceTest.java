package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.CreateIngredient;
import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.mapper.IngredientMapper;
import de.slevermann.cocktails.backend.model.mapper.IngredientMapperImpl;
import de.slevermann.cocktails.backend.model.mapper.IngredientTypeMapper;
import de.slevermann.cocktails.backend.model.mapper.IngredientTypeMapperImpl;
import de.slevermann.cocktails.backend.service.problem.MissingReferenceProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ReferencedEntityProblem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static de.slevermann.cocktails.backend.service.problem.ResourceType.INGREDIENT;
import static de.slevermann.cocktails.backend.service.problem.ResourceType.INGREDIENT_TYPE;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@MockitoSettings
public class IngredientServiceTest {

    private final IngredientTypeDao ingredientTypeDao = Mockito.mock(IngredientTypeDao.class);

    private final IngredientDao ingredientDao = Mockito.mock(IngredientDao.class);

    private final IngredientTypeMapper ingredientTypeMapper = new IngredientTypeMapperImpl();

    private final IngredientMapper ingredientMapper = new IngredientMapperImpl(ingredientTypeMapper);

    private final IngredientService ingredientService = new IngredientService(
            ingredientDao, ingredientTypeDao, ingredientMapper
    );


    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L, ((long) Integer.MAX_VALUE) + 1})
    void testCount(final long count) {
        when(ingredientDao.count()).thenReturn(count);

        assertEquals(count, ingredientService.count(), "Ingredient count returning incorrect value");
    }

    @Test
    void testList() {
        final var type = new DbIngredientType(randomUUID(), "name");
        when(ingredientDao.getAll(0, 2)).thenReturn(List.of(
                new DbIngredient(randomUUID(), type, "name", "description"),
                new DbIngredient(randomUUID(), type, "name", "description")
        ));
        when(ingredientDao.count()).thenReturn(3L);
        final var pagedIngredients = ingredientService.ingredients(1, 2);
        assertEquals(2, pagedIngredients.getIngredients().size());
        assertEquals(3, pagedIngredients.getTotal());
        assertEquals(2, pagedIngredients.getLastPage());
    }

    @Test
    void testListEven() {
        final var type = new DbIngredientType(randomUUID(), "name");
        when(ingredientDao.getAll(0, 2)).thenReturn(List.of(
                new DbIngredient(randomUUID(), type, "name", "description"),
                new DbIngredient(randomUUID(), type, "name", "description")
        ));
        when(ingredientDao.count()).thenReturn(4L);
        final var pagedIngredients = ingredientService.ingredients(1, 2);
        assertEquals(2, pagedIngredients.getIngredients().size());
        assertEquals(4, pagedIngredients.getTotal());
        assertEquals(2, pagedIngredients.getLastPage());
    }

    @Test
    void testGetById() {
        final var typeId = randomUUID();
        final var type = new DbIngredientType(typeId, "name");
        final var ingredientId = randomUUID();
        final var ingredient = new Ingredient().name("beer").description("description").type(
                new IngredientType().name("name").id(typeId)
        ).id(ingredientId);
        when(ingredientDao.getById(any()))
                .thenReturn(new DbIngredient(ingredientId, type, "beer", "description"));
        assertEquals(ingredient, ingredientService.get(randomUUID()));
    }

    @Test
    void testGetByIdNotFound() {
        final var id = randomUUID();
        when(ingredientDao.getById(id)).thenReturn(null);

        final var ex = assertThrows(NoSuchResourceProblem.class,
                () -> ingredientService.get(id));
        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(INGREDIENT, ex.getResourceType());
    }

    @Test
    void testDelete() {
        when(ingredientDao.usedByCount(any())).thenReturn(0L);
        when(ingredientDao.shelfCount(any())).thenReturn(0L);
        when(ingredientDao.delete(any())).thenReturn(1);

        assertDoesNotThrow(() -> ingredientService.delete(randomUUID()));
    }

    @Test
    void testDeleteNotFound() {
        final var id = randomUUID();
        when(ingredientDao.usedByCount(id)).thenReturn(0L);
        when(ingredientDao.shelfCount(id)).thenReturn(0L);
        when(ingredientDao.delete(id)).thenReturn(0);

        final var ex = assertThrows(NoSuchResourceProblem.class,
                () -> ingredientService.delete(id));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(INGREDIENT, ex.getResourceType());
    }

    @Test
    void testDeleteUsed() {
        final var id = randomUUID();
        when(ingredientDao.usedByCount(id)).thenReturn(1L);

        final var ex = assertThrows(ReferencedEntityProblem.class,
                () -> ingredientService.delete(id));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(INGREDIENT, ex.getResourceType());
    }

    @Test
    void testDeleteOwned() {
        final var id = randomUUID();
        when(ingredientDao.usedByCount(id)).thenReturn(0L);
        when(ingredientDao.shelfCount(id)).thenReturn(1L);

        final var ex = assertThrows(ReferencedEntityProblem.class,
                () -> ingredientService.delete(id));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(INGREDIENT, ex.getResourceType());
    }

    @Test
    void testCreate() {
        final var ingredientId = randomUUID();
        final var typeId = randomUUID();
        final var type = new DbIngredientType(typeId, "tasty things");
        when(ingredientTypeDao.getById(any())).thenReturn(type);
        final var ingredient = new CreateIngredient().type(typeId).name("beer").description("tasty");
        final var dbIngredient = new DbIngredient(ingredientId, type, "beer", "tasty");
        when(ingredientDao.create(any())).thenReturn(dbIngredient);
        final var finishedIngredient = new Ingredient().name("beer").description("tasty")
                .id(ingredientId).type(new IngredientType().id(typeId).name("tasty things"));

        assertEquals(finishedIngredient, ingredientService.create(ingredient));
    }

    @Test
    void testCreateMissingType() {
        final var id = randomUUID();
        when(ingredientTypeDao.getById(id)).thenReturn(null);

        final var ex = assertThrows(MissingReferenceProblem.class,
                () -> ingredientService.create(new CreateIngredient().type(id).name("beer").description("tasty")));

        assertEquals(ex.getResourceIds(), Set.of(id.toString()));
        assertEquals(ex.getReferencedResourceType(), INGREDIENT_TYPE);
    }

    @Test
    void testUpdate() {
        final var typeId = randomUUID();
        final var ingredientId = randomUUID();
        final var type = new DbIngredientType(typeId, "tasty things");
        when(ingredientTypeDao.getById(any())).thenReturn(type);
        final var ingredient = new CreateIngredient().type(typeId).name("beer").description("tasty");
        final var dbIngredient = new DbIngredient(ingredientId, type, "beer", "tasty");
        when(ingredientDao.update(any(), any())).thenReturn(dbIngredient);
        final var finishedIngredient = new Ingredient().name("beer").description("tasty")
                .id(ingredientId).type(new IngredientType().name("tasty things").id(typeId));

        assertEquals(finishedIngredient, ingredientService.update(ingredient, ingredientId));
    }

    @Test
    void testUpdateMissingType() {
        final var id = UUID.randomUUID();
        when(ingredientTypeDao.getById(any())).thenReturn(null);

        final var ex = assertThrows(MissingReferenceProblem.class,
                () -> ingredientService.update(new CreateIngredient().type(id).name("beer").description("tasty"), id));

        assertEquals(ex.getResourceIds(), Set.of(id.toString()));
        assertEquals(ex.getReferencedResourceType(), INGREDIENT_TYPE);
    }

    @Test
    void testUpdateMissingIngredient() {
        final var id = randomUUID();
        final var type = new DbIngredientType(id, "tasty things");
        when(ingredientTypeDao.getById(any())).thenReturn(type);
        when(ingredientDao.update(any(), any())).thenReturn(null);

        final var ex = assertThrows(NoSuchResourceProblem.class,
                () -> ingredientService.update(new CreateIngredient().type(id).name("beer").description("tasty"), id));
        assertEquals(ex.getResourceId(), id.toString());
        assertEquals(ex.getResourceType(), INGREDIENT);
    }

    @Test
    void testFindByType() {
        final var type = new DbIngredientType(UUID.randomUUID(), "type");
        when(ingredientTypeDao.getById(any())).thenReturn(type);
        when(ingredientDao.findByType(any(), anyInt(), anyInt())).thenReturn(List.of(
                new DbIngredient(randomUUID(), type, "name", "description"),
                new DbIngredient(randomUUID(), type, "name", "description")
        ));
        when(ingredientDao.countByType(any())).thenReturn(3L);
        final var pagedIngredients = ingredientService.findByType(UUID.randomUUID(), 1, 2);
        assertEquals(2, pagedIngredients.getIngredients().size());
        assertEquals(3, pagedIngredients.getTotal());
        assertEquals(2, pagedIngredients.getLastPage());
    }

    @Test
    void testFindByTypeEven() {
        final var type = new DbIngredientType(UUID.randomUUID(), "type");
        when(ingredientTypeDao.getById(any())).thenReturn(type);
        when(ingredientDao.findByType(any(), anyInt(), anyInt())).thenReturn(List.of(
                new DbIngredient(randomUUID(), type, "name", "description"),
                new DbIngredient(randomUUID(), type, "name", "description")
        ));
        when(ingredientDao.countByType(any())).thenReturn(4L);
        final var pagedIngredients = ingredientService.findByType(UUID.randomUUID(), 1, 2);
        assertEquals(2, pagedIngredients.getIngredients().size());
        assertEquals(4, pagedIngredients.getTotal());
        assertEquals(2, pagedIngredients.getLastPage());
    }

    @Test
    void testFindByTypeMissing() {
        when(ingredientTypeDao.getById(any())).thenReturn(null);

        final var id = UUID.randomUUID();
        final var ex = assertThrows(NoSuchResourceProblem.class,
                () -> ingredientService.findByType(id, 1, 2));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(INGREDIENT_TYPE, ex.getResourceType());
    }
}
