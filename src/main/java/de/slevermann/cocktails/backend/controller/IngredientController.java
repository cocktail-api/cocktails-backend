package de.slevermann.cocktails.backend.controller;

import de.slevermann.cocktails.api.model.CreateIngredient;
import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.api.model.PagedCocktails;
import de.slevermann.cocktails.api.model.PagedIngredients;
import de.slevermann.cocktails.backend.service.CocktailService;
import de.slevermann.cocktails.backend.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RequestMapping("/ingredients")
@RequiredArgsConstructor
@RestController
@Validated
public class IngredientController {

    private final IngredientService ingredientService;

    private final CocktailService cocktailService;

    @GetMapping
    public PagedIngredients getAll(@RequestParam(name = "page", defaultValue = "1") @Min(1) final int page,
                                   @RequestParam(name = "pageSize", defaultValue = "20") @Min(1) @Max(50) final int pageSize) {
        return ingredientService.ingredients(page, pageSize);
    }

    @GetMapping("/{uuid}/cocktails")
    public PagedCocktails getByIngredient(@RequestParam(name = "page", defaultValue = "1") @Min(1) final int page,
                                          @RequestParam(name = "pageSize", defaultValue = "20") @Min(1) @Max(50) final int pageSize,
                                          @PathVariable("uuid") final UUID uuid) {
        return cocktailService.findByIngredient(page, pageSize, uuid);
    }

    @GetMapping("/count")
    public long count() {
        return ingredientService.count();
    }

    @PostMapping
    public ResponseEntity<Ingredient> create(@RequestBody @Valid CreateIngredient createIngredient) {
        final var createdIngredient = ingredientService.create(createIngredient);
        return ResponseEntity.created(fromMethodCall(
                        on(IngredientController.class).getById(createdIngredient.getId())).build().toUri())
                .body(createdIngredient);
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

    @PutMapping("/{uuid}")
    public Ingredient update(@RequestBody @Valid CreateIngredient createIngredient,
                             @PathVariable("uuid") final UUID uuid) {
        return ingredientService.update(createIngredient, uuid);
    }
}
