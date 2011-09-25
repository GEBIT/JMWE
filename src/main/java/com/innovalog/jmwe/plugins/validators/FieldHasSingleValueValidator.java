/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.innovalog.googlecode.jsu.annotation.Argument;
import com.innovalog.googlecode.jsu.util.FieldCollectionsUtils;
import com.innovalog.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author fischerd
 *
 */
public class FieldHasSingleValueValidator extends GenericValidator {

	@Argument("fieldKey")
	private String fieldKey;

	@Argument("jira.excludingSubtasks")
	private String excludingSubtasks;

    public FieldHasSingleValueValidator(WorkflowUtils workflowUtils, FieldCollectionsUtils fieldCollectionsUtils) {
        super(workflowUtils, fieldCollectionsUtils);
    }

    /* (non-Javadoc)
      * @see com.innovalog.jmwe.plugins.validators.GenericValidator#validate()
      */
	@Override
	protected void validate() throws InvalidInputException, WorkflowException {
		Field field = workflowUtils.getFieldFromKey(fieldKey);
		final Issue issue = getIssue();
		Object value = workflowUtils.getFieldValueFromIssue(issue, field);
		if (value instanceof Collection && fieldCollectionsUtils.isIssueHasField(issue, field) && ((Collection)value).size() > 1)
			if (excludingSubtasks != null && excludingSubtasks.equals("yes"))
				//we should look at subtasks and exclude values coming from their corresponding field
			{
				Set values = new HashSet((Collection)value);
				//iterate through subtasks
				for (Object subtask : issue.getSubTaskObjects())
				{
					if (fieldCollectionsUtils.isIssueHasField((Issue)subtask, field))
					{
						//get value of field
						Object subtaskValue = workflowUtils.getFieldValueFromIssue((Issue)subtask, field);
						if (subtaskValue instanceof Collection)
							//remove values from subtask field
							values.removeAll((Collection) subtaskValue);
					}
				}
				//check remaining values
				if (values.size() > 1)
				{
					//report error
					this.setExceptionMessage(
							field, 
							field.getName() + " should not have more than one value (excluding values copied from sub-tasks).", 
							field.getName() + " should not have more than one value (excluding values copied from sub-tasks). But it is not present on screen."
					);
				}
			}
			else
				this.setExceptionMessage(
						field, 
						field.getName() + " should not have more than one value.", 
						field.getName() + " should not have more than one value. But it is not present on screen."
				);
	}

}
