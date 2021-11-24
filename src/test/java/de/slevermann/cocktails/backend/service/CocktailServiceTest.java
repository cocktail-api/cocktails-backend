package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CocktailIngredient;
import de.slevermann.cocktails.api.model.CocktailInstruction;
import de.slevermann.cocktails.api.model.CreateCocktail;
import de.slevermann.cocktails.api.model.CreateCocktailIngredient;
import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.api.model.Unit;
import de.slevermann.cocktails.backend.dao.CocktailDao;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.DbInstruction;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import de.slevermann.cocktails.backend.model.mapper.CocktailMapper;
import de.slevermann.cocktails.backend.model.mapper.CocktailMapperImpl;
import de.slevermann.cocktails.backend.service.problem.MissingReferenceProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static de.slevermann.cocktails.backend.service.problem.ResourceType.INGREDIENT;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@MockitoSettings
public class CocktailServiceTest {

    private final CocktailDao cocktailDao = Mockito.mock(CocktailDao.class);

    private final IngredientDao ingredientDao = Mockito.mock(IngredientDao.class);

    private final CocktailMapper cocktailMapper = new CocktailMapperImpl();

    private final CocktailService cocktailService = new CocktailService(
            cocktailDao,
            ingredientDao,
            cocktailMapper
    );


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
        final var typeId = randomUUID();
        final var type = new DbIngredientType(typeId, "type");
        final var ingredientId = randomUUID();
        final var ingredient = new DbIngredient(ingredientId, type, "name", "description");
        final var ingredients = List.of(new DbCocktailIngredient(ingredient,
                20d,
                DbUnit.milliliters,
                false,
                false));
        when(cocktailDao.getIngredients(id)).thenReturn(ingredients);
        final var apiCocktail = new Cocktail()
                .id(id).name("name").description("description").ingredients(List.of(
                        new CocktailIngredient().amount(20d).unit(Unit.MILLILITERS).optional(false).garnish(false)
                                .name("name").description("description").id(ingredientId)
                                .type(new IngredientType().id(typeId).name("type"))
                ));

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

    @Test
    void testCreate() {
        final var ingredient = new CreateCocktailIngredient()
                .id(UUID.randomUUID())
                .amount(20d)
                .garnish(false)
                .optional(false);
        final var secondIngredient = new CreateCocktailIngredient()
                .id(UUID.randomUUID())
                .amount(10d)
                .garnish(true)
                .optional(true);
        final var cocktail = new CreateCocktail()
                .ingredients(List.of(ingredient, secondIngredient))
                .name("name")
                .description("description")
                .instructions(List.of(new CocktailInstruction().text("Do the thing"),
                        new CocktailInstruction().text("Do the other thing")));

        when(ingredientDao.findIngredients(any()))
                .thenReturn(Set.of(ingredient.getId(), secondIngredient.getId()));

        when(cocktailDao.create(any())).thenReturn(new DbCocktail(UUID.randomUUID(), "name", "description"));
        when(cocktailDao.getIngredients(any())).thenReturn(List.of(ingredient(), ingredient()));

        when(cocktailDao.addInstructions(any(), any())).thenReturn(List.of(
                new DbInstruction("Do the other thing", 1, null, null),
                new DbInstruction("Do the thing", 0, null, null)
        ));

        final var created = cocktailService.create(cocktail);

        assertEquals(2, created.getIngredients().size());
        // Ensure the instructions are sorted right
        assertEquals("Do the thing", created.getInstructions().get(0).getText());
        assertEquals("Do the other thing", created.getInstructions().get(1).getText());
    }

    private DbCocktailIngredient ingredient() {
        return new DbCocktailIngredient(
                new DbIngredient(
                        randomUUID(),
                        new DbIngredientType(randomUUID(), "name"),
                        "name",
                        "description"
                ),
                20d,
                null,
                false,
                false
        );
    }

    @Test
    void testCreateMissingIngredient() {
        final var ingredient = new CreateCocktailIngredient()
                .id(UUID.randomUUID())
                .amount(20d)
                .garnish(false)
                .optional(false);
        final var missingIngredient = new CreateCocktailIngredient()
                .id(UUID.randomUUID())
                .amount(10d)
                .garnish(true)
                .optional(true);
        final var cocktail = new CreateCocktail()
                .ingredients(List.of(ingredient, missingIngredient))
                .name("name")
                .description("description")
                .instructions(List.of(new CocktailInstruction().text("Do the thing")));

        when(ingredientDao.findIngredients(any()))
                .thenReturn(Set.of(ingredient.getId()));

        final var ex = assertThrows(MissingReferenceProblem.class,
                () -> cocktailService.create(cocktail));
        assertEquals(INGREDIENT, ex.getReferencedResourceType());
        final var ids = ex.getResourceIds();

        assertEquals(1, ids.size());
        assertTrue(ids.contains(missingIngredient.getId().toString()));
    }

    @Test
    void testCreateMissingIngredients() {
        final var firstIngredient = new CreateCocktailIngredient()
                .id(UUID.randomUUID())
                .amount(20d)
                .garnish(false)
                .optional(false);
        final var secondIngredient = new CreateCocktailIngredient()
                .id(UUID.randomUUID())
                .amount(10d)
                .garnish(true)
                .optional(true);
        final var cocktail = new CreateCocktail()
                .ingredients(List.of(firstIngredient, secondIngredient))
                .name("name")
                .description("description")
                .instructions(List.of(new CocktailInstruction().text("Do the thing")));

        when(ingredientDao.findIngredients(any()))
                .thenReturn(Set.of());

        final var ex = assertThrows(MissingReferenceProblem.class,
                () -> cocktailService.create(cocktail));
        assertEquals(INGREDIENT, ex.getReferencedResourceType());
        final var ids = ex.getResourceIds();

        assertEquals(2, ids.size());
        assertTrue(ids.contains(firstIngredient.getId().toString()));
        assertTrue(ids.contains(secondIngredient.getId().toString()));
    }
}
