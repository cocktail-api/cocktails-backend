package de.slevermann.cocktails.backend.controller;

import de.slevermann.cocktails.api.model.CreateIngredientType;
import de.slevermann.cocktails.api.model.IngredientType;
import de.slevermann.cocktails.backend.service.IngredientTypeService;
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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RequestMapping("/types")
@RequiredArgsConstructor
@RestController
@Validated
public class IngredientTypeController {

    private final IngredientTypeService typeService;

    @GetMapping
    public List<IngredientType> getAll(@RequestParam(name = "page", defaultValue = "1") @Min(1) final int page,
                                       @RequestParam(name = "pageSize", defaultValue = "10") @Min(1) @Max(50) final int pageSize) {
        return typeService.types(page, pageSize);
    }

    @GetMapping("/count")
    public long count() {
        return typeService.count();
    }

    @GetMapping("/{uuid}")
    public IngredientType get(@PathVariable("uuid") final UUID uuid) {
        return typeService.get(uuid);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable("uuid") final UUID uuid) {
        typeService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<IngredientType> create(@RequestBody final CreateIngredientType type) {
        final var createdType = typeService.create(type.getName());
        return ResponseEntity.created(fromMethodCall(
                        on(IngredientTypeController.class).get(createdType.getId())).build().toUri())
                .body(createdType);
    }

    @PutMapping("/{uuid}")
    public IngredientType update(@RequestBody final String name,
                                 @PathVariable("uuid") final UUID uuid) {
        return typeService.update(name, uuid);
    }
}
