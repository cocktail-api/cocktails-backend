package de.slevermann.cocktails.backend.service;

import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.api.model.PagedTypes;
import de.slevermann.cocktails.backend.dao.IngredientTypeDao;
import de.slevermann.cocktails.backend.model.mapper.IngredientTypeMapper;
import de.slevermann.cocktails.backend.service.problem.ConflictProblem;
import de.slevermann.cocktails.backend.service.problem.NoSuchResourceProblem;
import de.slevermann.cocktails.backend.service.problem.ReferencedEntityProblem;
import de.slevermann.cocktails.backend.service.problem.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class IngredientTypeService {

    private final IngredientTypeDao ingredientTypeDao;

    private final IngredientTypeMapper typeMapper;

    public PagedTypes types(final int page, final int pageSize) {
        final var types = ingredientTypeDao.getAll((page - 1) * pageSize, pageSize)
                .stream().map(typeMapper::fromDb).toList();
        final var count = count();
        var totalPages = count / pageSize;
        if (count % pageSize != 0) {
            totalPages++;
        }
        return new PagedTypes()
                .types(types)
                .total(count)
                .lastPage(totalPages);
    }

    public long count() {
        return ingredientTypeDao.count();
    }

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

    public IngredientType create(final String name) {
        final var type = ingredientTypeDao.findByName(name);
        if (type != null) {
            throw new ConflictProblem("name",
                    name,
                    ResourceType.INGREDIENT_TYPE,
                    type.id().toString());
        }
        return typeMapper.fromDb(ingredientTypeDao.create(name));
    }

    public IngredientType update(final String name, final UUID uuid) {
        var type = ingredientTypeDao.findByNameAndNotId(name, uuid);
        if (type != null) {
            throw new ConflictProblem("name",
                    name,
                    ResourceType.INGREDIENT_TYPE,
                    type.id().toString());
        }
        type = ingredientTypeDao.update(uuid, name);
        if (type == null) {
            throw new NoSuchResourceProblem(ResourceType.INGREDIENT_TYPE, uuid.toString());
        }
        return typeMapper.fromDb(type);
    }
}
