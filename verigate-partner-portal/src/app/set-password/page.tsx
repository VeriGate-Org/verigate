import { Suspense } from "react";
import { SetPasswordForm } from "@/components/features/auth/SetPasswordForm.client";

export default function SetPasswordPage() {
  return (
    <Suspense>
      <SetPasswordForm />
    </Suspense>
  );
}
