package com.innovalog.jmwe.plugins.functions;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.DefaultRoleActors;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.workflow.WorkflowException;

public class SetIssueSecurityFromRoleFunction extends AbstractJiraFunctionProvider
{
	private static final Category log = Category.getInstance(SetIssueSecurityFromRoleFunction.class);

	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException
	{
		MutableIssue issue = this.getIssue(transientVars);
		String rawprojectRoleId = (String) args.get("projectrole.id");
		String rawissueSecurityId = (String) args.get("security.id");
		Long projectRoleId = null;

		//fetch the project role object
		if (StringUtils.isBlank(rawprojectRoleId))
		{
			log.warn("SetIssueSecurityFromRole not configured with a valid projectroleid.");
			return;
		}
		try
		{
			projectRoleId = new Long(Long.parseLong(rawprojectRoleId));

		} catch (NumberFormatException e)
		{
			StringBuffer sb = new StringBuffer();
			log.warn(sb.append("SetIssueSecurityFromRolee not configured with a valid projectroleid, the project role id: ")
					.append(projectRoleId).append(" can not be parsed.").toString());
			return;

		}
		ProjectRoleManager projectRoleManager = (ProjectRoleManager) ComponentManager
				.getComponentInstanceOfType(ProjectRoleManager.class);
		ProjectRole projectRole = projectRoleManager.getProjectRole(projectRoleId);
		if (projectRole == null)
		{
			StringBuffer sb = new StringBuffer();
			log.warn(sb.append("SetIssueSecurityFromRole is configured with a project role that doesn\'t exist: id is ").append(
					projectRoleId).toString());
			return;
		}

		User curUser = this.getCaller(transientVars, args);
		if (curUser == null)
			return; // transition is ran anonymously
		Project project = issue.getProjectObject();
		DefaultRoleActors roleActors = projectRoleManager.getProjectRoleActors(projectRole, project);
		if (roleActors != null && roleActors.contains(curUser))
		{
			// current user is in specified project role. Set issue security
			// accordingly
			if (StringUtils.isBlank(rawissueSecurityId))
			{
				log.warn("SetIssueSecurityFromRole not configured with a valid issueSecurityId.");
				return;
			}
			Long issueSecurityId = null;
			try
			{
				issueSecurityId = new Long(Long.parseLong(rawissueSecurityId));
			} catch (NumberFormatException e)
			{
				StringBuffer sb = new StringBuffer();
				log.warn(sb.append(
						"SetIssueSecurityFromRolee not configured with a valid issueSecurityId, the issue security id: ").append(
						issueSecurityId).append(" can not be parsed.").toString());
				return;
			}

			issue.setSecurityLevelId(issueSecurityId);
		}
	}

}
