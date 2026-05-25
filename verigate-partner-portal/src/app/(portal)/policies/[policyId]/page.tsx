import { PolicyDetailPage } from "@/components/features/policies/PolicyDetailPage.client";

export const revalidate = 0;

export default async function PolicyDetail({
  params,
}: {
  params: Promise<{ policyId: string }>;
}) {
  const { policyId } = await params;
  return <PolicyDetailPage policyId={policyId} />;
}
