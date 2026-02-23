interface DotPatternProps {
  className?: string;
  opacity?: number;
}

const DotPattern = ({ className = "", opacity = 0.2 }: DotPatternProps) => {
  return (
    <div 
      className={`absolute inset-0 ${className}`}
      style={{
        backgroundImage: `radial-gradient(circle, hsl(var(--foreground)) 1px, transparent 1px)`,
        backgroundSize: '32px 32px',
        opacity: opacity,
      }}
    />
  );
};

export default DotPattern;
