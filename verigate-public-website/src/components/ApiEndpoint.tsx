import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { CodeBlock } from "@/components/CodeBlock";

interface ApiEndpointProps {
  method: "GET" | "POST" | "PUT" | "DELETE" | "PATCH";
  endpoint: string;
  description: string;
  parameters?: Array<{
    name: string;
    type: string;
    required: boolean;
    description: string;
  }>;
  requestExample?: string;
  responseExample?: string;
  language?: string;
}

const methodColors = {
  GET: "bg-green-500/10 text-green-500 border-green-500/20",
  POST: "bg-blue-500/10 text-blue-500 border-blue-500/20",
  PUT: "bg-orange-500/10 text-orange-500 border-orange-500/20",
  DELETE: "bg-red-500/10 text-red-500 border-red-500/20",
  PATCH: "bg-purple-500/10 text-purple-500 border-purple-500/20",
};

export const ApiEndpoint = ({
  method,
  endpoint,
  description,
  parameters,
  requestExample,
  responseExample,
  language = "javascript"
}: ApiEndpointProps) => {
  return (
    <Card className="mb-8">
      <CardHeader>
        <div className="flex items-center gap-3 mb-2">
          <Badge className={`${methodColors[method]} font-mono font-bold`}>
            {method}
          </Badge>
          <code className="text-sm bg-gray-100 dark:bg-gray-800 px-3 py-1 rounded font-mono">
            {endpoint}
          </code>
        </div>
        <CardDescription>{description}</CardDescription>
      </CardHeader>

      <CardContent className="space-y-6">
        {/* Parameters */}
        {parameters && parameters.length > 0 && (
          <div>
            <h4 className="font-semibold mb-3">Parameters</h4>
            <div className="border rounded-lg divide-y dark:border-gray-700">
              {parameters.map((param, index) => (
                <div key={index} className="p-3 grid grid-cols-12 gap-4 items-start text-sm">
                  <div className="col-span-3">
                    <code className="font-mono text-primary">{param.name}</code>
                    {param.required && (
                      <Badge variant="destructive" className="ml-2 text-xs">required</Badge>
                    )}
                  </div>
                  <div className="col-span-2">
                    <code className="text-xs bg-gray-100 dark:bg-gray-800 px-2 py-1 rounded">
                      {param.type}
                    </code>
                  </div>
                  <div className="col-span-7 text-muted-foreground">
                    {param.description}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Request Example */}
        {requestExample && (
          <div>
            <h4 className="font-semibold mb-2">Request Example</h4>
            <CodeBlock code={requestExample} language={language} />
          </div>
        )}

        {/* Response Example */}
        {responseExample && (
          <div>
            <h4 className="font-semibold mb-2">Response Example</h4>
            <CodeBlock code={responseExample} language="json" />
          </div>
        )}
      </CardContent>
    </Card>
  );
};
