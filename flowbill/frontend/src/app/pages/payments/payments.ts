import { Component, signal, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Payment, Client, Invoice } from '../../core/api';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payments.html',
  styleUrl: './payments.css'
})
export class PaymentsComponent implements OnInit {

  showAddForm = signal(false);
  activeMonth = signal('ALL');
  payments: Payment[] = [];
  clients: Client[] = [];
  clientInvoices: Invoice[] = [];
  paymentModes = ['UPI', 'Bank Transfer', 'Cash', 'Cheque', 'Other'];
  currentMonth = this.formatMonth(new Date().toISOString().slice(0, 7));

  newPayment = {
    selectedClientId: '',
    invoiceId: '',
    amountPaid: '',
    paymentDate: '',
    paymentMode: 'UPI',
    notes: ''
  };

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    this.loadPayments();
    this.loadClients();
  }

  loadPayments() {
    this.api.getPayments().subscribe({
      next: (data) => {
        this.payments = [...data];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error:', err)
    });
  }

  loadClients() {
    this.api.getClients().subscribe({
      next: (data) => {
        this.clients = [...data];
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error:', err)
    });
  }

  onClientSelect(clientId: string) {
    this.newPayment.invoiceId = '';
    this.clientInvoices = [];
    if (!clientId) return;

    this.api.getInvoicesByClient(parseInt(clientId)).subscribe({
      next: (data) => {
        this.clientInvoices = data.filter(i => i.status !== 'PAID');
        this.cdr.detectChanges(); // markForCheck ki jagah detectChanges
      },
      error: (err) => console.error('Error:', err)
    });
  }

  onInvoiceSelect(invoiceId: string) {
    if (!invoiceId) return;
    const inv = this.clientInvoices.find(i => i.id === parseInt(invoiceId));
    if (inv) {
      this.newPayment.amountPaid = inv.amount.toString();
    }
  }

  get selectedInvoice(): Invoice | null {
    if (!this.newPayment.invoiceId) return null;
    return this.clientInvoices.find(
      i => i.id === parseInt(this.newPayment.invoiceId)
    ) || null;
  }

  get months(): string[] {
    const monthSet = new Set<string>();
    this.payments.forEach(p => {
      if (p.paymentDate) monthSet.add(p.paymentDate.slice(0, 7));
    });
    return ['ALL', ...Array.from(monthSet).sort().reverse()];
  }

  get filteredPayments(): Payment[] {
    const m = this.activeMonth();
    if (m === 'ALL') return this.payments;
    return this.payments.filter(p => p.paymentDate?.startsWith(m));
  }

  get totalReceived(): number {
    return this.filteredPayments.reduce((sum, p) => sum + p.amountPaid, 0);
  }

  get thisMonthTotal(): number {
    const now = new Date().toISOString().slice(0, 7);
    return this.payments
      .filter(p => p.paymentDate?.startsWith(now))
      .reduce((sum, p) => sum + p.amountPaid, 0);
  }

  get upiTotal(): number {
    return this.payments
      .filter(p => p.paymentMode === 'UPI')
      .reduce((sum, p) => sum + p.amountPaid, 0);
  }

  formatAmount(val: number): string {
    if (!val) return '₹0';
    if (val >= 100000) return '₹' + (val / 100000).toFixed(2) + 'L';
    if (val >= 1000) return '₹' + (val / 1000).toFixed(1) + 'K';
    return '₹' + val;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  }

  formatMonth(m: string): string {
    if (m === 'ALL') return 'All Time';
    const d = new Date(m + '-01');
    return d.toLocaleDateString('en-IN', { month: 'short', year: 'numeric' });
  }

  getInitials(name: string): string {
    if (!name) return '??';
    return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
  }

  getModeIcon(mode: string): string {
    const icons: Record<string, string> = {
      'UPI': 'ti-brand-google-pay',
      'Bank Transfer': 'ti-building-bank',
      'Cash': 'ti-cash',
      'Cheque': 'ti-writing',
      'Other': 'ti-credit-card'
    };
    return icons[mode] || 'ti-credit-card';
  }

  getModeColor(mode: string): string {
    const colors: Record<string, string> = {
      'UPI': 'accent',
      'Bank Transfer': 'success',
      'Cash': 'warning',
      'Cheque': 'warning',
      'Other': 'neutral'
    };
    return colors[mode] || 'neutral';
  }

  openAddForm() { this.showAddForm.set(true); }

  closeAddForm() {
    this.showAddForm.set(false);
    this.clientInvoices = [];
    this.newPayment = {
      selectedClientId: '',
      invoiceId: '',
      amountPaid: '',
      paymentDate: '',
      paymentMode: 'UPI',
      notes: ''
    };
  }

  savePayment() {
    if (!this.newPayment.invoiceId || !this.newPayment.amountPaid) return;

    const payload = {
      invoiceId: parseInt(this.newPayment.invoiceId),
      amountPaid: parseFloat(this.newPayment.amountPaid),
      paymentDate: this.newPayment.paymentDate || new Date().toISOString().slice(0, 10),
      paymentMode: this.newPayment.paymentMode,
      notes: this.newPayment.notes
    };

    this.api.recordPayment(payload).subscribe({
      next: () => {
        this.loadPayments();
        this.closeAddForm();
      },
      error: (err) => console.error('Error:', err)
    });
  }

  deletePayment(id: number) {
    this.api.deletePayment(id).subscribe({
      next: () => this.loadPayments(),
      error: (err) => console.error('Error:', err)
    });
  }
}