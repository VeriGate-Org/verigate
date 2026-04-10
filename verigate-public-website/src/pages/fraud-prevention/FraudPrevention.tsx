import { useParams } from "react-router-dom";
import { fraudPreventionData } from "@/data/fraudPrevention";
import FraudPreventionTemplate from "@/components/templates/FraudPreventionTemplate";
import NotFound from "@/pages/NotFound";

const FraudPrevention = () => {
  const { slug } = useParams<{ slug: string }>();
  const data = fraudPreventionData.find((item) => item.slug === slug);

  if (!data) return <NotFound />;

  return <FraudPreventionTemplate data={data} />;
};

export default FraudPrevention;
