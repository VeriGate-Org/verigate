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
    SBX = 1
    DEV = 2
    PPE = 3
    PRD = 4

    @staticmethod
    def from_string(str_env: str):
        match str_env.lower():
            case "sbx" | "sandbox":
                return Environment.SBX
            case "dev" | "development":
                return Environment.DEV
            case "ppe" | "preproduction" | "pre-production":
                return Environment.PPE
            case "prd" | "production":
                return Environment.PRD
        raise Exception("Unsupported environment: [%s]" % str_env) 