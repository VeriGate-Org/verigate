import { useParams } from "react-router-dom";
import { industriesData } from "@/data/industries";
import IndustryTemplate from "@/components/templates/IndustryTemplate";
import NotFound from "@/pages/NotFound";

const Solution = () => {
  const { slug } = useParams<{ slug: string }>();
  const data = industriesData.find((item) => item.slug === slug);

  if (!data) return <NotFound />;

  return <IndustryTemplate data={data} />;
};

export default Solution;
