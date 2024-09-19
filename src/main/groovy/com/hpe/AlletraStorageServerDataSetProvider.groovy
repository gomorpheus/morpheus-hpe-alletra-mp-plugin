package com.hpe

import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.core.data.DataFilter
import com.morpheusdata.core.data.DataQuery
import com.morpheusdata.core.data.DatasetInfo
import com.morpheusdata.core.data.DatasetQuery
import com.morpheusdata.core.providers.AbstractDatasetProvider
import com.morpheusdata.model.StorageServer
import io.reactivex.rxjava3.core.Observable

class AlletraStorageServerDataSetProvider extends AbstractDatasetProvider<StorageServer, Long> {
	public static final providerName = 'Alletra Storage Servers'
	public static final providerNamespace = 'alletra-mp'
	public static final providerKey = 'alletra-storage-servers'
	public static final providerDescription = 'Filters available storage servers down to just Alletra Mp Storage Servers'


	AlletraStorageServerDataSetProvider(Plugin plugin, MorpheusContext morpheus) {
		this.plugin = plugin
		this.morpheusContext = morpheus
	}
	@Override
	DatasetInfo getInfo() {
		new DatasetInfo(
						name: providerName,
						namespace: providerNamespace,
						key: providerKey,
						description: providerDescription
		)
	}



	@Override
	Class<StorageServer> getItemType() {
		return StorageServer.class
	}

	@Override
	Observable<StorageServer> list(DatasetQuery datasetQuery) {
		datasetQuery.withFilters(new DataFilter('type.code', 'alletramp-storage-provider'))
		return morpheusContext.async.storageServer.list(datasetQuery)
	}

	@Override
	Observable<Map> listOptions(DatasetQuery datasetQuery) {
		datasetQuery.withFilters(new DataFilter('type.code', 'alletramp-storage-provider'))
		return morpheusContext.async.storageServer.list(datasetQuery).map { StorageServer storageServer ->
			[ value: storageServer.id, name: storageServer.name ]
		}
	}

	@Override
	StorageServer fetchItem(Object o) {
		if(o instanceof Long) {
			return morpheusContext.services.storageServer.get((Long)o)
		} else if (o instanceof String) {//code lookup
			return morpheusContext.services.storageServer.find(new DataQuery().withFilter("code",o.toString()))
		} else {
			return null
		}
	}

	@Override
	StorageServer item(Long id) {
		morpheusContext.services.storageServer.get(id)
	}

	@Override
	String itemName(StorageServer storageServer) {
		return storageServer.name
	}

	@Override
	Long itemValue(StorageServer storageServer) {
		return storageServer.id
	}





}
