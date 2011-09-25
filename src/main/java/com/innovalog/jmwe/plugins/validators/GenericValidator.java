package com.innovalog.jmwe.plugins.validators;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.innovalog.googlecode.jsu.annotation.AnnotationProcessor;
import com.innovalog.googlecode.jsu.annotation.Argument;
import com.innovalog.googlecode.jsu.annotation.MapFieldProcessor;
import com.innovalog.googlecode.jsu.annotation.TransientVariable;
import com.innovalog.googlecode.jsu.util.FieldCollectionsUtils;
import com.innovalog.googlecode.jsu.util.ValidatorErrorsBuilder;
import com.innovalog.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

import java.util.Map;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id: GenericValidator.java,v 1.1 2008/08/07 17:08:03 fischer Exp $
 */
public abstract class GenericValidator implements Validator {
	private ValidatorErrorsBuilder errorBuilder;
	private FieldScreen fieldScreen = null;
	private Issue issue = null;
    private String transitionComment = null;

    protected final FieldCollectionsUtils fieldCollectionsUtils;
    protected final WorkflowUtils workflowUtils;

    public GenericValidator(WorkflowUtils workflowUtils, FieldCollectionsUtils fieldCollectionsUtils) {
        this.workflowUtils = workflowUtils;
        this.fieldCollectionsUtils = fieldCollectionsUtils;
    }

    protected abstract void validate() throws InvalidInputException, WorkflowException;
	
	@SuppressWarnings("unchecked")
	public final void validate(
			Map transientVars, Map args, PropertySet ps
	) throws InvalidInputException, WorkflowException {
		initObject(transientVars, args);

		this.fieldScreen = initScreen(transientVars);
		this.errorBuilder = new ValidatorErrorsBuilder(hasViewScreen());
		this.issue = (Issue) transientVars.get("issue");
        this.transitionComment = (String) transientVars.get("comment");

		this.validate();
		
		this.errorBuilder.process();
	}
	
	/**
	 * Initialize object with maps of parameters.
	 * @param vars
	 * @param arguments
	 */
	protected void initObject(Map<String, Object> vars, Map<String, Object> arguments) {
		final AnnotationProcessor processor = new AnnotationProcessor();
		
		processor.addVisitor(new MapFieldProcessor(Argument.class, arguments));
		processor.addVisitor(new MapFieldProcessor(TransientVariable.class, vars));
		
		processor.processAnnotations(this);
	}

	protected final Issue getIssue() {
		return this.issue;
	}
	
    protected final String getTransitionComment() {
        return this.transitionComment;
    }

	protected final boolean hasViewScreen() {
		return (fieldScreen != null); 
	}

	protected final FieldScreen getFieldScreen() {
		return this.fieldScreen; 
	}
	
	/**
	 * Setting error message for validator.
	 * 
	 * @param field
	 * @param messageIfOnScreen
	 * @param messageIfHidden
	 */
	protected final void setExceptionMessage(
			Field field,
			String messageIfOnScreen, String messageIfHidden
	) {
		if (hasViewScreen()) {
			if (fieldCollectionsUtils.isFieldOnScreen(this.issue, field, getFieldScreen())) {
                if (fieldCollectionsUtils.cannotSetValidationMessageToField(field)) {
                    this.errorBuilder.addError(messageIfOnScreen);
                } else {
				this.errorBuilder.addError(field, messageIfOnScreen);
                }
			} else {
				this.errorBuilder.addError(messageIfHidden);
			}
		} else {
			this.errorBuilder.addError(messageIfOnScreen);
		}
	}
	
	private FieldScreen initScreen(Map<String, Object> vars) {
		if (vars.containsKey("descriptor") && vars.containsKey("actionId")) {
			WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor) vars.get("descriptor");
			Integer actionId = (Integer) vars.get("actionId");
			ActionDescriptor actionDescriptor = workflowDescriptor.getAction(actionId.intValue());

			return workflowUtils.getFieldScreen(actionDescriptor);
		} else {
			return null;
		}
	}
}
