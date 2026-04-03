import argparse
import boto3
import sys
from argparse import Namespace
from mypy_boto3_glue.client import GlueClient
from mypy_boto3_ssm.client import SSMClient

from utils.ssm_resolver import SSMResolver
from utils.schema_registry_manager import SchemaRegistryManager
from utils.schema_file_manager import SchemaFileManager, LocalSchemaReference
from utils.logger import Logger
from utils.types import SchemaVersionId

def parse_args() -> Namespace:
    """
    Configures and runs the command-line argument parser.
    """
    parser = argparse.ArgumentParser(
        description='AWS Glue Schema registry maintenance'
    )
    parser.add_argument('-p', '--profile', help='AWS credentials profile (optional)')
    parser.add_argument('-r', '--region', help='AWS region (optional)')
    parser.add_argument('-s', '--stack-name', required=True, help='Name of stack')
    parser.add_argument('-c', '--config-file', required=True, help='Configuration file')
    parser.add_argument('-e', '--environment-shortname', required=True, help='Environment shortname (e.g., dev, prod)')
    return parser.parse_args()

def register_avro_schema(
    schema_to_register: LocalSchemaReference,
    schema_registry_manager: SchemaRegistryManager,
    schema_file_manager: SchemaFileManager
) -> None:
    """
    (Re)register an Avro schema. This will first ensure the actual schema exists (create if not), and then the schema version (create if not).
    Both actions (schema and schema version) are considered idempotent.
    """
    Logger.log(f"\n(Re)registering [{schema_to_register.schema_file}] in Glue registry [{schema_to_register.schema_registry}] ..")
    schema_definition = schema_file_manager.load_schema_definition(schema_to_register)
    schema_registry_manager.create_schema_idempotent(
        schema_to_register.schema_registry,
        schema_to_register.schema_name,
        schema_definition
    )
    schema_registry_manager.create_schema_version_idempotent(
        schema_to_register.schema_registry,
        schema_to_register.schema_name,
        schema_definition
    )

def register_avro_schemas(
    schemas_to_register: list[LocalSchemaReference],
    schema_registry_manager: SchemaRegistryManager,
    schema_file_manager: SchemaFileManager
) -> None:
    """
    (Re)register all the supplied schema references in AWS Glue.
    """
    for schema_to_register in schemas_to_register:
        register_avro_schema(schema_to_register, schema_registry_manager, schema_file_manager)

if __name__ == '__main__':
    Logger.log('Avro schema maintenance in AWS Glue Schema Registry starting..\n')
    args: Namespace = parse_args()

    stack_name = args.stack_name
    config_file_name = args.config_file
    Logger.log('- Profile: ' + (args.profile if args.profile else '-'))
    Logger.log('- Region : ' + (args.region if args.region else '-'))
    Logger.log('- Stack  : ' + stack_name)
    Logger.log('- Config : ' + config_file_name)
    Logger.log('- Environment Shortname: ' + args.environment_shortname)
    Logger.log('\n')

    boto_session = boto3.Session(
        profile_name=args.profile,
        region_name=args.region,
    )
    aws_glue_client: GlueClient = boto_session.client('glue')
    aws_ssm_client: SSMClient = boto_session.client('ssm')

    # Initialize utility classes
    ssm_resolver = SSMResolver(aws_ssm_client)
    schema_registry_manager = SchemaRegistryManager(aws_glue_client)
    schema_file_manager = SchemaFileManager(ssm_resolver)

    try:
        schemas_to_register = schema_file_manager.get_schemas_to_register(
            args.config_file,
            stack_name,
            args.environment_shortname
        )
        register_avro_schemas(schemas_to_register, schema_registry_manager, schema_file_manager)
    except Exception as e:
        Logger.error(f"{str(e)}\n")
        Logger.error('Avro schema maintenance in AWS Glue Schema Registry failed.')
        sys.exit(1)

    Logger.log('\nAvro schema maintenance in AWS Glue Schema Registry successfully completed.')
    sys.exit(0)
