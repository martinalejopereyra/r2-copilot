const SESSION_KEY = 'r2_session_id';

let token = null;
let sessionId = localStorage.getItem(SESSION_KEY) || null;
let isWaiting = false;

// configure marked — safe rendering
marked.setOptions({
    breaks: true,    // \n becomes <br> in paragraphs
    gfm: true        // github flavored markdown — tables, code blocks, etc
});

async function login() {
    const partnerId = document.getElementById('partnerId').value.trim();
    if (!partnerId) return;

    document.getElementById('loginError').textContent = '';

    try {
        const response = await fetch('http://localhost:9999/default/token', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `grant_type=client_credentials&client_id=${partnerId}&client_secret=secret`
        });

        if (!response.ok) throw new Error('Auth failed');

        const data = await response.json();
        token = data.access_token;

        document.getElementById('loginSection').style.display = 'none';
        document.getElementById('chatSection').style.display = 'flex';
        document.getElementById('partnerLabel').textContent = partnerId;

        appendMessage('assistant', 'Welcome! I am your R2 integration engineer. How can I help you today?');

    } catch (err) {
        document.getElementById('loginError').textContent = 'Login failed. Is mock-auth running?';
    }
}

async function sendMessage() {
    const input = document.getElementById('messageInput');
    const message = input.value.trim();
    if (!message || isWaiting || !token) return;

    setWaiting(true);
    appendMessage('user', message);
    input.value = '';
    input.style.height = 'auto';

    const responseDiv = appendMessage('assistant', '');
    let fullText = '';

    try {
        const headers = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'text/plain',
            'Accept': 'text/event-stream'
        };
        if (sessionId) headers['X-Session-Id'] = sessionId;

        const response = await fetch('/api/v1/chat', {
            method: 'POST',
            headers,
            body: message
        });

        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const newSessionId = response.headers.get('X-Session-Id');
        if (newSessionId) {
            sessionId = newSessionId;
            localStorage.setItem(SESSION_KEY, sessionId);
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });

            const lines = buffer.split('\n');
            buffer = lines.pop();

            for (const line of lines) {
                if (line.startsWith('data:')) {
                    // decode newline placeholder back to \n
                    const chunk = line.slice(5).replace(/↵/g, '\n');
                    fullText += chunk;
                    renderMarkdown(responseDiv, fullText);
                    scrollToBottom();
                }
            }
        }

        // flush remaining buffer
        if (buffer.startsWith('data:')) {
            const chunk = buffer.slice(5).replace(/↵/g, '\n');
            fullText += chunk;
            renderMarkdown(responseDiv, fullText);
        }

        if (!fullText) responseDiv.textContent = 'No response received.';

    } catch (err) {
        responseDiv.textContent = 'Error — please try again.';
        console.error('Chat error:', err);
    } finally {
        setWaiting(false);
        input.focus();
    }
}

function renderMarkdown(element, text) {
    // use marked to parse markdown — renders bold, lists, code blocks, etc
    element.innerHTML = marked.parse(text);
}

function appendMessage(role, text) {
    const messages = document.getElementById('messages');
    const div = document.createElement('div');
    div.className = `message ${role}`;
    if (role === 'assistant' && text) {
        div.innerHTML = marked.parse(text);
    } else {
        div.textContent = text;
    }
    messages.appendChild(div);
    scrollToBottom();
    return div;
}

function scrollToBottom() {
    const messages = document.getElementById('messages');
    messages.scrollTop = messages.scrollHeight;
}

function setWaiting(waiting) {
    isWaiting = waiting;
    const input = document.getElementById('messageInput');
    const btn = document.getElementById('sendBtn');
    input.disabled = waiting;
    btn.disabled = waiting;
    btn.textContent = waiting ? '...' : 'Send';
}

function newChat() {
    sessionId = null;
    localStorage.removeItem(SESSION_KEY);
    document.getElementById('messages').innerHTML = '';
    appendMessage('assistant', 'New chat started. How can I help you?');
}

document.addEventListener('DOMContentLoaded', () => {
    // Enter on partner ID input triggers login
    const partnerInput = document.getElementById('partnerId');
    if (partnerInput) {
        partnerInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') login();
        });
    }

    // Enter on message input sends message, Shift+Enter adds newline
    const messageInput = document.getElementById('messageInput');
    if (messageInput) {
        messageInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }
});