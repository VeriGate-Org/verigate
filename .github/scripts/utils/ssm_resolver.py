from mypy_boto3_ssm.client import SSMClient
from mypy_boto3_ssm.type_defs import GetParameterResultTypeDef

class SSMResolver:
    """
    Group AWS SSM operations into a class.
    """
    
    def __init__(self, ssm_client: SSMClient):
        self.ssm_client = ssm_client

    def lookup_parameter(self, ssm_parameter_name: str) -> str:
        """
        Query SSM parameter value for the given parameter name.
        """
        try:
            get_parameter_result: GetParameterResultTypeDef = self.ssm_client.get_parameter(
                Name=ssm_parameter_name
            )
            return get_parameter_result['Parameter']['Value']
        except Exception as e:
            raise Exception("Failed to look up SSM parameter [%s]" % (ssm_parameter_name), e)

    def resolve_ssm_references(self, pattern: str) -> str:
        """
        Replace all '{{resolve:ssm:parameter-name}}' placeholders in the supplied pattern with real SSM
        parameter values by looking up the 'parameter-name'.
        """
        resolve_ssm_pattern_prefix = '{{resolve:ssm:'
        resolve_ssm_pattern_suffix = '}}'
        start_index = pattern.find(resolve_ssm_pattern_prefix)
        if start_index == -1:
            return pattern

        end_index = pattern.find(resolve_ssm_pattern_suffix, start_index)
        if end_index == -1:
            return pattern

        ssm_parameter_name = pattern[start_index + len(resolve_ssm_pattern_prefix):end_index]
        replacement = self.lookup_parameter(ssm_parameter_name)
        new_string = pattern[:start_index] + replacement + pattern[end_index + 2:]
        return self.resolve_ssm_references(new_string) 