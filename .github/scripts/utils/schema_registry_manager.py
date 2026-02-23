import time
from typing import Dict, List
from mypy_boto3_glue.client import GlueClient
from mypy_boto3_glue.type_defs import GetSchemaVersionResponseTypeDef, RegisterSchemaVersionResponseTypeDef, SchemaVersionStatusType, GetSchemaResponseTypeDef

from utils.logger import Logger
from utils.types import SchemaVersionId, SchemaDefinition, SchemaName, GlueSchemaRegistryName

class SchemaRegistryManager:
    """
    Group AWS Glue Schema Registry operations into a class.
    """
    
    def __init__(self, glue_client: GlueClient):
        self.glue_client = glue_client

    def create_schema_idempotent(self, registry_name: str, schema_name: str, schema_definition: str) -> None:
        """
        Create a Glue schema in AWS if it does not exist yet. Idempotent in the sense that re-creation of an existing schema will also succeed without any modification.
        """
        try:
            self.glue_client.create_schema(
                RegistryId={'RegistryName': registry_name},
                SchemaName=schema_name,
                DataFormat='AVRO',
                Compatibility='FULL_ALL',
                SchemaDefinition=schema_definition
            )
            Logger.log('Schema successfully created', indent=1)
        except self.glue_client.exceptions.EntityNotFoundException:
            raise Exception("Registry [%s] does not exist" % registry_name)
        except self.glue_client.exceptions.AlreadyExistsException:
            Logger.log('Schema already existed', indent=1)

    def upsert_gsr_schema(self, schema_name: SchemaName, schema_registry_name: GlueSchemaRegistryName, schema_compatibility: str, schema_definition: SchemaDefinition) -> None:
        """
        Create a Glue schema in AWS if it does not exist yet. If it already exists, it will be updated to reflect the latest desired configuration.
        """
        try:
            # See if the schema exists
            get_schema_response: GetSchemaResponseTypeDef = self.glue_client.get_schema(
                SchemaId={
                    'RegistryName': schema_registry_name,
                    'SchemaName': schema_name
                }
            )
            # Now that we know it exists, make sure its configuration aligns
            # with the latest expected values. Currently only support updating the
            # schema compatibility configuration
            if get_schema_response['Compatibility'] == schema_compatibility:
                Logger.log('Schema exist already; no update of the schema required', indent=1)
                return

            self.glue_client.update_schema(
                SchemaId={
                    'RegistryName': schema_registry_name,
                    'SchemaName': schema_name
                },
                Compatibility=schema_compatibility,
                SchemaVersionNumber={
                    'LatestVersion': True
                }
            )
            Logger.log('Schema existed already; updated to reflect latest configuration', indent=1)
            return
        except self.glue_client.exceptions.EntityNotFoundException:
            Logger.log('Schema does not exist yet. Attempt creation..', indent=1)

        try:
            self.glue_client.create_schema(
                RegistryId={'RegistryName': schema_registry_name},
                SchemaName=schema_name,
                DataFormat='AVRO',
                Compatibility=schema_compatibility,
                SchemaDefinition=schema_definition
            )
            Logger.log('Schema successfully created', indent=1)
        except self.glue_client.exceptions.EntityNotFoundException:
            raise Exception("Registry [%s] does not exist" % schema_registry_name)

    def get_schema_version_status(self, schema_version_id: SchemaVersionId) -> SchemaVersionStatusType:
        """
        Query AWS Glue for the latest status of the supplied Glue Schema version id.
        """
        get_schema_version_response: GetSchemaVersionResponseTypeDef = self.glue_client.get_schema_version(
            SchemaVersionId=schema_version_id
        )
        return get_schema_version_response['Status']

    def handle_schema_version_status(self, count: int, schema_version_id: SchemaVersionId, schema_version_status: SchemaVersionStatusType) -> None:
        """
        React based on the latest schema status. For the happy case, expect 'AVAILABLE'.
        'PENDING' will be polled again, up to a limited number of times.
        Any other status will raise an Exception.
        """
        max_iteration_count: int = 10
        if count >= max_iteration_count:
            raise Exception("Giving up polling for final schema version status after %d attempt(s)" % count)
        
        match schema_version_status:
            case 'AVAILABLE':
                Logger.log('Schema version successfully (re)registered', indent=1)
            case 'DELETING':
                raise Exception('Unexpected DELETING state for schema version')
            case 'FAILURE':
                raise Exception('Failed to register schema version (status = FAILURE). Check schema syntax and/or compatibility.')
            case 'PENDING':
                Logger.log("Schema version PENDING; Polling for final status (%d of %d).." % (count + 1, max_iteration_count), indent=1)
                time.sleep(2)
                new_schema_version_status = self.get_schema_version_status(schema_version_id)
                self.handle_schema_version_status(count + 1, schema_version_id, new_schema_version_status)
            case _:
                raise Exception("Unsupported schema version status: [%s]" % (schema_version_status))

    def create_schema_version_idempotent(self, registry_name: str, schema_name: str, schema_definition: str) -> None:
        """
        Create an AWS glue schema version. Succeeds without modification if it already exists. Even if the definition matches an older version (non-current).
        """
        register_schema_response: RegisterSchemaVersionResponseTypeDef = self.glue_client.register_schema_version(
            SchemaId={
                'RegistryName': registry_name,
                'SchemaName': schema_name
            },
            SchemaDefinition=schema_definition
        )
        schema_version_id = register_schema_response['SchemaVersionId']
        schema_version_number = register_schema_response['VersionNumber']
        schema_version_status = register_schema_response['Status']
        Logger.log("Schema version [%d] (id: [%s]) created, or already existed; confirming status.." % (schema_version_number, schema_version_id), indent=1)
        self.handle_schema_version_status(0, schema_version_id, schema_version_status)

    def create_gsr_schema_version_idempotent(self, schema_name: SchemaName, schema_registry_name: GlueSchemaRegistryName, schema_definition: SchemaDefinition) -> None:
        """
        Alias for create_schema_version_idempotent to maintain compatibility with comprehensive script naming.
        """
        self.create_schema_version_idempotent(schema_registry_name, schema_name, schema_definition) 