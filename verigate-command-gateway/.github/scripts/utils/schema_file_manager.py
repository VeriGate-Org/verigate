import os
import json
from typing import Dict, List, Optional
from dataclasses import dataclass

from utils.logger import Logger
from utils.ssm_resolver import SSMResolver
from utils.types import GlueSchemaRegistryAlias, GlueSchemaRegistryName, Environment
from utils.avro_schema import AvroSchema

@dataclass
class LocalSchemaReference:
    schema_module: str
    schema_registry: str
    schema_name: str
    schema_file: str

class SchemaFileManager:
    def __init__(self, ssm_resolver: SSMResolver):
        self.ssm_resolver = ssm_resolver

    def schema_directory_for_module(self, schema_module_name: str) -> str:
        """
        Construct a relative path to the directory containing schemas to register for the given module
        """
        return f'./src/{schema_module_name}/src/main/resources/avro/owned/'

    def resolve_glue_schema_registry_name(self, registry_name_pattern: str, stack_name: str, environment_shortname: str) -> GlueSchemaRegistryName:
        """
        Resolve a Glue Schema registry name from the supplied pattern.
        Supported placeholders in the pattern include:
        1. '${StackName}' - to be replaced with the stack name.
        2. '${EnvironmentShortname}' - to be replaced with the environment shortname.
        3. '{{resolve:ssm:parameter-name}}' - to be replaced with SSM parameter lookups in AWS.
        """
        try:
            registry_name_pattern = registry_name_pattern.replace('${StackName}', stack_name)
            registry_name_pattern = registry_name_pattern.replace('${EnvironmentShortname}', environment_shortname)
            registry_name_pattern = self.ssm_resolver.resolve_ssm_references(registry_name_pattern)
            return registry_name_pattern
        except Exception as e:
            raise Exception(f"Failed to resolve Glue schema registry name [{registry_name_pattern}]", e)

    def resolve_glue_schema_registries(self, config_json: dict, stack_name: str, environment_shortname: str) -> Dict[GlueSchemaRegistryAlias, GlueSchemaRegistryName]:
        """
        Creates an 'alias:name' dictionary for all Glue schema registries listed in the config file.
        """
        glue_schema_registries: Dict[GlueSchemaRegistryAlias, GlueSchemaRegistryName] = {}
        glue_schema_registry_aliases = config_json['glue-schema-registries'].keys()
        for glue_schema_registry_alias in glue_schema_registry_aliases:
            try:
                glue_schema_registry_name = self.resolve_glue_schema_registry_name(
                    config_json['glue-schema-registries'][glue_schema_registry_alias]['registry-name'],
                    stack_name,
                    environment_shortname
                )
                glue_schema_registries[glue_schema_registry_alias] = glue_schema_registry_name
            except Exception as e:
                raise Exception(f"Failed to resolve Glue schema registry name for alias [{glue_schema_registry_alias}]", e)
        return glue_schema_registries

    def resolve_local_schema_references_for_module_and_registry_alias(
        self, 
        schema_module_name: str, 
        registry_alias: GlueSchemaRegistryAlias, 
        glue_schema_registries: Dict[GlueSchemaRegistryAlias, GlueSchemaRegistryName]
    ) -> List[LocalSchemaReference]:
        """
        Create 'LocalSchemaReference' entries for all the Avro schema files in the given module and registry alias.
        """
        local_schema_references: List[LocalSchemaReference] = []
        if registry_alias not in glue_schema_registries.keys():
            raise Exception(f"Schema module [{schema_module_name}] defines a registry alias directory [{registry_alias}] which is not configured to resolve to a real registry name")
        
        glue_schema_registry_name = glue_schema_registries[registry_alias]
        owned_schemas_dir: str = self.schema_directory_for_module(schema_module_name) + registry_alias
        
        if not os.path.exists(owned_schemas_dir):
            Logger.log(f"Schema directory [{owned_schemas_dir}] does not exist, skipping...")
            return local_schema_references
            
        schema_file_dir_entries = [dir_entry for dir_entry in os.scandir(owned_schemas_dir) 
                                 if dir_entry.is_file() and dir_entry.name.endswith('.avsc')]
        
        for schema_file_dir_entry in schema_file_dir_entries:
            local_schema_references.append(LocalSchemaReference(
                schema_module_name,
                glue_schema_registry_name,
                schema_file_dir_entry.name[:-5],  # strips the '.avsc' from the file name for schema name
                schema_file_dir_entry.path
            ))
        return local_schema_references

    def resolve_local_schema_references_for_module(
        self, 
        schema_module_name: str, 
        glue_schema_registries: Dict[GlueSchemaRegistryAlias, GlueSchemaRegistryName]
    ) -> List[LocalSchemaReference]:
        """
        Create 'LocalSchemaReference' entries for all the Avro schema files in the given module (across all registry aliases).
        """
        local_schema_references: List[LocalSchemaReference] = []
        owned_schemas_dir: str = self.schema_directory_for_module(schema_module_name)
        
        if not os.path.exists(owned_schemas_dir):
            Logger.log(f"Schema module directory [{owned_schemas_dir}] does not exist, skipping...")
            return local_schema_references
            
        registry_alias_directories = [f.name for f in os.scandir(owned_schemas_dir) if f.is_dir()]
        
        for registry_alias_directory in registry_alias_directories:
            local_schema_references.extend(
                self.resolve_local_schema_references_for_module_and_registry_alias(
                    schema_module_name, 
                    registry_alias_directory, 
                    glue_schema_registries
                )
            )
        return local_schema_references

    def resolve_local_schema_references_for_modules(
        self, 
        schema_module_names: List[str], 
        glue_schema_registries: Dict[GlueSchemaRegistryAlias, GlueSchemaRegistryName]
    ) -> List[LocalSchemaReference]:
        """
        Create 'LocalSchemaReference' entries for all the Avro schema files in the given modules (all registry aliases of all modules).
        """
        local_schema_references: List[LocalSchemaReference] = []
        for schema_module_name in schema_module_names:
            local_schema_references.extend(
                self.resolve_local_schema_references_for_module(schema_module_name, glue_schema_registries)
            )
        return local_schema_references

    def load_schema_definition(self, local_schema_reference: LocalSchemaReference) -> str:
        """
        Load the Avro schema definition from the supplied file.
        """
        with open(local_schema_reference.schema_file, 'r') as file:
            return file.read()

    def get_schemas_to_register(self, config_file_name: str, stack_name: str, environment_shortname: str) -> List[LocalSchemaReference]:
        """
        Create 'LocalSchemaReference' entries for all the Avro schema files linked to from the supplied configuration file.
        This includes parsing the config file for schema modules as well as resolving of Glue registry aliases.
        """
        Logger.log(f"Loading config file [{config_file_name}]..")
        with open(config_file_name) as config_file:
            config_json = json.load(config_file)
        
        Logger.log("Resolving registry aliases..")
        glue_schema_registries = self.resolve_glue_schema_registries(config_json, stack_name, environment_shortname)
        Logger.log(f"Successfully resolved {len(glue_schema_registries)} registry alias(es) from [{config_file_name}]")
        
        schema_module_names = config_json['schema-modules'].keys()
        Logger.log("Load Avro schema references for (re)registration..")
        
        schemas_to_register = self.resolve_local_schema_references_for_modules(schema_module_names, glue_schema_registries)
        Logger.log(f"Successfully loaded {len(schemas_to_register)} Avro schema reference(s) eligible for (re)registration across {len(schema_module_names)} schema module(s)")
        
        return schemas_to_register

    def filter_schemas_by_environment(self, schemas: List[AvroSchema], environment: Environment) -> List[AvroSchema]:
        """
        Filter schemas based on environment eligibility (e.g., exclude V0 schemas from production environments).
        """
        eligible_schemas = []
        for schema in schemas:
            if schema.eligible_for_gsr_registration(environment):
                eligible_schemas.append(schema)
            else:
                Logger.log("WARNING: Skipping registration of Avro schema considered ineligible for environment [%s]: %s" % (environment.name, schema.schema_relative_path))
        return eligible_schemas 