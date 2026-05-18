"use client";

import { useCallback, useEffect, useState } from "react";
import { useToast } from "@/components/ui/Toast";
import { ConfirmationDialog } from "@/components/ui/Modal/Modal";
import {
  Modal,
  ModalContent,
  ModalHeader,
  ModalTitle,
  ModalDescription,
  ModalFooter,
} from "@/components/ui/Modal/Modal";
import { SkeletonTable } from "@/components/ui/Loading/Skeleton";
import { formatDateTime } from "@/lib/utils/date";
import {
  listTeamMembers,
  inviteTeamMember,
  updateTeamMemberRole,
  deactivateTeamMember,
  reactivateTeamMember,
  removeTeamMember,
  type BffTeamMember,
} from "@/lib/bff-client";

const VALID_ROLES = ["admin", "operator", "viewer", "auditor"] as const;

const ROLE_BADGE_STYLES: Record<string, string> = {
  admin: "bg-accent/10 text-accent",
  operator: "bg-[color:var(--color-base-200)] text-text",
  viewer: "bg-[color:var(--color-base-200)] text-text-muted",
  auditor: "bg-[color:var(--color-base-200)] text-text-muted",
};

const STATUS_BADGE_STYLES: Record<string, string> = {
  INVITED: "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400",
  ACTIVE: "bg-success/10 text-success",
  DEACTIVATED: "bg-[color:var(--color-base-200)] text-text-muted",
};

