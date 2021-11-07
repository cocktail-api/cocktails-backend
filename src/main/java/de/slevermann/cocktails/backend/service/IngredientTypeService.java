package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class IngredientTypeService {

    private final IngredientTypeDao ingredientTypeDao;

    public List<DbIngredientType> types(final int page, final int pageSize) {
        return ingredientTypeDao.getAll((page - 1) * pageSize, pageSize);
    }

}
