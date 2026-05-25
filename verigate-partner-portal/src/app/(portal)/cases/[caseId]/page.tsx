import { CaseDetailPage } from "@/components/features/cases/detail/CaseDetailPage.client";

export const revalidate = 0;

export default async function CaseDetail({
  params,
}: {
  params: Promise<{ caseId: string }>;
}) {
  const { caseId } = await params;
  return <CaseDetailPage caseId={caseId} />;
}
