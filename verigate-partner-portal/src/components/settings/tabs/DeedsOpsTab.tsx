"use client";

import { useCallback, useEffect, useState } from "react";
import { useToast } from "@/components/ui/Toast";
import { SkeletonTable } from "@/components/ui/Loading/Skeleton";
import { formatDateTime } from "@/lib/utils/date";
import {
  createDeedsTeamMember,
  deleteDeedsTeamMember,
  listDeedsAuditEvents,
  listDeedsTeamMembers,
  runDeedsRefreshCycle,
  updateDeedsTeamMember,
  type BffDeedsAuditEventResponse,
  type BffDeedsTeamMemberResponse,
} from "@/lib/bff-client";

export default function DeedsOpsTab() {
  const { toast } = useToast();
  const [team, setTeam] = useState<BffDeedsTeamMemberResponse[]>([]);
  const [audit, setAudit] = useState<BffDeedsAuditEventResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [memberName, setMemberName] = useState("");
  const [memberEmail, setMemberEmail] = useState("");
  const [memberRole, setMemberRole] = useState("deeds_operator");
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(() => {
    setLoading(true);
    Promise.all([listDeedsTeamMembers(), listDeedsAuditEvents()])
      .then(([teamData, auditData]) => {
        setTeam(teamData);
        setAudit(auditData);
      })
      .catch(() => toast({ title: "Failed to load deeds operations data", variant: "error" }))
      .finally(() => setLoading(false));
  }, [toast]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleCreateMember = useCallback(async () => {
    if (!memberName.trim() || !memberEmail.trim()) return;
    try {
      await createDeedsTeamMember({
        name: memberName,
        email: memberEmail,
        role: memberRole,
        status: "ACTIVE",
      });
      setMemberName("");
      setMemberEmail("");
      setMemberRole("deeds_operator");
      load();
      toast({ title: "Team member added", variant: "success" });
    } catch (err) {
      toast({ title: "Add member failed", description: err instanceof Error ? err.message : "Could not add member.", variant: "error" });
    }
  }, [load, memberEmail, memberName, memberRole, toast]);

  const toggleMemberStatus = useCallback(async (member: BffDeedsTeamMemberResponse) => {
    try {
      await updateDeedsTeamMember(member.id, {
        status: member.status === "ACTIVE" ? "PAUSED" : "ACTIVE",
      });
      load();
    } catch (err) {
      toast({ title: "Update failed", description: err instanceof Error ? err.message : "Could not update member.", variant: "error" });
    }
  }, [load, toast]);

  const handleDeleteMember = useCallback(async (memberId: string) => {
    try {
      await deleteDeedsTeamMember(memberId);
      load();
    } catch (err) {
      toast({ title: "Delete failed", description: err instanceof Error ? err.message : "Could not remove member.", variant: "error" });
    }
  }, [load, toast]);

  const handleRefresh = useCallback(async () => {
    setRefreshing(true);
    try {
      const result = await runDeedsRefreshCycle();
      toast({
        title: "Refresh cycle complete",
        description: `${result.refreshedReports} reports refreshed, ${result.recalculatedWatches} watches recalculated.`,
        variant: "success",
      });
      load();
    } catch (err) {
      toast({ title: "Refresh failed", description: err instanceof Error ? err.message : "Could not run refresh cycle.", variant: "error" });
    } finally {
      setRefreshing(false);
    }
  }, [load, toast]);

  if (loading) {
    return <SkeletonTable rows={3} columns={4} />;
  }

  return (
    <div className="space-y-6">
      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">Deeds refresh jobs</div>
            <div className="text-xs text-text-muted">
              Trigger the provider-independent refresh cycle for saved reports and cached watches.
            </div>
          </div>
          <button onClick={handleRefresh} className="aws-button aws-button--primary text-xs" disabled={refreshing}>
            {refreshing ? "Running…" : "Run refresh cycle"}
          </button>
        </div>
      </div>

      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">Deeds team</div>
            <div className="text-xs text-text-muted">Manage delegated deeds admins, operators, and viewers.</div>
          </div>
        </div>
        <div className="console-card-body space-y-4">
          <div className="grid gap-3 md:grid-cols-4">
            <input className="aws-input" placeholder="Name" value={memberName} onChange={(e) => setMemberName(e.target.value)} />
            <input className="aws-input" placeholder="Email" value={memberEmail} onChange={(e) => setMemberEmail(e.target.value)} />
            <select className="aws-select" value={memberRole} onChange={(e) => setMemberRole(e.target.value)}>
              <option value="deeds_admin">Deeds admin</option>
              <option value="deeds_operator">Deeds operator</option>
              <option value="deeds_viewer">Deeds viewer</option>
            </select>
            <button onClick={handleCreateMember} className="aws-button aws-button--primary text-xs">
              Add member
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full text-left text-sm">
              <thead className="bg-[color:var(--color-base-200)] text-xs uppercase tracking-wide text-text-muted">
                <tr>
                  <th className="px-4 py-2.5">Name</th>
                  <th className="px-4 py-2.5">Role</th>
                  <th className="px-4 py-2.5">Status</th>
                  <th className="px-4 py-2.5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {team.length === 0 ? (
                  <tr><td colSpan={4} className="px-4 py-8 text-center text-xs text-text-muted">No delegated deeds users configured.</td></tr>
                ) : (
                  team.map((member) => (
                    <tr key={member.id} className="border-b border-border last:border-0">
                      <td className="px-4 py-2.5">
                        <div className="font-medium text-text">{member.name}</div>
                        <div className="text-xs text-text-muted">{member.email}</div>
                      </td>
                      <td className="px-4 py-2.5 text-text-muted">{member.role}</td>
                      <td className="px-4 py-2.5 text-text-muted">{member.status}</td>
                      <td className="px-4 py-2.5 text-right space-x-2">
                        <button onClick={() => toggleMemberStatus(member)} className="aws-button aws-button--secondary text-xs">
                          {member.status === "ACTIVE" ? "Pause" : "Resume"}
                        </button>
                        <button onClick={() => handleDeleteMember(member.id)} className="aws-button aws-button--destructive text-xs">
                          Remove
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div className="console-card">
        <div className="console-card-header">
          <div>
            <div className="text-sm font-semibold text-text">Deeds audit log</div>
            <div className="text-xs text-text-muted">Recent provider-independent deeds operations and admin actions.</div>
          </div>
        </div>
        <div className="console-card-body space-y-3">
          {audit.length === 0 ? (
            <div className="text-sm text-text-muted">No deeds audit events recorded yet.</div>
          ) : (
            audit.slice(0, 12).map((event) => (
              <div key={event.id} className="rounded border border-border bg-[color:var(--color-base-200)] p-3">
                <div className="flex flex-wrap items-start justify-between gap-2">
                  <div>
                    <div className="text-sm font-medium text-text">{event.action}</div>
                    <div className="text-xs text-text-muted">{event.category} • {event.actor}</div>
                  </div>
                  <div className="text-xs text-text-muted">{formatDateTime(event.createdAt)}</div>
                </div>
                {event.targetId && (
                  <div className="mt-2 text-xs text-text-muted">
                    {event.targetType}: {event.targetId}
                  </div>
                )}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
