package com.innovalog.jmwe.plugins.functions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import webwork.action.ActionContext;
import webwork.action.factory.ParameterMap;

import com.atlassian.core.action.ActionDispatcher;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.innovalog.googlecode.jsu.util.CommonPluginUtils;
import com.innovalog.googlecode.jsu.util.WorkflowFactoryUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

public class WorkflowTransitionParentIssueFunction extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	private Logger log = Logger.getLogger(WorkflowTransitionParentIssueFunction.class);

	public static final String TRANSITION = "transition";

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		this.getVelocityParamsForInput(velocityParams);
		String transitionName = (String) ((FunctionDescriptor) descriptor).getArgs().get(TRANSITION);
		velocityParams.put(TRANSITION, transitionName);
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForInput(Map velocityParams) {
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put(TRANSITION, (String) ((FunctionDescriptor) descriptor).getArgs().get(TRANSITION));
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getDescriptorParams(Map conditionParams) {
		Map<String, String> params = new HashMap<String, String>();

		try{
			String transition = extractSingleParam(conditionParams, TRANSITION);
			params.put(TRANSITION, transition);
		} catch(IllegalArgumentException e) {
			log.warn(e, e);
		}

		return params;
	}
}
