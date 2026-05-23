"use client";

import { useState, useRef, useEffect, useCallback } from "react";
import {
  X,
  Send,
  Zap,
  Briefcase,
  BarChart3,
  Shield,
  FileSearch,
} from "lucide-react";
import { cn } from "@/lib/cn";
import { useChat, type ChatMessage } from "./ChatContext";

/* ─── Demo reply picker ──────────────────────────────── */

const DEMO_REPLIES: Record<string, string> = {
  cases:
    "You have **3 open cases** needing review:\n\n- **CS-7b3a91e2** -- Jane Smith, credit fail (risk 87)\n- **CS-1d4b8f12** -- Acme Corp, sanctions partial match (risk 78)\n- **CS-5e9d3c47** -- Thandiwe Khumalo, suspected synthetic ID (risk 94)\n\nThe last one has been open 26 hours -- 6 hours from SLA escalation. Want me to draft an escalation note to compliance?",
  sla: "Across the last 24 hours:\n\n- **3 verifications** have been in 'In Progress' past their 30-minute SLA\n- All 3 are stuck on **DHA** responses (longest: 1h 12m)\n- DHA's average response time today is **42 minutes** vs target 30m\n\nProvider health shows DHA as **Watch**. Consider pausing high-volume KYC batches until response time recovers.",
  score:
    "Sanctions score is a 0-1 fuzzy match between the screened subject and entries in OpenSanctions. The score combines:\n\n- **Name similarity** (Jaro-Winkler, weighted 0.6)\n- **Date-of-birth proximity** (weighted 0.25)\n- **Nationality / jurisdiction match** (weighted 0.15)\n\nThe default threshold is **0.70** -- anything above triggers a match. You can tune this per check in *Advanced options*.",
  email:
    'Here\'s a POPIA-compliant rejection draft:\n\n> Dear [Subject],\n>\n> Following our verification process under the Protection of Personal Information Act (POPIA), we regret to inform you that we are unable to proceed with your application at this time.\n>\n> You have the right to request the basis for this decision and to dispute it under POPIA section 23. Please contact io@verigate.co.za within 30 days.\n>\n> Regards,\n> VeriGate Compliance Team\n\nWant me to attach a verification reference and send via the case?',
  default:
    "Here's what I can tell from the portal data: you've got 6 active verifications today (5 cleared, 1 in review), 9 open cases, and one provider (Umalusi) is degraded. Want me to dig into any of those?",
};

function pickReply(prompt: string): string {
  const p = prompt.toLowerCase();
  if (p.includes("case") || p.includes("summarise") || p.includes("summarize"))
    return DEMO_REPLIES.cases;
  if (p.includes("sla") || p.includes("stuck") || p.includes("failing"))
    return DEMO_REPLIES.sla;
  if (p.includes("score") || p.includes("sanction") || p.includes("explain"))
    return DEMO_REPLIES.score;
  if (p.includes("email") || p.includes("popia") || p.includes("draft"))
    return DEMO_REPLIES.email;
  return DEMO_REPLIES.default;
}

/* ─── Starter prompts ────────────────────────────────── */

const STARTER_PROMPTS = [
  { text: "Explain this verification", icon: FileSearch },
  { text: "Summarize open cases", icon: Briefcase },
  { text: "What checks are failing?", icon: BarChart3 },
  { text: "Help with sanctions screening", icon: Shield },
];

/* ─── Message bubble ─────────────────────────────────── */

function renderFormattedText(text: string, isUser: boolean) {
  return text.split("\n").map((line, i) => {
    if (line.startsWith("> ")) {
      return (
        <div
          key={i}
          className={cn(
            "border-l-2 border-accent pl-2 my-1 italic",
            isUser ? "text-white/80" : "text-text-muted",
          )}
        >
          {line.slice(2)}
        </div>
      );
    }
    if (line.startsWith("- ")) {
      return (
        <div key={i} className="pl-3 relative">
          <span className="absolute left-0 text-accent">&bull;</span>
          {renderInlineBold(line.slice(2), isUser)}
        </div>
      );
    }
    if (!line.trim()) {
      return <div key={i} className="h-1.5" />;
    }
    return <div key={i}>{renderInlineBold(line, isUser)}</div>;
  });
}

function renderInlineBold(text: string, isUser: boolean) {
  const parts = text.split(/(\*\*[^*]+\*\*)/g);
  return parts.map((p, i) => {
    if (p.startsWith("**") && p.endsWith("**")) {
      return (
        <strong key={i} className={isUser ? "text-white" : "text-text"}>
          {p.slice(2, -2)}
        </strong>
      );
    }
    return <span key={i}>{p}</span>;
  });
}

