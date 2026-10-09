import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { toast } from 'react-toastify'
import axios from 'axios'

export default function ArticlePage() {
    const { id } = useParams()
    const [article, setArticle] = useState(null)

    useEffect(() => {
        const loadArticle = async () => {
            try {
                const response = await axios.get(`/api/articles/${id}`)
                setArticle(response.data)
            }
            catch (error) {
                toast.error(error.response.data.error || "Article not found")
            }
        }
        loadArticle()
    }, [id])

    if (!article) {
        return <div className="card"><div className="cardBody">Loading...</div></div>
    }

    return (
        <div className="stack">
            <div className="card">
                <div className="cardBody prose">
                    <h1 className="pageTitle" style={{ marginBottom: 8 }}>{article.title}</h1>
                    <div className="metaRow" style={{ marginBottom: 14 }}>
                        <span>Published: {new Date(article.publishTime).toLocaleString()}</span>
                    </div>

                    <h3>Summary</h3>
                    <p className="muted">{article.summary}</p>

                    <h3>Body</h3>
                    <p>{article.body}</p>
                </div>
            </div>

            {article.references && article.references.length > 0 && (
                <div className="card">
                    <div className="cardBody">
                        <h3 style={{ marginTop: 0 }}>Referenced Articles</h3>
                        <ul style={{ margin: 0, paddingLeft: 18 }}>
                            {article.references.map((ref) => (
                                <li key={ref.id}>
                                    <Link to={`/article/${ref.id}`}>{ref.title}</Link>
                                    <span className="muted"> — referenced by {ref.referenceCount}</span>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            )}
        </div>
    )
}