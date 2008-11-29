/**
 * 
 */
package com.innovalog.jmwe.plugins.conditions;

import java.util.HashMap;
import java.util.Map;

import webwork.action.ActionContext;
import webwork.action.factory.ParameterMap;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;

/**
 * @author fischerd
 *
 */
public class WorkflowPreviousStatusCondition extends
		AbstractWorkflowPluginFactory implements WorkflowPluginConditionFactory{

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	@Override
	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
	}

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	@Override
	protected void getVelocityParamsForInput(Map velocityParams) {
		//fill in velocity parameters needed for possible values (combobox for instance)
		
		//get current workflow
		ActionContext ctx = ActionContext.getContext();
		ParameterMap pm = (ParameterMap)ctx.get("webwork.action.ActionContext.parameters");
		String workflowName = ((String[])pm.get("workflowName"))[0];
		WorkflowManager wm = ComponentManager.getInstance().getWorkflowManager();
		JiraWorkflow workflow = wm.getWorkflow(workflowName);
		
		//get list of statuses
       velocityParams.put("statusList", workflow.getLinkedStatusObjects());
	}

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	@Override
	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		ConditionDescriptor conditionDescriptor = (ConditionDescriptor)descriptor;
		String statusString = (String) conditionDescriptor.getArgs().get("jira.previousstatus");
		velocityParams.put("selectedStatus", statusString);
		String mostRecentStatusOnly = (String) conditionDescriptor.getArgs().get("jira.mostRecentStatusOnly");
		velocityParams.put("mostRecentOnly", mostRecentStatusOnly);
	}

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map formParams) {
		Map<String, String> params = new HashMap<String, String>();

		try{
			String selectedStatus = extractSingleParam(formParams, "status");
			params.put("jira.previousstatus", selectedStatus);
		} catch(IllegalArgumentException e) {
		}
		try{
			String mostRecentStatusOnlyString = extractSingleParam(formParams, "mostRecentOnly");
			params.put("jira.mostRecentStatusOnly", mostRecentStatusOnlyString);
		}
		catch (IllegalArgumentException e)
		{
			params.put("jira.mostRecentStatusOnly","no");
		}
		return params;
	}

}
