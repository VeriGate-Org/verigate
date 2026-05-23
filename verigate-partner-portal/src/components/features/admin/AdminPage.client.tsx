"use client";

import { useState, useMemo } from "react";
import { type ColumnDef } from "@tanstack/react-table";
import {
  Users,
  Shield,
  ScrollText,
  UserPlus,
  Pencil,
  UserX,
  Check,
  Minus,
} from "lucide-react";
import { cn } from "@/lib/cn";
import { useAuth } from "@/lib/auth/AuthProvider";
import {
  ROLE_PERMISSIONS,
  type Role,
  type Permission,
} from "@/lib/permissions";
import {
  PageHeader,
  Badge,
  Button,
  Card,
  CardHeader,
  CardBody,
  DataTable,
  Modal,
  Input,
  Select,
} from "@/components/ui";

/* ─── Types ──────────────────────────────────────────── */

type AdminTab = "users" | "roles" | "audit";

interface MockUser {
  id: string;
  name: string;
  email: string;
  role: Role;
  status: "active" | "inactive" | "invited";
  lastLogin: string;
}

interface AuditEvent {
  id: string;
  timestamp: string;
  actor: string;
  action: string;
  resource: string;
  ipAddress: string;
}

/* ─── Mock data ──────────────────────────────────────── */

const MOCK_USERS: MockUser[] = [
  {
    id: "u-1",
    name: "Arthur Manena",
    email: "arthur@verigate.co.za",
    role: "admin",
    status: "active",
    lastLogin: "2 min ago",
  },
  {
    id: "u-2",
    name: "Naledi Nkosi",
    email: "naledi@verigate.co.za",
    role: "admin",
    status: "active",
    lastLogin: "14 min ago",
  },
  {
    id: "u-3",
    name: "Sipho Dlamini",
    email: "sipho@verigate.co.za",
    role: "operator",
    status: "active",
    lastLogin: "1 hr ago",
  },
  {
    id: "u-4",
    name: "Lerato Mokoena",
    email: "lerato@verigate.co.za",
    role: "operator",
    status: "invited",
    lastLogin: "\u2014",
  },
  {
    id: "u-5",
    name: "Thandiwe Khumalo",
    email: "thandi@verigate.co.za",
    role: "viewer",
    status: "active",
    lastLogin: "Yesterday",
  },
  {
    id: "u-6",
    name: "Pieter v.d. Merwe",
    email: "pieter@verigate.co.za",
    role: "auditor",
    status: "inactive",
    lastLogin: "32 days ago",
  },
];

const MOCK_AUDIT: AuditEvent[] = [
  {
    id: "a-1",
    timestamp: "2026-05-20 09:42:18",
    actor: "arthur@verigate.co.za",
    action: "user.invite",
    resource: "lerato@verigate.co.za",
    ipAddress: "196.21.45.102",
  },
  {
    id: "a-2",
    timestamp: "2026-05-20 08:31:05",
    actor: "naledi@verigate.co.za",
    action: "policy.publish",
    resource: "KYC Standard v3",
    ipAddress: "105.186.12.44",
  },
  {
    id: "a-3",
    timestamp: "2026-05-19 16:22:41",
    actor: "sipho@verigate.co.za",
    action: "verification.create",
    resource: "VER-9f3a1b2c",
    ipAddress: "197.84.130.21",
  },
  {
    id: "a-4",
    timestamp: "2026-05-19 14:08:33",
    actor: "arthur@verigate.co.za",
    action: "user.role_change",
    resource: "thandi@verigate.co.za -> viewer",
    ipAddress: "196.21.45.102",
  },
  {
    id: "a-5",
    timestamp: "2026-05-19 11:55:12",
    actor: "naledi@verigate.co.za",
    action: "api_key.rotate",
    resource: "prod-key-***84f2",
    ipAddress: "105.186.12.44",
  },
  {
    id: "a-6",
    timestamp: "2026-05-18 17:44:09",
    actor: "arthur@verigate.co.za",
    action: "settings.update",
    resource: "webhook_url",
    ipAddress: "196.21.45.102",
  },
  {
    id: "a-7",
    timestamp: "2026-05-18 15:30:28",
    actor: "sipho@verigate.co.za",
    action: "verification.retry",
    resource: "VER-4d8e7f1a",
    ipAddress: "197.84.130.21",
  },
  {
    id: "a-8",
    timestamp: "2026-05-18 09:12:51",
    actor: "pieter@verigate.co.za",
    action: "report.export",
    resource: "Monthly compliance report",
    ipAddress: "41.0.22.184",
  },
  {
    id: "a-9",
    timestamp: "2026-05-17 14:28:07",
    actor: "naledi@verigate.co.za",
    action: "user.deactivate",
    resource: "pieter@verigate.co.za",
    ipAddress: "105.186.12.44",
  },
  {
    id: "a-10",
    timestamp: "2026-05-17 10:05:39",
    actor: "arthur@verigate.co.za",
    action: "policy.create",
    resource: "Enhanced Due Diligence v1",
    ipAddress: "196.21.45.102",
  },
];

