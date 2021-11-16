package de.slevermann.cocktails.backend.service.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class BadReferenceProblem extends AbstractThrowableProblem {

    private final ResourceType referencedResourceType;

    private final String resourceId;

    public BadReferenceProblem(final ResourceType referencedResourceType,
                               final String resourceId) {
        super(null,
                "Referenced entity does not exist",
                Status.UNPROCESSABLE_ENTITY,
                String.format("The referenced resource %s with ID %s does not exist", referencedResourceType.getType(), resourceId));
        this.resourceId = resourceId;
        this.referencedResourceType = referencedResourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public ResourceType getReferencedResourceType() {
        return referencedResourceType;
    }
}
