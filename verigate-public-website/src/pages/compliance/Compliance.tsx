import { useParams } from "react-router-dom";
import { complianceData } from "@/data/compliance";
import ComplianceTemplate from "@/components/templates/ComplianceTemplate";
import NotFound from "@/pages/NotFound";

const Compliance = () => {
  const { slug } = useParams<{ slug: string }>();
  const data = complianceData.find((item) => item.slug === slug);

  if (!data) return <NotFound />;

  return <ComplianceTemplate data={data} />;
};

export default Compliance;
