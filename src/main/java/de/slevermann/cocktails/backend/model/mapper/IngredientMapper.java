package de.slevermann.cocktails.backend.model.mapper;

import de.slevermann.cocktails.api.model.CreateIngredient;
import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import org.mapstruct.Mapper;

@Mapper(uses = IngredientTypeMapper.class)
public interface IngredientMapper {

    Ingredient fromDb(final DbIngredient ingredient);

    DbCreateIngredient fromApi(final CreateIngredient createIngredient);
}
