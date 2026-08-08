# Ledger — Finance Dashboard UI

A React frontend for the `finance-dashboard` Spring Boot backend. Built with Vite,
Tailwind, and Recharts. Talks to the backend via JWT (see the backend's `/api/auth/login`).

## Local development

```bash
npm install
cp .env.example .env
# edit .env if your backend isn't on http://localhost:8082
npm run dev
```

The app runs on **http://localhost:5500** by default — this matches the CORS origin
already allowed in the backend's `SecurityConfig`. If you change the port, update
`SecurityConfig.corsConfigurationSource()` on the backend to match.

Log in with whatever admin account your backend created on startup
(`app.admin.email` / `app.admin.password` from the backend's env vars).

## What's included

- **Login** — JWT auth, token stored in `localStorage`, auto-redirect to `/login` on 401
- **Dashboard** — net balance, income/expense totals, category bar chart, monthly trend
  line chart (Admin/Analyst only, matching backend role restrictions), recent activity
- **Records** — paginated table, filter by type/category/date range, create/edit/delete
  (Admin only for writes, matching backend `@PreAuthorize` rules)
- **Users** — list and edit role/status (Admin only)

Role-based UI restrictions mirror the backend exactly: Viewers land on Records instead
of Dashboard (since `/api/dashboard/**` is Admin/Analyst-only on the backend), and
write actions are hidden for non-Admins rather than just failing silently.

## Deploying to Vercel

1. Push this project to its own GitHub repo (or a subfolder of your backend repo).
2. In Vercel: **New Project → Import** this repo.
3. Framework preset: **Vite**. Build command `npm run build`, output dir `dist`
   (Vercel usually detects this automatically).
4. Add an environment variable in Vercel's project settings:
   - `VITE_API_BASE_URL` = your deployed backend's public URL
5. Deploy.

`vercel.json` is already included so client-side routing (React Router) works on
page refresh/direct links.

### Don't forget the backend CORS update

Once deployed, add your Vercel domain (e.g. `https://your-app.vercel.app`) to the
allowed origins in the backend's `SecurityConfig.corsConfigurationSource()`, or the
browser will block requests with a CORS error. Consider externalizing that origin
list to an env var too, the same way the DB credentials were.

## Design notes

The visual language is an "accountant's ledger": paper-toned surfaces, a navy
sidebar, ledger-green for income and brick-red for expense, and tabular monospace
figures (IBM Plex Mono) so amounts align like a real ledger. The recurring hairline
"tick" mark next to labels and nav items is the one signature element used
throughout — everything else stays quiet on purpose.
