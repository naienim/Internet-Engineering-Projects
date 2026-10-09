import { Routes, Route, Link } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import HomePage from './pages/HomePage'
import ArticlePage from './pages/ArticlePage'
import AddArticlePage from './pages/AddArticlePage'

function App() {
    return (
        <div>
            <header className="navbar">
                <div className="container navbarInner">
                    <div className="brand">
                        <Link to="/" style={{ textDecoration: 'none' }}>UT Article Platform</Link>
                    </div>

                    <nav className="navLinks">
                        <Link className="navLink" to="/">Home</Link>
                        <Link className="navLink" to="/add-article">Submit Article</Link>
                    </nav>
                </div>
            </header>

            <main className="container page">
                <Routes>
                    <Route path="/" element={<HomePage/>} />
                    <Route path="/article/:id" element={<ArticlePage/>}/>
                    <Route path="/add-article" element={<AddArticlePage/>}/>
                </Routes>
            </main>

            <ToastContainer position="bottom-right"/>
        </div>
    )
}

export default App