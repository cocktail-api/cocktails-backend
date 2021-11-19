package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CocktailListEntry;
import de.slevermann.cocktails.backend.dao.CocktailDao;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.model.mapper.CocktailMapper;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class CocktailService {

    private final CocktailDao cocktailDao;

    private final IngredientDao ingredientDao;

    private final CocktailMapper cocktailMapper;

    public List<CocktailListEntry> cocktails(final int page, final int pageSize) {
        return cocktailDao.getAll((page - 1) * pageSize, pageSize)
                .stream().map(cocktailMapper::fromDb).toList();
    }

    public Cocktail cocktail(final UUID uuid) {
        final var fromDb = cocktailDao.getById(uuid);

        if (fromDb == null) {
            throw new NoSuchResourceProblem(ResourceType.COCKTAIL, uuid.toString());
        }

        final var ingredients = ingredientDao.findByCocktail(fromDb.id());

        return cocktailMapper.fromDb(fromDb, ingredients);
    }

    public long count() {
        return cocktailDao.count();
    }
}
