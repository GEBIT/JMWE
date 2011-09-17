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
import com.opensymphony.workflow.WorkflowException;

/**
 * @author fischerd
 *
 */
public class NonInteractiveCondition extends AbstractJiraCondition {

	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public boolean passesCondition(Map transientVars, Map args, PropertySet ps)
			throws WorkflowException {
		try
		{
			throw new Exception();
		}
		catch(Exception e)
		{
			StackTraceElement[] stack = e.getStackTrace();
			boolean passes = true;
			for (StackTraceElement entry : stack)
			{
 				if (entry.getClassName().equals("com.opensymphony.workflow.AbstractWorkflow") && entry.getMethodName().equals("getAvailableActions"))
					passes = false;
				else if (entry.getClassName().equals("com.atlassian.jira.issue.IssueUtilsBean") && entry.getMethodName().equals("isValidAction"))
					return true;
			}
			return passes;
		}
	}

}
