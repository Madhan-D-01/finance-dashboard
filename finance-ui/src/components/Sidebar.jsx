import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

const linkClasses = ({ isActive }) =>
  `ledger-tick block py-2.5 text-sm tracking-wide transition-colors ${
    isActive ? 'text-paper opacity-100' : 'text-paper/60 hover:text-paper/90'
  }`

export default function Sidebar() {
  const { user, logout } = useAuth()

  return (
    <aside className="w-60 shrink-0 bg-navy min-h-screen flex flex-col justify-between py-8 px-6">
      <div>
        <div className="mb-10">
          <p className="font-display text-2xl text-paper leading-none">Ledger</p>
          <p className="text-paper/40 text-[11px] uppercase tracking-[0.2em] mt-1">
            Finance Dashboard
          </p>
        </div>

        <nav className="space-y-1">
          <NavLink to="/" end className={linkClasses}>
            Dashboard
          </NavLink>
          <NavLink to="/records" className={linkClasses}>
            Records
          </NavLink>
          {user?.role === 'ADMIN' && (
            <NavLink to="/users" className={linkClasses}>
              Users
            </NavLink>
          )}
        </nav>
      </div>

      <div className="border-t border-paper/10 pt-4">
        <p className="text-paper/70 text-sm truncate">{user?.email}</p>
        <p className="text-paper/40 text-xs uppercase tracking-wider mb-3">{user?.role}</p>
        <button
          onClick={logout}
          className="text-paper/50 hover:text-paper text-xs uppercase tracking-wider transition-colors"
        >
          Sign out
        </button>
      </div>
    </aside>
  )
}
