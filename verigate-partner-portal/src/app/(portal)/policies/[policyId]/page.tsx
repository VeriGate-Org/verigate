import { PolicyDetailPage } from "@/components/features/policies/PolicyDetailPage.client";

export default async function PolicyDetail({
  params,
}: {
  params: Promise<{ policyId: string }>;
}) {
  const { policyId } = await params;
  return <PolicyDetailPage policyId={policyId} />;
}
