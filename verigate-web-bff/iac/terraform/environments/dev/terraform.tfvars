environment_shortname = "dev"
stack_name            = "verigate"
enable_custom_domain  = true
api_domain            = "api.dev.verigate.co.za"
acm_certificate_arn   = "arn:aws:acm:af-south-1:379992419891:certificate/558fb97f-453a-4717-a397-d82b795de905"

# Health check URLs
health_deedsweb_url   = "http://deedssoap.deeds.gov.za:80/deeds-registration-soap/"
health_worldcheck_url = "https://api-worldcheck.dev.refinitiv.com"
