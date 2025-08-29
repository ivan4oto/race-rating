// time-ago.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeAgo',
  standalone: true,
  pure: true,
})
export class TimeAgoPipe implements PipeTransform {
  transform(input: Date | string | number | null | undefined): string {
    if (!input) return '';
    const then = new Date(input).getTime();
    if (isNaN(then)) return '';
    let delta = Math.max(1, Math.floor((Date.now() - then) / 1000)); // seconds

    const steps: [number, Intl.RelativeTimeFormatUnit][] = [
      [60, 'second'],
      [60, 'minute'],
      [24, 'hour'],
      [7, 'day'],
      [4.34524, 'week'],
      [12, 'month'],
      [Number.POSITIVE_INFINITY, 'year'],
    ];

    let unit: Intl.RelativeTimeFormatUnit = 'second';
    for (const [factor, u] of steps) {
      if (delta < factor) { unit = u; break; }
      delta = Math.floor(delta / factor);
      unit = u;
    }

    // negative value for "x ago"
    const rtf = new Intl.RelativeTimeFormat(undefined, { numeric: 'auto' });
    return rtf.format(-delta, unit);
  }
}
