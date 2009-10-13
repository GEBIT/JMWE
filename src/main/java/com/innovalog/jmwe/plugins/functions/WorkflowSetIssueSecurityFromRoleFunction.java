package com.innovalog.jmwe.plugins.functions;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.innovalog.googlecode.jsu.util.WorkflowFactoryUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

public class WorkflowSetIssueSecurityFromRoleFunction extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	private Logger log = Logger.getLogger(WorkflowSetIssueSecurityFromRoleFunction.class);

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		this.getVelocityParamsForInput(velocityParams);
		this.getVelocityParamsForView(velocityParams,descriptor);
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForInput(Map velocityParams) {
		//the list of project roles
		Map<String,String> projectRoleMap=new HashMap<String, String>();
		ProjectRoleManager projectRoleManager=(ProjectRoleManager)ComponentManager.getComponentInstanceOfType(ProjectRoleManager.class);
		for (Object role : projectRoleManager.getProjectRoles())
		{
			ProjectRole projectRole = (ProjectRole)role;
			projectRoleMap.put(projectRole.getId().toString(),projectRole.getName());
		}
		velocityParams.put("projectroles",projectRoleMap);
		
		//and the list of issue security levels
		Map<String,String> securityLevelsMap = new HashMap<String,String>();
		IssueSecurityLevelManager issueSecurityLevelManager = ManagerFactory.getIssueSecurityLevelManager();
		IssueSecuritySchemeManager issueSecuritySchemeManager = ManagerFactory.getIssueSecuritySchemeManager();
		
		//iterate through the security schemes
		try
		{
			for (Object o : issueSecuritySchemeManager.getSchemes())
			{
				GenericValue scheme = (GenericValue)o;
				//now iterate through the security levels of each scheme
				for (Object o1 : issueSecurityLevelManager.getSchemeIssueSecurityLevels(scheme.getLong("id")))
				{
					GenericValue securityLevel = (GenericValue)o1;
					securityLevelsMap.put(securityLevel.getLong("id").toString(),securityLevel.getString("name"));
				}
			}
			velocityParams.put("securitylevels",securityLevelsMap);
		} catch (GenericEntityException e)
		{
			log.error("Error building list of security levels", e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put("selectedProjectRoleId", ((FunctionDescriptor)descriptor).getArgs().get("projectrole.id"));
		velocityParams.put("selectedSecurityLevelId", ((FunctionDescriptor)descriptor).getArgs().get("security.id"));
		
		if (velocityParams.get("selectedProjectRoleId") != null)
		{
			ProjectRoleManager projectRoleManager=(ProjectRoleManager)ComponentManager.getComponentInstanceOfType(ProjectRoleManager.class);
			ProjectRole projectRole=projectRoleManager.getProjectRole(new Long((String)velocityParams.get("selectedProjectRoleId")));
			velocityParams.put("selectedProjectRoleName", projectRole.getName());
		}
		if (velocityParams.get("selectedSecurityLevelId") != null)
		{
			IssueSecurityLevelManager issueSecurityLevelManager=(IssueSecurityLevelManager)ComponentManager.getComponentInstanceOfType(IssueSecurityLevelManager.class);
			GenericValue securityLevel;
			try
			{
				securityLevel = issueSecurityLevelManager.getIssueSecurityLevel(new Long((String)velocityParams.get("selectedSecurityLevelId")));
				velocityParams.put("selectedSecurityLevelName", securityLevel.getString("name"));
			} catch (NumberFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GenericEntityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getDescriptorParams(Map functionParams) {
		Map<String, String> params = new HashMap<String, String>();

		try{
			params.put("projectrole.id", extractSingleParam(functionParams, "selectedProjectRoleId"));
			params.put("security.id", extractSingleParam(functionParams, "selectedSecurityLevelId"));
		} catch(IllegalArgumentException e) {
			log.warn(e, e);
		}

		return params;
	}
}
