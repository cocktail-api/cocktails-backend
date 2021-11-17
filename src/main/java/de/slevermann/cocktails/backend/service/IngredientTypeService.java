package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.mapper.IngredientTypeMapper;
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

    @Transactional
    public void delete(final UUID uuid) {
        if (ingredientTypeDao.usedByCount(uuid) > 0) {
            throw new ReferencedEntityProblem(ResourceType.INGREDIENT_TYPE, uuid.toString());
        }

        if (ingredientTypeDao.delete(uuid) == 0) {
            throw new NoSuchResourceProblem(ResourceType.INGREDIENT_TYPE, uuid.toString());
        }
    }

    public IngredientType get(final UUID uuid) {
        final var type = ingredientTypeDao.getById(uuid);
        if (type == null) {
            throw new NoSuchResourceProblem(ResourceType.INGREDIENT_TYPE, uuid.toString());
        }
        return typeMapper.fromDb(type);
    }
}
