from utils.ssm_resolver import SSMResolver
from utils.types import GlueSchemaRegistryAliasName, GlueSchemaRegistryName


class GlueSchemaRegistryAlias:
    """
    An alias:name mapping for a Glue Schema registry.
    """
    
    def __init__(self, alias_name: GlueSchemaRegistryAliasName, registry_name: GlueSchemaRegistryName):
        self.alias_name = alias_name
        self.registry_name = registry_name

    @staticmethod
    def resolve_glue_schema_registry_name(registry_name_pattern: str, stack_name: str, ssm_resolver: SSMResolver) -> GlueSchemaRegistryName:
        """
        Resolve a Glue Schema registry name from the supplied pattern.
        Supported placeholders in the pattern include:
        1. '${StackName}' - to be replaced with the --stack-name script argument.
        2. '{{resolve:ssm:parameter-name}}' - to be replaced with SSM parameter lookups in AWS.
        """
        try:
            registry_name_pattern = registry_name_pattern.replace('${StackName}', stack_name)
            registry_name_pattern = ssm_resolver.resolve_ssm_references(registry_name_pattern)
            return registry_name_pattern
        except Exception as e:
            raise Exception("Failed to resolve Glue schema registry name [%s]" % (registry_name_pattern), e)

    @staticmethod
    def parse_from_json_config(alias_name: GlueSchemaRegistryAliasName, json_object, stack_name: str, ssm_resolver: SSMResolver):
        registry_name_pattern = json_object['registry-name']
        registry_name = GlueSchemaRegistryAlias.resolve_glue_schema_registry_name(registry_name_pattern, stack_name, ssm_resolver)
        return GlueSchemaRegistryAlias(alias_name, registry_name)


class GlueSchemaRegistryAliases:
    """
    A grouping of all schema registry aliases configured in the registration configuration file.
    """
    
    def __init__(self, aliases_by_name: dict[GlueSchemaRegistryAliasName, GlueSchemaRegistryAlias]):
        self.aliases_by_name = aliases_by_name

    @staticmethod
    def parse_from_json_config(json_object, stack_name: str, ssm_resolver: SSMResolver):
        aliases_by_name: dict[GlueSchemaRegistryAliasName, GlueSchemaRegistryAlias] = {}
        alias_names = json_object.keys()
        for alias_name in alias_names:
            aliases_by_name[alias_name] = GlueSchemaRegistryAlias.parse_from_json_config(alias_name, json_object[alias_name], stack_name, ssm_resolver)
        return GlueSchemaRegistryAliases(aliases_by_name)

    def size(self):
        return len(self.aliases_by_name)

    def get_glue_schema_registry_name(self, alias_name: GlueSchemaRegistryAliasName) -> GlueSchemaRegistryName:
        if alias_name not in self.aliases_by_name:
            raise Exception("Schema registry alias [%s] is not configured" % alias_name)
        return self.aliases_by_name[alias_name].registry_name 