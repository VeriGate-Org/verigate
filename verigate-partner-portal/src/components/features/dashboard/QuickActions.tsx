"use client";

import { useRouter } from "next/navigation";
import {
  Plus,
  FileSearch,
  Briefcase,
  Shield,
  BarChart3,
  Settings,
} from "lucide-react";
import { Button } from "@/components/ui/Button";

const ACTIONS = [
  { label: "New Verification", icon: Plus, href: "/verifications", variant: "cta" as const },
  { label: "View Verifications", icon: FileSearch, href: "/verifications", variant: "secondary" as const },
  { label: "Open Cases", icon: Briefcase, href: "/cases", variant: "secondary" as const },
  { label: "Sanctions Check", icon: Shield, href: "/services/sanctions", variant: "secondary" as const },
  { label: "Reports", icon: BarChart3, href: "/reports", variant: "secondary" as const },
  { label: "Settings", icon: Settings, href: "/settings", variant: "secondary" as const },
];

export function QuickActions() {
  const router = useRouter();

  return (
    <div className="console-card">
      <div className="console-card-header">
        <span className="text-sm font-semibold text-text">Quick actions</span>
      </div>
      <div className="console-card-body">
        <div className="flex flex-wrap gap-2">
          {ACTIONS.map((action) => (
            <Button
              key={action.label}
              variant={action.variant}
              icon={<action.icon size={13} />}
              onClick={() => router.push(action.href)}
            >
              {action.label}
            </Button>
          ))}
        </div>
      </div>
    </div>
  );
}
