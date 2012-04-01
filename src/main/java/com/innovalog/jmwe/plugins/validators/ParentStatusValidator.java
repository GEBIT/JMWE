/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import java.util.Set;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.status.Status;
import com.googlecode.jsu.annotation.Argument;
import com.googlecode.jsu.util.FieldCollectionsUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author fischerd
 *
 */
public class ParentStatusValidator extends GenericValidator {

	@Argument("jira.parentstatuses")
	private String parentStatuses;

    public ParentStatusValidator(WorkflowUtils workflowUtils, FieldCollectionsUtils fieldCollectionsUtils) {
        super(workflowUtils, fieldCollectionsUtils);
    }

    /* (non-Javadoc)
      * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
      */
	public void validate()
			throws InvalidInputException, WorkflowException {
        Issue genericIssue =  getIssue();

        Set<String> statuses = WorkflowParentStatusValidator.getStringSet(parentStatuses);
        
        Issue parentIssue = genericIssue.getParentObject();
        if (parentIssue == null)
        	return;
        
        Status parentStatus = parentIssue.getStatusObject();
        
        if (parentStatus != null && !statuses.contains(parentStatus.getName()))
        {
        	//parent issue is not in required status
    		throw new WorkflowException("Transition is not authorized because current Issue's parent Issue should be in one of the following statuses: "+statuses.toString());
        }
	}

}
