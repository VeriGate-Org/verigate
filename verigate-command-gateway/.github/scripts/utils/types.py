from typing import TypeAlias
from enum import Enum

SchemaVersionId: TypeAlias = str
SchemaDefinition: TypeAlias = str
SchemaName: TypeAlias = str
GlueSchemaRegistryAlias: TypeAlias = str
GlueSchemaRegistryName: TypeAlias = str
GlueSchemaRegistryAliasName: TypeAlias = str

# A regex used to match Avro schemas in a directory structure.
AVRO_SCHEMA_FILE_PATTERN = '^avro/.*\.avsc$'

class Environment(Enum):
    """
    Supported target environments.
    """
    DEV = 1
    PROD = 2

    @staticmethod
    def from_string(str_env: str):
        match str_env.lower():
            case "dev" | "development":
                return Environment.DEV
            case "prod" | "production":
                return Environment.PROD
        raise Exception("Unsupported environment: [%s]" % str_env)
