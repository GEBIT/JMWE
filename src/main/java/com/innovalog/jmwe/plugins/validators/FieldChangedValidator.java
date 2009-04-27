/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.innovalog.googlecode.jsu.annotation.Argument;
import com.innovalog.googlecode.jsu.util.CommonPluginUtils;
import com.innovalog.googlecode.jsu.util.WorkflowUtils;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.innovalog.jmwe.plugins.validators.GenericValidator#validate()
	 */
	@Override
	protected void validate() throws InvalidInputException, WorkflowException
	{
		Field field = WorkflowUtils.getFieldFromKey(fieldKey);
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
