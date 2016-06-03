/*
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2014 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.component.exceptions;

import tigase.server.Packet;
import tigase.xmpp.Authorization;
import tigase.xmpp.PacketErrorTypeException;

public class ComponentException extends Exception {

	private static final long serialVersionUID = 1L;

	private Authorization errorCondition;

	private String text;

	public ComponentException(final Authorization errorCondition) {
		this(errorCondition, (String) null, (String) null);
	}

	/**
	 * 
	 * @param errorCondition
	 * @param text
	 *            human readable message will be send to client
	 */
	public ComponentException(Authorization errorCondition, String text) {
		this(errorCondition, text, (String) null);
	}

	/**
	 * 
	 * @param errorCondition
	 * @param message
	 *            exception message for logging
	 * @param text
	 *            human readable message will be send to client
	 */
	public ComponentException(Authorization errorCondition, String text, String message) {
		super(message);
		this.errorCondition = errorCondition;
		this.text = text;
	}

	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return String.valueOf(this.errorCondition.getErrorCode());
	}

	public Authorization getErrorCondition() {
		return errorCondition;
	}

	protected String getErrorMessagePrefix() {
		return "XMPP error condition: ";
	}

	@Override
	public String getMessage() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getErrorMessagePrefix());
		sb.append(errorCondition.getCondition()).append(" ");
		if (text != null) {
			sb.append("with message: \"").append(text).append("\" ");
		}
		if (super.getMessage() != null) {
			sb.append("(").append(super.getMessage()).append(") ");
		}

		return sb.toString();
	}

	public String getMessageWithPosition() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getMessage());

		StackTraceElement[] stack = getStackTrace();
		if (stack.length > 0) {
			sb.append("generated by ");
			sb.append(getStackTrace()[0].toString());
			sb.append(" ");
		}

		return sb.toString();
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return errorCondition.getCondition();
	}

	public String getText() {
		return text;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return errorCondition.getErrorType();
	}

	public Packet makeElement(Packet packet, boolean insertOriginal) throws PacketErrorTypeException {
		Packet result = errorCondition.getResponseMessage(packet, text, insertOriginal);
		return result;
	}

}
