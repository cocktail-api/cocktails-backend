package de.slevermann.cocktails.backend.controller;

import de.slevermann.cocktails.api.model.CreateIngredient;
import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.backend.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RequestMapping("/ingredients")
@RequiredArgsConstructor
@RestController
@Validated
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAll(@RequestParam(name = "page", defaultValue = "1") @Min(1) final int page,
                                   @RequestParam(name = "pageSize", defaultValue = "10") @Min(1) @Max(50) final int pageSize) {
        return ingredientService.ingredients(page, pageSize);
    }

    @GetMapping("/count")
    public long count() {
        return ingredientService.count();
    }

    @PostMapping
    public Ingredient create(@RequestBody @Valid CreateIngredient createIngredient) {
        return ingredientService.create(createIngredient);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable final UUID uuid) {
        ingredientService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}")
    public Ingredient getById(@PathVariable final UUID uuid) {
        return ingredientService.get(uuid);
    }
}
