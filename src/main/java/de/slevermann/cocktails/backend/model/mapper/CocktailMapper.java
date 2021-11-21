package de.slevermann.cocktails.backend.model.mapper;

import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CocktailInstruction;
import de.slevermann.cocktails.api.model.CocktailListEntry;
import de.slevermann.cocktails.api.model.CreateCocktail;
import de.slevermann.cocktails.api.model.CreateCocktailIngredient;
import de.slevermann.cocktails.api.model.Unit;
import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.DbInstruction;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import de.slevermann.cocktails.backend.model.db.create.DbCreateCocktail;
import de.slevermann.cocktails.backend.model.db.create.DbCreateCocktailIngredient;
import de.slevermann.cocktails.backend.model.db.create.DbCreateInstruction;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

import java.util.List;

@Mapper(uses = {IngredientMapper.class})
public interface CocktailMapper {

    CocktailListEntry fromDb(final DbCocktail cocktail);

    Cocktail fromDb(final DbCocktail cocktail,
                    final List<DbCocktailIngredient> ingredients,
                    final List<DbInstruction> instructions);

    @ValueMappings({
            @ValueMapping(source = "milliliters", target = "MILLILITERS"),
            @ValueMapping(source = "grams", target = "GRAMS"),
            @ValueMapping(source = "barspoons", target = "BARSPOONS")
    })
    Unit fromDb(final DbUnit unit);

    @ValueMappings({
            @ValueMapping(target = "milliliters", source = "MILLILITERS"),
            @ValueMapping(target = "grams", source = "GRAMS"),
            @ValueMapping(target = "barspoons", source = "BARSPOONS")
    })
    DbUnit fromApi(final Unit unit);

    DbCreateCocktail fromApi(final CreateCocktail cocktail);

    DbCreateCocktailIngredient fromApi(final CreateCocktailIngredient ingredient);

    DbCreateInstruction fromApi(final CocktailInstruction instruction, final int number);

    CocktailInstruction fromDb(final DbInstruction instruction);
}
