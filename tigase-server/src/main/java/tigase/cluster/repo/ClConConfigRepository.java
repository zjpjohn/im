/*
 * ClConConfigRepository.java
 *
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2013 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
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
 */



package tigase.cluster.repo;

//~--- non-JDK imports --------------------------------------------------------

import tigase.db.comp.ConfigRepository;

import tigase.sys.TigaseRuntime;
import tigase.util.DNSResolver;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tigase.db.DBInitException;

import tigase.sys.ShutdownHook;

/**
 * Class description
 *
 *
 * @version        5.2.0, 13/03/09
 * @author         <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 */
public class ClConConfigRepository
		extends ConfigRepository<ClusterRepoItem>
		implements ShutdownHook {

	private static final Logger log = Logger.getLogger(ClConConfigRepository.class.getName());

	/** Field description */
	public static final String AUTORELOAD_INTERVAL_PROP_KEY = "repo-autoreload-interval";

	/** Field description */
	public static final long AUTORELOAD_INTERVAL_PROP_VAL = 15;

	//~--- fields ---------------------------------------------------------------

	protected long autoreload_interval = AUTORELOAD_INTERVAL_PROP_VAL;
	protected long lastReloadTime = 0;
	protected long lastReloadTimeFactor = 10;

	@Override
	public void destroy() {
		// Nothing to do
	}
	
	//~--- get methods ----------------------------------------------------------

	@Override
	public String[] getDefaultPropetyItems() {
		return ClConRepoDefaults.getDefaultPropetyItems();
	}

	@Override
	public String getName() {
		return "Cluster repository clean-up";
	}

	@Override
	public String getPropertyKey() {
		return ClConRepoDefaults.getPropertyKey();
	}

	@Override
	public String getConfigKey() {
		return ClConRepoDefaults.getConfigKey();
	}

	@Override
	public ClusterRepoItem getItemInstance() {
		return ClConRepoDefaults.getItemInstance();
	}

	//~--- methods --------------------------------------------------------------

	@Override
	public void initRepository(String resource_uri, Map<String, String> params) throws DBInitException {
		// Nothing to do
	}
	
	@Override
	public void reload() {
		super.reload();

		String          host = DNSResolver.getDefaultHostname();
		ClusterRepoItem item = getItem(host);

		if (item == null) {
			item = getItemInstance();
			item.setHostname(host);
		}
		item.setLastUpdate(System.currentTimeMillis());
		item.setCpuUsage(TigaseRuntime.getTigaseRuntime().getCPUUsage());
		item.setMemUsage(TigaseRuntime.getTigaseRuntime().getHeapMemUsage());
		storeItem(item);
	}

	public void itemLoaded(ClusterRepoItem item) {
		if (System.currentTimeMillis() - item.getLastUpdate() <= 5000 * autoreload_interval && clusterRecordValid(item)) {
			addItem(item);
		} else {
			removeItem(item.getHostname());
		}
	}

	@Override
	public boolean itemChanged(ClusterRepoItem oldItem, ClusterRepoItem newItem) {
		return !oldItem.getPassword().equals(newItem.getPassword()) || (oldItem
				.getPortNo() != newItem.getPortNo());
	}

	//~--- get methods ----------------------------------------------------------

	@Override
	public void getDefaults(Map<String, Object> defs, Map<String, Object> params) {
		super.getDefaults(defs, params);
		defs.put(AUTORELOAD_INTERVAL_PROP_KEY, AUTORELOAD_INTERVAL_PROP_VAL);

		String[] items_arr = (String[]) defs.get(getConfigKey());

		for (String it : items_arr) {
			ClusterRepoItem item = getItemInstance();

			item.initFromPropertyString(it);
			addItem(item);
		}
		if (getItem(DNSResolver.getDefaultHostname()) == null) {
			ClusterRepoItem item = getItemInstance();

			item.initFromPropertyString(DNSResolver.getDefaultHostname());
			addItem(item);
		}
	}

	//~--- set methods ----------------------------------------------------------

	@Override
	public void setProperties(Map<String, Object> props) {
		super.setProperties(props);
		autoreload_interval = (Long) props.get(AUTORELOAD_INTERVAL_PROP_KEY);
		setAutoloadTimer(autoreload_interval);
		TigaseRuntime.getTigaseRuntime().addShutdownHook(this);
	}

	@Override
	public String shutdown() {
		String host = DNSResolver.getDefaultHostname();
		removeItem( host );
		return "== " + "Removing cluster_nodes item: " + host + "\n";
	}

	//~--- methods --------------------------------------------------------------

	public void storeItem(ClusterRepoItem item) {}

	private boolean clusterRecordValid( ClusterRepoItem item ) {

		// we ignore faulty addresses
		boolean isCorrect = !item.getHostname().equalsIgnoreCase( "localhost" );

		if ( !isCorrect && log.isLoggable( Level.FINE ) ){
			log.log( Level.FINE, "Incorrect entry in cluster table, skipping: {0}", item );
		}
		return isCorrect;
	}
}
