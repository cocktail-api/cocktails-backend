package de.slevermann.cocktails.backend.service.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.util.Map;


public class NoSuchResourceProblem extends AbstractThrowableProblem {

    public NoSuchResourceProblem(final ResourceType resourceType, final String resourceId) {
        super(null,
                "Not found",
                Status.NOT_FOUND,
                String.format("%s %s not found", resourceType.getType(), resourceId),
                null,
                null,
                Map.of("parameter", resourceId));
    }
}
