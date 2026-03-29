// Permission and role management system

export type Permission = 
  | "view_dashboard"
  | "view_verifications"
  | "create_verifications"
  | "delete_verifications"
  | "view_services"
  | "configure_services"
  | "view_policies"
  | "edit_policies"
  | "publish_policies"
  | "view_adapters"
  | "configure_adapters"
  | "view_audit"
  | "view_settings"
  | "manage_settings"
  | "manage_users"
  | "view_reports"
  | "create_reports"
  | "view_deeds_map"
  | "manage_deeds_reports"
  | "manage_deeds_watches"
  | "manage_deeds_exports"
  | "manage_deeds_users"
  | "use_deeds_conversion"
  | "use_deeds_valuation";

export type Role = "admin" | "operator" | "viewer" | "auditor";

export const ROLE_PERMISSIONS: Record<Role, Permission[]> = {
  admin: [
    "view_dashboard",
    "view_verifications",
    "create_verifications",
    "delete_verifications",
    "view_services",
    "configure_services",
    "view_policies",
    "edit_policies",
    "publish_policies",
    "view_adapters",
    "configure_adapters",
    "view_audit",
    "view_settings",
    "manage_settings",
    "manage_users",
    "view_reports",
    "create_reports",
    "view_deeds_map",
    "manage_deeds_reports",
    "manage_deeds_watches",
    "manage_deeds_exports",
    "manage_deeds_users",
    "use_deeds_conversion",
    "use_deeds_valuation",
  ],
  operator: [
    "view_dashboard",
    "view_verifications",
    "create_verifications",
    "view_services",
    "configure_services",
    "view_policies",
    "view_adapters",
    "view_audit",
    "view_reports",
    "view_deeds_map",
    "manage_deeds_reports",
    "manage_deeds_watches",
    "use_deeds_conversion",
    "use_deeds_valuation",
  ],
  viewer: [
    "view_dashboard",
    "view_verifications",
    "view_services",
    "view_policies",
    "view_adapters",
    "view_audit",
    "view_reports",
    "view_deeds_map",
    "use_deeds_conversion",
    "use_deeds_valuation",
  ],
  auditor: [
    "view_dashboard",
    "view_verifications",
    "view_services",
    "view_policies",
    "view_adapters",
    "view_audit",
    "view_reports",
    "create_reports",
    "view_deeds_map",
  ],
};

export interface User {
  id: string;
  name: string;
  email: string;
  role: Role;
  customPermissions?: Permission[];
}

export class PermissionService {
  private user: User | null = null;

  setUser(user: User) {
    this.user = user;
  }

  getUser() {
    return this.user;
  }

  hasPermission(permission: Permission): boolean {
    if (!this.user) return false;

    const rolePermissions = ROLE_PERMISSIONS[this.user.role] || [];
    const customPermissions = this.user.customPermissions || [];
    
    return rolePermissions.includes(permission) || customPermissions.includes(permission);
  }

  hasAnyPermission(permissions: Permission[]): boolean {
    return permissions.some(p => this.hasPermission(p));
  }

  hasAllPermissions(permissions: Permission[]): boolean {
    return permissions.every(p => this.hasPermission(p));
  }

  hasRole(role: Role): boolean {
    return this.user?.role === role;
  }

  canAccessRoute(route: string): boolean {
    const routePermissions: Record<string, Permission[]> = {
      "/dashboard": ["view_dashboard"],
      "/verifications": ["view_verifications"],
      "/services": ["view_services"],
      "/policies": ["view_policies"],
      "/adapters": ["view_adapters"],
      "/audit": ["view_audit"],
      "/settings": ["view_settings"],
      "/reports": ["view_reports"],
      "/services/deeds-map": ["view_deeds_map"],
      "/services/property-conversion": ["use_deeds_conversion"],
      "/services/property-valuation": ["use_deeds_valuation"],
    };

    const requiredPermissions = routePermissions[route];
    if (!requiredPermissions) return true; // Public route
    
    return this.hasAnyPermission(requiredPermissions);
  }
}

// Singleton instance
export const permissionService = new PermissionService();

// Mock user for development (replace with actual auth)
if (typeof window !== "undefined" && process.env.NODE_ENV === "development") {
  permissionService.setUser({
    id: "dev-user",
    name: "Developer",
    email: "dev@verigate.com",
    role: "admin",
  });
}
