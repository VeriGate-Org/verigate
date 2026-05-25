import { VerificationDetailPage } from "@/components/features/verifications/detail/VerificationDetailPage.client";

export const revalidate = 0;

export default async function VerificationDetail({
  params,
}: {
  params: Promise<{ correlationId: string }>;
}) {
  const { correlationId } = await params;
  return <VerificationDetailPage correlationId={correlationId} />;
}
