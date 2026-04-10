import { useParams } from "react-router-dom";
import { verificationTypes } from "@/data/verificationTypes";
import VerificationTypeTemplate from "@/components/templates/VerificationTypeTemplate";
import NotFound from "@/pages/NotFound";

const VerificationType = () => {
  const { slug } = useParams<{ slug: string }>();
  const data = verificationTypes.find((item) => item.slug === slug);

  if (!data) return <NotFound />;

  return <VerificationTypeTemplate data={data} />;
};

export default VerificationType;
