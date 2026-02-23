import os
import re
import zipfile
import xml.etree.ElementTree as ET
from typing import Optional

from utils.logger import Logger
from utils.avro_schema import AvroSchemasDir, AvroSchema
from utils.types import AVRO_SCHEMA_FILE_PATTERN


class MavenModuleName:
    """
    Basic name for a Maven module
    """
    
    def __init__(self, name: str):
        self.name = name

    def pom_file_path(self) -> str:
        return './' + self.name + '/pom.xml'


class MavenDependency:
    """
    Represents a Maven dependency, which is a JAR (zip) archive containing a specific version of a specific artifact.
    The implementation has the ability to find the dependency in a local Maven repo, extract schemas from it and register them in GSR.
    """
    
    def __init__(self, group_id: str, artifact_id: str, version: str):
        self.group_id = group_id
        self.artifact_id = artifact_id
        self.version = version

    @staticmethod
    def load_maven_dependency_version(pom_file_path: str, maven_dependency_group_id: str, maven_dependency_artifact_id: str) -> str:
        """
        Loads the `version` XML attribute from the specified Maven dependency in the specified pom.xml file
        """
        tree = ET.parse(pom_file_path)
        root = tree.getroot()
        # XML namespaces (Maven POM uses a default namespace)
        ns = {'m': 'http://maven.apache.org/POM/4.0.0'}
        for dependency in root.findall('.//m:dependencies/m:dependency', ns):
            group_id = dependency.find('m:groupId', ns)
            artifact_id = dependency.find('m:artifactId', ns)
            version = dependency.find('m:version', ns)
            if group_id is not None and artifact_id is not None:
                if group_id.text == maven_dependency_group_id and artifact_id.text == maven_dependency_artifact_id:
                    return version.text
        raise Exception("Unable to find the version of Maven dependency [%s].[%s] in pom [%s]" % (maven_dependency_group_id, maven_dependency_artifact_id, pom_file_path))

    @staticmethod
    def parse_from_json_config(maven_module_name: MavenModuleName, json_object):
        group_id = json_object['group-id']
        artifact_id = json_object['artifact-id']
        version = MavenDependency.load_maven_dependency_version(maven_module_name.pom_file_path(), group_id, artifact_id)
        return MavenDependency(group_id, artifact_id, version)

    def maven_local_repo_jar_path(self):
        return os.path.expanduser('~/.m2/repository/' + self.group_id.replace('.', '/') + '/' + self.artifact_id + '/' + self.version + '/' + self.artifact_id + '-' + self.version + '.jar')

    def get_avro_schemas(self, application_name: Optional[str] = None, stack_name: Optional[str] = None) -> list[AvroSchema]:
        """
        Extract and return Avro schemas from this Maven dependency.
        """
        Logger.log("Extracting Avro schemas from dependency [%s].[%s].[%s].." % (self.group_id, self.artifact_id, self.version))
        tmp_extract_dir = '/tmp/avro-schema-registration/' + self.group_id + '.' + self.artifact_id + '.' + self.version
        extracted_schemas_relative_paths = self.extract_files_from_dependency_by_regex(self.maven_local_repo_jar_path(), tmp_extract_dir, AVRO_SCHEMA_FILE_PATTERN)
        avro_schemas_dir = AvroSchemasDir(tmp_extract_dir, extracted_schemas_relative_paths)
        return avro_schemas_dir.get_eligible_schemas(application_name, stack_name)

    def extract_files_from_dependency_by_regex(self, jar_path, output_dir, pattern) -> list[str]:
        """
        Extracts files from a JAR (zip) archive that match a regex pattern.
        """
        if not os.path.exists(jar_path):
            raise Exception("Jar file [%s] for Maven dependency does not exist locally" % jar_path)
        os.makedirs(output_dir, exist_ok=True)
        regex = re.compile(pattern)
        extracted_files = []

        with zipfile.ZipFile(jar_path, 'r') as jar:
            for file_info in jar.infolist():
                if regex.search(file_info.filename):
                    jar.extract(file_info, output_dir)
                    extracted_files.append(file_info.filename)
        return extracted_files


class MavenModule:
    """
    Represents a single Maven module configured in the registration configuration file.
    """
    
    def __init__(self, name: MavenModuleName, dependencies: list[MavenDependency]):
        self.name = name
        self.dependencies = dependencies

    @staticmethod
    def parse_from_json_config(name: MavenModuleName, json_object):
        dependencies: list[MavenDependency] = []
        if 'maven-dependencies' in json_object.keys():
            for dependency in json_object['maven-dependencies']:
                dependencies.append(MavenDependency.parse_from_json_config(name, dependency))
        return MavenModule(name, dependencies)

    def source_base_avro_path(self):
        return './' + self.name.name + '/src/main/resources'

    def get_avro_schemas_from_source(self, application_name: Optional[str] = None, stack_name: Optional[str] = None) -> list[AvroSchema]:
        """
        Get Avro schemas from the source directory of this Maven module.
        """
        Logger.log("Getting Avro schemas from source for module [%s].." % self.name.name)
        avro_schemas_dir = AvroSchemasDir.from_source_directory(self.source_base_avro_path())
        return avro_schemas_dir.get_eligible_schemas(application_name, stack_name)

    def get_all_avro_schemas(self, application_name: Optional[str] = None, stack_name: Optional[str] = None) -> list[AvroSchema]:
        """
        Get all Avro schemas from both source and dependencies for this Maven module.
        """
        schemas = []
        
        # Get schemas from source
        schemas.extend(self.get_avro_schemas_from_source(application_name, stack_name))
        
        # Get schemas from dependencies
        for maven_dependency in self.dependencies:
            schemas.extend(maven_dependency.get_avro_schemas(application_name, stack_name))
            
        return schemas


class MavenModules:
    """
    A grouping of all Maven modules configured in the registration configuration file.
    """
    
    def __init__(self, modules: list[MavenModule]):
        self.modules = modules

    @staticmethod
    def parse_from_json_config(json_object):
        modules: list[MavenModule] = []
        for module_name_str in json_object.keys():
            module_name = MavenModuleName(module_name_str)
            modules.append(MavenModule.parse_from_json_config(module_name, json_object[module_name_str]))
        return MavenModules(modules)

    def size(self):
        return len(self.modules)

    def get_all_avro_schemas(self, application_name: Optional[str] = None, stack_name: Optional[str] = None) -> list[AvroSchema]:
        """
        Get all Avro schemas from all modules and their dependencies.
        """
        schemas = []
        for module in self.modules:
            schemas.extend(module.get_all_avro_schemas(application_name, stack_name))
        return schemas 