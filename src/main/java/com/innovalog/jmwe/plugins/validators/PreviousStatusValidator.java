/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import java.util.List;

import com.googlecode.jsu.util.FieldCollectionsUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.googlecode.jsu.annotation.Argument;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author fischerd
 *
 */
public class PreviousStatusValidator extends GenericValidator {

	@Argument("jira.previousstatus")
	private String statusToLookFor;

	@Argument("jira.mostRecentStatusOnly")
	private String mostRecentOnlyString;

    public PreviousStatusValidator(WorkflowUtils workflowUtils, FieldCollectionsUtils fieldCollectionsUtils) {
        super(workflowUtils, fieldCollectionsUtils);
    }

    /* (non-Javadoc)
      * @see com.innovalog.jmwe.plugins.validators.GenericValidator#validate()
      */
	@Override
	protected void validate() throws InvalidInputException, WorkflowException {
		final Issue genericIssue = getIssue();
		final boolean mostRecentStatusOnly = mostRecentOnlyString.equals("yes");
		
       	ChangeHistoryManager changeHistoryManager = (ChangeHistoryManager)ComponentManager.getComponentInstanceOfType(ChangeHistoryManager.class);
    	//changeHistory represents each changeset of the issue. we get each field changed per changeset below in changeItemBeans
    	List changeHistory = changeHistoryManager.getChangeHistoriesForUser(genericIssue, null);

    	for (int i = changeHistory.size() - 1; i >= 0; i--) {
    		ChangeHistory changeHistoryItem = (ChangeHistory) changeHistory.get(i);

    		//changeItemBeans is a List of the fields that were modified in this issue change
    		List changeItemBeans = changeHistoryItem.getChangeItems();
    		java.util.Iterator it = changeItemBeans.iterator();
    		//loop over all the fields updated in this change and look for a change in 'status'
    		while ( it.hasNext() ) {
    			GenericValue change = (GenericValue)it.next();
    			String changedField = change.getString("field");
    			/*
    			 * (GenericValue)change seems to have properties matching the columns of the changeitem db table
    			 * we're interested in change items where the field == 'status'
    			 * The old and new data is capture in two forms: oldvalue/oldstring and newvalue/newstring
    			 * The "value" appears to be the step id, while the "string" is the Status name
    			 */
    			if ( changedField.equalsIgnoreCase("status") ) {
    				//we've found a transition
    				String oldStatus = change.getString("oldstring");
    				
    				if (oldStatus.compareToIgnoreCase(statusToLookFor)==0)
    				//we've found our status, validate
    					return;
    				else if (mostRecentStatusOnly)
    					//since our latest status is NOT the one we're looking for, invalidate
    					i = 0;	//to exit our "for" loop
    					break;
	    			}
    		}
    	}

        //if we reached this point, it means we haven't found the status we were looking for
    	StringBuilder sb = new StringBuilder();
    	sb.append("Transition is not authorized because current Issue ");
    	if (mostRecentStatusOnly)
    		sb.append("was not previously in status "+statusToLookFor);
    	else
    		sb.append("has never been in status "+statusToLookFor+" before.");
		throw new WorkflowException(sb.toString());
	}

}
