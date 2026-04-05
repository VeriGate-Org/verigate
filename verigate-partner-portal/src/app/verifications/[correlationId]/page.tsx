import VerificationDetail from "./VerificationDetail.client";

export async function generateStaticParams() {
  return [{ correlationId: "_" }];
}

export default function VerificationDetailPage() {
  return <VerificationDetail />;
}
