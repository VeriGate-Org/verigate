/* global React, PortalIcon, PortalButton */
const { useState: useStateA, useRef: useRefA, useEffect: useEffectA } = React;

/* ──────────────────────────────────────────────────────────
   AI Chat Sidebar — global slide-in assistant
   Persistent floating launcher → opens 380px right-side panel
   Mirrors verigate-partner-portal/components/ai/AiChatSidebar.client.tsx
   ──────────────────────────────────────────────────────── */

const STARTER_PROMPTS = [
  { text: 'Summarise the open cases',                       icon: 'Briefcase' },
  { text: 'Which verifications are stuck in SLA breach?',   icon: 'BarChart' },
  { text: 'Explain how the sanctions score is calculated',  icon: 'Shield' },
  { text: 'Draft a POPIA-compliant rejection email',        icon: 'FileSearch' },
];

const DEMO_REPLIES = {
  default: "I'm your VeriGate assistant. I can summarise verifications, explain risk scores, draft compliance copy, and help you find anything in the portal. Try one of the prompts on the left, or ask me anything in plain English.",
  cases:   "You have **3 open cases** needing review:\n\n• **CS-7b3a91e2** — Jane Smith · credit fail (risk 87)\n• **CS-1d4b8f12** — Acme Corp · sanctions partial match (risk 78)\n• **CS-5e9d3c47** — Thandiwe Khumalo · suspected synthetic ID (risk 94)\n\nThe last one has been open 26 hours — 6 hours from SLA escalation. Want me to draft an escalation note to compliance?",
  sla:     "Across the last 24 hours:\n\n• **3 verifications** have been in 'In Progress' past their 30-minute SLA\n• All 3 are stuck on **DHA** responses (longest: 1h 12m)\n• DHA's average response time today is **42 minutes** vs target 30m\n\nProvider health shows DHA as **Watch**. Consider pausing high-volume KYC batches until response time recovers.",
  score:   "Sanctions score is a 0–1 fuzzy match between the screened subject and entries in OpenSanctions. The score combines:\n\n• **Name similarity** (Jaro-Winkler, weighted 0.6)\n• **Date-of-birth proximity** (weighted 0.25)\n• **Nationality / jurisdiction match** (weighted 0.15)\n\nThe default threshold is **0.70** — anything above triggers a match. You can tune this per check in *Advanced options*.",
  email:   "Here's a POPIA-compliant rejection draft:\n\n> Dear [Subject],\n> \n> Following our verification process under the Protection of Personal Information Act (POPIA), we regret to inform you that we are unable to proceed with your application at this time.\n> \n> You have the right to request the basis for this decision and to dispute it under POPIA section 23. Please contact io@verigate.co.za within 30 days.\n> \n> Regards,\n> VeriGate Compliance Team\n\nWant me to attach a verification reference and send via the case?",
};

function pickReply(prompt) {
  const p = prompt.toLowerCase();
  if (p.includes('case'))             return DEMO_REPLIES.cases;
  if (p.includes('sla') || p.includes('stuck')) return DEMO_REPLIES.sla;
  if (p.includes('score') || p.includes('sanction')) return DEMO_REPLIES.score;
  if (p.includes('email') || p.includes('popia') || p.includes('draft')) return DEMO_REPLIES.email;
  return "Here's what I can tell from the portal data: you've got 6 active verifications today (5 cleared, 1 in review), 9 open cases, and one provider (Umalusi) is degraded. Want me to dig into any of those?";
}

