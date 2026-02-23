import { Suspense } from "react";
import Settings from "@/components/settings/Settings.client";

export default function SettingsPage() {
  return (
    <Suspense>
      <Settings />
    </Suspense>
  );
}
