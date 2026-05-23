/* global React */

const webBtn = {
  base: { display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 6, padding: '10px 20px', borderRadius: 6, fontSize: 14, fontWeight: 500, fontFamily: "'Inter', sans-serif", cursor: 'pointer', border: '2px solid transparent', transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)', textDecoration: 'none' },
  hero: { background: 'linear-gradient(90deg,#00B3D9,#0ea5e9,#00B3D9)', backgroundSize: '200%', color: '#fff', fontWeight: 600, boxShadow: '0 4px 15px rgba(0,179,217,0.3)' },
  default: { background: '#1A2E4B', color: '#fff', boxShadow: '0 4px 6px -1px rgba(26,46,75,0.1)' },
  outline: { borderColor: '#1A2E4B', color: '#1A2E4B', background: 'transparent' },
  ghost: { color: '#1A2E4B', background: 'transparent' },
  destructive: { background: '#E23D36', color: '#fff' },
};

function Button({ variant = 'default', children, onClick, style, icon, ...rest }) {
  const [hover, setHover] = React.useState(false);
  const hovered = hover ? {
    hero: { boxShadow: '0 8px 25px rgba(0,179,217,0.5)', transform: 'scale(1.05)', backgroundPosition: 'right' },
    default: { background: '#152640', boxShadow: '0 10px 15px -3px rgba(26,46,75,0.1)' },
    outline: { background: '#1A2E4B', color: '#fff' },
    ghost: { background: 'rgba(0,179,217,0.08)', color: '#00B3D9' },
    destructive: { background: '#c82f29' },
  }[variant] : {};
  return (
    <button
      style={{ ...webBtn.base, ...webBtn[variant], ...hovered, ...style }}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      onClick={onClick}
      {...rest}
    >
      {children}
      {icon && <span style={{ display: 'inline-flex' }}>{icon}</span>}
    </button>
  );
}

window.Button = Button;
