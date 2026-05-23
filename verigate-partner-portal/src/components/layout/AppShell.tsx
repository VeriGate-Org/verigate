"use client";

import { SidebarProvider } from "./SidebarContext";
import { Sidebar } from "./Sidebar";
import { TopBar } from "./TopBar";
import { ChatProvider } from "@/components/features/ai-chat/ChatContext";
import { ChatToggle } from "@/components/features/ai-chat/ChatToggle";
import { AiChatSidebar } from "@/components/features/ai-chat/AiChatSidebar.client";

export function AppShell({ children }: { children: React.ReactNode }) {
  return (
    <SidebarProvider>
      <ChatProvider>
        <div className="flex h-screen overflow-hidden">
          <Sidebar />
          <div className="flex flex-col flex-1 min-w-0">
            <TopBar />
            <main className="flex-1 overflow-y-auto p-6 bg-background">
              {children}
            </main>
          </div>
        </div>
        <ChatToggle />
        <AiChatSidebar />
      </ChatProvider>
    </SidebarProvider>
  );
}
