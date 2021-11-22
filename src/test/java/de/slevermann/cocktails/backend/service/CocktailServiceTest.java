package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CocktailListEntry;
import de.slevermann.cocktails.backend.dao.CocktailDao;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import de.slevermann.cocktails.backend.model.mapper.CocktailMapper;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;

import static de.slevermann.cocktails.backend.service.problem.ResourceType.INGREDIENT;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@MockitoSettings
public class CocktailServiceTest {

    @Mock
    private CocktailDao cocktailDao;

    @Mock
    private IngredientDao ingredientDao;

    @Mock
    private CocktailMapper cocktailMapper;

    @InjectMocks
    private CocktailService cocktailService;


    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L, ((long) Integer.MAX_VALUE) + 1})
    void testCount(final long count) {
        when(cocktailDao.count()).thenReturn(count);

        assertEquals(count, cocktailService.count(), "Cocktail count returning incorrect value");
    }

    @Test
    void testList() {
        when(cocktailDao.getAll(0, 2)).thenReturn(List.of(
                new DbCocktail(randomUUID(), "name", "description"),
                new DbCocktail(randomUUID(), "name", "description")
        ));
        when(cocktailMapper.fromDb((DbCocktail) any())).thenReturn(new CocktailListEntry()
                .id(randomUUID()));
        when(cocktailDao.count()).thenReturn(3L);
        final var cocktails = cocktailService.cocktails(1, 2);
        assertEquals(2, cocktails.getCocktails().size());
        assertEquals(3, cocktails.getTotal());
        assertEquals(2, cocktails.getLastPage());
    }

    @Test
    void testListEven() {
        when(cocktailDao.getAll(0, 2)).thenReturn(List.of(
                new DbCocktail(randomUUID(), "name", "description"),
                new DbCocktail(randomUUID(), "name", "description")
        ));
        when(cocktailMapper.fromDb((DbCocktail) any())).thenReturn(new CocktailListEntry()
                .id(randomUUID()));
        when(cocktailDao.count()).thenReturn(4L);
        final var cocktails = cocktailService.cocktails(1, 2);
        assertEquals(2, cocktails.getCocktails().size());
        assertEquals(4, cocktails.getTotal());
        assertEquals(2, cocktails.getLastPage());
    }

    @Test
    void testGetCocktail() {
        final var id = randomUUID();
        final var cocktail = new DbCocktail(id, "name", "description");
        when(cocktailDao.getById(id)).thenReturn(cocktail);
        final var type = new DbIngredientType(randomUUID(), "type");
        final var ingredient = new DbIngredient(randomUUID(), type, "name", "description");
        final var ingredients = List.of(new DbCocktailIngredient(ingredient,
                20d,
                DbUnit.milliliters,
                false,
                false));
        when(cocktailDao.getIngredients(id)).thenReturn(ingredients);
        final var apiCocktail = new Cocktail()
                .id(id).name("name").description("description");
        when(cocktailMapper.fromDb(any(), any(), any())).thenReturn(apiCocktail);

        assertEquals(apiCocktail, cocktailService.cocktail(id));
    }

    @Test
    void testCocktailNotFound() {
        when(cocktailDao.getById(any())).thenReturn(null);

        final var id = randomUUID();
        final var ex = assertThrows(NoSuchResourceProblem.class, () -> cocktailService.cocktail(id));

        assertEquals(ex.getResourceType(), ResourceType.COCKTAIL);
        assertEquals(ex.getResourceId(), id.toString());
    }

    @Test
    void testFindByIngredient() {
        final var type = new DbIngredientType(randomUUID(), "type");
        final var dbIngredient = new DbIngredient(randomUUID(), type, "ingredientName", "ingredientDescription");

        when(ingredientDao.getById(any())).thenReturn(dbIngredient);

        final var first = new DbCocktail(randomUUID(), "nameOne", "descriptionOne");
        final var second = new DbCocktail(randomUUID(), "nameTwo", "descriptionTwo");
        when(cocktailDao.findByIngredient(anyInt(), anyInt(), any())).thenReturn(List.of(
                first, second
        ));
        when(cocktailDao.countByIngredient(any())).thenReturn(3L);

        final var cocktails = cocktailService.findByIngredient(1, 2, randomUUID());
        assertEquals(2, cocktails.getCocktails().size());
        assertEquals(3, cocktails.getTotal());
        assertEquals(2, cocktails.getLastPage());
    }

    @Test
    void testFindByIngredientEven() {
        final var type = new DbIngredientType(randomUUID(), "type");
        final var dbIngredient = new DbIngredient(randomUUID(), type, "ingredientName", "ingredientDescription");

        when(ingredientDao.getById(any())).thenReturn(dbIngredient);

        final var first = new DbCocktail(randomUUID(), "nameOne", "descriptionOne");
        final var second = new DbCocktail(randomUUID(), "nameTwo", "descriptionTwo");
        when(cocktailDao.findByIngredient(anyInt(), anyInt(), any())).thenReturn(List.of(
                first, second
        ));
        when(cocktailDao.countByIngredient(any())).thenReturn(4L);

        final var cocktails = cocktailService.findByIngredient(1, 2, randomUUID());
        assertEquals(2, cocktails.getCocktails().size());
        assertEquals(4, cocktails.getTotal());
        assertEquals(2, cocktails.getLastPage());
    }

    @Test
    void testFindByIngredientMissing() {
        when(ingredientDao.getById(any())).thenReturn(null);

        final var id = randomUUID();
        final var ex = assertThrows(NoSuchResourceProblem.class,
                () -> cocktailService.findByIngredient(1, 2, id));

        assertEquals(id.toString(), ex.getResourceId());
        assertEquals(INGREDIENT, ex.getResourceType());
    }
}
