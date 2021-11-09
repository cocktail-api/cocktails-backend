package de.slevermann.cocktails.backend.model.mapper;

import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CocktailListEntry;
import de.slevermann.cocktails.api.model.Unit;
import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import org.mapstruct.EnumMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

import java.util.List;

@Mapper(uses = {IngredientMapper.class})
public interface CocktailMapper {

    CocktailListEntry fromDb(final DbCocktail cocktail);

    Cocktail fromDb(final DbCocktail cocktail, final List<DbCocktailIngredient> ingredients);

    @ValueMappings({
            @ValueMapping(source = "milliliters", target = "MILLILITERS"),
            @ValueMapping(source = "grams", target = "GRAMS"),
            @ValueMapping(source = "barspoons", target = "BARSPOONS")
    })
    Unit fromDb(final DbUnit unit);
}
