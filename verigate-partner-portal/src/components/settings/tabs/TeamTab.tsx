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
import { DataGrid, useDataGrid, StatusIndicator } from "@/components/ui/DataGrid";
import type { DataGridColumn } from "@/components/ui/DataGrid";

const VALID_ROLES = ["admin", "operator", "viewer", "auditor"] as const;

export default function TeamTab() {
  const { toast } = useToast();
  const [members, setMembers] = useState<BffTeamMember[]>([]);
  const [total, setTotal] = useState(0);
  const [maxAllowed, setMaxAllowed] = useState(0);
  const [loading, setLoading] = useState(true);
  const [inviteOpen, setInviteOpen] = useState(false);
  const [removeTarget, setRemoveTarget] = useState<BffTeamMember | null>(null);
  const { state, actions, processData } = useDataGrid();

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

  const columns: DataGridColumn<BffTeamMember>[] = [
    {
      id: "name",
      header: "Name",
      cell: (member) => (
        <span className="font-medium text-text">{member.name}</span>
      ),
    },
    {
      id: "email",
      header: "Email",
      cell: (member) => (
        <span className="text-text-muted">{member.email}</span>
      ),
    },
    {
      id: "role",
      header: "Role",
      cell: (member) => (
        <select
          value={member.role}
          onChange={(e) => handleRoleChange(member, e.target.value)}
          onClick={(e) => e.stopPropagation()}
          className="rounded border border-border bg-transparent px-2 py-0.5 text-xs text-text focus:outline-none focus:ring-1 focus:ring-accent"
        >
          {VALID_ROLES.map((role) => (
            <option key={role} value={role}>
              {role.charAt(0).toUpperCase() + role.slice(1)}
            </option>
          ))}
        </select>
      ),
    },
    {
      id: "status",
      header: "Status",
      cell: (member) => (
        <StatusIndicator
          status={member.status}
          label={
            member.status === "INVITED"
              ? "Invited"
              : member.status === "ACTIVE"
                ? "Active"
                : "Deactivated"
          }
        />
      ),
    },
    {
      id: "createdAt",
      header: "Joined",
      cell: (member) => (
        <span className="text-text-muted">{formatDateTime(member.createdAt)}</span>
      ),
    },
    {
      id: "actions",
      header: "Actions",
      align: "right",
      cell: (member) => (
        <div
          className="flex items-center justify-end gap-2"
          onClick={(e) => e.stopPropagation()}
        >
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
      ),
    },
  ];

  const { data, total: filteredTotal, totalPages } = processData(members, {
    searchFields: ["name", "email"],
  });

  const descriptionText = maxAllowed > 0
    ? `Manage who has access to this partner account. ${total} of ${maxAllowed} members`
    : "Manage who has access to this partner account.";

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

      <DataGrid
        columns={columns}
        data={data}
        getRowId={(member) => member.id}
        state={state}
        actions={actions}
        totalPages={totalPages}
        totalItems={filteredTotal}
        isLoading={loading}
        title="Team members"
        description={descriptionText}
        searchable
        searchPlaceholder="Search members..."
        toolbarActions={
          <button onClick={() => setInviteOpen(true)} className="aws-button aws-button--primary text-xs">
            Invite Member
          </button>
        }
        emptyTitle="No team members"
        emptyDescription="No team members yet. Invite your first team member to get started."
      />
    </div>
  );
}
