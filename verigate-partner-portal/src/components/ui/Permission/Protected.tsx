"use client";

import * as React from "react";
import { Permission, permissionService } from "@/lib/permissions";

// Component that renders children only if user has permission
export interface ProtectedProps {
  permission: Permission | Permission[];
  children: React.ReactNode;
  fallback?: React.ReactNode;
  requireAll?: boolean; // Require all permissions vs any
}

export const Protected: React.FC<ProtectedProps> = ({
  permission,
  children,
  fallback = null,
  requireAll = false,
}) => {
  const permissions = Array.isArray(permission) ? permission : [permission];
  
  const hasAccess = requireAll
    ? permissionService.hasAllPermissions(permissions)
    : permissionService.hasAnyPermission(permissions);

  if (!hasAccess) {
    return <>{fallback}</>;
  }

  return <>{children}</>;
};

// Hook to check permissions
export function usePermission(permission: Permission | Permission[]): boolean {
  const [hasPermission, setHasPermission] = React.useState(false);

  React.useEffect(() => {
    const permissions = Array.isArray(permission) ? permission : [permission];
    const has = permissions.some(p => permissionService.hasPermission(p));
    setHasPermission(has);
  }, [permission]);

  return hasPermission;
}

// Hook to get current user
export function useCurrentUser() {
  const [user, setUser] = React.useState(permissionService.getUser());

  React.useEffect(() => {
    setUser(permissionService.getUser());
  }, []);

  return user;
}

// Component to show permission-based UI elements
export interface ConditionalProps {
  show: boolean;
  children: React.ReactNode;
  fallback?: React.ReactNode;
}

export const Conditional: React.FC<ConditionalProps> = ({
  show,
  children,
  fallback = null,
}) => {
  return show ? <>{children}</> : <>{fallback}</>;
};