function ChatMessage({ msg }) {
  const isUser = msg.role === 'user';
  return (
    <div style={{ display: 'flex', gap: 8, padding: '10px 16px', flexDirection: isUser ? 'row-reverse' : 'row', alignItems: 'flex-start' }}>
      <div style={{ width: 28, height: 28, borderRadius: 999, flexShrink: 0, display: 'inline-flex', alignItems: 'center', justifyContent: 'center', background: isUser ? '#1A2E4B' : '#E23D36' }}>
        {isUser ? (
          <span style={{ color: '#fff', fontSize: 11, fontWeight: 600 }}>A</span>
        ) : (
          <svg width="14" height="16" viewBox="20 25 112 122"><path fill="#fff" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/><path d="M46 84 L63 102 L106 58" fill="none" stroke="#E23D36" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round"/></svg>
        )}
      </div>
      <div style={{ maxWidth: '78%', background: isUser ? '#1A2E4B' : '#F8FAFC', color: isUser ? '#fff' : '#1A2024', borderRadius: 8, padding: '10px 12px', fontSize: 12, lineHeight: 1.55, border: isUser ? 'none' : '1px solid #e9ebed' }}>
        {msg.text.split('\n').map((line, i) => {
          // Mini markdown: ** bold **, > quote
          if (line.startsWith('> ')) return <div key={i} style={{ borderLeft: '2px solid #00B3D9', paddingLeft: 8, margin: '4px 0', fontStyle: 'italic', color: isUser ? 'rgba(255,255,255,0.85)' : '#4F5B67' }}>{line.slice(2)}</div>;
          if (line.startsWith('• ')) return <div key={i} style={{ paddingLeft: 14, position: 'relative' }}><span style={{ position: 'absolute', left: 4, color: isUser ? '#00B3D9' : '#00B3D9' }}>•</span>{renderInlineBold(line.slice(2), isUser)}</div>;
          if (!line.trim()) return <div key={i} style={{ height: 6 }} />;
          return <div key={i}>{renderInlineBold(line, isUser)}</div>;
        })}
      </div>
    </div>
  );
}

function renderInlineBold(text, isUser) {
  const parts = text.split(/(\*\*[^*]+\*\*)/g);
  return parts.map((p, i) => {
    if (p.startsWith('**') && p.endsWith('**')) return <b key={i} style={{ color: isUser ? '#fff' : '#1A2024' }}>{p.slice(2, -2)}</b>;
    return <span key={i}>{p}</span>;
  });
}

