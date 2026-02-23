export default function JsonViewer({ data }: { data: unknown }) {
  return (
    <pre className="overflow-x-auto rounded-md bg-neutral-50 p-3 text-xs leading-relaxed text-neutral-800">
      {JSON.stringify(data, null, 2)}
    </pre>
  );
}

