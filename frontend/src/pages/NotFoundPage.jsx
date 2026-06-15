import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <section className="page">
      <div className="empty-state">
        <h1>Page not found</h1>
        <Link to="/">Back to home</Link>
      </div>
    </section>
  );
}
