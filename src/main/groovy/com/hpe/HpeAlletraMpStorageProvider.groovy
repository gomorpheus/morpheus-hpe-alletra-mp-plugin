package com.hpe

import com.morpheusdata.core.providers.StorageProviderVolumes
import com.morpheusdata.model.StorageGroup
import com.morpheusdata.model.StorageServer
import com.morpheusdata.model.StorageServerType
import com.morpheusdata.model.StorageBucket
import com.morpheusdata.model.StorageVolume
import com.morpheusdata.model.StorageVolumeType
import com.morpheusdata.model.OptionType
import com.morpheusdata.model.Icon
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.core.providers.StorageProvider
import com.morpheusdata.core.providers.StorageProviderBuckets
import com.morpheusdata.response.ServiceResponse
import groovy.util.logging.Slf4j

/**
 * HpeAlletraMpStorageBucketProvider is a storage bucket provider for Morpheus.
 * It adds a new {@link {StorageServerType}} named 'HPE Alletra MP' which can be created
 * to allow its storage buckets to be synced and used as a storage source or destination.
 * This also provides the ability to create and delete buckets and their objects.
 * 
 * @see StorageProvider
 * @see StorageProviderBuckets
 */
@Slf4j
class HpeAlletraMpStorageProvider implements StorageProvider, StorageProviderVolumes {
	
	protected MorpheusContext morpheusContext
	protected Plugin plugin

	HpeAlletraMpStorageProvider(Plugin plugin, MorpheusContext morpheusContext) {
		this.morpheusContext = morpheusContext
		this.plugin = plugin
	}

    /**
	 * Returns the Morpheus Context for interacting with data stored in the Main Morpheus Application
	 *
	 * @return an implementation of the MorpheusContext for running Future based rxJava queries
	 */
	@Override
	MorpheusContext getMorpheus() {
		return morpheusContext
	}

	/**
	 * Returns the instance of the Plugin class that this provider is loaded from
	 * @return Plugin class contains references to other providers
	 */
	@Override
	Plugin getPlugin() {
		return plugin
	}

	/**
	 * A unique shortcode used for referencing the provided provider. Make sure this is going to be unique as any data
	 * that is seeded or generated related to this provider will reference it by this code.
	 * @return short code string that should be unique across all other plugin implementations.
	 */
	@Override
	String getCode() {
		return "alletramp-storage-provider"
	}

	/**
	 * Provides the provider name for reference when adding to the Morpheus Orchestrator
	 * NOTE: This may be useful to set as an i18n key for UI reference and localization support.
	 *
	 * @return either an English name of a Provider or an i18n based key that can be scanned for in a properties file.
	 */
	@Override
	String getName() {
		return "HPE Alletra MP"
	}

	/**
	 * Provides the provider description
	 * NOTE: This may be useful to set as an i18n key for UI reference and localization support.
	 *
	 * @return either an English name of a Provider or an i18n based key that can be scanned for in a properties file.
	 */
	@Override
    String getDescription() {
        return "HPE Alletra MP Storage Bucket Provider"
    }

    /**
	 * Returns the Storage Bucket Integration logo for display when a user needs to view or add this integration
	 * @since 0.12.3
	 * @return Icon representation of assets stored in the src/assets of the project.
	 */
	@Override
	Icon getIcon() {
		return new Icon(path:"morpheus.svg", darkPath: "morpheus.svg")
	}

	/**
	 * Validation Method used to validate all inputs applied to the storage server integration upon save.
	 * If an input fails validation or authentication information cannot be verified, Error messages should be returned
	 * via a {@link ServiceResponse} object where the key on the error is the field name and the value is the error message.
	 * If the error is a generic authentication error or unknown error, a standard message can also be sent back in the response.
	 *
	 * @param storageServer The Storage Server object contains all the saved information regarding configuration of the Storage Provider
	 * @param opts an optional map of parameters that could be sent. This may not currently be used and can be assumed blank
	 * @return A response is returned depending on if the inputs are valid or not.
	 */
	@Override
	ServiceResponse verifyStorageServer(StorageServer storageServer, Map opts) {
		return ServiceResponse.success()
	}

	/**
	 * Called on the first save / update of a storage server integration. Used to do any initialization of a new integration
	 * Often times this calls the periodic refresh method directly.
	 * @param storageServer The Storage Server object contains all the saved information regarding configuration of the Storage Provider.
	 * @param opts an optional map of parameters that could be sent. This may not currently be used and can be assumed blank
	 * @return a ServiceResponse containing the success state of the initialization phase
	 */
	@Override
	ServiceResponse initializeStorageServer(StorageServer storageServer, Map opts) {
		return refreshStorageServer(storageServer, opts)
	}

