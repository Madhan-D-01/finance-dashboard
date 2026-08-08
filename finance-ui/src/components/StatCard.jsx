const tones = {
  green: 'text-ledger-green',
  brick: 'text-ledger-brick',
  ink: 'text-ink'
}

export default function StatCard({ label, value, tone = 'ink', hint }) {
  return (
    <div className="card p-6">
      <p className="label ledger-tick">{label}</p>
      <p className={`num text-3xl font-medium mt-2 ${tones[tone]}`}>
        {value}
      </p>
      {hint && <p className="text-xs text-ink-soft mt-1">{hint}</p>}
    </div>
  )
}
