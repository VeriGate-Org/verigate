import type { CheckSession, CheckSessionListResponse } from "@/lib/types/check-session";
import { config } from "@/lib/config";
import { saveCheckSessionBff, listCheckSessionsBff } from "@/lib/bff-client";
import { generateCheckSessionHistory } from "@/lib/mock-services";

// In-memory store for mock mode (same pattern as mock-db.ts)
const sessions: CheckSession[] = [];

export async function saveCheckSession(session: CheckSession): Promise<void> {
  if (config.useMockServices) {
    sessions.unshift(session);
    return;
  }
  await saveCheckSessionBff(session);
}

export async function listCheckSessions(params?: {
  cursor?: string;
  limit?: number;
}): Promise<CheckSessionListResponse> {
  if (config.useMockServices) {
    return paginateSessions(params);
  }
  try {
    const res = await listCheckSessionsBff(params);
    return res.items.length > 0 ? res : paginateSessions(params);
  } catch {
    return paginateSessions(params);
  }
}

function paginateSessions(params?: {
  cursor?: string;
  limit?: number;
}): CheckSessionListResponse {
  const seed = generateCheckSessionHistory();
  const all = [...sessions, ...seed];
  const limit = params?.limit ?? 50;
  const cursorIndex = params?.cursor
    ? all.findIndex((s) => s.sessionId === params.cursor) + 1
    : 0;
  const paged = all.slice(cursorIndex, cursorIndex + limit);
  const hasMore = cursorIndex + limit < all.length;
  const nextCursor = hasMore ? paged[paged.length - 1]?.sessionId ?? null : null;
  return { items: paged, cursor: nextCursor, hasMore };
}