	/**
	 * Refresh the provider with the associated data in the external system.
	 * This is where the buckets are synced.
	 * @param storageServer The Storage Server object contains all the saved information regarding configuration of the Storage Provider.
	 * @param opts an optional map of parameters that could be sent. This may not currently be used and can be assumed blank
	 * @return a {@link ServiceResponse} object. A ServiceResponse with a success value of 'false' will indicate the
	 * refresh process has failed and will change the storage server status to 'error'
	 */
	@Override
	ServiceResponse refreshStorageServer(StorageServer storageServer, Map opts) {
		ServiceResponse rtn = ServiceResponse.prepare()
		log.debug("refreshStorageServer: ${storageServer.name}")
		try {
			Date syncDate = new Date()
			Boolean hostOnline = true
			if(hostOnline) {
			    // cacheBuckets(storageServer, opts)
			    rtn.success = true
			} else {
			    log.warn("refresh - storageServer: ${storageServer.name} - Storage server appears to be offline")
			    rtn.msg = "Storage server appears to be offline"
			}
		} catch(e) {
			log.error("refreshStorageServer error: ${e}", e)
			rtn.msg = e.message
		}
		return rtn
	}

	/**
     * Provides a {@link StorageServerType} record that needs to be configured in the morpheus environment.
     * This record dicates configuration settings and other facets and behaviors for the storage type.
     * @return a {@link StorageServerType}
     */
    @Override
    StorageServerType getStorageServerType() {
        StorageServerType storageServerType = new StorageServerType(
            code:getCode(), name:getName(), description:getDescription(), hasBlock:true, hasObject:false,
            hasFile:false, hasDatastore:true, hasNamespaces:false, hasGroups:false, hasDisks:true, hasHosts:false,
            createBlock:true, createObject:false, createFile:false, createDatastore:true, createNamespaces:false,
            createGroup:false, createDisk:true, createHost:false, hasFileBrowser: true)
        storageServerType.optionTypes = getStorageServerOptionTypes()
        storageServerType.volumeTypes = getStorageVolumeTypes()
        return storageServerType
    }
    
    /**
     * Provide custom configuration options when creating a new {@link StorageServer}
     * @return a List of OptionType
     */
    Collection<OptionType> getStorageServerOptionTypes() {
        return [
            new OptionType(code: 'alletramp.credential', name: 'Credentials', inputType: OptionType.InputType.CREDENTIAL, fieldName: 'type', fieldLabel: 'Credentials', fieldContext: 'credential', required: true, displayOrder: 10, defaultValue: 'local',optionSource: 'credentials',config: '{"credentialTypes":["username-password"]}'),
            new OptionType(code: 'alletramp.serviceUsername', name: 'Username', inputType: OptionType.InputType.TEXT, fieldName: 'serviceUsername', fieldLabel: 'Username', fieldContext: 'domain', required: true, displayOrder: 11,localCredential: true),
            new OptionType(code: 'alletramp.servicePassword', name: 'Password', inputType: OptionType.InputType.PASSWORD, fieldName: 'servicePassword', fieldLabel: 'Password', fieldContext: 'domain', required: true, displayOrder: 12,localCredential: true),
            new OptionType(code: 'alletramp.serviceUrl', name: 'Endpoint', inputType: OptionType.InputType.TEXT , fieldName: 'serviceUrl', fieldLabel: 'Endpoint', fieldContext: 'domain', required: false, displayOrder: 13)
        ]
    }


	@Override
	ServiceResponse<StorageVolume> createVolume(StorageGroup storageGroup, StorageVolume storageVolume, Map map) {
		return null
	}

	@Override
	ServiceResponse<StorageVolume> resizeVolume(StorageGroup storageGroup, StorageVolume storageVolume, Map map) {
		return null
	}

	@Override
	ServiceResponse<StorageVolume> deleteVolume(StorageGroup storageGroup, StorageVolume storageVolume, Map map) {
		return null
	}

	/**
	 * Provides a collection of {@link StorageVolumeType} records that needs to be configured in the morpheus environment
	 * @return Collection of StorageVolumeType
	 */
	@Override
	Collection<StorageVolumeType> getStorageVolumeTypes() {
		return [
						new StorageVolumeType(code:'alletraMPLUN', displayName:'HPE Alletra MP LUN', name:'HPE Alletra MP LUN', description:'HPE Alletra MP LUN', volumeType:'block', enabled:false, displayOrder:1, customLabel:true, customSize:true, defaultType:true, autoDelete:true, allowSearch:true, volumeCategory:'volume')
		]
	}
}
