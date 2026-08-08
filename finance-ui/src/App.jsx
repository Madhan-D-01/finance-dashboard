import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import Sidebar from './components/Sidebar.jsx'
import Login from './pages/Login.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Records from './pages/Records.jsx'
import Users from './pages/Users.jsx'

function AppLayout({ children }) {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1">{children}</main>
    </div>
  )
}

export default function App() {
  const { user } = useAuth()

  // Viewers can't reach /api/dashboard/** on the backend, so send them
  // straight to Records instead of an empty/blocked Dashboard.
  const homePath = user?.role === 'VIEWER' ? '/records' : '/'

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to={homePath} replace /> : <Login />} />

      <Route
        path="/"
        element={
          <ProtectedRoute allowedRoles={['ADMIN', 'ANALYST']}>
            <AppLayout>
              <Dashboard />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/records"
        element={
          <ProtectedRoute allowedRoles={['ADMIN', 'ANALYST', 'VIEWER']}>
            <AppLayout>
              <Records />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/users"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AppLayout>
              <Users />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<Navigate to={user ? homePath : '/login'} replace />} />
    </Routes>
  )
}