export default function TeamTab() {
  const { toast } = useToast();
  const [members, setMembers] = useState<BffTeamMember[]>([]);
  const [total, setTotal] = useState(0);
  const [maxAllowed, setMaxAllowed] = useState(0);
  const [loading, setLoading] = useState(true);
  const [inviteOpen, setInviteOpen] = useState(false);
  const [removeTarget, setRemoveTarget] = useState<BffTeamMember | null>(null);

  // Invite form state
  const [inviteEmail, setInviteEmail] = useState("");
  const [inviteName, setInviteName] = useState("");
  const [inviteRole, setInviteRole] = useState("viewer");
  const [inviting, setInviting] = useState(false);

  const loadMembers = useCallback(() => {
    setLoading(true);
    listTeamMembers()
      .then((res) => {
        setMembers(res.members);
        setTotal(res.total);
        setMaxAllowed(res.maxAllowed);
      })
      .catch(() => toast({ title: "Failed to load team members", variant: "error" }))
      .finally(() => setLoading(false));
  }, [toast]);

  useEffect(() => {
    loadMembers();
  }, [loadMembers]);

  const handleInvite = useCallback(async () => {
    if (!inviteEmail.trim() || !inviteName.trim()) return;
    setInviting(true);
    try {
      await inviteTeamMember({ email: inviteEmail.trim(), name: inviteName.trim(), role: inviteRole });
      toast({ title: "Invitation sent", description: `${inviteName} has been invited as ${inviteRole}.`, variant: "success" });
      setInviteOpen(false);
      setInviteEmail("");
      setInviteName("");
      setInviteRole("viewer");
      loadMembers();
    } catch (err) {
      toast({
        title: "Invitation failed",
        description: err instanceof Error ? err.message : "Could not send invitation.",
        variant: "error",
      });
    } finally {
      setInviting(false);
    }
  }, [inviteEmail, inviteName, inviteRole, loadMembers, toast]);

  const handleRoleChange = useCallback(
    async (member: BffTeamMember, newRole: string) => {
      try {
        await updateTeamMemberRole(member.id, newRole);
        toast({ title: "Role updated", description: `${member.name} is now ${newRole}.`, variant: "success" });
        loadMembers();
      } catch (err) {
        toast({
          title: "Failed to update role",
          description: err instanceof Error ? err.message : "Could not update role.",
          variant: "error",
        });
      }
    },
    [loadMembers, toast],
  );

  const handleToggleStatus = useCallback(
    async (member: BffTeamMember) => {
      try {
        if (member.status === "DEACTIVATED") {
          await reactivateTeamMember(member.id);
          toast({ title: "Member reactivated", variant: "success" });
        } else {
          await deactivateTeamMember(member.id);
          toast({ title: "Member deactivated", variant: "success" });
        }
        loadMembers();
      } catch (err) {
        toast({
          title: "Action failed",
          description: err instanceof Error ? err.message : "Could not update member status.",
          variant: "error",
        });
      }
    },
    [loadMembers, toast],
  );

  const handleRemove = useCallback(
    async (member: BffTeamMember) => {
      try {
        await removeTeamMember(member.id);
        toast({ title: "Member removed", variant: "success" });
        loadMembers();
      } catch (err) {
        toast({
          title: "Removal failed",
          description: err instanceof Error ? err.message : "Could not remove member.",
          variant: "error",
        });
      } finally {
        setRemoveTarget(null);
      }
    },
    [loadMembers, toast],
  );

  return (
    <div className="space-y-4">
      {/* Remove confirmation dialog */}
      <ConfirmationDialog
        open={removeTarget !== null}
        onOpenChange={(open) => {
          if (!open) setRemoveTarget(null);
        }}
        title="Remove team member"
        description={`Are you sure you want to remove ${removeTarget?.name ?? "this member"}? This will permanently delete their account and revoke all access.`}
        confirmText="Remove"
        cancelText="Cancel"
        variant="destructive"
        onConfirm={() => {
          if (removeTarget) handleRemove(removeTarget);
        }}
      />

      {/* Invite dialog */}
      <Modal open={inviteOpen} onOpenChange={setInviteOpen}>
        <ModalContent>
          <ModalHeader>
            <ModalTitle>Invite team member</ModalTitle>
            <ModalDescription>
              Send an invitation email with temporary login credentials.
            </ModalDescription>
          </ModalHeader>

          <div className="space-y-4 py-2">
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Email address</span>
              <input
                type="email"
                value={inviteEmail}
                onChange={(e) => setInviteEmail(e.target.value)}
                placeholder="colleague@company.com"
                className="aws-input w-full"
              />
            </label>
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Full name</span>
              <input
                type="text"
                value={inviteName}
                onChange={(e) => setInviteName(e.target.value)}
                placeholder="Jane Doe"
                className="aws-input w-full"
              />
            </label>
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Role</span>
              <select
                value={inviteRole}
                onChange={(e) => setInviteRole(e.target.value)}
                className="aws-input w-full"
              >
                {VALID_ROLES.map((role) => (
                  <option key={role} value={role}>
                    {role.charAt(0).toUpperCase() + role.slice(1)}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <ModalFooter>
            <button
              onClick={() => setInviteOpen(false)}
              className="aws-button aws-button--secondary px-4 py-2"
              disabled={inviting}
            >
              Cancel
            </button>
            <button
              onClick={handleInvite}
              disabled={inviting || !inviteEmail.trim() || !inviteName.trim()}
              className="aws-button aws-button--primary px-4 py-2"
            >
              {inviting ? "Sending..." : "Send Invite"}
            </button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      {loading ? (
        <SkeletonTable rows={4} columns={6} />
      ) : (
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Team members</div>
              <div className="text-xs text-text-muted">
                Manage who has access to this partner account.
                {maxAllowed > 0 && (
                  <span className="ml-2 font-medium">
                    {total} of {maxAllowed} members
                  </span>
                )}
              </div>
            </div>
            <button onClick={() => setInviteOpen(true)} className="aws-button aws-button--primary text-xs">
              Invite Member
            </button>
          </div>
          <div className="console-card-body p-0">
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-[color:var(--color-base-200)] text-xs uppercase tracking-wide text-text-muted">
                  <tr>
                    <th className="px-4 py-2.5">Name</th>
                    <th className="px-4 py-2.5">Email</th>
                    <th className="px-4 py-2.5">Role</th>
                    <th className="px-4 py-2.5">Status</th>
                    <th className="px-4 py-2.5">Joined</th>
                    <th className="px-4 py-2.5 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {members.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="px-4 py-8 text-center text-xs text-text-muted">
                        No team members yet. Invite your first team member to get started.
                      </td>
                    </tr>
                  ) : (
                    members.map((member) => (
                      <tr key={member.id} className="border-b border-border last:border-0">
                        <td className="px-4 py-2.5 font-medium text-text">{member.name}</td>
                        <td className="px-4 py-2.5 text-text-muted">{member.email}</td>
                        <td className="px-4 py-2.5">
                          <select
                            value={member.role}
                            onChange={(e) => handleRoleChange(member, e.target.value)}
                            className="rounded border border-border bg-transparent px-2 py-0.5 text-xs text-text focus:outline-none focus:ring-1 focus:ring-accent"
                          >
                            {VALID_ROLES.map((role) => (
                              <option key={role} value={role}>
                                {role.charAt(0).toUpperCase() + role.slice(1)}
                              </option>
                            ))}
                          </select>
                        </td>
                        <td className="px-4 py-2.5">
                          <span
                            className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                              STATUS_BADGE_STYLES[member.status] ?? STATUS_BADGE_STYLES.INVITED
                            }`}
                          >
                            {member.status === "INVITED"
                              ? "Invited"
                              : member.status === "ACTIVE"
                                ? "Active"
                                : "Deactivated"}
                          </span>
                        </td>
                        <td className="px-4 py-2.5 text-text-muted">
                          {formatDateTime(member.createdAt)}
                        </td>
                        <td className="px-4 py-2.5 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button
                              onClick={() => handleToggleStatus(member)}
                              className="aws-button aws-button--secondary text-xs"
                            >
                              {member.status === "DEACTIVATED" ? "Reactivate" : "Deactivate"}
                            </button>
                            <button
                              onClick={() => setRemoveTarget(member)}
                              className="aws-button aws-button--destructive text-xs"
                            >
                              Remove
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
