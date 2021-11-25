package de.slevermann.cocktails.backend.service.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.util.UUID;

public class NoSuchResourceProblem extends AbstractThrowableProblem {

    private final ResourceType resourceType;

    private final String resourceId;

    public NoSuchResourceProblem(final ResourceType resourceType, final String resourceId) {
        super(null,
                "Not found",
                Status.NOT_FOUND,
                String.format("%s %s not found", resourceType.getType(), resourceId));
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    public NoSuchResourceProblem(final ResourceType resourceType, final UUID uuid) {
        this(resourceType, uuid.toString());
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
