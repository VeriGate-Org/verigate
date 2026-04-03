variable "default_tags" {
  description = "Default tags to apply to all resources"
  type        = map(string)
  default = {
    accountName         = "aws-verigate"
    applicationName     = "verigate"
    environment         = "dev"
    businessCluster     = "verigate"
    businessEntity      = "sds"
    applicationName     = "verigate"
    businessEntityOwner = "edwin theron"
    costCenterOwner     = "edwin theron"
    techOwner           = "giulio di giannatale"
    costCenter          = "a02788"
  }
}