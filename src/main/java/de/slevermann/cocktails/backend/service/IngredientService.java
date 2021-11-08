package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class IngredientService {

    private final IngredientDao ingredientDao;

    public List<DbIngredient> ingredients(final int page, final int pageSize) {
        return ingredientDao.getAll((page - 1) * pageSize, pageSize);
    }

    public long count() {
        return ingredientDao.count();
    }
}
