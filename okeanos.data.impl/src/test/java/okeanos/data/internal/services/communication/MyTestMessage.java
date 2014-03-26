package okeanos.data.internal.services.communication;

import de.dailab.jiactng.agentcore.knowledge.IFact;

public class MyTestMessage implements IFact {
	private static final long serialVersionUID = -5286612511286375346L;

	private String content;

	public MyTestMessage(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}