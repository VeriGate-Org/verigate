"use client";
import { useRouter, useSearchParams } from "next/navigation";
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/Button";

export default function Filters() {
  const router = useRouter();
  const params = useSearchParams();
  const [q, setQ] = useState(params.get("q") ?? "");
  const [status, setStatus] = useState(params.get("status") ?? "");
  const [type, setType] = useState(params.get("type") ?? "");
  const [provider, setProvider] = useState(params.get("provider") ?? "");
  const [from, setFrom] = useState(params.get("from") ?? "");
  const [to, setTo] = useState(params.get("to") ?? "");
  const [pageSize, setPageSize] = useState(params.get("pageSize") ?? "10");

  useEffect(() => {
    setQ(params.get("q") ?? "");
    setStatus(params.get("status") ?? "");
    setType(params.get("type") ?? "");
    setProvider(params.get("provider") ?? "");
    setFrom(params.get("from") ?? "");
    setTo(params.get("to") ?? "");
    setPageSize(params.get("pageSize") ?? "10");
  }, [params]);

  const apply = () => {
    const sp = new URLSearchParams();
    if (q) sp.set("q", q);
    if (status) sp.set("status", status);
    if (type) sp.set("type", type);
    if (provider) sp.set("provider", provider);
    if (from) sp.set("from", from);
    if (to) sp.set("to", to);
    if (pageSize) sp.set("pageSize", pageSize);
    router.push(`/verifications${sp.size ? `?${sp.toString()}` : ""}`);
  };

  const reset = () => {
    setQ("");
    setStatus("");
    setType("");
    setProvider("");
    setFrom("");
    setTo("");
    router.push(`/verifications`);
  };

  return (
    <div className="console-card">
      <div className="console-card-header">
        <h3 className="text-aws-heading-s font-medium text-text">Filters</h3>
      </div>
      <div className="console-card-body">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-aws-m">
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Search correlationId or partnerId"
            className="aws-input w-full"
          />

          <select
            value={status}
            onChange={(e) => setStatus(e.target.value)}
            className="aws-select"
          >
            <option value="">All statuses</option>
            <option value="in_progress">In progress</option>
            <option value="success">Success</option>
            <option value="soft_fail">Soft fail</option>
            <option value="hard_fail">Hard fail</option>
          </select>

          <select
            value={type}
            onChange={(e) => setType(e.target.value)}
            className="aws-select"
          >
            <option value="">All types</option>
            <option value="ID">ID</option>
            <option value="CIPC">CIPC</option>
            <option value="DEEDS">DEEDS</option>
            <option value="AVS">AVS</option>
            <option value="SANCTIONS">SANCTIONS</option>
          </select>

          <input
            value={provider}
            onChange={(e) => setProvider(e.target.value)}
            placeholder="Provider (e.g., Qlink)"
            className="aws-input"
          />

          <input
            value={from}
            onChange={(e) => setFrom(e.target.value)}
            type="datetime-local"
            placeholder="From date"
            className="aws-input"
          />

          <input
            value={to}
            onChange={(e) => setTo(e.target.value)}
            type="datetime-local"
            placeholder="To date"
            className="aws-input"
          />

          <select
            value={pageSize}
            onChange={(e) => setPageSize(e.target.value)}
            className="aws-select"
          >
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
          </select>

          <div className="flex gap-aws-s">
            <Button variant="primary" onClick={apply} className="flex-1">
              Apply
            </Button>
            <Button variant="secondary" onClick={reset} className="flex-1">
              Reset
            </Button>
          </div>
        </div>

        {/* Saved views */}
        <SavedViews />
      </div>
    </div>
  );
}

function SavedViews() {
  const router = useRouter();
  const params = useSearchParams();
  const [name, setName] = useState("");
  const [views, setViews] = useState<Array<{ name: string; query: string }>>([]);

  useEffect(() => {
    try {
      const raw = localStorage.getItem("vg_saved_views") || "[]";
      setViews(JSON.parse(raw));
    } catch {
      setViews([]);
    }
  }, []);

  const save = () => {
    const q = params.toString();
    if (!name.trim()) return;
    const next = [...views.filter((v) => v.name !== name.trim()), { name: name.trim(), query: q }];
    setViews(next);
    try { localStorage.setItem("vg_saved_views", JSON.stringify(next)); } catch { }
    setName("");
  };

  const apply = (q: string) => {
    router.push(`/verifications${q ? `?${q}` : ""}`);
  };

  const remove = (n: string) => {
    const next = views.filter((v) => v.name !== n);
    setViews(next);
    try { localStorage.setItem("vg_saved_views", JSON.stringify(next)); } catch { }
  };

  return (
    <div className="mt-aws-l pt-aws-l border-t border-border">
      <div className="flex flex-wrap items-center gap-aws-m">
        <div className="text-aws-body font-medium text-text">Saved views</div>
        <div className="flex items-center gap-aws-s">
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Name current filters"
            className="aws-input w-48"
          />
          <Button size="sm" onClick={save} disabled={!name.trim()}>
            Save
          </Button>
        </div>

        {views.length === 0 ? (
          <span className="text-text-muted">None yet</span>
        ) : (
          <div className="flex flex-wrap items-center gap-aws-s">
            {views.map((v) => (
              <div key={v.name} className="inline-flex items-center gap-aws-xs rounded-aws-token border border-border px-aws-s py-aws-xs bg-surface">
                <button
                  onClick={() => apply(v.query)}
                  className="text-accent hover:text-accent-strong text-aws-body"
                >
                  {v.name}
                </button>
                <button
                  onClick={() => remove(v.name)}
                  className="text-text-muted hover:text-danger text-aws-body ml-aws-xs"
                  aria-label={`Delete ${v.name}`}
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
