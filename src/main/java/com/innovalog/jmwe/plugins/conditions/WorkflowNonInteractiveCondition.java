/**
 * 
 */
package com.innovalog.jmwe.plugins.conditions;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;

/**
 * @author fischerd
 *
 */
public class WorkflowNonInteractiveCondition extends
		AbstractWorkflowPluginFactory implements WorkflowPluginConditionFactory{

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map<String, ?> getDescriptorParams(Map<String, Object> formParams) {
		return new HashMap<String, String>();
	}

	@Override
	protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
		
	}

	@Override
	protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		
	}

	@Override
	protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		
	}

}
