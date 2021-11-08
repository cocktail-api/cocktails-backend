package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.backend.dao.CocktailDao;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.model.Cocktail;
import de.slevermann.cocktails.backend.model.db.DbCocktail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CocktailService {

    private final CocktailDao cocktailDao;

    private final IngredientDao ingredientDao;

    public List<DbCocktail> cocktails(final int page, final int pageSize) {
        return cocktailDao.getAll((page - 1) * pageSize, pageSize);
    }

    @Transactional
    public Cocktail cocktail(final UUID uuid) {
        final var fromDb = cocktailDao.findById(uuid);

        if (fromDb == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        final var ingredients = ingredientDao.findByCocktail(fromDb.id());

        return new Cocktail(fromDb, ingredients);
    }

    public long count() {
        return cocktailDao.count();
    }
}
