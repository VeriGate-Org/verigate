"use client";

import { MessageSquare } from "lucide-react";
import { useChat } from "./ChatContext";

export function ChatToggle() {
  const { toggleChat, isOpen } = useChat();

  if (isOpen) return null;

  return (
    <button
      onClick={toggleChat}
      className="fixed bottom-6 right-6 z-40 w-12 h-12 rounded-full bg-accent text-white flex items-center justify-center shadow-[0_8px_24px_rgba(0,28,36,0.25)] hover:shadow-[0_12px_28px_rgba(0,28,36,0.35),0_0_24px_rgba(0,179,217,0.4)] hover:-translate-y-0.5 transition-all duration-200"
      aria-label="Open AI Chat"
    >
      <MessageSquare size={20} />
    </button>
  );
}
