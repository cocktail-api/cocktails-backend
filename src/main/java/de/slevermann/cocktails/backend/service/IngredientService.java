package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.mapper.IngredientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class IngredientService {

    private final IngredientDao ingredientDao;

    private final IngredientMapper ingredientMapper;

    public List<Ingredient> ingredients(final int page, final int pageSize) {
        return ingredientDao.getAll((page - 1) * pageSize, pageSize)
                .stream().map(ingredientMapper::fromDb).toList();
    }

    public long count() {
        return ingredientDao.count();
    }
}
