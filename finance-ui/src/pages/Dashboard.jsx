import { useEffect, useState } from 'react'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line,
  CartesianGrid
} from 'recharts'
import client from '../api/client'
import StatCard from '../components/StatCard.jsx'
import { useAuth } from '../context/AuthContext.jsx'

const formatCurrency = (n) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(n || 0)
export default function Dashboard() {
  const { user } = useAuth()
  const [summary, setSummary] = useState(null)
  const [categoryTotals, setCategoryTotals] = useState({})
  const [monthlyTrends, setMonthlyTrends] = useState({})
  const [recent, setRecent] = useState([])
  const [loading, setLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState(null)

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      setErrorMsg(null)
      try {
        const requests = [
          client.get('/api/dashboard/summary'),
          client.get('/api/dashboard/category-summary'),
          client.get('/api/dashboard/recent?limit=6')
        ]
        // Monthly trends is ADMIN/ANALYST only per backend security config
        if (user?.role === 'ADMIN' || user?.role === 'ANALYST') {
          requests.push(client.get('/api/dashboard/monthly-trends'))
        }
        const results = await Promise.all(requests)
        setSummary(results[0].data)
        setCategoryTotals(results[1].data)
        setRecent(results[2].data)
        if (results[3]) setMonthlyTrends(results[3].data)
      } catch (err) {
        setErrorMsg('Could not load dashboard data. Check the backend connection.')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [user])

  const categoryData = Object.entries(categoryTotals).map(([category, total]) => ({
    category,
    total
  }))

  const trendData = Object.entries(monthlyTrends)
    .map(([month, total]) => ({ month, total }))
    .sort((a, b) => a.month.localeCompare(b.month))

  if (loading) {
    return <div className="p-10 text-ink-soft text-sm">Loading dashboard…</div>
  }

  if (errorMsg) {
    return <div className="p-10 text-ledger-brick text-sm ledger-tick">{errorMsg}</div>
  }

  return (
    <div className="p-10 space-y-10 max-w-6xl">
      <div>
        <p className="label ledger-tick">Net balance</p>
        <p
          className={`font-display num text-5xl mt-1 ${summary.netBalance >= 0 ? 'text-ledger-green' : 'text-ledger-brick'
            }`}
        >
          {formatCurrency(summary.netBalance)}
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <StatCard label="Total income" value={formatCurrency(summary.totalIncome)} tone="green" />
        <StatCard label="Total expense" value={formatCurrency(summary.totalExpense)} tone="brick" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card p-6">
          <p className="label ledger-tick mb-4">Category totals</p>
          {categoryData.length === 0 ? (
            <p className="text-ink-soft text-sm">No categorized records yet.</p>
          ) : (
            <ResponsiveContainer width="100%" height={260}>
              <BarChart data={categoryData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#DEDBD2" vertical={false} />
                <XAxis dataKey="category" tick={{ fontSize: 11, fill: '#5B6460' }} />
                <YAxis tick={{ fontSize: 11, fill: '#5B6460' }} />
                <Tooltip
                  formatter={(v) => formatCurrency(v)}
                  contentStyle={{ borderRadius: 2, borderColor: '#DEDBD2', fontSize: 13 }}
                />
                <Bar dataKey="total" fill="#1F5C4E" radius={[2, 2, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>

        <div className="card p-6">
          <p className="label ledger-tick mb-4">Monthly trend</p>
          {trendData.length === 0 ? (
            <p className="text-ink-soft text-sm">
              {user?.role === 'VIEWER'
                ? 'Monthly trends are available to Analysts and Admins.'
                : 'Not enough data yet to show a trend.'}
            </p>
          ) : (
            <ResponsiveContainer width="100%" height={260}>
              <LineChart data={trendData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#DEDBD2" vertical={false} />
                <XAxis dataKey="month" tick={{ fontSize: 11, fill: '#5B6460' }} />
                <YAxis tick={{ fontSize: 11, fill: '#5B6460' }} />
                <Tooltip
                  formatter={(v) => formatCurrency(v)}
                  contentStyle={{ borderRadius: 2, borderColor: '#DEDBD2', fontSize: 13 }}
                />
                <Line type="monotone" dataKey="total" stroke="#B08D4F" strokeWidth={2} dot={{ r: 3 }} />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>

      <div>
        <p className="label ledger-tick mb-4">Recent activity</p>
        <div className="card divide-y divide-line">
          {recent.length === 0 && <p className="p-5 text-ink-soft text-sm">Nothing recorded yet.</p>}
          {recent.map((r) => (
            <div key={r.id} className="flex items-center justify-between px-5 py-3 text-sm">
              <div>
                <p>{r.category}</p>
                <p className="text-ink-soft text-xs num">{r.date}</p>
              </div>
              <p
                className={`num font-medium ${r.type === 'INCOME' ? 'text-ledger-green' : 'text-ledger-brick'
                  }`}
              >
                {r.type === 'INCOME' ? '△ ' : '▽ '}
                {formatCurrency(r.amount)}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