function MessageBubble({ msg }: { msg: ChatMessage }) {
  const isUser = msg.role === "user";

  return (
    <div
      className={cn(
        "flex gap-2 px-4 py-1.5",
        isUser ? "flex-row-reverse" : "flex-row",
      )}
    >
      <div
        className={cn(
          "w-7 h-7 rounded-full flex items-center justify-center shrink-0 text-[11px] font-semibold",
          isUser ? "bg-primary text-white" : "bg-accent/10 text-accent",
        )}
      >
        {isUser ? "U" : <Zap size={14} />}
      </div>
      <div
        className={cn(
          "max-w-[78%] rounded-lg px-3 py-2.5 text-[13px] leading-relaxed",
          isUser
            ? "bg-primary text-white"
            : "bg-[#F8FAFC] text-text border border-border",
        )}
      >
        {renderFormattedText(msg.text, isUser)}
      </div>
    </div>
  );
}

/* ─── Thinking indicator ─────────────────────────────── */

function ThinkingIndicator() {
  return (
    <div className="flex gap-2 px-4 py-1.5 items-start">
      <div className="w-7 h-7 rounded-full bg-accent/10 text-accent flex items-center justify-center shrink-0">
        <Zap size={14} />
      </div>
      <div className="bg-[#F8FAFC] border border-border rounded-lg px-3 py-2.5">
        <span className="inline-flex gap-1">
          <span className="w-1.5 h-1.5 rounded-full bg-accent animate-pulse" />
          <span
            className="w-1.5 h-1.5 rounded-full bg-accent animate-pulse"
            style={{ animationDelay: "200ms" }}
          />
          <span
            className="w-1.5 h-1.5 rounded-full bg-accent animate-pulse"
            style={{ animationDelay: "400ms" }}
          />
        </span>
      </div>
    </div>
  );
}

/* ─── Main sidebar ───────────────────────────────────── */

export function AiChatSidebar() {
  const { isOpen, messages, closeChat, addMessage } = useChat();
  const [input, setInput] = useState("");
  const [thinking, setThinking] = useState(false);
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages, thinking]);

  const send = useCallback(
    (text: string) => {
      if (!text.trim() || thinking) return;
      addMessage("user", text);
      setInput("");
      setThinking(true);
      setTimeout(() => {
        addMessage("assistant", pickReply(text));
        setThinking(false);
      }, 900);
    },
    [addMessage, thinking],
  );

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    send(input);
  };

  return (
    <div
      className={cn(
        "fixed top-0 right-0 bottom-0 w-[400px] max-w-[100vw] bg-surface border-l border-border shadow-[-12px_0_28px_rgba(0,28,36,0.10)] flex flex-col z-50 transition-transform duration-200 ease-out",
        isOpen ? "translate-x-0" : "translate-x-full",
      )}
    >
      {/* Tri-color top bar */}
      <div className="flex h-[3px] shrink-0">
        <div className="flex-[3] bg-[#E23D36]" />
        <div className="flex-[5] bg-primary" />
        <div className="flex-[2] bg-accent" />
      </div>

      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 border-b border-border shrink-0">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-lg bg-accent/10 flex items-center justify-center">
            <Zap size={16} className="text-accent" />
          </div>
          <div>
            <h2 className="text-sm font-semibold text-text">
              VeriGate AI Assistant
            </h2>
            <div className="flex items-center gap-1.5 text-[10px] text-text-muted">
              <span className="w-1.5 h-1.5 rounded-full bg-[#2C974B] shadow-[0_0_6px_#2C974B]" />
              Online &middot; context: this portal
            </div>
          </div>
        </div>
        <button
          onClick={closeChat}
          className="rounded p-1 text-text-muted hover:text-text hover:bg-background transition-colors"
          aria-label="Close chat"
        >
          <X size={18} />
        </button>
      </div>

      {/* Message area */}
      <div ref={scrollRef} className="flex-1 overflow-y-auto py-3">
        {messages.map((msg) => (
          <MessageBubble key={msg.id} msg={msg} />
        ))}
        {thinking && <ThinkingIndicator />}
      </div>

      {/* Starter prompts (only show when few messages) */}
      {messages.length <= 2 && !thinking && (
        <div className="px-4 pb-3 shrink-0">
          <p className="text-[10px] font-semibold text-text-muted uppercase tracking-wide mb-2">
            Try asking
          </p>
          <div className="grid grid-cols-2 gap-2">
            {STARTER_PROMPTS.map((prompt) => (
              <button
                key={prompt.text}
                onClick={() => send(prompt.text)}
                className="flex items-center gap-2 px-2.5 py-2 text-left text-xs text-text bg-[#F8FAFC] border border-border rounded-md hover:bg-white transition-colors"
              >
                <prompt.icon size={12} className="text-accent shrink-0" />
                {prompt.text}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Input area */}
      <div className="px-4 py-3 border-t border-border shrink-0">
        <form onSubmit={handleSubmit} className="flex gap-2">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Ask anything about your data..."
            className="aws-input flex-1 text-[13px]"
          />
          <button
            type="submit"
            disabled={!input.trim() || thinking}
            className={cn(
              "aws-button aws-button--cta py-[7px] px-3 text-[13px]",
              "disabled:opacity-50 disabled:cursor-not-allowed",
            )}
          >
            <Send size={14} />
          </button>
        </form>
        <p className="text-[10px] text-text-muted mt-2 text-center">
          VeriGate AI &middot; context-aware &middot; data stays in your tenant
        </p>
      </div>
    </div>
  );
}
