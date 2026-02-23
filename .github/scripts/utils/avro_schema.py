import os
import json
import re
from typing import Optional

from utils.logger import Logger
from utils.types import (
    SchemaName, 
    SchemaDefinition, 
    GlueSchemaRegistryName, 
    Environment,
    AVRO_SCHEMA_FILE_PATTERN
)

class AvroSchema:
    """
    Represents a specific Avro schema, regardless of where it really originated from, but it is available on local disk.
    """
    
    def __init__(self, base_path: str, schema_relative_path: str, application_name: Optional[str] = None, stack_name: Optional[str] = None):
        self.base_path = base_path
        self.schema_relative_path = schema_relative_path
        self.application_name = application_name
        self.stack_name = stack_name

    def absolute_path_to_schema(self):
        return os.path.join(self.base_path, self.schema_relative_path)

    def get_schema_name(self) -> SchemaName:
        return os.path.basename(self.absolute_path_to_schema())[:-5]  # strips the '.avsc' from the file name for schema name

    def get_schema_registry_name_from_metadata(self) -> Optional[GlueSchemaRegistryName]:
        """
        Loads the schema registry name from schema metadata, if it exists.
        Note that if the metadata exists and specifies an application owner, it is also validated against the application this registration is run for.
        """
        absolute_path = self.absolute_path_to_schema()
        schema_file = open(absolute_path)
        schema_json = json.load(schema_file)
        schema_file.close()
        
        if 'sft-metadata' not in schema_json.keys():
            Logger.log("No 'sft-metadata' found in schema file [%s]; skipping schema registry name resolution from metadata.." % absolute_path)
            return None
            
        sft_metadata = schema_json['sft-metadata']
        if 'owner-application' in sft_metadata.keys() and self.application_name:
            owner_application = sft_metadata['owner-application']
            if owner_application.lower() != self.application_name.lower():
                raise Exception("Schema file [%s] specifies an 'owner-application' of [%s], but it does not match the supplied application name [%s]" % (absolute_path, owner_application, self.application_name))
                
        registry_name_pattern = sft_metadata['aws-glue-schema-registry-name-pattern']
        
        # Basic string substitution for StackName
        if self.stack_name:
            registry_name_pattern = registry_name_pattern.replace('${StackName}', self.stack_name)
            
        return registry_name_pattern

    def load_schema_definition(self) -> SchemaDefinition:
        """
        Load the Avro schema definition from the supplied file.
        """
        with open(self.absolute_path_to_schema(), 'r') as file:
            file_content = file.read()
        return file_content

    def is_v0_schema(self) -> bool:
        """
        V0 schemas are different in that they support backwards incompatible schema changes for faster iteration.
        The idea is to only register and use these in test environments.
        """
        return self.get_schema_name().endswith('V0')

    def get_gsr_compatibility(self) -> str:
        """
        Get the Glue Schema Registry compatibility setting for this schema.
        """
        # The default compatibility is fully forwards and backwards compatible
        # Support overriding this to no compatibility for "V0" schemas to allow for iteration on schemas
        schema_compatibility = 'FULL_ALL'
        if self.is_v0_schema():
            Logger.log("WARNING: Default schema compatibility (FULL_ALL) overridden to NONE for: %s" % self.get_schema_name(), indent=1)
            schema_compatibility = 'NONE'
        return schema_compatibility

    def eligible_for_gsr_registration(self, environment: Environment) -> bool:
        """
        Determine if this schema is eligible for registration in the given environment.
        """
        match environment:
            # Anything goes in SBX and DEV
            case Environment.SBX | Environment.DEV:
                return True
            # Avoid V0 (backwards incompatible) schemas in PPE and PRD
            case Environment.PPE | Environment.PRD:
                return not self.is_v0_schema()
        raise Exception("Unsupported environment: [%s]" % environment.name)


class AvroSchemasDir:
    """
    Represents a grouping of Avro schemas from a single directory (typically a Maven module).
    """
    
    def __init__(self, base_path: str, schema_relative_paths: list[str]):
        self.base_path = base_path
        self.schema_relative_paths = schema_relative_paths

    def uses_owned_grouping(self):
        """
        Grouping schemas by "owned" vs "consumed" is supported in a directory structure, but is optional.
        This implementation detects if any of the schemas in this directory makes use of this structure.
        If there is at least one, only the "owned" schemas will be registered.
        """
        for schema_relative_path in self.schema_relative_paths:
            if schema_relative_path.startswith('avro/owned/'):
                return True
        return False

    def get_eligible_schemas(self, application_name: Optional[str] = None, stack_name: Optional[str] = None) -> list[AvroSchema]:
        """
        Get all eligible schemas from this directory as AvroSchema objects.
        """
        uses_owned_grouping = self.uses_owned_grouping()
        schemas = []
        
        for schema_relative_path in self.schema_relative_paths:
            if uses_owned_grouping and not schema_relative_path.startswith('avro/owned/'):
                continue
            schemas.append(AvroSchema(self.base_path, schema_relative_path, application_name, stack_name))
            
        return schemas

    @staticmethod
    def list_relative_files_matching(root_path: str, pattern: str) -> list[str]:
        """
        List all files in the given directory that match the specified regex pattern.
        """
        regex = re.compile(pattern)
        relative_files: list[str] = []
        
        if not os.path.exists(root_path):
            return relative_files
            
        for dirpath, _, filenames in os.walk(root_path):
            for filename in filenames:
                full_path = os.path.join(dirpath, filename)
                rel_path = os.path.relpath(full_path, root_path)
                if regex.search(rel_path):
                    relative_files.append(rel_path)
        return relative_files

    @staticmethod
    def from_source_directory(source_path: str) -> 'AvroSchemasDir':
        """
        Create an AvroSchemasDir from a source directory by scanning for Avro schema files.
        """
        schemas_relative_paths = AvroSchemasDir.list_relative_files_matching(source_path, AVRO_SCHEMA_FILE_PATTERN)
        return AvroSchemasDir(source_path, schemas_relative_paths) 