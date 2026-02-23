import { Suspense } from "react";
import Help from "@/components/help/Help.client";

export default function HelpPage() {
  return (
    <Suspense>
      <Help />
    </Suspense>
  );
}
