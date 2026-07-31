import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Client {
  id?: number;
  name: string;
  email: string;
  phone: string;
  companyName: string;
}

export interface Invoice {
  id?: number;
  invoiceNumber: string;
  clientName: string;
  clientEmail: string; 
  projectTitle: string;
  issueDate: string;
  dueDate: string;
  amount: number;
  status: string;
}

export interface Payment {
  id?: number;
  invoiceId: number;
  invoiceNumber: string;
  clientName: string;
  amountPaid: number;
  paymentDate: string;
  paymentMode: string;
  notes: string;
}

export interface DashboardSummary {
  totalEarned: number;
  totalPending: number;
  totalOverdue: number;
  activeClients: number;
  overdueCount: number;
  recentInvoices: Invoice[];
  topClients: Client[];
}

export interface Project {
  id?: number;
  clientId?: number;
  clientName?: string;
  title: string;
  description: string;
  totalAmount: number;
  status?: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {

  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  // Clients
  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.base}/clients`);
  }

  createClient(client: Omit<Client, 'id'>): Observable<Client> {
    return this.http.post<Client>(`${this.base}/clients`, client);
  }

  updateClient(id: number, client: Omit<Client, 'id'>): Observable<Client> {
    return this.http.put<Client>(`${this.base}/clients/${id}`, client);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/clients/${id}`);
  }

  // Invoices
  getInvoices(status?: string): Observable<Invoice[]> {
    const url = status
      ? `${this.base}/invoices?status=${status}`
      : `${this.base}/invoices`;
    return this.http.get<Invoice[]>(url);
  }

  getInvoicesByClient(clientId: number): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.base}/invoices/client/${clientId}`);
  }

  createInvoice(invoice: any): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.base}/invoices`, invoice);
  }

  getOverdueInvoices(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.base}/invoices/overdue`);
  }

  updateInvoiceStatus(id: number, status: string): Observable<Invoice> {
    return this.http.patch<Invoice>(
      `${this.base}/invoices/${id}/status?status=${status}`, {}
    );
  }

  deleteInvoice(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/invoices/${id}`);
  }

  // Payments
  getPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.base}/payments`);
  }

  recordPayment(payment: Omit<Payment, 'id' | 'invoiceNumber' | 'clientName'>): Observable<Payment> {
    return this.http.post<Payment>(`${this.base}/payments`, payment);
  }

  deletePayment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/payments/${id}`);
  }

  // Dashboard
  getDashboardSummary(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${this.base}/dashboard/summary`);
  }

  // Projects
  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.base}/projects`);
  }

  getProjectsByClient(clientId: number): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.base}/projects/client/${clientId}`);
  }

  createProject(project: Omit<Project, 'id'>): Observable<Project> {
    return this.http.post<Project>(`${this.base}/projects`, project);
  }

  // AI
  generateReminder(data: {
    clientName: string;
    amount: number;
    daysOverdue: number;
    tone: string;
  }): Observable<{ email: string }> {
    return this.http.post<{ email: string }>(
      `${this.base}/ai/generate-reminder`, data
    );
  }

  sendReminder(data: {
    clientName: string;
    clientEmail: string;
    amount: number;
    daysOverdue: number;
    tone: string;
  }): Observable<{ email: string; message: string }> {
    return this.http.post<{ email: string; message: string }>(
      `${this.base}/ai/send-reminder`, data
    );
  }
}