package okeanos.management.internal.services.platformmanagement;

import javax.inject.Inject;

import okeanos.spring.misc.stereotypes.ChildOf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.dailab.jiactng.agentcore.comm.broker.ActiveMQTransportConnector;

@Component("MyTCPConnectorFixedPortJava")
@ChildOf(parent = "ActiveMQTransportConnector")
@Scope("prototype")
public class MyTCPConnectorFixedPort extends ActiveMQTransportConnector {

	public MyTCPConnectorFixedPort() {
		super();

		setTransportURI("");
		setDiscoveryURI("");
		setNetworkURI("");
	}

	private String myrandom = String.valueOf((int) (Math.random() * 1000));

	@Override
	public void setTransportURI(final String newTransportURI) {
		super.setTransportURI("tcp://127.0.0.1:0");
	}

	@Override
	public void setDiscoveryURI(final String newDiscoveryURI) {
		super.setDiscoveryURI("multicast://default?group=" + myrandom);
	}

	@Override
	public void setNetworkURI(final String newNetworkURI) {
		super.setNetworkURI("multicast://default?group=" + myrandom);
	}
}
