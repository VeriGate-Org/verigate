import CaseDetail from "./CaseDetail.client";

export async function generateStaticParams() {
  return [{ caseId: "_" }];
}

export default function CaseDetailPage() {
  return (
    <div className="max-w-6xl mx-auto">
      <CaseDetail />
    </div>
  );
}
