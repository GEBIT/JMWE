/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.googlecode.jsu.annotation.Argument;
import com.googlecode.jsu.util.FieldCollectionsUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author fischerd
 * 
 */
public class FieldChangedValidator extends GenericValidator
{
	@Argument("fieldKey")
	private String fieldKey;

    public FieldChangedValidator(WorkflowUtils workflowUtils, FieldCollectionsUtils fieldCollectionsUtils) {
        super(workflowUtils, fieldCollectionsUtils);
    }

    /*
      * (non-Javadoc)
      *
      * @see com.innovalog.jmwe.plugins.validators.GenericValidator#validate()
      */
	@Override
	protected void validate() throws InvalidInputException, WorkflowException
	{
		Field field = workflowUtils.getFieldFromKey(fieldKey);
		final Issue issue = getIssue();
		MutableIssue mu = null;
		if (issue instanceof MutableIssue)
			mu = (MutableIssue) issue;
		if (mu != null)
		{
			ModifiedValue mv = (ModifiedValue) mu.getModifiedFields().get(fieldKey);

			if (mv != null
					&& (mv.getOldValue() == mv.getNewValue() || mv.getOldValue() != null
							&& mv.getOldValue().equals(mv.getNewValue())))
			{
				// report error
				this.setExceptionMessage(field, field.getName() + " should be modified during this transition.", "Field "
						+ field.getName()
						+ " should be modified during this transition, but it is not present on the transition screen.");
			}
		}
	}
}
