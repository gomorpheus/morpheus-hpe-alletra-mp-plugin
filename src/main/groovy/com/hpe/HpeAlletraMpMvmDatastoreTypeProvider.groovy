package com.hpe

import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.core.providers.DatastoreTypeProvider
import com.morpheusdata.core.util.HttpApiClient
import com.morpheusdata.model.ComputeServer
import com.morpheusdata.model.ComputeServerGroup
import com.morpheusdata.model.Datastore
import com.morpheusdata.model.OptionType
import com.morpheusdata.model.Snapshot
import com.morpheusdata.model.StorageVolume
import com.morpheusdata.model.TaskResult
import com.morpheusdata.model.VirtualImage
import com.morpheusdata.response.ServiceResponse

class HpeAlletraMpMvmDatastoreTypeProvider implements DatastoreTypeProvider, DatastoreTypeProvider.MvmProvisionFacet, DatastoreTypeProvider.SnapshotFacet.SnapshotServerFacet {

	protected MorpheusContext morpheusContext
	protected Plugin plugin

	HpeAlletraMpMvmDatastoreTypeProvider(Plugin plugin, MorpheusContext morpheusContext) {
		this.morpheusContext = morpheusContext
		this.plugin = plugin
	}

	@Override
	String getProvisionTypeCode() {
		return "kvm"
	}

	@Override
	String getStorageProviderCode() {
		return "alletramp-storage-provider"
	}

	@Override
	List<OptionType> getOptionTypes() {
		return [] //TODO: implement
	}

	@Override
	boolean getCreatable() {
		return true
	}

	@Override
	boolean getEditable() {
		return true
	}

	@Override
	boolean getRemovable() {
		return true
	}

	@Override
	ServiceResponse removeVolume(StorageVolume storageVolume, ComputeServer computeServer, boolean b, boolean b1) {
		HttpApiClient apiClient = new HttpApiClient()
		try {
			//apiClient.callJsonApi("/api/v1/storage-volumes", "POST", storageVolume, StorageVolume)
		} finally {
			apiClient.shutdownClient()
		}
		return null
	}

	@Override
	ServiceResponse<StorageVolume> createVolume(StorageVolume storageVolume, ComputeServer computeServer) {
		HttpApiClient apiClient = new HttpApiClient()
		ComputeServer hypervisorHost = computeServer.parentServer
		//lets load the full datastore object since the default parent is just a projection
		Datastore currentDatastore = morpheusContext.services.cloud.datastore.get(storageVolume.datastore.id)
		try {
			//apiClient.callJsonApi("/api/v1/storage-volumes", "POST", storageVolume, StorageVolume)
			//execute operations on the hypervisor with:

			TaskResult result = morpheusContext.executeCommandOnServer(hypervisorHost, "sudo virsh pool-refresh '${currentDatastore.uniqueId}'", [:]).blockingGet()
			if(result.isSuccess()) {
				//get data from stdout
				String stdout = result.getOutput()
			} else {
				//handle failure
			}

		} finally {
			apiClient.shutdownClient()
		}
		return null
	}

	@Override
	ServiceResponse<StorageVolume> cloneVolume(StorageVolume storageVolume, ComputeServer computeServer, StorageVolume storageVolume1) {
		HttpApiClient apiClient = new HttpApiClient()
		try {
			//apiClient.callJsonApi("/api/v1/storage-volumes", "POST", storageVolume, StorageVolume)
		} finally {
			apiClient.shutdownClient()
		}
		return null
	}

	@Override
	ServiceResponse<StorageVolume> cloneVolume(StorageVolume storageVolume, ComputeServer computeServer, VirtualImage virtualImage, com.bertramlabs.plugins.karman.CloudFileInterface cloudFileInterface) {
		HttpApiClient apiClient = new HttpApiClient()
		try {
			//apiClient.callJsonApi("/api/v1/storage-volumes", "POST", storageVolume, StorageVolume)
		} finally {
			apiClient.shutdownClient()
		}
		return null
	}

	@Override
	ServiceResponse<StorageVolume> resizeVolume(StorageVolume storageVolume, ComputeServer computeServer, Long aLong) {
		HttpApiClient apiClient = new HttpApiClient()
		try {
			//apiClient.callJsonApi("/api/v1/storage-volumes", "POST", storageVolume, StorageVolume)
			//
			morp
		} finally {
			apiClient.shutdownClient()
		}
		return null

	}

	@Override
	ServiceResponse<Datastore> createDatastore(Datastore datastore) {
		return null
	}

	@Override
	ServiceResponse removeDatastore(Datastore datastore) {
		return null
	}

	@Override
	MorpheusContext getMorpheus() {
		return morpheusContext
	}

	@Override
	Plugin getPlugin() {
		return plugin
	}

	@Override
	String getCode() {
		return "datastore-alletra-mp"
	}

	@Override
	String getName() {
		return "HPE Alletra MP"
	}

	@Override
	ServiceResponse<StorageVolume> prepareHostForVolume(ComputeServerGroup computeServerGroup, StorageVolume storageVolume) {
		return null
	}

	@Override
	ServiceResponse<MvmDiskConfig> buildDiskConfig(ComputeServerGroup computeServerGroup, ComputeServer computeServer, StorageVolume storageVolume) {
		return null
	}

	@Override
	ServiceResponse<StorageVolume> releaseVolumeFromHost(ComputeServerGroup computeServerGroup, StorageVolume storageVolume) {
		return null
	}

	@Override
	ServiceResponse<Snapshot> createSnapshot(ComputeServer computeServer) {
		return null
	}

	@Override
	ServiceResponse<Snapshot> revertSnapshot(ComputeServer computeServer) {
		return null
	}
}
