package de.slevermann.cocktails.backend.controller;

import de.slevermann.cocktails.api.model.Cocktail;
import de.slevermann.cocktails.api.model.CreateCocktail;
import de.slevermann.cocktails.api.model.PagedCocktails;
import de.slevermann.cocktails.backend.service.CocktailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RequestMapping("/cocktails")
@RequiredArgsConstructor
@RestController
@Validated
public class CocktailController {

    private final CocktailService cocktailService;

    @GetMapping
    public PagedCocktails getAll(@RequestParam(name = "page", defaultValue = "1") @Min(1) final int page,
                                 @RequestParam(name = "pageSize", defaultValue = "20") @Min(1) @Max(50) final int pageSize) {
        return cocktailService.cocktails(page, pageSize);
    }

    @GetMapping("/{uuid}")
    public Cocktail getById(@PathVariable @Valid final UUID uuid) {
        return cocktailService.cocktail(uuid);
    }

    @GetMapping("/count")
    public long count() {
        return cocktailService.count();
    }

    @PostMapping
    public ResponseEntity<Cocktail> create(@RequestBody @Valid final CreateCocktail cocktail) {
        final var c = cocktailService.create(cocktail);
        return ResponseEntity.created(fromMethodCall(
                        on(CocktailController.class).getById(c.getId())).build().toUri())
                .body(c);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("uuid") final UUID uuid) {
        cocktailService.delete(uuid);
    }

    @PutMapping("/{uuid}")
    public Cocktail update(@RequestBody @Valid final CreateCocktail cocktail, @PathVariable("uuid") final UUID uuid) {
        return cocktailService.update(cocktail, uuid);
    }
}
