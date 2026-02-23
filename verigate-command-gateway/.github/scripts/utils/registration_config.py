import json
from typing import Optional

from utils.logger import Logger
from utils.maven_dependency import MavenModules
from utils.registry_alias import GlueSchemaRegistryAliases
from utils.ssm_resolver import SSMResolver


class RegistrationConfig:
    """
    Represents the entire configuration file for this script.
    """
    
    def __init__(self, version: int, maven_modules: MavenModules, glue_schema_registry_aliases: GlueSchemaRegistryAliases):
        self.version = version
        self.maven_modules = maven_modules
        self.glue_schema_registry_aliases = glue_schema_registry_aliases

    @staticmethod
    def parse_from_config(config_file_path: str, stack_name: str, ssm_resolver: SSMResolver):
        Logger.log("Loading config file [%s].." % (config_file_path))
        config_file = open(config_file_path)
        config_json = json.load(config_file)
        config_file.close()
        
        if 'version' not in config_json.keys():
            version = 1
        else:
            version = config_json['version']
            
        maven_modules = MavenModules.parse_from_json_config(config_json['schema-modules'])
        Logger.log("Loaded %d schema module(s) configuration" % maven_modules.size())
        
        if 'glue-schema-registries' not in config_json.keys():
            Logger.log("No Glue schema registries configured in 'glue-schema-registries' in the config file; skipping registry alias resolution..")
            glue_schema_registry_aliases = GlueSchemaRegistryAliases({})
        else:
            glue_schema_registry_aliases = GlueSchemaRegistryAliases.parse_from_json_config(config_json['glue-schema-registries'], stack_name, ssm_resolver)
            Logger.log("Loaded %d Glue schema registry alias(es) configuration" % glue_schema_registry_aliases.size())

        return RegistrationConfig(version, maven_modules, glue_schema_registry_aliases) 