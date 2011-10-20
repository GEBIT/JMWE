package com.innovalog.jmwe.plugins.functions;

import java.util.Map;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.UserPropertyManager;
import org.apache.log4j.Logger;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.innovalog.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * 
 */
public class SetFieldFromUserPropFunction extends AbstractPreserveChangesPostFunction {
	private static final Logger log = Logger.getLogger(SetFieldFromUserPropFunction.class);
    private final WorkflowUtils workflowUtils;
    private final UserPropertyManager userPropertyManager;

    public SetFieldFromUserPropFunction(WorkflowUtils workflowUtils, UserPropertyManager userPropertyManager) {
        this.workflowUtils = workflowUtils;
        this.userPropertyManager = userPropertyManager;
    }

    @SuppressWarnings("unchecked")
	public void executeFunction(Map transientVars, Map args, PropertySet ps, IssueChangeHolder holder)
			throws WorkflowException {
		log.debug("");
		String sourcePropKey = (String) args
				.get(WorkflowSetFieldFromUserPropFunction.FUNCPARAM);
		User curUser = this.getCaller(transientVars, args);
		if (curUser == null)
			return; // transition is ran anonymously

		if (!userPropertyManager.getPropertySet(curUser).exists("jira.meta." + sourcePropKey)) {
			log
					.warn(String
							.format(
									"Unable to find user property [%s] on current user",
									sourcePropKey));
			return;
		}
		String value = userPropertyManager.getPropertySet(curUser).getString(
				"jira.meta." + sourcePropKey);

		String sourceFieldKey = (String) args.get(WorkflowSetFieldFromUserPropFunction.FIELD);
		Field fieldFrom = (Field) workflowUtils.getFieldFromKey(sourceFieldKey);
		if (fieldFrom == null) {
			log.warn(String.format("Unable to find field with key [%s]", sourceFieldKey));
			throw new WorkflowException(String.format("Unable to find field with key [%s]", sourceFieldKey));
		}

		try {
			MutableIssue issue = getIssue(transientVars);
			workflowUtils.setFieldValue(issue, fieldFrom, value, holder);
			issue.store();

			if (log.isDebugEnabled()) {
				log.debug(String.format("Set field %s to [%s]", sourceFieldKey, value));
			}
		} catch (Exception e) {
			final String message = "Unable to copy value from user property " + sourcePropKey
					+ " to field " + sourceFieldKey;

			log.error(message, e);

			throw new WorkflowException(message);
		}
	}
}
