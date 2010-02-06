/*
 * Copyright (c) 2002-2004
 * All rights reserved.
 */
package com.innovalog.jmwe.plugins.functions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

public class WorkflowAssignToLastRoleMemberFunctionImpl extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory
{	  	

	protected void getVelocityParamsForInput(Map velocityParams)
	{
		Collection projectRoles= null;
		Iterator iterator= null;
		Map projectRoleMap= null;
		ProjectRole projectRole= null;
		ProjectRoleManager projectRoleManager= null;
		
		projectRoleMap=new ListOrderedMap();;
		projectRoleManager=(ProjectRoleManager)ComponentManager.getComponentInstanceOfType(ProjectRoleManager.class);
		projectRoles=projectRoleManager.getProjectRoles();
		iterator=projectRoles.iterator();
		while(true)
		{
			if(iterator.hasNext()==false)
			{
				break;
			}
			if(iterator.hasNext()!=false)
			{
				projectRole=(ProjectRole)iterator.next();
				projectRoleMap.put(projectRole.getId().toString(),projectRole.getName());
				continue  ;

			}

		}
		velocityParams.put("key","jira.projectrole.id");
		velocityParams.put("projectroles",projectRoleMap);
		return;
	}

	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor)
	{
		this.getVelocityParamsForInput(velocityParams);
		this.getVelocityParamsForView(velocityParams,descriptor);
		return;
	}

	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor)
	{
		FunctionDescriptor fucntionDescriptor= null;
		ProjectRole projectRole= null;
		ProjectRoleManager projectRoleManager= null;
		String id= null;
		
		if(!(descriptor instanceof com.opensymphony.workflow.loader.FunctionDescriptor))			
		{
			IllegalArgumentException exp = new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
			throw exp;

		}
		fucntionDescriptor=(FunctionDescriptor)descriptor;
		projectRoleManager=(ProjectRoleManager)ComponentManager.getComponentInstanceOfType(ProjectRoleManager.class);
		id=(String)fucntionDescriptor.getArgs().get("jira.projectrole.id");
		Long longId = new Long(id);
		projectRole=projectRoleManager.getProjectRole(longId);
				
		velocityParams.put("includeReporter", fucntionDescriptor.getArgs().get("includeReporter"));
		velocityParams.put("projectrole",id);
		if(projectRole!=  null)
		{
			velocityParams.put("projectrolename",projectRole.getName());
			return ;   
		}
	}

	public Map getDescriptorParams(Map functionParams)
	{
		Map<String, String> params = new HashMap<String, String>();
		String value= null;
		value=this.extractSingleParam(functionParams,"jira.projectrole.id");
		params.put("jira.projectrole.id",value);
		try {
			params.put("includeReporter", this.extractSingleParam(functionParams,"includeReporter"));
		} catch (IllegalArgumentException e) {
			params.put("includeReporter", "no");
		}
		return params;
	}

}
