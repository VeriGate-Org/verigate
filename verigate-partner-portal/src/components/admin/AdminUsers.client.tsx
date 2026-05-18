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
  listAdminUsers,
  inviteAdminUser,
  deactivateAdminUser,
  reactivateAdminUser,
  removeAdminUser,
  type BffAdminUser,
} from "@/lib/bff-client";

const STATUS_BADGE_STYLES: Record<string, string> = {
  CONFIRMED: "bg-success/10 text-success",
  FORCE_CHANGE_PASSWORD: "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400",
  DEACTIVATED: "bg-[color:var(--color-base-200)] text-text-muted",
};

function statusLabel(status: string): string {
  if (status === "CONFIRMED") return "Active";
  if (status === "FORCE_CHANGE_PASSWORD") return "Invited";
  if (status === "DEACTIVATED") return "Deactivated";
  return status;
}

export default function AdminUsers() {
  const { toast } = useToast();
  const [users, setUsers] = useState<BffAdminUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [inviteOpen, setInviteOpen] = useState(false);
  const [removeTarget, setRemoveTarget] = useState<BffAdminUser | null>(null);

  // Invite form state
  const [inviteEmail, setInviteEmail] = useState("");
  const [inviteName, setInviteName] = useState("");
  const [inviting, setInviting] = useState(false);

  const loadUsers = useCallback(() => {
    setLoading(true);
    listAdminUsers()
      .then(setUsers)
      .catch(() => toast({ title: "Failed to load admin users", variant: "error" }))
      .finally(() => setLoading(false));
  }, [toast]);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  const handleInvite = useCallback(async () => {
    if (!inviteEmail.trim() || !inviteName.trim()) return;
    setInviting(true);
    try {
      await inviteAdminUser({ email: inviteEmail.trim(), name: inviteName.trim() });
      toast({ title: "Admin invited", description: `${inviteName} has been invited as a platform admin.`, variant: "success" });
      setInviteOpen(false);
      setInviteEmail("");
      setInviteName("");
      loadUsers();
    } catch (err) {
      toast({
        title: "Invitation failed",
        description: err instanceof Error ? err.message : "Could not send invitation.",
        variant: "error",
      });
    } finally {
      setInviting(false);
    }
  }, [inviteEmail, inviteName, loadUsers, toast]);

  const handleToggleStatus = useCallback(
    async (user: BffAdminUser) => {
      try {
        if (user.status === "DEACTIVATED") {
          await reactivateAdminUser(user.id);
          toast({ title: "Admin reactivated", variant: "success" });
        } else {
          await deactivateAdminUser(user.id);
          toast({ title: "Admin deactivated", variant: "success" });
        }
        loadUsers();
      } catch (err) {
        toast({
          title: "Action failed",
          description: err instanceof Error ? err.message : "Could not update admin status.",
          variant: "error",
        });
      }
    },
    [loadUsers, toast],
  );

  const handleRemove = useCallback(
    async (user: BffAdminUser) => {
      try {
        await removeAdminUser(user.id);
        toast({ title: "Admin removed", variant: "success" });
        loadUsers();
      } catch (err) {
        toast({
          title: "Removal failed",
          description: err instanceof Error ? err.message : "Could not remove admin.",
          variant: "error",
        });
      } finally {
        setRemoveTarget(null);
      }
    },
    [loadUsers, toast],
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-1">
        <h1 className="text-xl font-semibold text-text">User Management</h1>
        <p className="text-sm text-text-muted">
          Manage platform administrator accounts.
        </p>
      </div>

      {/* Remove confirmation dialog */}
      <ConfirmationDialog
        open={removeTarget !== null}
        onOpenChange={(open) => {
          if (!open) setRemoveTarget(null);
        }}
        title="Remove admin user"
        description={`Are you sure you want to remove ${removeTarget?.name ?? "this admin"}? This will permanently delete their account and revoke all platform access.`}
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
            <ModalTitle>Invite platform admin</ModalTitle>
            <ModalDescription>
              Send an invitation email with temporary login credentials. The new admin will have full platform access.
            </ModalDescription>
          </ModalHeader>

          <div className="space-y-4 py-2">
            <label className="block space-y-1 text-sm">
              <span className="font-medium text-text">Email address</span>
              <input
                type="email"
                value={inviteEmail}
                onChange={(e) => setInviteEmail(e.target.value)}
                placeholder="admin@verigate.co.za"
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
        <SkeletonTable rows={4} columns={5} />
      ) : (
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Platform administrators</div>
              <div className="text-xs text-text-muted">
                {users.length} admin{users.length !== 1 ? "s" : ""}
              </div>
            </div>
            <button onClick={() => setInviteOpen(true)} className="aws-button aws-button--primary text-xs">
              Invite Admin
            </button>
          </div>
          <div className="console-card-body p-0">
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-[color:var(--color-base-200)] text-xs uppercase tracking-wide text-text-muted">
                  <tr>
                    <th className="px-4 py-2.5">Name</th>
                    <th className="px-4 py-2.5">Email</th>
                    <th className="px-4 py-2.5">Status</th>
                    <th className="px-4 py-2.5">Created</th>
                    <th className="px-4 py-2.5 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="px-4 py-8 text-center text-xs text-text-muted">
                        No admin users found.
                      </td>
                    </tr>
                  ) : (
                    users.map((user) => (
                      <tr key={user.id} className="border-b border-border last:border-0">
                        <td className="px-4 py-2.5 font-medium text-text">{user.name || "—"}</td>
                        <td className="px-4 py-2.5 text-text-muted">{user.email}</td>
                        <td className="px-4 py-2.5">
                          <span
                            className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                              STATUS_BADGE_STYLES[user.status] ?? "bg-[color:var(--color-base-200)] text-text-muted"
                            }`}
                          >
                            {statusLabel(user.status)}
                          </span>
                        </td>
                        <td className="px-4 py-2.5 text-text-muted">
                          {formatDateTime(user.createdAt)}
                        </td>
                        <td className="px-4 py-2.5 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button
                              onClick={() => handleToggleStatus(user)}
                              className="aws-button aws-button--secondary text-xs"
                            >
                              {user.status === "DEACTIVATED" ? "Reactivate" : "Deactivate"}
                            </button>
                            <button
                              onClick={() => setRemoveTarget(user)}
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
