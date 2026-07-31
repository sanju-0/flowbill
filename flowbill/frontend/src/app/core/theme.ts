import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {

  isDark = signal<boolean>(true);

  constructor() {
    const saved = localStorage.getItem('flowbill-theme');
    const dark = saved ? saved === 'dark' : true;
    this.isDark.set(dark);
    this.applyTheme(dark);
  }

  toggle() {
    const newVal = !this.isDark();
    this.isDark.set(newVal);
    this.applyTheme(newVal);
    localStorage.setItem('flowbill-theme', newVal ? 'dark' : 'light');
  }

  private applyTheme(dark: boolean) {
    if (dark) {
      document.documentElement.classList.remove('light');
    } else {
      document.documentElement.classList.add('light');
    }
  }
}