import { useState } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { CodeBlock } from "@/components/CodeBlock";

interface CodeExample {
  language: string;
  label: string;
  code: string;
  filename?: string;
}

interface MultiLanguageCodeProps {
  examples: CodeExample[];
  title?: string;
}

export const MultiLanguageCode = ({ examples, title }: MultiLanguageCodeProps) => {
  const [activeTab, setActiveTab] = useState(examples[0]?.language || "javascript");

  return (
    <div className="my-6">
      {title && <h4 className="font-semibold mb-3">{title}</h4>}
      
      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="w-full justify-start overflow-x-auto flex-wrap h-auto">
          {examples.map((example) => (
            <TabsTrigger 
              key={example.language} 
              value={example.language}
              className="font-mono text-xs"
            >
              {example.label}
            </TabsTrigger>
          ))}
        </TabsList>

        {examples.map((example) => (
          <TabsContent key={example.language} value={example.language} className="mt-0">
            <CodeBlock 
              code={example.code} 
              language={example.language}
              filename={example.filename}
            />
          </TabsContent>
        ))}
      </Tabs>
    </div>
  );
};
