package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.CreateIngredient;
import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.backend.dao.IngredientDao;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.mapper.IngredientMapper;
import de.slevermann.cocktails.backend.service.problem.MissingReferenceProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ReferencedEntityProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class IngredientService {

    private final IngredientDao ingredientDao;

    private final IngredientTypeDao ingredientTypeDao;

    private final IngredientMapper ingredientMapper;

    public List<Ingredient> ingredients(final int page, final int pageSize) {
        return ingredientDao.getAll((page - 1) * pageSize, pageSize)
                .stream().map(ingredientMapper::fromDb).toList();
    }

    public long count() {
        return ingredientDao.count();
    }

    @Transactional
    public Ingredient create(final CreateIngredient createIngredient) {
        final var typeId = createIngredient.getType();
        final var type = ingredientTypeDao.getById(typeId);
        if (type == null) {
            throw new MissingReferenceProblem(ResourceType.INGREDIENT_TYPE, typeId.toString());
        }
        return ingredientMapper.fromDb(
                ingredientDao.create(ingredientMapper.fromApi(createIngredient)));
    }

    @Transactional
    public void delete(final UUID uuid) {
        if (ingredientDao.usedByCount(uuid) > 0) {
            throw new ReferencedEntityProblem(ResourceType.INGREDIENT, uuid.toString());
        }

        if (ingredientDao.shelfCount(uuid) > 0) {
            throw new ReferencedEntityProblem(ResourceType.INGREDIENT, uuid.toString());
        }

        if (ingredientDao.delete(uuid) == 0) {
            throw new NoSuchResourceProblem(ResourceType.INGREDIENT, uuid.toString());
        }
    }

    public Ingredient get(final UUID uuid) {
        final var ingredient = ingredientDao.getById(uuid);
        if (ingredient == null) {
            throw new NoSuchResourceProblem(ResourceType.INGREDIENT, uuid.toString());
        }
        return ingredientMapper.fromDb(ingredient);
    }
}
