// notifications.component.ts
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { AsyncPipe, DatePipe, NgForOf, NgIf } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

import { NotificationStore, Tab } from './NotificationStore';
import { TimeAgoPipe } from './time-ago.pipe';
import { NotificationItem } from './notification.model';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [
    NgIf, NgForOf, AsyncPipe, DatePipe,
    MatIconModule, MatButtonModule,
    RouterLink,
    TimeAgoPipe,
  ],
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationsComponent {
  public readonly store = inject(NotificationStore);

  activeTab: Tab = 'all';

  // Expose streams directly to template
  readonly items$ = this.store.items$;
  readonly loading$ = this.store.loading$;
  readonly error$ = this.store.error$;

  setTab(tab: Tab) {
    this.activeTab = tab;
    this.store.setTab(tab);
  }

  markRead(id: string, read = true) {
    // id is string in UI; convert back to number for API
    const recipientId = Number(id);
    if (!Number.isFinite(recipientId)) return;
    this.store.markRead(recipientId, read).subscribe();
  }

  dismiss(id: string) {
    const recipientId = Number(id);
    if (!Number.isFinite(recipientId)) return;
    this.store.delete(recipientId).subscribe();
  }

  markAllRead() {
    this.store.markAllRead().subscribe();
  }

  clearAll() {
    this.items$.subscribe(items => {
      items.forEach(i => this.dismiss(i.id));
    }).unsubscribe();
  }

  trackById(_: number, n: NotificationItem) {
    return n.id;
  }
}
