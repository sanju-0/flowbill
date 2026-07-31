import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService, DashboardSummary } from '../../core/api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {

  summary: DashboardSummary = {
    totalEarned: 0,
    totalPending: 0,
    totalOverdue: 0,
    activeClients: 0,
    overdueCount: 0,
    recentInvoices: [],
    topClients: []
  };

  loading = true;

  constructor(private api: ApiService, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.api.getDashboardSummary().subscribe({
      next: (data) => {
        console.log('Dashboard data:', data);
        this.summary = { ...data };
        this.loading = false;
        this.cdr.markForCheck(); // 2. Trigger change detection
      },
      error: (err) => {
        console.error('Dashboard error:', err);
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  get flowTotal() {
    return (this.summary.totalEarned || 0) +
           (this.summary.totalPending || 0) +
           (this.summary.totalOverdue || 0) || 1;
  }

  get earnedPct() {
    return ((this.summary.totalEarned || 0) / this.flowTotal * 100).toFixed(1);
  }

  get pendingPct() {
    return ((this.summary.totalPending || 0) / this.flowTotal * 100).toFixed(1);
  }

  get overduePct() {
    return ((this.summary.totalOverdue || 0) / this.flowTotal * 100).toFixed(1);
  }

  formatAmount(val: number): string {
    if (!val) return '₹0';
    if (val >= 100000) return '₹' + (val / 100000).toFixed(2) + 'L';
    if (val >= 1000) return '₹' + (val / 1000).toFixed(1) + 'K';
    return '₹' + val;
  }

  getInitials(name: string): string {
    if (!name) return '??';
    return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
  }

  goToNewInvoice() {
    this.router.navigate(['/invoices']);
  }
}