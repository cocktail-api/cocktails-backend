package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.CreateIngredient;
import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.db.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.mapper.IngredientMapper;
import de.slevermann.cocktails.backend.service.problem.MissingReferenceProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ReferencedEntityProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        assertEquals(ResourceType.INGREDIENT, ex.getResourceType());
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
        assertEquals(ResourceType.INGREDIENT, ex.getResourceType());
    }

    @Test
    void testDeleteUsed() {
        final var id = randomUUID();
        when(ingredientDao.usedByCount(id)).thenReturn(1L);

        final var ex = assertThrows(ReferencedEntityProblem.class,
                () -> ingredientService.delete(id));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(ResourceType.INGREDIENT, ex.getResourceType());
    }

    @Test
    void testDeleteOwned() {
        final var id = randomUUID();
        when(ingredientDao.usedByCount(id)).thenReturn(0L);
        when(ingredientDao.shelfCount(id)).thenReturn(1L);

        final var ex = assertThrows(ReferencedEntityProblem.class,
                () -> ingredientService.delete(id));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(ResourceType.INGREDIENT, ex.getResourceType());
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
        assertEquals(ex.getReferencedResourceType(), ResourceType.INGREDIENT_TYPE);
    }

}
