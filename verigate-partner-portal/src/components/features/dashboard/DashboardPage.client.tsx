"use client";

import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth/AuthProvider";
import { PageHeader } from "@/components/ui/PageHeader";
import { Button } from "@/components/ui/Button";
import { Download, Plus } from "lucide-react";
import { KpiStrip } from "./KpiStrip";
import { AttentionPanel } from "./AttentionPanel";
import { ActivityFeed } from "./ActivityFeed";
import { QuickActions } from "./QuickActions";

export function DashboardPage() {
  const router = useRouter();
  const { user } = useAuth();
  const displayName = user?.email?.split("@")[0] || "Partner";

  return (
    <div className="space-y-aws-l">
      <PageHeader
        category="Dashboard"
        title={`Welcome back, ${displayName}`}
        description="Here's what's happened across your verifications today."
        actions={
          <>
            <Button
              variant="secondary"
              icon={<Download size={13} />}
            >
              Export today
            </Button>
            <Button
              variant="cta"
              icon={<Plus size={13} />}
              onClick={() => router.push("/verifications")}
            >
              New verification
            </Button>
          </>
        }
      />

      <KpiStrip />

      <div className="grid grid-cols-1 lg:grid-cols-[1.4fr_1fr] gap-4">
        <AttentionPanel onViewAll={() => router.push("/verifications")} />
        <ActivityFeed />
      </div>

      <QuickActions />
    </div>
  );
}
