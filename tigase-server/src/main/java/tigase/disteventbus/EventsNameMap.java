package tigase.disteventbus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import tigase.disteventbus.LocalEventBus.EventName;

public class EventsNameMap<M> {

	private final static String NULL_NAME = new String(new byte[] { 0 });

	private final Map<String, Map<String, Collection<M>>> dataMap = createMainDataMap();

	protected Collection<M> createDataList() {
		return new HashSet<M>();
	}

	protected Map<String, Map<String, Collection<M>>> createMainDataMap() {
		return new ConcurrentHashMap<String, Map<String, Collection<M>>>();
	}

	protected Map<String, Collection<M>> createNamesDataMap() {
		return new ConcurrentHashMap<String, Collection<M>>();
	}

	public void delete(M data) {
		Iterator<Entry<String, Map<String, Collection<M>>>> namesIt = dataMap.entrySet().iterator();
		while (namesIt.hasNext()) {
			Map<String, Collection<M>> datas = namesIt.next().getValue();
			Iterator<Entry<String, Collection<M>>> dataIt = datas.entrySet().iterator();
			while (dataIt.hasNext()) {
				Collection<M> d = dataIt.next().getValue();
				d.remove(data);
			}
		}
	}

	public void delete(String name, String xmlns, M data) {
		final String eventName = name == null ? NULL_NAME : name;

		Map<String, Collection<M>> namesData = dataMap.get(xmlns);
		if (namesData == null)
			return;

		Collection<M> dataCollection = namesData.get(eventName);
		if (dataCollection == null)
			return;

		dataCollection.remove(data);

		if (dataCollection.isEmpty()) {
			namesData.remove(eventName);
		}

		if (namesData.isEmpty()) {
			dataMap.remove(xmlns);
		}
	}

	public Collection<M> get(String name, String xmlns) {
		final String eventName = name == null ? NULL_NAME : name;

		Map<String, Collection<M>> namesData = dataMap.get(xmlns);
		if (namesData == null)
			return Collections.emptyList();

		Collection<M> dataList = namesData.get(eventName);
		if (dataList == null)
			return Collections.emptyList();

		return dataList;
	}

	public Set<EventName> getAllListenedEvents() {
		HashSet<EventName> result = new HashSet<LocalEventBus.EventName>();
		Iterator<Entry<String, Map<String, Collection<M>>>> xmlnsIt = dataMap.entrySet().iterator();

		while (xmlnsIt.hasNext()) {
			Entry<String, Map<String, Collection<M>>> e = xmlnsIt.next();
			final String xmlns = e.getKey();

			Iterator<String> namesIt = e.getValue().keySet().iterator();
			while (namesIt.hasNext()) {
				String n = namesIt.next();
				result.add(new EventName(n == NULL_NAME ? null : n, xmlns));
			}
		}

		return result;
	}

	public boolean hasData(String name, String xmlns) {
		final String eventName = name == null ? NULL_NAME : name;
		Map<String, Collection<M>> namesData = dataMap.get(xmlns);
		if (namesData == null || namesData.isEmpty())
			return false;

		Collection<M> dataList = namesData.get(eventName);
		if (dataList == null || dataList.isEmpty())
			return false;
		else
			return true;
	}

	public void put(String name, String xmlns, M data) {
		final String eventName = name == null ? NULL_NAME : name;

		Map<String, Collection<M>> namesData = dataMap.get(xmlns);
		if (namesData == null) {
			namesData = createNamesDataMap();
			dataMap.put(xmlns, namesData);
		}

		Collection<M> dataList = namesData.get(eventName);
		if (dataList == null) {
			dataList = createDataList();
			namesData.put(eventName, dataList);
		}

		dataList.add(data);
	}

}
