"use client";

import {
  createContext,
  useCallback,
  useContext,
  useState,
  type ReactNode,
} from "react";

export interface ChatMessage {
  id: string;
  role: "user" | "assistant";
  text: string;
  timestamp: number;
}

interface ChatContextValue {
  isOpen: boolean;
  messages: ChatMessage[];
  toggleChat: () => void;
  openChat: () => void;
  closeChat: () => void;
  addMessage: (role: ChatMessage["role"], text: string) => void;
  clearMessages: () => void;
}

const ChatContext = createContext<ChatContextValue | undefined>(undefined);

const WELCOME_MESSAGE: ChatMessage = {
  id: "welcome",
  role: "assistant",
  text: "I'm your VeriGate assistant. I can summarise verifications, explain risk scores, draft compliance copy, and help you find anything in the portal. Try one of the prompts below, or ask me anything in plain English.",
  timestamp: Date.now(),
};

export function ChatProvider({ children }: { children: ReactNode }) {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([WELCOME_MESSAGE]);

  const toggleChat = useCallback(() => setIsOpen((prev) => !prev), []);
  const openChat = useCallback(() => setIsOpen(true), []);
  const closeChat = useCallback(() => setIsOpen(false), []);

  const addMessage = useCallback(
    (role: ChatMessage["role"], text: string) => {
      const msg: ChatMessage = {
        id: `${role}-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
        role,
        text,
        timestamp: Date.now(),
      };
      setMessages((prev) => [...prev, msg]);
    },
    [],
  );

  const clearMessages = useCallback(() => {
    setMessages([WELCOME_MESSAGE]);
  }, []);

  return (
    <ChatContext.Provider
      value={{
        isOpen,
        messages,
        toggleChat,
        openChat,
        closeChat,
        addMessage,
        clearMessages,
      }}
    >
      {children}
    </ChatContext.Provider>
  );
}

export function useChat(): ChatContextValue {
  const ctx = useContext(ChatContext);
  if (!ctx) throw new Error("useChat must be used within ChatProvider");
  return ctx;
}
