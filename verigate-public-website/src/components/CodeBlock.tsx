import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Check, Copy } from "lucide-react";

interface CodeBlockProps {
  code: string;
  language?: string;
  filename?: string;
  showLineNumbers?: boolean;
}

export const CodeBlock = ({ code, language = "javascript", filename, showLineNumbers = true }: CodeBlockProps) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    await navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="relative rounded-lg overflow-hidden border border-gray-200 dark:border-gray-700 bg-gray-900 my-4">
      {/* Header */}
      {filename && (
        <div className="px-4 py-2 bg-gray-800 border-b border-gray-700 flex items-center justify-between">
          <span className="text-sm text-gray-400 font-mono">{filename}</span>
          <span className="text-xs text-gray-500 uppercase">{language}</span>
        </div>
      )}
      
      {/* Code Content */}
      <div className="relative">
        <Button
          size="sm"
          variant="ghost"
          className="absolute top-2 right-2 h-8 px-2 text-gray-400 hover:text-white hover:bg-gray-800"
          onClick={handleCopy}
        >
          {copied ? (
            <>
              <Check className="h-4 w-4 mr-1" />
              Copied!
            </>
          ) : (
            <>
              <Copy className="h-4 w-4 mr-1" />
              Copy
            </>
          )}
        </Button>
        
        <pre className="p-4 overflow-x-auto">
          <code className="text-sm text-gray-100 font-mono leading-relaxed">
            {code}
          </code>
        </pre>
      </div>
    </div>
  );
};
