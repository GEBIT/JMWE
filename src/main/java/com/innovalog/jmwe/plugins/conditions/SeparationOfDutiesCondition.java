/**
 * 
 */
package com.innovalog.jmwe.plugins.conditions;

import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author fischerd
 *
 */
public class SeparationOfDutiesCondition extends AbstractJiraCondition {

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public boolean passesCondition(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
        Issue genericIssue =  (Issue) transientVars.get("issue");
        String fromStatus = (String)args.get("fromStatus");
        String toStatus = (String)args.get("toStatus");
        
        //current user
		WorkflowContext context = (WorkflowContext) transientVars.get("context");
		String userName = context.getCaller();
        
       	ChangeHistoryManager changeHistoryManager = (ChangeHistoryManager)ComponentManager.getComponentInstanceOfType(ChangeHistoryManager.class);
    	//changeHistory represents each changeset of the issue. we get each field changed per changeset below in changeItemBeans
    	List changeHistory = changeHistoryManager.getChangeHistoriesForUser(genericIssue, null);

    	for (int i = changeHistory.size() - 1; i >= 0; i--) {
    		ChangeHistory changeHistoryItem = (ChangeHistory) changeHistory.get(i);
    		if (changeHistoryItem.getUsername()==null || !changeHistoryItem.getUsername().equals(userName))
    			//we're only interested in changes by the current user
    			continue;

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
    				String newStatus = change.getString("newstring");
    				
    				if (!fromStatus.equals("") && !oldStatus.equalsIgnoreCase(fromStatus))
    					continue;
    				if (newStatus.equalsIgnoreCase(toStatus))
    					//we've found a transition by the current user from fromStatus to toStatus
    					return false;	//forbid transition
	    			}
    		}
    	}

        return true;
	}

}
