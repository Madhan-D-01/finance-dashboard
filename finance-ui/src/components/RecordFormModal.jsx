import { useState, useEffect } from 'react'

const emptyForm = {
  amount: '',
  type: 'INCOME',
  category: '',
  date: new Date().toISOString().slice(0, 10),
  notes: '',
  userId: ''
}

export default function RecordFormModal({ open, onClose, onSubmit, initial, currentUserId }) {
  const [form, setForm] = useState(emptyForm)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (initial) {
      setForm({
        amount: initial.amount,
        type: initial.type,
        category: initial.category,
        date: initial.date,
        notes: initial.notes || '',
        userId: initial.user?.id || currentUserId || ''
      })
    } else {
      setForm({ ...emptyForm, userId: currentUserId || '' })
    }
    setError(null)
  }, [initial, open, currentUserId])

  if (!open) return null

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      await onSubmit({
        ...form,
        amount: parseFloat(form.amount),
        userId: form.userId ? Number(form.userId) : undefined
      })
      onClose()
    } catch (err) {
      setError(err.response?.data || 'Could not save this record. Check the fields and try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-ink/40 flex items-center justify-center z-50 p-4">
      <div className="card w-full max-w-md p-6 bg-surface">
        <p className="font-display text-xl mb-5">
          {initial ? 'Edit record' : 'New record'}
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Type</label>
              <select
                className="input"
                value={form.type}
                onChange={(e) => setForm({ ...form, type: e.target.value })}
              >
                <option value="INCOME">Income</option>
                <option value="EXPENSE">Expense</option>
              </select>
            </div>
            <div>
              <label className="label">Amount</label>
              <input
                type="number"
                step="0.01"
                required
                className="input num"
                value={form.amount}
                onChange={(e) => setForm({ ...form, amount: e.target.value })}
              />
            </div>
          </div>

          <div>
            <label className="label">Category</label>
            <input
              type="text"
              required
              className="input"
              placeholder="e.g. Salary, Groceries, Rent"
              value={form.category}
              onChange={(e) => setForm({ ...form, category: e.target.value })}
            />
          </div>

          <div>
            <label className="label">Date</label>
            <input
              type="date"
              required
              className="input"
              value={form.date}
              onChange={(e) => setForm({ ...form, date: e.target.value })}
            />
          </div>

          <div>
            <label className="label">Notes (optional)</label>
            <textarea
              className="input"
              rows={2}
              value={form.notes}
              onChange={(e) => setForm({ ...form, notes: e.target.value })}
            />
          </div>

          <div>
            <label className="label">User ID</label>
            <input
              type="number"
              required
              className="input num"
              placeholder="ID of the user this record belongs to"
              value={form.userId}
              onChange={(e) => setForm({ ...form, userId: e.target.value })}
            />
          </div>

          {error && <p className="text-ledger-brick text-xs">{String(error)}</p>}

          <div className="flex justify-end gap-3 pt-2">
            <button type="button" className="btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? 'Saving…' : 'Save record'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
