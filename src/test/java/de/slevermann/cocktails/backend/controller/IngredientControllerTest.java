package de.slevermann.cocktails.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.slevermann.cocktails.api.model.CreateIngredient;
import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.service.IngredientService;
import de.slevermann.cocktails.backend.service.problem.MissingReferenceProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ReferencedEntityProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import org.hamcrest.core.StringEndsWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

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

@WebMvcTest(IngredientController.class)
public class IngredientControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    IngredientService ingredientService;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 5, 10, 1000, Integer.MAX_VALUE})
    void testCount(final long count) throws Exception {
        when(ingredientService.count()).thenReturn(count);

        mockMvc.perform(get("/ingredients/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(count));
    }

    @Test
    void testGetAll() throws Exception {
        final var type = new IngredientType().name("type").id(UUID.randomUUID());
        final var first = new Ingredient().description("desc").name("name")
                .id(UUID.randomUUID()).type(type);
        final var second = new Ingredient().description("descTwo").name("nameTwo")
                .id(UUID.randomUUID()).type(type);
        when(ingredientService.ingredients(anyInt(), anyInt())).thenReturn(
                List.of(first, second)
        );

        mockMvc.perform(get("/ingredients").param("page", "1")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(first.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(first.getName()))
                .andExpect(jsonPath("$[0].description").value(first.getDescription()))
                .andExpect(jsonPath("$[0].type.id").value(type.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(second.getId().toString()))
                .andExpect(jsonPath("$[1].name").value(second.getName()))
                .andExpect(jsonPath("$[1].description").value(second.getDescription()))
                .andExpect(jsonPath("$[1].type.id").value(type.getId().toString()));
    }

    @Test
    void testGet() throws Exception {
        final var type = new IngredientType().name("type").id(UUID.randomUUID());
        final var ingredient = new Ingredient().description("desc").name("name")
                .id(UUID.randomUUID()).type(type);
        when(ingredientService.get(any())).thenReturn(ingredient);

        mockMvc.perform(get("/ingredients/{uuid}", ingredient.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ingredient.getId().toString()))
                .andExpect(jsonPath("$.name").value(ingredient.getName()))
                .andExpect(jsonPath("$.description").value(ingredient.getDescription()))
                .andExpect(jsonPath("$.type.id").value(type.getId().toString()));
    }

    @Test
    void testGetMissing() throws Exception {
        final var id = UUID.randomUUID();
        final var problem = new NoSuchResourceProblem(ResourceType.INGREDIENT, id.toString());
        when(ingredientService.get(any())).thenThrow(problem);

        mockMvc.perform(get("/ingredients/{uuid}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testDelete() throws Exception {
        final var id = UUID.randomUUID();
        doNothing().when(ingredientService).delete(any());

        mockMvc.perform(delete("/ingredients/{uuid}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteMissing() throws Exception {
        final var id = UUID.randomUUID();
        final var problem = new NoSuchResourceProblem(ResourceType.INGREDIENT, id.toString());
        doThrow(problem).when(ingredientService).delete(any());

        mockMvc.perform(delete("/ingredients/{uuid}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testDeleteUsed() throws Exception {
        final var id = UUID.randomUUID();
        final var problem = new ReferencedEntityProblem(ResourceType.INGREDIENT, id.toString());
        doThrow(problem).when(ingredientService).delete(any());

        mockMvc.perform(delete("/ingredients/{uuid}", id))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testCreate() throws Exception {
        final var id = UUID.randomUUID();
        final var typeId = UUID.randomUUID();
        final var ingredient = new CreateIngredient().type(typeId).name("name").description("desc");
        when(ingredientService.create(any())).thenReturn(new Ingredient()
                .type(new IngredientType().id(typeId).name("type")).name("name").description("desc").id(id));

        mockMvc.perform(post("/ingredients")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", new StringEndsWith(id.toString())))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.type.id").value(typeId.toString()));
    }

    @Test
    void testCreateMissingType() throws Exception {
        final var typeId = UUID.randomUUID();
        final var ingredient = new CreateIngredient().type(typeId).name("name").description("desc");
        final var problem = new MissingReferenceProblem(ResourceType.INGREDIENT_TYPE, typeId.toString());
        when(ingredientService.create(any())).thenThrow(problem);

        mockMvc.perform(post("/ingredients")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.referencedResourceType")
                        .value(problem.getReferencedResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testUpdate() throws Exception {
        final var id = UUID.randomUUID();
        final var typeId = UUID.randomUUID();
        final var ingredient = new CreateIngredient().type(typeId).name("name").description("desc");
        when(ingredientService.update(any(), any())).thenReturn(new Ingredient()
                .type(new IngredientType().id(typeId).name("type")).name("name").description("desc").id(id));

        mockMvc.perform(put("/ingredients/{uuid}", id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.type.id").value(typeId.toString()));
    }

    @Test
    void testUpdateMissingType() throws Exception {
        final var typeId = UUID.randomUUID();
        final var ingredient = new CreateIngredient().type(typeId).name("name").description("desc");
        final var problem = new MissingReferenceProblem(ResourceType.INGREDIENT_TYPE, typeId.toString());
        when(ingredientService.update(any(), any())).thenThrow(problem);

        mockMvc.perform(put("/ingredients/{uuid}", UUID.randomUUID())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.referencedResourceType")
                        .value(problem.getReferencedResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testUpdateMissingIngredient() throws Exception {
        final var typeId = UUID.randomUUID();
        final var ingredient = new CreateIngredient().type(typeId).name("name").description("desc");
        final var id = UUID.randomUUID();
        final var problem = new NoSuchResourceProblem(ResourceType.INGREDIENT, id.toString());
        when(ingredientService.update(any(), any())).thenThrow(problem);

        mockMvc.perform(put("/ingredients/{uuid}", id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resourceType")
                        .value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }
}
