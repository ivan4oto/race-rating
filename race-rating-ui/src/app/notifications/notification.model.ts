// notification.model.ts
export type NotificationType = 'info' | 'success' | 'warning' | 'error';
export type NotificationSeverity = NotificationType;
export type NotificationName = 'NEW_RACE';

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: NotificationType;
  createdAt: Date;
  read: boolean;
  link?: string; // optional deeplink
}

export interface NotificationDto {
  id: number;               // id of NotificationRecipient (for markRead)
  type: NotificationName;   // backend logical type
  title: string;
  body: string;
  metadataJson: unknown;
  createdAt: string;        // ISO date
  isRead: boolean;
  readAt?: string | null;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;           // current page (0-based)
  size: number;
}

function parseBackendDate(raw: string | number): Date {
  if (typeof raw === 'number') {
    // If it looks like seconds (10 digits or float near that range), multiply
    return raw < 1e12 ? new Date(raw * 1000) : new Date(raw);
  }
  // If it's a string, try ISO first
  const asNum = Number(raw);
  if (!isNaN(asNum)) {
    return asNum < 1e12 ? new Date(asNum * 1000) : new Date(asNum);
  }
  return new Date(raw); // fallback: assume ISO string
}

// ---- Mapping helpers (UI never touches raw DTOs) ----
export function mapDtoToItem(dto: NotificationDto): NotificationItem {
  const createdAt = parseBackendDate(dto.createdAt);
  // Heuristic type -> UI type (extend if you add more names)
  const type: NotificationType =
    dto.type === 'NEW_RACE' ? 'success' : 'info';

  // Try to find a deeplink in metadata if available
  let link: string | undefined;
  try {
    const meta = dto.metadataJson as Record<string, unknown> | null;
    if (meta && typeof meta === 'object') {
      // common keys the backend might send
      if (typeof meta['link'] === 'string') link = meta['link'] as string;
      else if (typeof meta['url'] === 'string') link = meta['url'] as string;
      else if (typeof meta['route'] === 'string') link = meta['route'] as string;
    }
  } catch { /* noop */ }

  return {
    id: String(dto.id),                 // normalize to string for UI
    title: dto.title,
    message: dto.body,
    type,
    createdAt: createdAt,
    read: dto.isRead,
    link,
  };
}
