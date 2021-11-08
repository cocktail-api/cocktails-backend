package de.slevermann.cocktails.backend.controller;

import de.slevermann.cocktails.api.model.Ingredient;
import de.slevermann.cocktails.backend.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

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

}
