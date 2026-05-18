"use client";

import { useCallback, useEffect, useState } from "react";
import { useToast } from "@/components/ui/Toast";
import { ConfirmationDialog } from "@/components/ui/Modal/Modal";
import { SkeletonTable } from "@/components/ui/Loading/Skeleton";
import { formatDateTime } from "@/lib/utils/date";
import {
  listApiKeys,
  generateApiKey,
  revokeApiKey,
} from "@/lib/bff-client";

interface ApiKeyRow {
  keyPrefix: string;
  status: string;
  createdAt: string | null;
  createdBy: string | null;
}

export default function ApiKeysTab() {
  const { toast } = useToast();
  const [keys, setKeys] = useState<ApiKeyRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [generatedKey, setGeneratedKey] = useState<string | null>(null);
  const [revokeTarget, setRevokeTarget] = useState<string | null>(null);

  const loadKeys = useCallback(() => {
    setLoading(true);
    listApiKeys()
      .then((res) => setKeys(res.keys))
      .catch(() => toast({ title: "Failed to load API keys", variant: "error" }))
      .finally(() => setLoading(false));
  }, [toast]);

  useEffect(() => { loadKeys(); }, [loadKeys]);

  const handleGenerate = useCallback(async () => {
    try {
      const res = await generateApiKey();
      setGeneratedKey(res.apiKey);
      loadKeys();
      toast({ title: "API key generated", description: "Copy it now — it won't be shown again.", variant: "success" });
    } catch (err) {
      toast({ title: "Generation failed", description: err instanceof Error ? err.message : "Could not generate key.", variant: "error" });
    }
  }, [loadKeys, toast]);

  const handleRevoke = useCallback(async (prefix: string) => {
    try {
      await revokeApiKey(prefix);
      loadKeys();
      toast({ title: "API key revoked", variant: "success" });
    } catch (err) {
      toast({ title: "Revocation failed", description: err instanceof Error ? err.message : "Could not revoke key.", variant: "error" });
    } finally {
      setRevokeTarget(null);
    }
  }, [loadKeys, toast]);

  return (
    <div className="space-y-4">
      {/* Revoke confirmation dialog */}
      <ConfirmationDialog
        open={revokeTarget !== null}
        onOpenChange={(open) => { if (!open) setRevokeTarget(null); }}
        title="Revoke API key"
        description="Are you sure you want to revoke this API key? This action cannot be undone and any integrations using this key will stop working."
        confirmText="Revoke"
        cancelText="Cancel"
        variant="destructive"
        onConfirm={() => { if (revokeTarget) handleRevoke(revokeTarget); }}
      />

      {/* One-time key display */}
      {generatedKey && (
        <div className="console-card console-card--success">
          <div className="console-card-body space-y-2">
            <div className="text-sm font-semibold text-text">Your new API key</div>
            <p className="text-xs text-text-muted">
              Copy this key now. For security, it will not be displayed again.
            </p>
            <div className="flex items-center gap-2">
              <code className="flex-1 rounded border border-border bg-[color:var(--color-base-200)] px-3 py-2 font-mono text-sm text-text">
                {generatedKey}
              </code>
              <button
                onClick={() => {
                  navigator.clipboard.writeText(generatedKey);
                  toast({ title: "Copied to clipboard", variant: "success" });
                }}
                className="aws-button aws-button--primary text-xs"
              >
                Copy
              </button>
            </div>
            <button
              onClick={() => setGeneratedKey(null)}
              className="text-xs text-text-muted hover:text-text"
            >
              Dismiss
            </button>
          </div>
        </div>
      )}

      {loading ? (
        <SkeletonTable rows={3} columns={4} />
      ) : (
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">API keys</div>
              <div className="text-xs text-text-muted">
                Manage the keys used to authenticate API requests from your integrations.
              </div>
            </div>
            <button onClick={handleGenerate} className="aws-button aws-button--primary text-xs">
              Generate New Key
            </button>
          </div>
          <div className="console-card-body p-0">
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-[color:var(--color-base-200)] text-xs uppercase tracking-wide text-text-muted">
                  <tr>
                    <th className="px-4 py-2.5">Prefix</th>
                    <th className="px-4 py-2.5">Status</th>
                    <th className="px-4 py-2.5">Created</th>
                    <th className="px-4 py-2.5 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {keys.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="px-4 py-8 text-center text-xs text-text-muted">
                        No API keys configured. Generate one to get started.
                      </td>
                    </tr>
                  ) : (
                    keys.map((key) => (
                      <tr key={key.keyPrefix} className="border-b border-border last:border-0">
                        <td className="px-4 py-2.5 font-mono text-sm text-text">{key.keyPrefix}…</td>
                        <td className="px-4 py-2.5">
                          <span
                            className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${
                              key.status === "ACTIVE"
                                ? "bg-success/10 text-success"
                                : "bg-[color:var(--color-base-200)] text-text-muted"
                            }`}
                          >
                            {key.status === "ACTIVE" ? "Active" : "Revoked"}
                          </span>
                        </td>
                        <td className="px-4 py-2.5 text-text-muted">{formatDateTime(key.createdAt)}</td>
                        <td className="px-4 py-2.5 text-right">
                          {key.status === "ACTIVE" && (
                            <button
                              onClick={() => setRevokeTarget(key.keyPrefix)}
                              className="aws-button aws-button--destructive text-xs"
                            >
                              Revoke
                            </button>
                          )}
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

      <div className="console-card">
        <div className="console-card-header">
          <div className="text-sm font-semibold text-text">Usage notes</div>
        </div>
        <div className="console-card-body space-y-2 text-sm text-text-muted">
          <ul className="list-disc space-y-1 pl-5">
            <li>API keys grant full access to the VeriGate API on behalf of your partner account.</li>
            <li>Store keys securely and never expose them in client-side code or public repositories.</li>
            <li>Revoked keys take effect immediately. Rotate keys periodically for best security practice.</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
