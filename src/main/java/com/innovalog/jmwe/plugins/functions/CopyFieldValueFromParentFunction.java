package com.innovalog.jmwe.plugins.functions;

import java.util.Map;

import org.apache.log4j.Logger;

import webwork.dispatcher.ActionResult;

import com.atlassian.core.ofbiz.CoreFactory;
import com.atlassian.core.util.map.EasyMap;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.action.ActionNames;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.jira.util.ImportUtils;
import com.innovalog.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class CopyFieldValueFromParentFunction extends AbstractPreserveChangesPostFunction
{
	private Logger log = Logger.getLogger(CopyFieldValueFromParentFunction.class);
	private static final String FIELD = "field";

	public void executeFunction(Map transientVars, Map args, PropertySet ps, IssueChangeHolder holder) throws WorkflowException
	{
		String fieldKey = (String) args.get(FIELD);
		Field field = (Field) WorkflowUtils.getFieldFromKey(fieldKey);
		if (field == null)
		{
			log.error("Error while executing function : field [" + fieldKey + "] not found");
			return;
		}

		try
		{
			MutableIssue issue = getIssue(transientVars);

			// get the parent issue
			MutableIssue parentIssue = (MutableIssue) issue.getParentObject();
			if (parentIssue != null)
			{
				Object sourceValue = WorkflowUtils.getFieldValueFromIssue(parentIssue, field);
				WorkflowUtils.setFieldValue(issue, field, sourceValue, holder);
			}
		} catch (Exception e)
		{
			log.warn("Error while executing Copy Field Value to Parent function: " + e, e);
		}
	}
}
