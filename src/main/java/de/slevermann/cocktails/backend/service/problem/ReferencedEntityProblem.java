package de.slevermann.cocktails.backend.service.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ReferencedEntityProblem extends AbstractThrowableProblem {

    private final ResourceType resourceType;

    private final String resourceId;

    public ReferencedEntityProblem(final ResourceType resourceType,
                                   final String resourceId) {
        super(null,
                "Entity is referenced by other resource",
                Status.BAD_REQUEST,
                String.format("The resource %s with ID %s cannot be deleted because" +
                        " it is in use by another entity", resourceType.getType(), resourceId));
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }
}
