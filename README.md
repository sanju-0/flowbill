# FlowBill — Freelancer Invoice & Payment Tracker

FlowBill automates the most tedious part of freelancing: chasing overdue payments. Instead of tracking invoices across WhatsApp messages and Excel sheets, FlowBill detects overdue invoices automatically and sends AI-generated, tone-appropriate reminder emails — without any manual follow-up.



## 🎥 Demo

https://drive.google.com/file/d/12Em5hAhzNn-LZSvODw0HR1NJM8-hKQlc/view?usp=sharing

---

## The Problem

Millions of freelancers — designers, developers, consultants — manage client payments manually. Clients go quiet after a project ends, follow-ups get forgotten, and money gets lost simply because there's no system tracking who owes what and for how long. This doesn't scale past a handful of active clients.

## What FlowBill Does

- **Client & Project Management** — organize clients, projects, and invoices in one place
- **Automatic Overdue Detection** — a daily scheduled job flags every invoice past its due date
- **AI-Powered Reminder Emails** — an LLM generates the reminder email, with tone that escalates based on days overdue (polite → firm → final notice)
- **One-Click PDF Invoices** — generate clean, professional invoices instantly
- **Partial Payment Tracking** — invoices support multiple partial payments, with status auto-updating (Pending → Partially Paid → Paid)
- **Reminder Audit Log** — every reminder attempt (sent or failed) is logged, so nothing happens silently

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.2.5 (Java 17), Maven |
| Frontend | Angular 17 (standalone components, signals) |
| Database | PostgreSQL |
| AI (email generation) | Groq API (open-weight LLMs) |
| PDF Generation | iText7 |
| Email Delivery | JavaMailSender (Gmail SMTP) |
| Automation | Spring Scheduler (`@Scheduled` cron jobs) |

## Architecture

```
freelancer (1) ─── (many) client (1) ─── (many) project (1) ─── (many) invoice (1) ─── (many) payment
                                                                              │
                                                                              └─── (many) reminder_log
```

A daily cron job queries invoices where `due_date < today` and `status != PAID`, generates a tone-appropriate reminder via the AI service for each one, sends it via email, and logs the outcome.

## Getting Started

### Prerequisites
- Java 17+
- Maven
- Node.js 18+ and Angular CLI
- PostgreSQL

### 1. Database Setup
```bash
psql -U postgres -c "CREATE DATABASE freelancer_tracker;"
psql -U postgres -d freelancer_tracker -f database/schema.sql
```

### 2. Backend Setup
Configure `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.mail.username=your-email@gmail.com
spring.mail.password=your_gmail_app_password
groq.api.key=your_groq_api_key
groq.model=openai/gpt-oss-120b
```

Run:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`.

### 3. Frontend Setup
```bash
cd frontend
npm install
ng serve
```
Frontend runs on `http://localhost:4200`.

## Key Design Decisions

**Why AI-generated reminders instead of static templates?**
Payment follow-ups aren't one-size-fits-all — a day-2 nudge and a day-15 follow-up need a different tone. Generating the email dynamically based on days overdue keeps every reminder contextually appropriate instead of robotic and repetitive.

**Why log every reminder attempt?**
Automation is only trustworthy if it's observable. The `reminder_log` table tracks every send attempt (success or failure) so nothing silently fails.

**Why iText7 for PDFs?**
Runs entirely server-side with no external API dependency — one less point of failure, no API key management, no rate limits.

## Roadmap

- [ ] JWT-based authentication (currently single-freelancer context)
- [ ] Dedicated UI for invoice/project creation (currently API-driven)
- [ ] Live deployment (Railway/Render)
- [ ] Email delivery via a transactional email provider for better deliverability at scale

## Feedback

This is a v1 build, actively evolving. If you're a freelancer who's dealt with payment-chasing pain, or a developer with thoughts on the architecture, issues and discussions are welcome.
