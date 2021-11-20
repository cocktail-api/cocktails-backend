package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.CreateIngredient;
import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.create.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.mapper.IngredientMapper;
import de.slevermann.cocktails.backend.service.problem.MissingReferenceProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ReferencedEntityProblem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;
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

    @Mock
    private IngredientTypeDao ingredientTypeDao;

    @Mock
    private IngredientDao ingredientDao;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientService ingredientService;


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
        when(ingredientMapper.fromDb(any())).thenReturn(new Ingredient());

        assertEquals(2, ingredientService.ingredients(1, 2).size());
    }

    @Test
    void testGetById() {
        final var type = new DbIngredientType(randomUUID(), "name");
        final var ingredient = new Ingredient().name("beer");
        when(ingredientDao.getById(any()))
                .thenReturn(new DbIngredient(randomUUID(), type, "name", "description"));
        when(ingredientMapper.fromDb(any())).thenReturn(ingredient);
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
        final var id = randomUUID();
        final var type = new DbIngredientType(id, "tasty things");
        when(ingredientTypeDao.getById(id)).thenReturn(type);
        final var ingredient = new CreateIngredient().type(id).name("beer").description("tasty");
        final var dbCreateIngredient = new DbCreateIngredient(id, "beer", "tasty");
        when(ingredientMapper.fromApi(ingredient)).thenReturn(dbCreateIngredient);
        final var dbIngredient = new DbIngredient(randomUUID(), type, "beer", "tasty");
        when(ingredientDao.create(dbCreateIngredient)).thenReturn(dbIngredient);
        final var finishedIngredient = new Ingredient().name("beer").description("tasty");
        when(ingredientMapper.fromDb(dbIngredient)).thenReturn(finishedIngredient);

        assertEquals(finishedIngredient, ingredientService.create(ingredient));
    }

    @Test
    void testCreateMissingType() {
        final var id = randomUUID();
        when(ingredientTypeDao.getById(id)).thenReturn(null);

        final var ex = assertThrows(MissingReferenceProblem.class,
                () -> ingredientService.create(new CreateIngredient().type(id).name("beer").description("tasty")));

        assertEquals(ex.getResourceId(), id.toString());
        assertEquals(ex.getReferencedResourceType(), INGREDIENT_TYPE);
    }

    @Test
    void testUpdate() {
        final var id = randomUUID();
        final var type = new DbIngredientType(id, "tasty things");
        when(ingredientTypeDao.getById(any())).thenReturn(type);
        final var ingredient = new CreateIngredient().type(id).name("beer").description("tasty");
        final var dbCreateIngredient = new DbCreateIngredient(id, "beer", "tasty");
        when(ingredientMapper.fromApi(any())).thenReturn(dbCreateIngredient);
        final var dbIngredient = new DbIngredient(randomUUID(), type, "beer", "tasty");
        when(ingredientDao.update(any(), any())).thenReturn(dbIngredient);
        final var finishedIngredient = new Ingredient().name("beer").description("tasty");
        when(ingredientMapper.fromDb(any())).thenReturn(finishedIngredient);

        assertEquals(finishedIngredient, ingredientService.update(ingredient, id));
    }

    @Test
    void testUpdateMissingType() {
        final var id = UUID.randomUUID();
        when(ingredientTypeDao.getById(any())).thenReturn(null);

        final var ex = assertThrows(MissingReferenceProblem.class,
                () -> ingredientService.update(new CreateIngredient().type(id).name("beer").description("tasty"), id));

        assertEquals(ex.getResourceId(), id.toString());
        assertEquals(ex.getReferencedResourceType(), INGREDIENT_TYPE);
    }

    @Test
    void testUpdateMissingIngredient() {
        final var id = randomUUID();
        final var type = new DbIngredientType(id, "tasty things");
        when(ingredientTypeDao.getById(any())).thenReturn(type);
        final var dbCreateIngredient = new DbCreateIngredient(id, "beer", "tasty");
        when(ingredientMapper.fromApi(any())).thenReturn(dbCreateIngredient);
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
        when(ingredientMapper.fromDb(any())).thenReturn(new Ingredient());

        assertEquals(2, ingredientService.findByType(UUID.randomUUID(), 1, 2).size());
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
