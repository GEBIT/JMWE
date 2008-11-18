/**
 * 
 */
package com.innovalog.jmwe.plugins.validators;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.innovalog.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * @author fischerd
 *
 */
public class WorkflowParentStatusValidator extends
		AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory{

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
		String statusString = (String) validatorDescriptor.getArgs().get("jira.parentstatuses");
		velocityParams.put("selectedStatuses", getStringSet(statusString));
	}

	@SuppressWarnings("unchecked")
	static Set<String> getStringSet(String s)
	{
		if (s == null)
			return (Set<String>)Collections.EMPTY_SET;
		String[] arr = s.split(WorkflowUtils.SPLITTER);
		Set<String> set = new HashSet<String>();
		for (String s1 : arr)
			set.add(s1);
		return set;
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map formParams) {
		Map<String, String> params = new HashMap<String, String>();

		try{
			String[] selectedStatuses = (String[])formParams.get("status");
			StringBuilder sb = new StringBuilder();
			for (String s : selectedStatuses)
				sb.append(s).append(WorkflowUtils.SPLITTER);
			params.put("jira.parentstatuses", sb.toString());
		} catch(IllegalArgumentException e) {
		}
		return params;
	}

}
