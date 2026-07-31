import { Component, signal, HostListener } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../../core/theme';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class SidebarComponent {

  isOpen = signal(false);

  constructor(public theme: ThemeService) {}

  navItems = [
    { label: 'Dashboard', icon: 'ti-layout-dashboard', route: '/dashboard' },
    { label: 'Clients',   icon: 'ti-users',            route: '/clients'   },
    { label: 'Invoices',  icon: 'ti-file-invoice',     route: '/invoices'  },
    { label: 'Payments',  icon: 'ti-coin-rupee',       route: '/payments'  },
  ];

  bottomItems = [
    { label: 'Reminders', icon: 'ti-bell',     route: '/reminders' },
    { label: 'Settings',  icon: 'ti-settings', route: '/settings'  },
  ];

  toggleSidebar() {
    this.isOpen.set(!this.isOpen());
  }

  closeSidebar() {
    this.isOpen.set(false);
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    this.closeSidebar();
  }
}