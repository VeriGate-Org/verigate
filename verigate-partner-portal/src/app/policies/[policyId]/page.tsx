import EditPolicyPage from "./EditPolicy.client";

export async function generateStaticParams() {
  return [{ policyId: "_" }];
}

export default function PolicyDetailPage() {
  return <EditPolicyPage />;
}
