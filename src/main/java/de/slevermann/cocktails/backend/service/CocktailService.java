package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CocktailListEntry;
import de.slevermann.cocktails.api.model.CreateCocktail;
import de.slevermann.cocktails.api.model.CreateCocktailIngredient;
import de.slevermann.cocktails.backend.dao.CocktailDao;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.DbInstruction;
import de.slevermann.cocktails.backend.model.db.create.DbCreateInstruction;
import de.slevermann.cocktails.backend.model.mapper.CocktailMapper;
import de.slevermann.cocktails.backend.service.problem.MissingReferenceProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

        final var ingredients = cocktailDao.getIngredients(fromDb.id());
        final var instructions = cocktailDao.getInstructions(fromDb.id());
        return cocktailMapper.fromDb(fromDb, ingredients, instructions);
    }

    public long count() {
        return cocktailDao.count();
    }

    public List<CocktailListEntry> findByIngredient(final int page, final int pageSize, final UUID uuid) {
        if (ingredientDao.getById(uuid) == null) {
            throw new NoSuchResourceProblem(ResourceType.INGREDIENT, uuid.toString());
        }

        return cocktailDao.findByIngredient((page - 1) * pageSize, pageSize, uuid)
                .stream().map(cocktailMapper::fromDb).toList();
    }

    public Cocktail create(final CreateCocktail cocktail) {
        final var cocktailIds = cocktail.getIngredients().stream().map(CreateCocktailIngredient::getId)
                .collect(Collectors.toSet());
        final var dbIds = ingredientDao.findIngredients(cocktailIds);

        /*
         * Relative complement of the two sets.
         *
         * cocktailIds now contains all the IDs that were in the request but are not in the database
         */
        cocktailIds.removeAll(dbIds);
        cocktailIds.stream().findAny().ifPresent(id ->
        {
            throw new MissingReferenceProblem(ResourceType.INGREDIENT, id.toString());
        });

        final var dbCreateCocktail = cocktailMapper.fromApi(cocktail);
        final var dbCocktail = cocktailDao.create(dbCreateCocktail);
        final var ingredients = cocktail.getIngredients().stream()
                .map(cocktailMapper::fromApi).collect(Collectors.toList());
        cocktailDao.addIngredients(dbCocktail.id(), ingredients);
        final List<DbCocktailIngredient> dbIngredients = cocktailDao.getIngredients(dbCocktail.id());

        final Set<DbCreateInstruction> createInstructions = new HashSet<>();
        final var apiInstructions = cocktail.getInstructions();
        for (int i = 0; i < apiInstructions.size(); i++) {
            createInstructions.add(cocktailMapper.fromApi(apiInstructions.get(i), i));
        }
        // Due to the batching, we are not guaranteed the correct ordering on insert, only on updates. Sort manually
        final List<DbInstruction> instructions = cocktailDao.addInstructions(dbCocktail.id(), createInstructions);
        instructions.sort(Comparator.comparing(DbInstruction::number));

        return cocktailMapper.fromDb(dbCocktail, dbIngredients, instructions);
    }
}
