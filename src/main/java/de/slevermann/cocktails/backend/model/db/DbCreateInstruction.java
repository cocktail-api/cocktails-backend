package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

public record DbCreateInstruction(@NonNull String text,
                            int number) {
}
