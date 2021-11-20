package de.slevermann.cocktails.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.slevermann.cocktails.api.model.CreateIngredientType;
import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.service.IngredientTypeService;
import de.slevermann.cocktails.backend.service.problem.ConflictProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ReferencedEntityProblem;
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

import static de.slevermann.cocktails.backend.service.problem.ResourceType.INGREDIENT_TYPE;
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

@WebMvcTest(IngredientTypeController.class)
class IngredientTypeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    IngredientTypeService ingredientTypeService;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 5, 10, 1000, Integer.MAX_VALUE})
    void testCount(final long count) throws Exception {
        when(ingredientTypeService.count()).thenReturn(count);

        mockMvc.perform(get("/types/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(count));
    }

    @Test
    void testGetAll() throws Exception {
        final var first = new IngredientType().id(UUID.randomUUID()).name("first");
        final var second = new IngredientType().id(UUID.randomUUID()).name("second");
        when(ingredientTypeService.types(anyInt(), anyInt())).thenReturn(
                List.of(first, second)
        );

        mockMvc.perform(get("/types")
                        .param("page", "1")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(first.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(second.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(first.getName()))
                .andExpect(jsonPath("$[1].name").value(second.getName()));
    }

    @Test
    void testGet() throws Exception {
        final var type = new IngredientType().id(UUID.randomUUID()).name("first");
        when(ingredientTypeService.get(any())).thenReturn(type);

        mockMvc.perform(get("/types/{uuid}", type.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.name").value(type.getName()));
    }

    @Test
    void testGetMissing() throws Exception {
        final var id = UUID.randomUUID();
        final var problem = new NoSuchResourceProblem(INGREDIENT_TYPE, id.toString());
        when(ingredientTypeService.get(any())).thenThrow(problem);

        mockMvc.perform(get("/types/{uuid}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testDelete() throws Exception {
        final var id = UUID.randomUUID();
        doNothing().when(ingredientTypeService).delete(any());

        mockMvc.perform(delete("/types/{uuid}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteMissing() throws Exception {
        final var id = UUID.randomUUID();
        final var problem = new NoSuchResourceProblem(INGREDIENT_TYPE, id.toString());
        doThrow(problem).when(ingredientTypeService).delete(any());

        mockMvc.perform(delete("/types/{uuid}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testDeleteUsed() throws Exception {
        final var id = UUID.randomUUID();
        final var problem = new ReferencedEntityProblem(INGREDIENT_TYPE, id.toString());
        doThrow(problem).when(ingredientTypeService).delete(any());

        mockMvc.perform(delete("/types/{uuid}", id))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testCreate() throws Exception {
        final var id = UUID.randomUUID();
        final var ingredientType = new CreateIngredientType().name("test");
        when(ingredientTypeService.create(any())).thenReturn(new IngredientType().id(id).name("test"));

        mockMvc.perform(post("/types")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredientType)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", new StringEndsWith(id.toString())))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    void testCreateConflict() throws Exception {
        final var ingredient = new CreateIngredientType().name("test");
        final var id = UUID.randomUUID();
        final var problem = new ConflictProblem("name",
                "test",
                INGREDIENT_TYPE,
                id.toString());
        when(ingredientTypeService.create(any())).thenThrow(problem);

        mockMvc.perform(post("/types")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.conflictFieldName").value(problem.getConflictFieldName()))
                .andExpect(jsonPath("$.conflictFieldValue").value(problem.getConflictFieldValue()))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testUpdate() throws Exception {
        final var id = UUID.randomUUID();
        final var ingredient = new CreateIngredientType().name("test");
        final var type = new IngredientType().id(id).name("test");

        when(ingredientTypeService.update(any(), any())).thenReturn(type);

        mockMvc.perform(put("/types/{uuid}", id)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    void testUpdateMissing()  throws Exception {
        final var ingredient = new CreateIngredientType().name("test");
        final var id = UUID.randomUUID();
        final var problem = new NoSuchResourceProblem(INGREDIENT_TYPE, id.toString());

        when(ingredientTypeService.update(any(), any())).thenThrow(problem);

        mockMvc.perform(put("/types/{uuid}", id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }

    @Test
    void testUpdateConflict() throws Exception {
        final var ingredient = new CreateIngredientType().name("test");
        final var id = UUID.randomUUID();
        final var problem = new ConflictProblem("name",
                "test",
                INGREDIENT_TYPE,
                id.toString());
        when(ingredientTypeService.update(any(), any())).thenThrow(problem);

        mockMvc.perform(put("/types/{uuid}", id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ingredient)))
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.conflictFieldName").value(problem.getConflictFieldName()))
                .andExpect(jsonPath("$.conflictFieldValue").value(problem.getConflictFieldValue()))
                .andExpect(jsonPath("$.resourceType").value(problem.getResourceType().getType()))
                .andExpect(jsonPath("$.resourceId").value(problem.getResourceId()));
    }
}
