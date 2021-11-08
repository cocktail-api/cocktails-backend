package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.mapper.IngredientTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class IngredientTypeService {

    private final IngredientTypeDao ingredientTypeDao;

    private final IngredientTypeMapper typeMapper;

    public List<IngredientType> types(final int page, final int pageSize) {
        return ingredientTypeDao.getAll((page - 1) * pageSize, pageSize)
                .stream().map(typeMapper::fromDb).toList();
    }

    public long count() {
        return ingredientTypeDao.count();
    }

}
