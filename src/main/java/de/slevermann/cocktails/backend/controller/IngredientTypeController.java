package de.slevermann.cocktails.backend.controller;

import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.service.IngredientTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RequestMapping("/types")
@RequiredArgsConstructor
@RestController
@Validated
public class IngredientTypeController {

    private final IngredientTypeService typeService;

    @GetMapping
    public List<DbIngredientType> getAll(@RequestParam(name = "page", defaultValue = "1") @Min(1) final int page,
                                         @RequestParam(name = "pageSize", defaultValue = "10") @Min(1) @Max(50) final int pageSize) {
        return typeService.types(page, pageSize);
    }

    @GetMapping("/count")
    public long count() {
        return typeService.count();
    }
}
