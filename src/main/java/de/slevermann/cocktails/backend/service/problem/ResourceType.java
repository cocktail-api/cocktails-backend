package de.slevermann.cocktails.backend.service.problem;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceType {
    COCKTAIL("Cocktail"),
    INGREDIENT("Ingredient"),
    INGREDIENT_TYPE("IngredientType"),
    USER("User");

    private final String type;

    ResourceType(final String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
