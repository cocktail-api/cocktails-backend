package de.slevermann.cocktails.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CocktailIngredient;
import de.slevermann.cocktails.api.model.CocktailInstruction;
import de.slevermann.cocktails.api.model.CocktailListEntry;
import de.slevermann.cocktails.api.model.CreateCocktail;
import de.slevermann.cocktails.api.model.CreateCocktailIngredient;
import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.api.model.PagedCocktails;
import de.slevermann.cocktails.api.model.Unit;
import de.slevermann.cocktails.backend.service.CocktailService;
import de.slevermann.cocktails.backend.service.problem.MissingReferenceProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;

import static de.slevermann.cocktails.backend.service.problem.ResourceType.COCKTAIL;
import static de.slevermann.cocktails.backend.service.problem.ResourceType.INGREDIENT;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CocktailController.class)
class CocktailControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CocktailService cocktailService;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 5, 10, 1000, Integer.MAX_VALUE})
    void testCount(final long count) throws Exception {
        when(cocktailService.count()).thenReturn(count);

        mockMvc.perform(get("/cocktails/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(count));
    }

    @Test
    void testGetAll() throws Exception {
        final var firstId = randomUUID();
        final var firstCocktail = new CocktailListEntry().name("first").description("firstDesc").id(firstId);
        final var secondId = randomUUID();
        final var secondCocktail = new CocktailListEntry().name("second").description("secondDesc").id(secondId);
        final var cocktails = new PagedCocktails()
                .cocktails(List.of(
                        firstCocktail,
                        secondCocktail
                ))
                .lastPage(2L)
                .total(3L);
        when(cocktailService.cocktails(anyInt(), anyInt())).thenReturn(cocktails);
        mockMvc.perform(get("/cocktails").param("page", "1")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.cocktails[0].id").value(firstId.toString()))
                .andExpect(jsonPath("$.cocktails[0].name").value(firstCocktail.getName()))
                .andExpect(jsonPath("$.cocktails[0].description").value(firstCocktail.getDescription()))
                .andExpect(jsonPath("$.cocktails[1].id").value(secondId.toString()))
                .andExpect(jsonPath("$.cocktails[1].name").value(secondCocktail.getName()))
                .andExpect(jsonPath("$.cocktails[1].description").value(secondCocktail.getDescription()))
                .andExpect(jsonPath("$.lastPage").value(2L))
                .andExpect(jsonPath("$.total").value(3L));
    }

    @Test
    void testGetById() throws Exception {
        final var cocktail = new Cocktail()
                .id(randomUUID())
                .name("Daiquiri")
                .description("Sour rum cocktail")
                .ingredients(List.of(
                        new CocktailIngredient(),
                        new CocktailIngredient(),
                        new CocktailIngredient()
                ))
                .instructions(List.of(
                        new CocktailInstruction(),
                        new CocktailInstruction()
                ));
        when(cocktailService.cocktail(any())).thenReturn(cocktail);

        mockMvc.perform(get("/cocktails/{uuid}", cocktail.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(cocktail.getId().toString()))
                .andExpect(jsonPath("$.name").value("Daiquiri"))
                .andExpect(jsonPath("$.description").value("Sour rum cocktail"))
                .andExpect(jsonPath("$.ingredients.length()").value(3))
                .andExpect(jsonPath("$.instructions.length()").value(2));
    }

    @Test
    void testGetByIdMissing() throws Exception {
        final var id = randomUUID();

        final var problem = new NoSuchResourceProblem(COCKTAIL, id.toString());
        when(cocktailService.cocktail(any())).thenThrow(problem);

        mockMvc.perform(get("/cocktails/{uuid}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testCreate() throws Exception {
        final var id = randomUUID();
        final var cocktail = new CreateCocktail()
                .name("Daiquiri")
                .description("Sour rum cocktail")
                .instructions(List.of(new CocktailInstruction().text("hello")))
                .ingredients(List.of(new CreateCocktailIngredient()
                        .id(randomUUID())
                        .amount(20d)
                        .garnish(false)
                        .optional(false)));

        final var typeId = randomUUID();
        final var ingredientId = randomUUID();
        final var created = new Cocktail()
                .id(id)
                .name("Daiquiri")
                .description("Sour rum cocktail")
                .instructions(List.of(new CocktailInstruction().text("hello")))
                .ingredients(List.of(new CocktailIngredient()
                        .type(new IngredientType().id(typeId).name("type"))
                        .id(ingredientId)
                        .name("ingredient")
                        .description("ingredientDescription")
                        .garnish(false)
                        .optional(false)
                        .unit(Unit.MILLILITERS)
                        .amount(20d)));

        when(cocktailService.create(cocktail)).thenReturn(created);
        mockMvc.perform(post("/cocktails")
                        .content(objectMapper.writeValueAsBytes(cocktail))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", new StringEndsWith(id.toString())))
                .andExpect(jsonPath("$.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.name").value(created.getName()))
                .andExpect(jsonPath("$.description").value(created.getDescription()))
                .andExpect(jsonPath("$.instructions.length()").value(created.getInstructions().size()))
                .andExpect(jsonPath("$.instructions[0].text").value(created.getInstructions().get(0).getText()))
                .andExpect(jsonPath("$.ingredients.length()").value(created.getIngredients().size()))
                .andExpect(jsonPath("$.ingredients[0].type.id").value(typeId.toString()))
                .andExpect(jsonPath("$.ingredients[0].type.name").value(created.getIngredients().get(0).getType().getName()))
                .andExpect(jsonPath("$.ingredients[0].id").value(created.getIngredients().get(0).getId().toString()))
                .andExpect(jsonPath("$.ingredients[0].name").value(created.getIngredients().get(0).getName()))
                .andExpect(jsonPath("$.ingredients[0].description").value(created.getIngredients().get(0).getDescription()))
                .andExpect(jsonPath("$.ingredients[0].garnish").value(created.getIngredients().get(0).getGarnish()))
                .andExpect(jsonPath("$.ingredients[0].optional").value(created.getIngredients().get(0).getOptional()))
                .andExpect(jsonPath("$.ingredients[0].unit").value(created.getIngredients().get(0).getUnit().getValue()))
                .andExpect(jsonPath("$.ingredients[0].amount").value(created.getIngredients().get(0).getAmount()))
        ;
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10, 20})
    void testCreateMissingIngredients(final int count) throws Exception {
        final var ids = new HashSet<String>();
        for (int i = 0; i < count; i++) {
            ids.add(randomUUID().toString());
        }
        final var problem = new MissingReferenceProblem(INGREDIENT, ids);
        when(cocktailService.create(any())).thenThrow(problem);

        final var cocktail = new CreateCocktail()
                .name("Daiquiri")
                .description("Sour rum cocktail")
                .instructions(List.of(new CocktailInstruction().text("hello")))
                .ingredients(List.of(new CreateCocktailIngredient()
                        .id(randomUUID())
                        .amount(20d)
                        .garnish(false)
                        .optional(false)));

        mockMvc.perform(post("/cocktails")
                        .content(objectMapper.writeValueAsBytes(cocktail))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.referencedResourceType")
                        .value(problem.getReferencedResourceType().getType()))
                .andExpect(jsonPath("$.resourceIds.length()").value(count));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(cocktailService).delete(any());

        mockMvc.perform(delete("/cocktails/{uuid}", randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteMissing() throws Exception {
        final var id = randomUUID();
        final var problem = new NoSuchResourceProblem(COCKTAIL, id.toString());
        doThrow(problem).when(cocktailService).delete(any());

        mockMvc.perform(delete("/cocktails/{uuid}", randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testUpdate() throws Exception {
        final var id = randomUUID();
        final var cocktail = new CreateCocktail()
                .name("Daiquiri")
                .description("Sour rum cocktail")
                .instructions(List.of(new CocktailInstruction().text("hello")))
                .ingredients(List.of(new CreateCocktailIngredient()
                        .id(randomUUID())
                        .amount(20d)
                        .garnish(false)
                        .optional(false)));
        final var typeId = randomUUID();
        final var ingredientId = randomUUID();
        final var updated = new Cocktail()
                .id(id)
                .name("Daiquiri")
                .description("Sour rum cocktail")
                .instructions(List.of(new CocktailInstruction().text("hello")))
                .ingredients(List.of(new CocktailIngredient()
                        .type(new IngredientType().id(typeId).name("type"))
                        .id(ingredientId)
                        .name("ingredient")
                        .description("ingredientDescription")
                        .garnish(false)
                        .optional(false)
                        .unit(Unit.MILLILITERS)
                        .amount(20d)));

        when(cocktailService.update(any(), any())).thenReturn(updated);

        mockMvc.perform(put("/cocktails/{uuid}", id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(cocktail)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updated.getId().toString()))
                .andExpect(jsonPath("$.name").value(updated.getName()))
                .andExpect(jsonPath("$.description").value(updated.getDescription()))
                .andExpect(jsonPath("$.instructions.length()").value(updated.getInstructions().size()))
                .andExpect(jsonPath("$.instructions[0].text").value(updated.getInstructions().get(0).getText()))
                .andExpect(jsonPath("$.ingredients.length()").value(updated.getIngredients().size()))
                .andExpect(jsonPath("$.ingredients[0].type.id").value(typeId.toString()))
                .andExpect(jsonPath("$.ingredients[0].type.name").value(updated.getIngredients().get(0).getType().getName()))
                .andExpect(jsonPath("$.ingredients[0].id").value(updated.getIngredients().get(0).getId().toString()))
                .andExpect(jsonPath("$.ingredients[0].name").value(updated.getIngredients().get(0).getName()))
                .andExpect(jsonPath("$.ingredients[0].description").value(updated.getIngredients().get(0).getDescription()))
                .andExpect(jsonPath("$.ingredients[0].garnish").value(updated.getIngredients().get(0).getGarnish()))
                .andExpect(jsonPath("$.ingredients[0].optional").value(updated.getIngredients().get(0).getOptional()))
                .andExpect(jsonPath("$.ingredients[0].unit").value(updated.getIngredients().get(0).getUnit().getValue()))
                .andExpect(jsonPath("$.ingredients[0].amount").value(updated.getIngredients().get(0).getAmount()));
    }

    @Test
    void testUpdateMissing() throws Exception {
        final var id = randomUUID();
        final var cocktail = new CreateCocktail()
                .name("Daiquiri")
                .description("Sour rum cocktail")
                .instructions(List.of(new CocktailInstruction().text("hello")))
                .ingredients(List.of(new CreateCocktailIngredient()
                        .id(randomUUID())
                        .amount(20d)
                        .garnish(false)
                        .optional(false)));
        final var problem = new NoSuchResourceProblem(COCKTAIL, id);
        when(cocktailService.update(any(), any())).thenThrow(problem);

        mockMvc.perform(put("/cocktails/{uuid}", id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(cocktail)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10, 20})
    void testUpdateMissingReference(final int count) throws Exception {
        final var ids = new HashSet<String>();
        for (int i = 0; i < count; i++) {
            ids.add(randomUUID().toString());
        }
        final var problem = new MissingReferenceProblem(INGREDIENT, ids);
        final var id = randomUUID();
        final var cocktail = new CreateCocktail()
                .name("Daiquiri")
                .description("Sour rum cocktail")
                .instructions(List.of(new CocktailInstruction().text("hello")))
                .ingredients(List.of(new CreateCocktailIngredient()
                        .id(randomUUID())
                        .amount(20d)
                        .garnish(false)
                        .optional(false)));
        when(cocktailService.update(any(), any())).thenThrow(problem);
        mockMvc.perform(put("/cocktails/{uuid}", id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(cocktail)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.referencedResourceType").value(problem.getReferencedResourceType().getType()))
                .andExpect(jsonPath("$.resourceIds.length()").value(count));
    }
}
