package de.slevermann.cocktails.backend.service.problem;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ConflictProblem extends AbstractThrowableProblem {

    private final String conflictFieldName;
    private final Object conflictFieldValue;
    private final ResourceType resourceType;
    private final String resourceId;

    public ConflictProblem(final String conflictFieldName,
                           final Object conflictFieldValue,
                           final ResourceType resourceType,
                           final String resourceId) {
        super(null,
                "Conflicting entity of type %s exists",
                Status.CONFLICT,
                String.format("A resource of type %s with '%s': '%s' already exists, and its ID is %s",
                        resourceType, conflictFieldName, conflictFieldValue, resourceId)
        );
        this.conflictFieldName = conflictFieldName;
        this.conflictFieldValue = conflictFieldValue;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getConflictFieldName() {
        return conflictFieldName;
    }

    public Object getConflictFieldValue() {
        return conflictFieldValue;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}
