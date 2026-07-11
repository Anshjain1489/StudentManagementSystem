import React, { useState } from 'react';
import api from '../services/api';
import { Sparkles, Send, X, Bot, User, Loader2 } from 'lucide-react';

const AiAssistant = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { sender: 'ai', text: "Hello! I am your AI academic advisor. I can help generate customized study plans, analyze exam reports, or provide performance suggestions. What can I do for you today?" }
  ]);
  const [inputText, setInputText] = useState('');
  const [loading, setLoading] = useState(false);

  const sendMessage = async (e) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    const userMsg = { sender: 'user', text: inputText };
    setMessages((prev) => [...prev, userMsg]);
    setInputText('');
    setLoading(true);

    try {
      const res = await api.post('/ai/chat', { message: userMsg.text });
      setMessages((prev) => [...prev, { sender: 'ai', text: res.data || res.message }]);
    } catch (err) {
      setMessages((prev) => [...prev, { sender: 'ai', text: 'Error connecting to the AI system. Please verify that OpenRouter key is set.' }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {/* Floating Toggle Button */}
      <button 
        onClick={() => setIsOpen(!isOpen)}
        className="btn-premium-primary position-fixed bottom-0 end-0 m-4 d-flex align-items-center gap-2"
        style={{ zIndex: 1050, borderRadius: '50px', padding: '1rem 1.5rem' }}
      >
        <Sparkles size={20} className={isOpen ? 'rotate-180' : 'animate-pulse'} />
        <span>Ask AI Advisor</span>
      </button>

      {/* Floating Chat Container */}
      {isOpen && (
        <div 
          className="ai-assistant-pane position-fixed bottom-0 end-0 m-4 glass-panel animated-fade-in d-flex flex-column"
          style={{ 
            width: '380px', 
            height: '500px', 
            zIndex: 1040, 
            bottom: '75px', 
            boxShadow: '0 10px 40px rgba(0,0,0,0.6)' 
          }}
        >
          {/* Header */}
          <div className="d-flex align-items-center justify-content-between p-3 border-bottom border-secondary">
            <div className="d-flex align-items-center gap-2">
              <Bot className="text-info" size={24} />
              <div>
                <h6 className="mb-0 fw-bold">Academic Copilot</h6>
                <small className="text-secondary">OpenRouter Intelligence</small>
              </div>
            </div>
            <button onClick={() => setIsOpen(false)} className="btn btn-sm btn-link text-secondary p-0">
              <X size={20} />
            </button>
          </div>

          {/* Messages Pane */}
          <div className="flex-grow-1 overflow-y-auto p-3 d-flex flex-column gap-3">
            {messages.map((msg, index) => (
              <div 
                key={index} 
                className={`d-flex gap-2 ${msg.sender === 'user' ? 'align-self-end flex-row-reverse' : 'align-self-start'}`}
                style={{ maxWidth: '85%' }}
              >
                <div 
                  className={`p-2 rounded-3 ${msg.sender === 'user' ? 'bg-primary text-white' : 'bg-secondary text-light'}`}
                  style={{ fontSize: '0.9rem', whiteSpace: 'pre-line' }}
                >
                  {msg.text}
                </div>
              </div>
            ))}
            {loading && (
              <div className="d-flex gap-2 align-items-center text-secondary">
                <Loader2 className="animate-spin text-info" size={16} />
                <span className="small">AI is thinking...</span>
              </div>
            )}
          </div>

          {/* Input Form */}
          <form onSubmit={sendMessage} className="p-3 border-top border-secondary d-flex gap-2">
            <input 
              type="text"
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              placeholder="Ask for study advice, schedule tips..."
              className="form-control form-glass flex-grow-1"
              disabled={loading}
            />
            <button type="submit" className="btn btn-info d-flex align-items-center justify-content-center" disabled={loading}>
              <Send size={16} />
            </button>
          </form>
        </div>
      )}
    </>
  );
};

export default AiAssistant;
