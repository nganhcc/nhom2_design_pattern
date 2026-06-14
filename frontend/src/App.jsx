import { Routes, Route } from 'react-router-dom'
import Header from './components/Header'
import Home from './pages/Home'
import Search from './pages/Search'
import Video from './pages/Video'
import Channel from './pages/Channel'
import Upload from './pages/Upload'
import Auth from './pages/Auth'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import './App.css'

function AppRoutes() {
  const { loaded } = useAuth()

  if (!loaded) {
    return <div className="page-content"><p>Loading app state...</p></div>
  }

  return (
    <main className="page-content">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/search" element={<Search />} />
        <Route path="/video/:id" element={<Video />} />
        <Route path="/channel/:id" element={<Channel />} />
        <Route path="/upload" element={<Upload />} />
        <Route path="/auth" element={<Auth />} />
      </Routes>
    </main>
  )
}

function App() {
  return (
    <AuthProvider>
      <div className="app-shell">
        <Header />
        <AppRoutes />
      </div>
    </AuthProvider>
  )
}

export default App
