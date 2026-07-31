import { Component, signal, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Invoice, Client, Project } from '../../core/api';

type InvoiceStatus = 'PAID' | 'UNPAID' | 'OVERDUE';

@Component({
  selector: 'app-invoices',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './invoices.html',
  styleUrl: './invoices.css'
})

export class InvoicesComponent implements OnInit {

  showAddForm = signal(false);
  showEmailModal = signal(false);
  activeFilter = signal<'ALL' | InvoiceStatus>('ALL');
  filters: Array<'ALL' | InvoiceStatus> = ['ALL', 'PAID', 'UNPAID', 'OVERDUE'];
  invoices: Invoice[] = [];
  clients: Client[] = [];
  clientProjects: Project[] = [];
  showCreateForm = signal(false);
  generatedEmail = '';
  generatingEmail = signal(false);
  selectedTone = 'polite';
  tones = ['polite', 'firm', 'final warning'];
  newInvoice = {
    selectedClientId: '',
    projectId: '',
    invoiceNumber: '',
    issueDate: '',
    dueDate: '',
    amount: ''
  };
  sendingEmail = signal(false);
  emailSent = signal(false);

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) { }

  ngOnInit() { this.loadInvoices(); this.loadClients(); }

  loadInvoices() {
    this.api.getInvoices().subscribe({
      next: (data) => {
        this.invoices = [...data];
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Error:', err)
    });
  }

  loadClients() {
    this.api.getClients().subscribe({
      next: (data) => {
        this.clients = [...data];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error:', err)
    });
  }

  get filteredInvoices() {
    const f = this.activeFilter();
    if (f === 'ALL') return this.invoices;
    return this.invoices.filter(i => i.status === f);
  }

  get totalAmount() {
    return this.filteredInvoices.reduce((sum, i) => sum + i.amount, 0);
  }

  getFilterCount(filter: 'ALL' | InvoiceStatus): number {
    if (filter === 'ALL') return this.invoices.length;
    return this.invoices.filter(i => i.status === filter).length;
  }

  getInitials(name: string): string {
    if (!name) return '??';
    return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
  }

  getAvatarColor(status: string): string {
    if (status === 'PAID') return 'success';
    if (status === 'OVERDUE') return 'danger';
    return 'warning';
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

  getDaysOverdue(dueDateStr: string): number {
    const due = new Date(dueDateStr);
    const today = new Date();
    const diff = today.getTime() - due.getTime();
    return Math.max(0, Math.floor(diff / (1000 * 60 * 60 * 24)));
  }

  isOverdue(inv: Invoice): boolean {
    return inv.status === 'OVERDUE';
  }

  setFilter(filter: 'ALL' | InvoiceStatus) {
    this.activeFilter.set(filter);
  }

  markAsPaid(id: number) {
    this.api.updateInvoiceStatus(id, 'PAID').subscribe({
      next: () => this.loadInvoices(),
      error: (err) => console.error('Error:', err)
    });
  }

  deleteInvoice(id: number) {
    this.api.deleteInvoice(id).subscribe({
      next: () => this.loadInvoices(),
      error: (err) => console.error('Error:', err)
    });
  }

  onClientSelectForInvoice(clientId: string) {
    this.newInvoice.projectId = '';
    this.clientProjects = [];
    if (!clientId) return;

    this.api.getProjectsByClient(parseInt(clientId)).subscribe({
      next: (data) => {
        this.clientProjects = [...data];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error:', err)
    });
  }

  onProjectSelect(projectId: string) {
    if (!projectId) return;
    const project = this.clientProjects.find(p => p.id === parseInt(projectId));
    if (project) {
      this.newInvoice.amount = project.totalAmount.toString();
    }
    // Auto generate invoice number
    const max = this.invoices.reduce((m, i) => {
      const num = parseInt(i.invoiceNumber?.split('-')[2] || '0');
      return num > m ? num : m;
    }, 0);
    this.newInvoice.invoiceNumber = `INV-2025-${String(max + 1).padStart(3, '0')}`;
  }

  get selectedProject(): Project | null {
    if (!this.newInvoice.projectId) return null;
    return this.clientProjects.find(
      p => p.id === parseInt(this.newInvoice.projectId)
    ) || null;
  }

  openCreateForm() { this.showCreateForm.set(true); }

  closeCreateForm() {
    this.showCreateForm.set(false);
    this.clientProjects = [];
    this.newInvoice = {
      selectedClientId: '',
      projectId: '',
      invoiceNumber: '',
      issueDate: '',
      dueDate: '',
      amount: ''
    };
  }

  saveInvoice() {
    if (!this.newInvoice.projectId || !this.newInvoice.amount) return;

    // Validation
    if (!this.newInvoice.issueDate || !this.newInvoice.dueDate) {
      alert('Please select Issue Date and Due Date');
      return;
    }

    const payload = {
      projectId: parseInt(this.newInvoice.projectId),
      invoiceNumber: this.newInvoice.invoiceNumber,
      issueDate: this.newInvoice.issueDate,      // format: "2025-07-01"
      dueDate: this.newInvoice.dueDate,          // format: "2025-07-15"
      amount: parseFloat(this.newInvoice.amount)
    };

    console.log('Sending payload:', payload);   // debug

    this.api.createInvoice(payload).subscribe({
      next: () => {
        this.loadInvoices();
        this.closeCreateForm();
      },
      error: (err) => {
        console.error('Error:', err);
        console.error('Error details:', err.error); // exact backend error
      }
    });
  }

  sendEmail() {
    if (!this.currentInvoice || !this.generatedEmail) return;
    this.sendingEmail.set(true);

    this.api.sendReminder({
      clientName: this.currentInvoice.clientName,
      clientEmail: this.currentInvoice.clientEmail,
      amount: this.currentInvoice.amount,
      daysOverdue: this.getDaysOverdue(this.currentInvoice.dueDate),
      tone: this.selectedTone
    }).subscribe({
      next: (res) => {
        this.sendingEmail.set(false);
        this.emailSent.set(true);
        this.generatedEmail = res.email;
        this.cdr.detectChanges();

        // 2 second baad success message hide karo
        setTimeout(() => {
          this.emailSent.set(false);
          this.closeEmailModal();
        }, 2000);
      },
      error: (err) => {
        console.error('Send error:', err);
        this.sendingEmail.set(false);
      }
    });
  }

  // AI Email
  currentInvoice: Invoice | null = null;

  openEmailModal(inv: Invoice) {
    this.currentInvoice = inv;
    this.generatedEmail = '';
    this.selectedTone = 'polite';
    this.showEmailModal.set(true);
  }

  closeEmailModal() {
    this.showEmailModal.set(false);
    this.currentInvoice = null;
    this.generatedEmail = '';
  }

  generateEmail() {
    if (!this.currentInvoice) return;
    this.generatingEmail.set(true);

    this.api.generateReminder({
      clientName: this.currentInvoice.clientName,
      amount: this.currentInvoice.amount,
      daysOverdue: this.getDaysOverdue(this.currentInvoice.dueDate),
      tone: this.selectedTone
    }).subscribe({
      next: (res) => {
        this.generatedEmail = res.email;
        this.generatingEmail.set(false);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('AI Error:', err);
        this.generatingEmail.set(false);
      }
    });
  }

  copyEmail() {
    navigator.clipboard.writeText(this.generatedEmail);
  }
}