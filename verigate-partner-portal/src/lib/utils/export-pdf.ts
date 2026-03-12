import html2canvas from "html2canvas";
import { jsPDF } from "jspdf";

export async function exportPdf(
  element: HTMLElement,
  filename: string,
): Promise<void> {
  const canvas = await html2canvas(element, {
    scale: 2,
    useCORS: true,
    logging: false,
  });

  const imgWidth = canvas.width;
  const imgHeight = canvas.height;
  const orientation = imgWidth > imgHeight ? "landscape" : "portrait";

  const pdf = new jsPDF({
    orientation,
    unit: "px",
    format: [imgWidth + 40, imgHeight + 40],
  });

  const imgData = canvas.toDataURL("image/png");
  pdf.addImage(imgData, "PNG", 20, 20, imgWidth, imgHeight);
  pdf.save(filename);
}
