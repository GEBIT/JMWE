/**
 * 
 */
package com.innovalog.jmwe.plugins.conditions;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author fischerd
 *
 */
public class PreviousStatusCondition extends AbstractJiraCondition {
	private Logger log = Logger.getLogger(PreviousStatusCondition.class);

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public boolean passesCondition(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
        Issue genericIssue =  (Issue) transientVars.get("issue");
        String statusToLookFor = (String)args.get("jira.previousstatus");
        boolean mostRecentStatusOnly = "yes".equals(args.get("jira.mostRecentStatusOnly"));
        boolean not = "yes".equals(args.get("jira.not"));

        log.debug("Issue "+genericIssue.getKey()+": looking for " + statusToLookFor + " previous status");
        
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
    				//we've found our status
    					return !not;
    				else if (mostRecentStatusOnly)
    					//since our latest status is NOT the one we're looking for, return not
    					return not;
	    			}
    		}
    	}

        return not;
	}

}
