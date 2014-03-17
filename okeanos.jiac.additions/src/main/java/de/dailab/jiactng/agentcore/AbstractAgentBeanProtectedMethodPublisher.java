package de.dailab.jiactng.agentcore;

import java.io.Serializable;

import de.dailab.jiactng.agentcore.action.Action;
import de.dailab.jiactng.agentcore.action.ActionResult;
import de.dailab.jiactng.agentcore.action.DoAction;
import de.dailab.jiactng.agentcore.action.Session;
import de.dailab.jiactng.agentcore.environment.ResultReceiver;
import de.dailab.jiactng.agentcore.knowledge.IMemory;
import de.dailab.jiactng.agentcore.ontology.IActionDescription;
import de.dailab.jiactng.agentcore.ontology.IAgentDescription;

public class AbstractAgentBeanProtectedMethodPublisher {
	public static IMemory getMemory(AbstractAgentBean agentBean) {
		return agentBean.memory;
	}

	public static IAgent getAgent(AbstractAgentBean agentBean) {
		return agentBean.thisAgent;
	}

	public static ActionResult invokeAndWaitForResult(
			AbstractAgentBean agentBean, IActionDescription actionDescription,
			Serializable[] serializable) {
		return agentBean
				.invokeAndWaitForResult(actionDescription, serializable);
	}

	public static ActionResult invokeAndWaitForResult(
			AbstractAgentBean agentBean, IActionDescription a,
			Serializable[] inputParams, Long timeout) {
		return agentBean.invokeAndWaitForResult(a, inputParams, timeout);
	}

	public static String invoke(AbstractAgentBean agentBean,
			IActionDescription a, Serializable[] inputParams) {
		return agentBean.invoke(a, inputParams);
	}

	public static String invoke(AbstractAgentBean agentBean,
			IActionDescription a, Serializable[] inputParams,
			ResultReceiver receiver) {
		return agentBean.invoke(a, inputParams, receiver);
	}

	public static String invoke(AbstractAgentBean agentBean,
			IActionDescription a, Serializable[] inputParams,
			ResultReceiver receiver, final Long timeOut) {
		return agentBean.invoke(a, inputParams, receiver, timeOut);
	}

	public static String invoke(AbstractAgentBean agentBean,
			IActionDescription a, Session parent, Serializable[] inputParams,
			ResultReceiver receiver) {
		return agentBean.invoke(a, parent, inputParams, receiver);
	}

	public static ActionResult invokeWithBacktracking(
			AbstractAgentBean agentBean, IActionDescription template,
			Serializable[] inputParams) {
		return agentBean.invokeWithBacktracking(template, inputParams);
	}

	public static ActionResult invokeWithBacktracking(
			AbstractAgentBean agentBean, IActionDescription template,
			Serializable[] inputParams, final Long timeOut) {
		return agentBean.invokeWithBacktracking(template, inputParams, timeOut);
	}

	public static void returnResult(AbstractAgentBean agentBean,
			DoAction origin, Serializable[] results) {
		agentBean.returnResult(origin, results);
	}

	public static void returnFailure(AbstractAgentBean agentBean,
			DoAction origin, Serializable failure) {
		agentBean.returnFailure(origin, failure);
	}

	public static Action retrieveAction(AbstractAgentBean agentBean,
			String actionName) {
		return agentBean.retrieveAction(actionName);
	}

	public static Action retrieveAction(AbstractAgentBean agentBean,
			String actionName, IAgentDescription provider) {
		return agentBean.retrieveAction(actionName, provider);
	}

	public static void sendAttributeChangeNotification(
			AbstractAgentBean agentBean, String attributeName,
			String attributeType, Object oldValue, Object newValue) {
		agentBean.sendAttributeChangeNotification(attributeName, attributeType,
				oldValue, newValue);
	}
}
