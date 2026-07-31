import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './layout/sidebar/sidebar'

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  template: `
    <div class="app-shell">
      <app-sidebar />
      <main class="main-content">
        <router-outlet />
      </main>
    </div>
  `,
  styles: [`
    .app-shell {
      display: flex;
      min-height: 100vh;
      background: var(--bg-primary);
    }
    .main-content {
      margin-left: 220px;
      flex: 1;
      padding: 28px;
      min-height: 100vh;
    }
  `]
})
export class App {}