package de.slevermann.cocktails.backend.service.problem;

public enum ResourceType {
    COCKTAIL("Cocktail"),
    INGREDIENT("Ingredient"),
    USER("User");

    private final String type;

    ResourceType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
