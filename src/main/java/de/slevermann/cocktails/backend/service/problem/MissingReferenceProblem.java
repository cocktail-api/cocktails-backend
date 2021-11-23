package de.slevermann.cocktails.backend.service.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.util.Set;

public class MissingReferenceProblem extends AbstractThrowableProblem {

    private final ResourceType referencedResourceType;

    private final Set<String> resourceIds;

    public MissingReferenceProblem(final ResourceType referencedResourceType,
                                   final String resourceId) {
        this(referencedResourceType, Set.of(resourceId));
    }

    public MissingReferenceProblem(final ResourceType referencedResourceType,
                                   final Set<String> resourceIds) {
        super(null,
                "Referenced entity does not exist",
                Status.UNPROCESSABLE_ENTITY,
                String.format("The referenced resource(s) %s with ID(s) %s does not exist", referencedResourceType.getType(), resourceIds));
        this.resourceIds = resourceIds;
        this.referencedResourceType = referencedResourceType;
    }

    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public ResourceType getReferencedResourceType() {
        return referencedResourceType;
    }
}