/* ─── Column definitions ─────────────────────────────── */

const ROLE_BADGE: Record<Role, "info" | "success" | "neutral" | "warning"> = {
  admin: "info",
  operator: "success",
  viewer: "neutral",
  auditor: "warning",
};

const STATUS_BADGE: Record<
  MockUser["status"],
  "success" | "danger" | "pending"
> = {
  active: "success",
  inactive: "danger",
  invited: "pending",
};

const userColumns: ColumnDef<MockUser, unknown>[] = [
  {
    accessorKey: "name",
    header: "Name",
    cell: ({ row }) => (
      <div className="flex items-center gap-2.5">
        <div className="w-7 h-7 rounded-full bg-primary text-white text-[10px] font-semibold flex items-center justify-center shrink-0">
          {row.original.name
            .split(" ")
            .map((p) => p[0])
            .slice(0, 2)
            .join("")}
        </div>
        <div>
          <div className="font-medium text-text text-sm">
            {row.original.name}
          </div>
          <div className="text-xs text-text-muted">{row.original.email}</div>
        </div>
      </div>
    ),
  },
  {
    accessorKey: "role",
    header: "Role",
    cell: ({ row }) => (
      <Badge variant={ROLE_BADGE[row.original.role]} noGlyph>
        {row.original.role.charAt(0).toUpperCase() + row.original.role.slice(1)}
      </Badge>
    ),
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => (
      <Badge variant={STATUS_BADGE[row.original.status]}>
        {row.original.status.charAt(0).toUpperCase() +
          row.original.status.slice(1)}
      </Badge>
    ),
  },
  {
    accessorKey: "lastLogin",
    header: "Last Login",
    cell: ({ row }) => (
      <span className="text-sm text-text-muted">{row.original.lastLogin}</span>
    ),
  },
  {
    id: "actions",
    header: "Actions",
    enableSorting: false,
    cell: () => (
      <div className="flex items-center gap-1">
        <Button variant="ghost" size="sm" icon={<Pencil size={13} />}>
          Edit
        </Button>
        <Button variant="ghost" size="sm" icon={<UserX size={13} />}>
          Deactivate
        </Button>
      </div>
    ),
  },
];

const auditColumns: ColumnDef<AuditEvent, unknown>[] = [
  {
    accessorKey: "timestamp",
    header: "Timestamp",
    cell: ({ row }) => (
      <span className="text-xs font-mono text-text-muted">
        {row.original.timestamp}
      </span>
    ),
  },
  {
    accessorKey: "actor",
    header: "Actor",
    cell: ({ row }) => (
      <span className="text-sm text-text">{row.original.actor}</span>
    ),
  },
  {
    accessorKey: "action",
    header: "Action",
    cell: ({ row }) => (
      <Badge variant="neutral" noGlyph>
        {row.original.action}
      </Badge>
    ),
  },
  {
    accessorKey: "resource",
    header: "Resource",
    cell: ({ row }) => (
      <span className="text-sm text-text">{row.original.resource}</span>
    ),
  },
  {
    accessorKey: "ipAddress",
    header: "IP Address",
    cell: ({ row }) => (
      <span className="text-xs font-mono text-text-muted">
        {row.original.ipAddress}
      </span>
    ),
  },
];

/* ─── Tab components ─────────────────────────────────── */

function UsersTab() {
  const [inviteOpen, setInviteOpen] = useState(false);
  const [inviteEmail, setInviteEmail] = useState("");
  const [inviteRole, setInviteRole] = useState("operator");

  return (
    <>
      <div className="flex items-center justify-between mb-4">
        <p className="text-sm text-text-muted">
          {MOCK_USERS.length} members &middot;{" "}
          {MOCK_USERS.filter((u) => u.status === "active").length} active
        </p>
        <Button
          variant="cta"
          icon={<UserPlus size={14} />}
          onClick={() => setInviteOpen(true)}
        >
          Invite User
        </Button>
      </div>

      <Card>
        <CardBody compact>
          <DataTable data={MOCK_USERS} columns={userColumns} pageSize={10} />
        </CardBody>
      </Card>

      <Modal
        open={inviteOpen}
        onClose={() => setInviteOpen(false)}
        title="Invite User"
      >
        <div className="space-y-4 mt-3">
          <Input
            label="Email address"
            type="email"
            placeholder="name@company.co.za"
            value={inviteEmail}
            onChange={(e) => setInviteEmail(e.target.value)}
          />
          <Select
            label="Role"
            value={inviteRole}
            onChange={(e) => setInviteRole(e.target.value)}
            options={[
              { value: "admin", label: "Admin" },
              { value: "operator", label: "Operator" },
              { value: "viewer", label: "Viewer" },
              { value: "auditor", label: "Auditor" },
            ]}
          />
          <div className="flex justify-end gap-2 pt-2">
            <Button
              variant="secondary"
              onClick={() => setInviteOpen(false)}
            >
              Cancel
            </Button>
            <Button
              variant="cta"
              onClick={() => setInviteOpen(false)}
              disabled={!inviteEmail.trim()}
            >
              Send Invite
            </Button>
          </div>
        </div>
      </Modal>
    </>
  );
}

