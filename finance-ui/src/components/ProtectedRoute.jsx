import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function ProtectedRoute({ children, allowedRoles }) {
  const { user } = useAuth()

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return (
      <div className="p-10">
        <p className="ledger-tick text-ink-soft text-sm">
          Your role ({user.role}) doesn't have access to this page.
        </p>
      </div>
    )
  }

  return children
}
