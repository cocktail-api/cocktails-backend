package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.mapper.IngredientTypeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings
class IngredientTypeServiceTest {

    @Mock
    private IngredientTypeDao dao;

    @Mock
    private IngredientTypeMapper mapper;

    @InjectMocks
    private IngredientTypeService service;

    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 100L, ((long) Integer.MAX_VALUE) + 1})
    void testCount(final long count) {
        when(dao.count()).thenReturn(count);

        assertEquals(count, service.count(), "Type count returning incorrect value");
    }

    @Test
    void testTypeList() {
        when(dao.getAll(0, 2)).thenReturn(List.of(
                new DbIngredientType(UUID.randomUUID(), "name"),
                new DbIngredientType(UUID.randomUUID(), "name")
        ));
        when(mapper.fromDb(any())).thenReturn(new IngredientType());

        assertEquals(2, service.types(1, 2).size());
    }
}
