import Link from "next/link";

export default function NotFound() {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center gap-4 text-center">
      <div className="text-6xl font-bold text-[color:var(--color-base-400)]">404</div>
      <h2 className="text-lg font-semibold text-text">Page not found</h2>
      <p className="max-w-md text-sm text-text-muted">
        The page you are looking for does not exist or has been moved.
      </p>
      <Link
        href="/dashboard"
        className="aws-button aws-button--primary mt-2 px-6 py-2"
      >
        Go to Dashboard
      </Link>
    </div>
  );
}
