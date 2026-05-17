import DocumentVerificationDetail from "./DocumentVerificationDetail.client";

export async function generateStaticParams() {
  return [{ verificationId: "_" }];
}

export default function DocumentVerificationDetailPage() {
  return <DocumentVerificationDetail />;
}
