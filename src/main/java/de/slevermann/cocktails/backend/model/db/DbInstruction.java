package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

public record DbInstruction(@NonNull String text,
                            int number) {
}
