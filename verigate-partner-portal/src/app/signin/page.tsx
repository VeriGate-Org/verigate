export default function SignInPage() {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="w-full max-w-md rounded-2xl border border-neutral-200 p-8 shadow-sm text-center">
        <div className="flex flex-col items-center gap-3">
          <span className="inline-flex h-8 w-8 items-center justify-center rounded-md bg-red-600 text-white">✓</span>
          <h1 className="text-xl font-semibold">Authentication Disabled</h1>
        </div>
        <p className="mt-4 text-sm text-neutral-600">
          Sign-in is currently disabled for this environment. Head back to the
          dashboard to continue working without authentication.
        </p>
      </div>
    </div>
  );
}
