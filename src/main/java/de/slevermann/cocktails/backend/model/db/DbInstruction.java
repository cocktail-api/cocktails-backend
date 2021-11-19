package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.time.OffsetDateTime;

public record DbInstruction(@NonNull String text,
                            int number,
                            OffsetDateTime created,
                            OffsetDateTime modified) {
}
