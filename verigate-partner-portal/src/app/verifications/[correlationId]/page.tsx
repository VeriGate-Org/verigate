import VerificationDetail from "./VerificationDetail.client";

interface Props {
  params: Promise<{ correlationId: string }>;
}

export default async function VerificationDetailPage({ params }: Props) {
  const { correlationId } = await params;
  return <VerificationDetail correlationId={correlationId} />;
}
