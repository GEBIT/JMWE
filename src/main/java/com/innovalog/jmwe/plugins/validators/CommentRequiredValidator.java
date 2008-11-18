/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.innovalog.googlecode.jsu.annotation.Argument;
import com.innovalog.googlecode.jsu.annotation.TransientVariable;
import com.opensymphony.util.TextUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author fischerd
 *
 */
public class CommentRequiredValidator extends GenericValidator {
	
	@TransientVariable(WorkflowTransitionUtil.FIELD_COMMENT)
	private String strComment;
	
	@Argument("errorMessage")
	private String errorMsg;

	/* (non-Javadoc)
	 * @see com.innovalog.jmwe.plugins.validators.GenericValidator#validate()
	 */
	@Override
	protected void validate() throws InvalidInputException, WorkflowException {
        if (!TextUtils.stringSet(strComment))
        {
        	//find Comment field
        	Field field = ManagerFactory.getFieldManager().getField(IssueFieldConstants.COMMENT);
        	this.setExceptionMessage(field, errorMsg, "A Comment is required but cannot be input. Please report this error to David.");
        }
	}

}
