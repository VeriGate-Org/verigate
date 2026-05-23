import { VerificationDetailPage } from "@/components/features/verifications/detail/VerificationDetailPage.client";

export default async function VerificationDetail({
  params,
}: {
  params: Promise<{ correlationId: string }>;
}) {
  const { correlationId } = await params;
  return <VerificationDetailPage correlationId={correlationId} />;
}
