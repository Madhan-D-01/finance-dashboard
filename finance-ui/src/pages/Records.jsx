import { useEffect, useState, useCallback } from 'react'
import client from '../api/client'
import LedgerTable from '../components/LedgerTable.jsx'
import RecordFormModal from '../components/RecordFormModal.jsx'
import { useAuth } from '../context/AuthContext.jsx'

export default function Records() {
  const { user } = useAuth()
  const canManage = user?.role === 'ADMIN'

  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState(null)
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)

  const [filters, setFilters] = useState({ type: '', category: '', startDate: '', endDate: '' })

  const load = useCallback(async () => {
    setLoading(true)
    setErrorMsg(null)
    try {
      const hasFilters = Object.values(filters).some(Boolean)
      let data
      if (hasFilters) {
        const params = new URLSearchParams(
          Object.entries(filters).filter(([, v]) => v)
        ).toString()
        const res = await client.get(`/api/records/filter?${params}`)
        data = res.data
      } else {
        const res = await client.get('/api/records?size=50&sort=date,desc')
        data = res.data.content
      }
      setRecords(data)
    } catch (err) {
      setErrorMsg('Could not load records.')
    } finally {
      setLoading(false)
    }
  }, [filters])

  useEffect(() => {
    load()
  }, [load])

  const handleCreate = () => {
    setEditing(null)
    setModalOpen(true)
  }

  const handleEdit = (record) => {
    setEditing(record)
    setModalOpen(true)
  }

  const handleDelete = async (record) => {
    if (!window.confirm(`Delete this ${record.category} record?`)) return
    await client.delete(`/api/records/${record.id}`)
    load()
  }

  const handleSubmit = async (form) => {
    if (editing) {
      await client.put(`/api/records/${editing.id}`, form)
    } else {
      await client.post('/api/records', form)
    }
    load()
  }

  return (
    <div className="p-10 max-w-6xl space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="font-display text-2xl">Records</p>
          <p className="text-ink-soft text-sm mt-1">Every income and expense entry, in order.</p>
        </div>
        {canManage && (
          <button className="btn-primary" onClick={handleCreate}>
            + New record
          </button>
        )}
      </div>

      <div className="card p-4 flex flex-wrap gap-3 items-end">
        <div>
          <label className="label">Type</label>
          <select
            className="input"
            value={filters.type}
            onChange={(e) => setFilters({ ...filters, type: e.target.value })}
          >
            <option value="">All</option>
            <option value="INCOME">Income</option>
            <option value="EXPENSE">Expense</option>
          </select>
        </div>
        <div>
          <label className="label">Category</label>
          <input
            className="input"
            placeholder="e.g. Salary"
            value={filters.category}
            onChange={(e) => setFilters({ ...filters, category: e.target.value })}
          />
        </div>
        <div>
          <label className="label">From</label>
          <input
            type="date"
            className="input"
            value={filters.startDate}
            onChange={(e) => setFilters({ ...filters, startDate: e.target.value })}
          />
        </div>
        <div>
          <label className="label">To</label>
          <input
            type="date"
            className="input"
            value={filters.endDate}
            onChange={(e) => setFilters({ ...filters, endDate: e.target.value })}
          />
        </div>
        <button
          className="btn-secondary"
          onClick={() => setFilters({ type: '', category: '', startDate: '', endDate: '' })}
        >
          Clear
        </button>
      </div>

      {loading ? (
        <p className="text-ink-soft text-sm">Loading records…</p>
      ) : errorMsg ? (
        <p className="text-ledger-brick text-sm ledger-tick">{errorMsg}</p>
      ) : (
        <LedgerTable
          records={records}
          canManage={canManage}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}

      <RecordFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleSubmit}
        initial={editing}
        currentUserId={user?.id}
      />
    </div>
  )
}
