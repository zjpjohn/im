/*
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2012 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * $Rev: $
 * Last modified by $Author: $
 * $Date: $
 */

/*
AS:Description: Change user inter-domain communication permission.
AS:CommandId: user-domain-perm
AS:Component: sess-man
*/

package tigase.admin

import tigase.server.*
import tigase.util.*
import tigase.xmpp.*
import tigase.xmpp.impl.DomainFilter
import tigase.vhosts.DomainFilterPolicy
import tigase.db.UserRepository
import tigase.db.UserNotFoundException

import tigase.vhosts.*

def vhost_man = (VHostManagerIfc)vhostMan

def JID = "jid"
def DOMAIN = "domain"
def DOMAIN_LIST = "domainList"

def p = (Iq)packet
def jid = Command.getFieldValue(p, JID)
def domain = Command.getFieldValue(p, DOMAIN)
def domainList = Command.getFieldValue(p, DOMAIN_LIST)

if (jid == null || domain == null ||
	(domain == DomainFilterPolicy.LIST.name() && domainList == null)) {
	def res = (Iq)p.commandResult(Command.DataType.form);
	Command.addFieldValue(res, JID, jid ?: "", "jid-single", "User JID")
	def domainStr = []
  DomainFilterPolicy.values().each { domainStr += it.name() }
	Command.addFieldValue(res, DOMAIN, domain ?: domainStr[0], "List of domains",
		(String[])domainStr, (String[])domainStr)
	Command.addFieldValue(res, DOMAIN_LIST, domainList ?: "", "text-single", "Domains List")
	return res
}

jid = JIDUtils.getNodeID(jid)

bareJID = BareJID.bareJIDInstance(jid)

def repo = (UserRepository)userRepository


def admins = (Set)adminsSet
def stanzaFromBare = p.getStanzaFrom().getBareJID()
def isServiceAdmin = admins.contains(stanzaFromBare)

VHostItem vhost = vhost_man.getVHostItem(bareJID.getDomain())

if ( !(isServiceAdmin || (vhost != null && (vhost.isOwner(stanzaFromBare.toString()) || vhost.isAdmin(stanzaFromBare.toString())))) ) {
	def result = p.commandResult(Command.DataType.result);
	Command.addTextField(result, "Error", "You do not have enough permissions to manage this domain");
	return result
}


try {
	def old_value = repo.getData(bareJID, null,
		DomainFilter.ALLOWED_DOMAINS_KEY, null)
	def old_value_domains = "";

	if (old_value ==  DomainFilterPolicy.LIST.name()
		|| old_value ==  DomainFilterPolicy.BLACKLIST.name()) {
		old_value_domains = repo.getData(bareJID, null,
			DomainFilter.ALLOWED_DOMAINS_LIST_KEY, null)
	}

	def new_value = domain
	if (domain == DomainFilterPolicy.LIST.name()
		|| domain == DomainFilterPolicy.BLACKLIST.name()) {
//		new_value = domainList
			repo.setData(bareJID, null, DomainFilter.ALLOWED_DOMAINS_LIST_KEY, domainList)
	}
	repo.setData(bareJID, null, DomainFilter.ALLOWED_DOMAINS_KEY, new_value)

	return "Changed an old value: $old_value (domains list: $old_value_domains) to a new value: $new_value (domains list: $domainList) for user: $jid"
} catch (e) {
  if (e in UserNotFoundException)	{
		return "The user $jid was not found in the user repository"
	} else {
		return "Unexpected error: " + e
	}
}
