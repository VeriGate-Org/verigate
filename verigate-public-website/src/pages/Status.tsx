import Navigation from "@/components/Navigation";
import Footer from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { 
  Activity,
  CheckCircle2,
  XCircle,
  Clock,
  TrendingUp,
  AlertTriangle,
  Server,
  Zap,
  Globe
} from "lucide-react";

const Status = () => {
  const currentStatus = {
    overall: "operational",
    lastIncident: "2024-12-15",
    uptime30Days: 99.97,
    uptime90Days: 99.95
  };

  const services = [
    {
      name: "API Gateway",
      status: "operational",
      uptime: 99.99,
      latency: "45ms",
      description: "Main API endpoints"
    },
    {
      name: "Verification Service",
      status: "operational",
      uptime: 99.98,
      latency: "1.2s",
      description: "Document and identity verification"
    },
    {
      name: "AML Screening",
      status: "operational",
      uptime: 99.97,
      latency: "850ms",
      description: "AML database screening"
    },
    {
      name: "Biometric Engine",
      status: "operational",
      uptime: 99.96,
      latency: "2.1s",
      description: "Facial recognition and liveness"
    },
    {
      name: "Webhook Delivery",
      status: "operational",
      uptime: 99.94,
      latency: "120ms",
      description: "Event notification system"
    },
    {
      name: "Dashboard",
      status: "operational",
      uptime: 99.99,
      latency: "180ms",
      description: "Web dashboard and portal"
    }
  ];

  const regions = [
    { name: "North America (US-East)", status: "operational", latency: "38ms" },
    { name: "North America (US-West)", status: "operational", latency: "42ms" },
    { name: "Europe (Frankfurt)", status: "operational", latency: "51ms" },
    { name: "Europe (London)", status: "operational", latency: "48ms" },
    { name: "Asia Pacific (Singapore)", status: "operational", latency: "65ms" },
    { name: "Asia Pacific (Tokyo)", status: "operational", latency: "58ms" }
  ];

  const incidents = [
    {
      date: "2024-12-15",
      title: "Increased API Latency - Resolved",
      status: "resolved",
      duration: "23 minutes",
      severity: "minor",
      description: "Brief increase in API response times due to database query optimization. Resolved by rolling back deployment.",
      timeline: [
        { time: "14:23 UTC", event: "Issue detected - API latency increased" },
        { time: "14:28 UTC", event: "Investigating - Team notified" },
        { time: "14:35 UTC", event: "Fix identified - Rolling back deployment" },
        { time: "14:46 UTC", event: "Resolved - Service restored to normal" }
      ]
    },
    {
      date: "2024-11-28",
      title: "Scheduled Maintenance - Completed",
      status: "completed",
      duration: "2 hours",
      severity: "maintenance",
      description: "Scheduled database maintenance and infrastructure updates.",
      timeline: [
        { time: "02:00 UTC", event: "Maintenance started" },
        { time: "03:30 UTC", event: "Database migration completed" },
        { time: "04:00 UTC", event: "Maintenance completed successfully" }
      ]
    }
  ];

  const upcomingMaintenance = [
    {
      date: "2025-02-01",
      time: "02:00-04:00 UTC",
      title: "Infrastructure Upgrade",
      impact: "Minimal - Read-only mode",
      description: "Upgrading database infrastructure for improved performance"
    }
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case "operational":
        return "text-green-500";
      case "degraded":
        return "text-amber-500";
      case "outage":
        return "text-red-500";
      default:
        return "text-muted-foreground";
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "operational":
        return <CheckCircle2 className="w-5 h-5 text-green-500" />;
      case "degraded":
        return <AlertTriangle className="w-5 h-5 text-amber-500" />;
      case "outage":
        return <XCircle className="w-5 h-5 text-red-500" />;
      default:
        return <Activity className="w-5 h-5" />;
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <Navigation />
      
      {/* Hero Section */}
      <section className="pt-24 pb-12 bg-gradient-to-br from-green-50 to-green-100 dark:from-green-950 dark:to-green-900">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl text-center">
            <div className="flex items-center justify-center gap-2 mb-4">
              <Activity className="w-6 h-6 text-green-600 dark:text-green-400" />
              <Badge className="bg-green-600 dark:bg-green-500">All Systems Operational</Badge>
            </div>
            <h1 className="text-4xl md:text-5xl font-bold mb-6">
              System Status
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Real-time status and uptime information for VeriGate services
            </p>
          </div>
        </div>
      </section>

      {/* Overall Stats */}
      <section className="py-8 border-b bg-background">
        <div className="container mx-auto">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 text-center max-w-4xl mx-auto">
            <div>
              <div className="text-3xl font-bold text-green-600 mb-1">99.97%</div>
              <div className="text-sm text-muted-foreground">30-Day Uptime</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-green-600 mb-1">99.95%</div>
              <div className="text-sm text-muted-foreground">90-Day Uptime</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">45ms</div>
              <div className="text-sm text-muted-foreground">Avg Response</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-primary mb-1">0</div>
              <div className="text-sm text-muted-foreground">Active Incidents</div>
            </div>
          </div>
        </div>
      </section>

      {/* Services Status */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">Service Status</h2>
            
            <div className="space-y-3">
              {services.map((service, index) => (
                <Card key={index}>
                  <CardContent className="p-6">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4 flex-1">
                        {getStatusIcon(service.status)}
                        <div className="flex-1">
                          <div className="font-semibold mb-1">{service.name}</div>
                          <div className="text-sm text-muted-foreground">{service.description}</div>
                        </div>
                      </div>
                      <div className="flex items-center gap-6 text-sm">
                        <div className="text-right">
                          <div className="text-muted-foreground mb-1">Uptime</div>
                          <div className="font-semibold">{service.uptime}%</div>
                        </div>
                        <div className="text-right">
                          <div className="text-muted-foreground mb-1">Latency</div>
                          <div className="font-semibold">{service.latency}</div>
                        </div>
                        <div className="w-24 text-right">
                          <Badge variant="outline" className="border-green-500 text-green-600">
                            Operational
                          </Badge>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Regional Status */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <div className="flex items-center gap-2 mb-8">
              <Globe className="w-6 h-6 text-primary" />
              <h2 className="text-3xl font-bold">Regional Status</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-4">
              {regions.map((region, index) => (
                <Card key={index}>
                  <CardContent className="p-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        {getStatusIcon(region.status)}
                        <span className="font-medium">{region.name}</span>
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {region.latency}
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Incident History */}
      <section className="py-16">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">Incident History</h2>
            
            <div className="space-y-6">
              {incidents.map((incident, index) => (
                <Card key={index}>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-3 mb-2">
                          <Badge variant={incident.status === "resolved" ? "outline" : "secondary"}>
                            {incident.status}
                          </Badge>
                          <Badge variant={incident.severity === "minor" ? "secondary" : "default"}>
                            {incident.severity}
                          </Badge>
                          <span className="text-sm text-muted-foreground">
                            {new Date(incident.date).toLocaleDateString('en-US', {
                              month: 'long',
                              day: 'numeric',
                              year: 'numeric'
                            })}
                          </span>
                        </div>
                        <CardTitle className="text-xl mb-2">{incident.title}</CardTitle>
                        <CardDescription>{incident.description}</CardDescription>
                      </div>
                      <div className="text-right">
                        <Clock className="w-4 h-4 inline mr-1" />
                        <span className="text-sm">{incident.duration}</span>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-2">
                      {incident.timeline.map((entry, idx) => (
                        <div key={idx} className="flex gap-3 text-sm">
                          <span className="text-muted-foreground font-mono w-20">{entry.time}</span>
                          <span>{entry.event}</span>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Upcoming Maintenance */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto">
          <div className="container mx-auto max-w-6xl">
            <h2 className="text-3xl font-bold mb-8">Scheduled Maintenance</h2>
            
            {upcomingMaintenance.length > 0 ? (
              <div className="space-y-4">
                {upcomingMaintenance.map((maintenance, index) => (
                  <Card key={index}>
                    <CardHeader>
                      <div className="flex items-start justify-between">
                        <div>
                          <div className="flex items-center gap-2 mb-2">
                            <Badge variant="outline">Scheduled</Badge>
                            <span className="text-sm text-muted-foreground">
                              {new Date(maintenance.date).toLocaleDateString('en-US', {
                                month: 'long',
                                day: 'numeric',
                                year: 'numeric'
                              })} • {maintenance.time}
                            </span>
                          </div>
                          <CardTitle className="text-xl mb-2">{maintenance.title}</CardTitle>
                          <CardDescription>{maintenance.description}</CardDescription>
                        </div>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="flex items-center gap-2 text-sm">
                        <span className="text-muted-foreground">Expected Impact:</span>
                        <span className="font-medium">{maintenance.impact}</span>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            ) : (
              <Card>
                <CardContent className="p-12 text-center">
                  <CheckCircle2 className="w-12 h-12 text-green-500 mx-auto mb-4" />
                  <p className="text-muted-foreground">No scheduled maintenance at this time</p>
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </section>

      {/* Subscribe */}
      <section className="py-16">
        <div className="container mx-auto">
          <Card className="max-w-3xl mx-auto bg-gradient-to-br from-primary to-primary/80 text-primary-foreground">
            <CardContent className="p-12 text-center">
              <h2 className="text-3xl font-bold mb-4">Get Status Updates</h2>
              <p className="text-lg mb-8 text-primary-foreground/90">
                Subscribe to get notified about incidents and scheduled maintenance
              </p>
              <Button size="lg" variant="secondary">
                Subscribe to Status Updates
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Status;
