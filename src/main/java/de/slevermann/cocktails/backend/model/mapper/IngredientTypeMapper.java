package de.slevermann.cocktails.backend.model.mapper;

import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import org.mapstruct.Mapper;

@Mapper
public interface IngredientTypeMapper {

    IngredientType fromDb(final DbIngredientType ingredientType);
}
