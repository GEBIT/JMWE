/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * @author fischerd
 *
 */
public class WorkflowPreviousStatusValidator extends
		AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {

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
		
		//get list of statuses
		ConstantsManager constantsManager = ComponentManager.getInstance().getConstantsManager();
        velocityParams.put("statusList", constantsManager.getStatusObjects());
	}

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	@Override
	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor)descriptor;
		String statusString = (String) validatorDescriptor.getArgs().get("jira.previousstatus");
		velocityParams.put("selectedStatus", statusString);
		String mostRecentStatusOnly = (String) validatorDescriptor.getArgs().get("jira.mostRecentStatusOnly");
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
