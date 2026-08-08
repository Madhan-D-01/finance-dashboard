import { useEffect, useState, useCallback } from 'react'
import client from '../api/client'
import UserFormModal from '../components/UserFormModal.jsx'

export default function Users() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [errorMsg, setErrorMsg] = useState(null)
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    setErrorMsg(null)
    try {
      const { data } = await client.get('/api/users')
      setUsers(data)
    } catch (err) {
      setErrorMsg('Could not load users.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const handleCreate = () => {
    setEditing(null)
    setModalOpen(true)
  }

  const handleEdit = (u) => {
    setEditing(u)
    setModalOpen(true)
  }

  const handleSubmit = async (form) => {
    if (editing) {
      await client.put(`/api/users/${editing.id}`, form)
    } else {
      await client.post('/api/users', form)
    }
    load()
  }

  return (
    <div className="p-10 max-w-4xl space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="font-display text-2xl">Users</p>
          <p className="text-ink-soft text-sm mt-1">Manage who has access and their role.</p>
        </div>
        <button className="btn-primary" onClick={handleCreate}>
          + New user
        </button>
      </div>

      {loading ? (
        <p className="text-ink-soft text-sm">Loading users…</p>
      ) : errorMsg ? (
        <p className="text-ledger-brick text-sm ledger-tick">{errorMsg}</p>
      ) : (
        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="hairline text-left">
                <th className="label px-5 py-3">Name</th>
                <th className="label px-5 py-3">Email</th>
                <th className="label px-5 py-3">Role</th>
                <th className="label px-5 py-3">Status</th>
                <th className="label px-5 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="hairline last:border-0 hover:bg-paper/60 transition-colors">
                  <td className="px-5 py-3">{u.name}</td>
                  <td className="px-5 py-3 text-ink-soft">{u.email}</td>
                  <td className="px-5 py-3">
                    <span className="ledger-tick text-xs uppercase tracking-wider">{u.role}</span>
                  </td>
                  <td className="px-5 py-3">
                    <span
                      className={`text-xs uppercase tracking-wider ${
                        u.status === 'ACTIVE' ? 'text-ledger-green' : 'text-ink-soft'
                      }`}
                    >
                      {u.status}
                    </span>
                  </td>
                  <td className="px-5 py-3 text-right">
                    <button
                      onClick={() => handleEdit(u)}
                      className="text-xs uppercase tracking-wider text-ink-soft hover:text-navy transition-colors"
                    >
                      Edit
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <UserFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleSubmit}
        initial={editing}
      />
    </div>
  )
}
