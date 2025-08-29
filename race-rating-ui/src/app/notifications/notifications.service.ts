// notifications.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { NotificationDto, Page } from './notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationsService {
  private readonly baseUrl = `${environment.apiUrl}api/notifications`;

  constructor(private http: HttpClient) {}

  list(opts: { unread?: boolean; page?: number; size?: number } = {}): Observable<Page<NotificationDto>> {
    const params = new HttpParams()
      .set('unread', String(opts.unread ?? false)) // 'All' by default
      .set('page', String(opts.page ?? 0))
      .set('size', String(opts.size ?? 20));

    return this.http.get<Page<NotificationDto>>(this.baseUrl, {
      params,
      withCredentials: true,
    });
  }

  countUnread(): Observable<number> {
    // If you have a dedicated /count endpoint, swap this out.
    return this.list({ unread: true, page: 0, size: 1 }).pipe(map(p => p.totalElements));
  }

  markRead(recipientId: number, read = true): Observable<void> {
    // If your API supports unread, change the URL accordingly.
    return this.http.post<void>(`${this.baseUrl}/${recipientId}/${read ? 'read' : 'unread'}`, null, {
      withCredentials: true,
    });
  }

  markAllRead(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/read-all`, null, {
      withCredentials: true,
    });
  }

  // Optional server-side delete endpoint (if present, prefer it over client filtering)
  delete(recipientId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${recipientId}`, { withCredentials: true });
  }
}
