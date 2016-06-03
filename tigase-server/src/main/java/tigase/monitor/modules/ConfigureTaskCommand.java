package tigase.monitor.modules;

import tigase.component.adhoc.AdHocCommand;
import tigase.component.adhoc.AdHocCommandException;
import tigase.component.adhoc.AdHocResponse;
import tigase.component.adhoc.AdhHocRequest;
import tigase.form.Form;
import tigase.monitor.ConfigurableTask;
import tigase.monitor.MonitorContext;
import tigase.xml.Element;
import tigase.xmpp.Authorization;
import tigase.xmpp.JID;

public class ConfigureTaskCommand implements AdHocCommand {

	public static final String NODE = "x-config";

	private MonitorContext ctx;

	public ConfigureTaskCommand(MonitorContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void execute(AdhHocRequest request, AdHocResponse response) throws AdHocCommandException {
		try {
			final Element data = request.getCommand().getChild("x", "jabber:x:data");

			if (request.getAction() != null && "cancel".equals(request.getAction())) {
				response.cancelSession();
			} else if (data == null) {
				final ConfigurableTask taskInstance = ctx.getKernel().getInstance(request.getIq().getStanzaTo().getResource());
				Form form = taskInstance.getCurrentConfiguration();
				response.getElements().add(form.getElement());
				response.startSession();
			} else {
				Form form = new Form(data);
				if ("submit".equals(form.getType())) {
					final ConfigurableTask taskInstance = ctx.getKernel().getInstance(
							request.getIq().getStanzaTo().getResource());
					taskInstance.setNewConfiguration(form);
					response.completeSession();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AdHocCommandException(Authorization.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public String getName() {
		return "Task config";
	}

	@Override
	public String getNode() {
		return NODE;
	}

	@Override
	public boolean isAllowedFor(JID jid) {
		return true;
	}

}
