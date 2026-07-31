import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard')
        .then(m => m.DashboardComponent)
  },
  {
    path: 'clients',
    loadComponent: () =>
      import('./pages/clients/clients')
        .then(m => m.ClientsComponent)
  },
  {
    path: 'invoices',
    loadComponent: () =>
      import('./pages/invoices/invoices')
        .then(m => m.InvoicesComponent)
  },
  {
    path: 'payments',
    loadComponent: () =>
      import('./pages/payments/payments')
        .then(m => m.PaymentsComponent)
  }
];