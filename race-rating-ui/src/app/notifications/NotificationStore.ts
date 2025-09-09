// NotificationStore.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, combineLatest, map, of, shareReplay, startWith, switchMap, tap } from 'rxjs';
import { NotificationsService } from './notifications.service';
import { mapDtoToItem, NotificationItem } from './notification.model';

export type Tab = 'all' | 'unread';

@Injectable({ providedIn: 'root' })
export class NotificationStore {
  // UI state
  private readonly tab$   = new BehaviorSubject<Tab>('all');
  private readonly page$  = new BehaviorSubject<number>(0);
  private readonly size$  = new BehaviorSubject<number>(20);

  // Loading + error signals for the current list request
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  readonly loading$ = this.loadingSubject.asObservable();

  private readonly errorSubject = new BehaviorSubject<string | null>(null);
  readonly error$ = this.errorSubject.asObservable();
  private readonly unreadRefresh$ = new BehaviorSubject<void>(undefined);

  // Unread count (separate request, cached)
  readonly unreadCount$ = this.unreadRefresh$.pipe(
    switchMap(() => this.api.countUnread()),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  // Main stream: (tab, page, size) -> Page<DTO> -> NotificationItem[]
  readonly items$ = combineLatest([this.tab$, this.page$, this.size$]).pipe(
    tap(() => {
      this.errorSubject.next(null);
      this.loadingSubject.next(true);
    }),
    switchMap(([tab, page, size]) =>
      this.api.list({ unread: tab === 'unread', page, size }).pipe(
        map(pageDto => pageDto.content.map(mapDtoToItem)),
        tap(() => this.loadingSubject.next(false)),
        catchError(err => {
          console.error('Failed to load notifications', err);
          this.errorSubject.next('Failed to load notifications.');
          this.loadingSubject.next(false);
          return of<NotificationItem[]>([]);
        })
      )
    ),
    // cache current emission for async pipes
    shareReplay({ bufferSize: 1, refCount: true })
  );

  constructor(private api: NotificationsService) {}

  // UI actions
  setTab(tab: Tab) {
    this.tab$.next(tab);
    this.page$.next(0); // reset paging on tab change
  }
  nextPage() { this.page$.next(this.page$.value + 1); }
  setPageSize(size: number) { this.size$.next(size); }

  refresh() {
    this.page$.next(this.page$.value);
  }

  bumpUnreadCount() {
    this.unreadRefresh$.next();
  }

  markRead(recipientId: number, read = true) {
    return this.api.markRead(recipientId, read).pipe(tap(() => {
      this.refresh();
      this.bumpUnreadCount();
    }));
  }

  markAllRead() {
    return this.api.markAllRead().pipe(tap(() => {
      this.refresh();
      this.bumpUnreadCount();
    }));
  }

  delete(recipientId: number) {
    return this.api.delete(recipientId).pipe(tap(() => {
      this.refresh();
      this.bumpUnreadCount();
    }));
  }
}
