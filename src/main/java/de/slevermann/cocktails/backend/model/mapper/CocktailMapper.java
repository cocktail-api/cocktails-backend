package de.slevermann.cocktails.backend.model.mapper;

import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CocktailListEntry;
import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {IngredientMapper.class})
public interface CocktailMapper {

    CocktailListEntry fromDb(final DbCocktail cocktail);

    Cocktail fromDb(final DbCocktail cocktail, final List<DbIngredient> ingredients);
}