function AiChatSidebar({ open, onClose }) {
  const [messages, setMessages] = useStateA([
    { role: 'assistant', text: DEMO_REPLIES.default },
  ]);
  const [input, setInput] = useStateA('');
  const [thinking, setThinking] = useStateA(false);
  const scrollRef = useRefA(null);

  useEffectA(() => {
    if (scrollRef.current) scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
  }, [messages, thinking]);

  const send = (text) => {
    if (!text.trim()) return;
    setMessages(m => [...m, { role: 'user', text }]);
    setInput('');
    setThinking(true);
    setTimeout(() => {
      setMessages(m => [...m, { role: 'assistant', text: pickReply(text) }]);
      setThinking(false);
    }, 900);
  };

  if (!open) return null;

  return (
    <div style={{ position: 'fixed', top: 0, right: 0, bottom: 0, width: 400, background: '#fff', borderLeft: '1px solid #D5DBDB', boxShadow: '-12px 0 28px rgba(0,28,36,0.10)', display: 'flex', flexDirection: 'column', zIndex: 90, animation: 'aiSlide 220ms cubic-bezier(0.4,0,0.2,1)' }}>
      <style>{`@keyframes aiSlide{from{transform:translateX(100%)}to{transform:translateX(0)}}`}</style>
      <div style={{ height: 3, display: 'flex' }}>
        <div style={{ flex: 3, background: '#E23D36' }} />
        <div style={{ flex: 5, background: '#1A2E4B' }} />
        <div style={{ flex: 2, background: '#00B3D9' }} />
      </div>
      <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{ width: 30, height: 30, borderRadius: 8, background: 'rgba(0,179,217,0.12)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
            <PortalIcon name="Zap" size={16} color="#00B3D9" />
          </div>
          <div>
            <div style={{ fontSize: 14, fontWeight: 600 }}>VeriGate Assistant</div>
            <div style={{ fontSize: 10, color: '#4F5B67', display: 'inline-flex', alignItems: 'center', gap: 4 }}>
              <span style={{ width: 6, height: 6, borderRadius: 999, background: '#2C974B', boxShadow: '0 0 6px #2C974B' }} />
              Online · context: this portal
            </div>
          </div>
        </div>
        <button onClick={onClose} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: '#4F5B67', fontSize: 22, lineHeight: 1, padding: 0 }}>×</button>
      </div>

      <div ref={scrollRef} style={{ flex: 1, overflowY: 'auto', padding: '12px 0' }}>
        {messages.map((m, i) => <ChatMessage key={i} msg={m} />)}
        {thinking && (
          <div style={{ display: 'flex', gap: 8, padding: '10px 16px', alignItems: 'flex-start' }}>
            <div style={{ width: 28, height: 28, borderRadius: 999, background: '#E23D36', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
              <svg width="14" height="16" viewBox="20 25 112 122"><path fill="#fff" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/><path d="M46 84 L63 102 L106 58" fill="none" stroke="#E23D36" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round"/></svg>
            </div>
            <div style={{ background: '#F8FAFC', border: '1px solid #e9ebed', borderRadius: 8, padding: '10px 14px', fontSize: 12, color: '#4F5B67' }}>
              <span style={{ display: 'inline-flex', gap: 4 }}>
                <span style={{ width: 6, height: 6, borderRadius: 999, background: '#00B3D9', animation: 'aiDot 1.2s ease-in-out infinite' }} />
                <span style={{ width: 6, height: 6, borderRadius: 999, background: '#00B3D9', animation: 'aiDot 1.2s ease-in-out 0.2s infinite' }} />
                <span style={{ width: 6, height: 6, borderRadius: 999, background: '#00B3D9', animation: 'aiDot 1.2s ease-in-out 0.4s infinite' }} />
              </span>
              <style>{`@keyframes aiDot{0%,80%,100%{opacity:0.3}40%{opacity:1}}`}</style>
            </div>
          </div>
        )}
      </div>

      {messages.length <= 2 && (
        <div style={{ padding: '0 16px 12px', display: 'flex', flexDirection: 'column', gap: 6 }}>
          <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Try asking</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
            {STARTER_PROMPTS.map(p => (
              <button key={p.text} onClick={() => send(p.text)} style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '7px 10px', background: '#F8FAFC', border: '1px solid #e9ebed', borderRadius: 6, fontSize: 12, color: '#1A2024', cursor: 'pointer', textAlign: 'left', fontFamily: "'Inter', sans-serif" }}
                onMouseEnter={e => e.currentTarget.style.background = '#fff'}
                onMouseLeave={e => e.currentTarget.style.background = '#F8FAFC'}>
                <PortalIcon name={p.icon} size={12} color="#00B3D9" />
                {p.text}
              </button>
            ))}
          </div>
        </div>
      )}

      <div style={{ padding: '12px 16px', borderTop: '1px solid #e9ebed' }}>
        <form onSubmit={e => { e.preventDefault(); send(input); }} style={{ display: 'flex', gap: 8 }}>
          <input value={input} onChange={e => setInput(e.target.value)} placeholder="Ask anything about your data…"
            style={{ flex: 1, padding: '8px 12px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: "'Inter', sans-serif", outline: 'none' }}
            onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
            onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }} />
          <PortalButton variant="cta" disabled={!input.trim()}>Send →</PortalButton>
        </form>
        <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 6, textAlign: 'center' }}>VeriGate AI · context-aware · data stays in your tenant</div>
      </div>
    </div>
  );
}

function AiChatLauncher({ onOpen }) {
  return (
    <button onClick={onOpen} style={{ position: 'fixed', bottom: 24, right: 24, zIndex: 80, padding: '10px 16px', background: 'linear-gradient(135deg, #1A2E4B, #1a3a5c)', color: '#fff', border: 'none', borderRadius: 999, cursor: 'pointer', fontSize: 13, fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: 8, boxShadow: '0 8px 24px rgba(0,28,36,0.25)', fontFamily: "'Inter', sans-serif", transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)' }}
      onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.boxShadow = '0 12px 28px rgba(0,28,36,0.35), 0 0 24px rgba(0,179,217,0.4)'; }}
      onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = '0 8px 24px rgba(0,28,36,0.25)'; }}>
      <span style={{ width: 24, height: 24, borderRadius: 999, background: 'rgba(0,179,217,0.18)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
        <PortalIcon name="Zap" size={13} color="#00B3D9" />
      </span>
      Ask AI
    </button>
  );
}

window.AiChatSidebar = AiChatSidebar;
window.AiChatLauncher = AiChatLauncher;
