const formatCurrency = (n) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(n)

export default function LedgerTable({ records, canManage, onEdit, onDelete }) {
  if (!records || records.length === 0) {
    return (
      <div className="card p-10 text-center">
        <p className="text-ink-soft text-sm">
          No records yet. {canManage ? 'Add your first one above.' : 'Check back once records are added.'}
        </p>
      </div>
    )
  }

  return (
    <div className="card overflow-hidden">
      <table className="w-full text-sm">
        <thead>
          <tr className="hairline text-left">
            <th className="th-cell px-5 py-3">Date</th>
            <th className="th-cell px-5 py-3">Category</th>
            <th className="th-cell px-5 py-3">Notes</th>
            <th className="th-cell px-5 py-3 text-right">Amount</th>
            {canManage && <th className="th-cell px-5 py-3 text-right">Actions</th>}
          </tr>
        </thead>
        <tbody>
          {records.map((r) => (
            <tr key={r.id} className="hairline last:border-0 hover:bg-paper/60 transition-colors">
              <td className="px-5 py-3 text-ink-soft num text-xs">{r.date}</td>
              <td className="px-5 py-3">{r.category}</td>
              <td className="px-5 py-3 text-ink-soft truncate max-w-[220px]">{r.notes || '—'}</td>
              <td
                className={`px-5 py-3 text-right num font-medium ${r.type === 'INCOME' ? 'text-ledger-green' : 'text-ledger-brick'
                  }`}
              >
                {r.type === 'INCOME' ? '△ ' : '▽ '}
                {formatCurrency(r.amount)}
              </td>
              {canManage && (
                <td className="px-5 py-3 text-right space-x-3 whitespace-nowrap">
                  <button
                    onClick={() => onEdit(r)}
                    className="text-xs uppercase tracking-wider text-ink-soft hover:text-navy transition-colors"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => onDelete(r)}
                    className="text-xs uppercase tracking-wider text-ink-soft hover:text-ledger-brick transition-colors"
                  >
                    Delete
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}