function RolesTab() {
  const roles: Role[] = ["admin", "operator", "viewer", "auditor"];
  const allPermissions: Permission[] = Array.from(
    new Set(Object.values(ROLE_PERMISSIONS).flat()),
  );

  return (
    <Card>
      <CardHeader>
        <div>
          <h3 className="text-sm font-semibold text-text">
            Permission Matrix
          </h3>
          <p className="text-xs text-text-muted mt-0.5">
            Read-only view of role-based permissions
          </p>
        </div>
      </CardHeader>
      <CardBody compact>
        <div className="overflow-x-auto">
          <table className="aws-table">
            <thead>
              <tr>
                <th className="min-w-[200px]">Permission</th>
                {roles.map((role) => (
                  <th key={role} className="text-center capitalize">
                    {role}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {allPermissions.map((perm) => (
                <tr key={perm}>
                  <td>
                    <span className="text-sm font-mono text-text">
                      {perm}
                    </span>
                  </td>
                  {roles.map((role) => (
                    <td key={role} className="text-center">
                      {ROLE_PERMISSIONS[role].includes(perm) ? (
                        <Check
                          size={14}
                          className="inline-block text-[#2C974B]"
                        />
                      ) : (
                        <Minus
                          size={14}
                          className="inline-block text-border"
                        />
                      )}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardBody>
    </Card>
  );
}

function AuditTab() {
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");

  const filteredAudit = useMemo(() => {
    return MOCK_AUDIT.filter((evt) => {
      if (dateFrom && evt.timestamp < dateFrom) return false;
      if (dateTo && evt.timestamp > dateTo + " 23:59:59") return false;
      return true;
    });
  }, [dateFrom, dateTo]);

  return (
    <>
      <div className="flex items-center gap-3 mb-4">
        <Input
          type="date"
          label="From"
          value={dateFrom}
          onChange={(e) => setDateFrom(e.target.value)}
        />
        <Input
          type="date"
          label="To"
          value={dateTo}
          onChange={(e) => setDateTo(e.target.value)}
        />
      </div>

      <Card>
        <CardBody compact>
          <DataTable
            data={filteredAudit}
            columns={auditColumns}
            pageSize={10}
          />
        </CardBody>
      </Card>
    </>
  );
}

/* ─── Tab definitions ────────────────────────────────── */

const TABS: { id: AdminTab; label: string; icon: React.ReactNode }[] = [
  { id: "users", label: "Users", icon: <Users size={14} /> },
  { id: "roles", label: "Roles", icon: <Shield size={14} /> },
  { id: "audit", label: "Audit Log", icon: <ScrollText size={14} /> },
];

/* ─── Main component ─────────────────────────────────── */

export function AdminPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<AdminTab>("users");

  // Access gate: only admins can view
  if (user?.role !== "admin") {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-center">
        <Shield size={48} className="text-text-muted mb-4" />
        <h2 className="text-lg font-semibold text-text">Access Denied</h2>
        <p className="text-sm text-text-muted mt-1 max-w-sm">
          You do not have permission to access the admin panel. Contact your
          organisation administrator to request access.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <PageHeader
        category="Admin"
        title="Administration"
        description="Manage users, roles, and review the audit trail for your organisation."
      />

      {/* Tab navigation */}
      <div className="flex gap-1 border-b border-border">
        {TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={cn(
              "flex items-center gap-1.5 px-4 py-2.5 text-sm font-medium transition-colors -mb-px border-b-2",
              activeTab === tab.id
                ? "border-accent text-accent"
                : "border-transparent text-text-muted hover:text-text hover:border-border",
            )}
          >
            {tab.icon}
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab content */}
      {activeTab === "users" && <UsersTab />}
      {activeTab === "roles" && <RolesTab />}
      {activeTab === "audit" && <AuditTab />}
    </div>
  );
}
