# HPE Alletra MP Datastore Plugin for MVM

This plugin provides interactions between an Alletra MP Storage server and allows for LUN creation/management per vdisk on a vm provisioned on the Morphues MVM Hypervisor Solution.

**NOTE:** This is a sample/prototype for use by HPE team members to kickstart Alletra MP Integration. It is incomplete in its current form.

### Requirements

- Java JDK 17+ Runtime Environment
- Access to Maven Central
- Access to HPE Alletra MP Storage Server

### Building


```bash
./gradlew shadowJar
```

Result artifact will be in `build/lib/` directory.

### Installation

Plugins can be installed by uploading from the Morpheus UI Interface in `Administration -> Integrations -> Plugins`
These are reloadable. Simply upload a plugin overtop of itself and morpheus will replace. Ensure the code does not change.

