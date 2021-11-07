package de.slevermann.cocktails.backend.controller;

import de.slevermann.cocktails.backend.model.Cocktail;
import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.service.CocktailService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RequestMapping("/cocktails")
@RequiredArgsConstructor
@RestController
@Validated
public class CocktailController {

    private final CocktailService cocktailService;

    @GetMapping
    public List<DbCocktail> getAll(@RequestParam(name = "page", defaultValue = "1") @Min(1) final int page,
                                   @RequestParam(name = "pageSize", defaultValue = "10") @Min(1) @Max(50) final int pageSize) {
        return cocktailService.cocktails(page, pageSize);
    }

    @GetMapping("/{uuid}")
    public Cocktail getById(@PathVariable @Valid final UUID uuid) {
        return cocktailService.cocktail(uuid);
    }
}
