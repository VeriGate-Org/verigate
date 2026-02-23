variable "default_tags" {
  description = "Default tags to apply to all resources"
  type        = map(string)
  default = {
    accountName         = "aws-verigate-sbx"
    applicationName     = "verigate"
    environment         = "sbx"
    businessCluster     = "verigate"
    businessEntity      = "sds"
    applicationName     = "verigate"
    businessEntityOwner = "edwin theron"
    costCenterOwner     = "edwin theron"
    techOwner           = "giulio di giannatale"
    costCenter          = "a02788"
  }
}