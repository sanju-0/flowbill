import { Component, signal, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Client } from '../../core/api';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './clients.html',
  styleUrl: './clients.css'
})
export class ClientsComponent implements OnInit {

  showAddForm = signal(false);
  searchQuery = '';
  loading = signal(false);
  clients: Client[] = [];
  avatarColors = ['accent', 'success', 'warning', 'danger'];
  showProjectSection = false;

  newClient = {
    name: '',
    email: '',
    phone: '',
    companyName: ''
  };

  newProject = {
    title: '',
    description: '',
    totalAmount: ''
  };

  constructor(private api: ApiService, private cdr: ChangeDetectorRef) { }

  ngOnInit() { this.loadClients(); }

  loadClients() {
    this.loading.set(true);
    this.api.getClients().subscribe({
      next: (data) => {
        this.clients = [...data];
        this.loading.set(false);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error:', err);
        this.loading.set(false);
      }
    });
  }

  get filteredClients() {
    const q = this.searchQuery.toLowerCase();
    if (!q) return this.clients;
    return this.clients.filter(c =>
      c.name?.toLowerCase().includes(q) ||
      c.email?.toLowerCase().includes(q) ||
      c.companyName?.toLowerCase().includes(q)
    );
  }

  getInitials(name: string): string {
    return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
  }

  getAvatarColor(index: number): string {
    return this.avatarColors[index % this.avatarColors.length];
  }

  formatAmount(val: number): string {
    if (!val) return '₹0';
    if (val >= 100000) return '₹' + (val / 100000).toFixed(2) + 'L';
    if (val >= 1000) return '₹' + (val / 1000).toFixed(1) + 'K';
    return '₹' + val;
  }

  openAddForm() { this.showAddForm.set(true); }

  closeAddForm() {
    this.showAddForm.set(false);
    this.showProjectSection = false;
    this.newClient = { name: '', email: '', phone: '', companyName: '' };
    this.newProject = { title: '', description: '', totalAmount: '' };
  }

  saveClient() {
    if (!this.newClient.name.trim()) return;

    this.api.createClient(this.newClient).subscribe({
      next: (createdClient) => {
        // Project bhi add karna hai?
        if (this.showProjectSection && this.newProject.title.trim()) {
          this.api.createProject({
            clientId: createdClient.id!,
            title: this.newProject.title,
            description: this.newProject.description,
            totalAmount: parseFloat(this.newProject.totalAmount) || 0
          }).subscribe({
            next: () => {
              this.loadClients();
              this.closeAddForm();
            },
            error: (err) => console.error('Project error:', err)
          });
        } else {
          this.loadClients();
          this.closeAddForm();
        }
      },
      error: (err) => console.error('Client error:', err)
    });
  }

  deleteClient(id: number) {
    if (!id) return;
    this.api.deleteClient(id).subscribe({
      next: () => this.loadClients(),
      error: (err) => console.error('Error:', err)
    });
  }
}