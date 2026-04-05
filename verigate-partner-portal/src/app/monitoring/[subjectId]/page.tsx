import SubjectDetail from "./SubjectDetail.client";

export async function generateStaticParams() {
  return [{ subjectId: "_" }];
}

export default function SubjectDetailPage() {
  return (
    <div className="max-w-6xl mx-auto">
      <SubjectDetail />
    </div>
  );
}
