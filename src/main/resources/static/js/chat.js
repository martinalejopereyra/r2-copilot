const chatBox = document.getElementById('chat-box');
const userInput = document.getElementById('user-input');
const sendBtn = document.getElementById('send-btn');
const partnerInput = document.getElementById('partner-id');
const statusBadge = document.querySelector('.status-badge');

let partnerAccessToken = "";

async function autoAuthenticate() {
    const mockServerUrl = "http://localhost:9999/default/token";
    const partnerId = partnerInput.value.trim() || "MercadoLibre";

    const clientSecret = "secret";

    // Encode credentials to Base64 for the 'Authorization: Basic' header
    const encodedCredentials = btoa(`${partnerId}:${clientSecret}`);

    try {
        statusBadge.innerText = "Authenticating...";

        const response = await fetch(mockServerUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': `Basic ${encodedCredentials}` // Fixes the 401
            },
            body: new URLSearchParams({
                'grant_type': 'client_credentials',
                'scope': 'openid',
                'sub': partnerId // Sets the 'sub' claim in the JWT for your Backend
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Auth failed (${response.status}): ${errorText}`);
        }

        const data = await response.json();
        partnerAccessToken = data.access_token;

        // Success UI update
        statusBadge.innerText = "RAG Active";
        statusBadge.style.background = "#27ae60";
        console.log("JWT acquired for partner:", partnerId);

    } catch (error) {
        statusBadge.innerText = "Auth Error";
        statusBadge.style.background = "#e74c3c";
        console.error("Token request failed:", error);
    }
}

/**
 * 2. Función de Renderizado
 */
function appendMessage(role, text) {
    const msgDiv = document.createElement('div');
    msgDiv.className = `msg ${role}`;
    msgDiv.innerText = text;
    chatBox.appendChild(msgDiv);

    // Scroll automático hacia abajo
    chatBox.scrollTop = chatBox.scrollHeight;
}

/**
 * 3. Enviar mensaje al backend
 */
async function sendMessage() {
    const message = userInput.value.trim();
    if (!message) return;

    // Si por alguna razón perdimos el token, intentamos reconectar
    if (!partnerAccessToken) await autoAuthenticate();

    appendMessage('user', message);
    userInput.value = '';

    const typingDiv = document.createElement('div');
    typingDiv.className = 'msg bot typing';
    typingDiv.innerText = '...';
    chatBox.appendChild(typingDiv);

    const savedId = localStorage.getItem('session_id');
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${partnerAccessToken}`,
    };
    if (savedId) {
        headers['X-Session-Id'] = savedId;
    }

    try {
        const response = await fetch(`/api/v1/chat`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(message)
        });

        const newSessionId = response.headers.get('X-Session-Id');
        console.log('Session ID from response:', newSessionId);

       if (newSessionId) {
           localStorage.setItem('session_id', newSessionId);
           console.log('Session ID saved to localStorage:', newSessionId);
       }
        const data = await response.text();
        chatBox.removeChild(typingDiv);
        appendMessage('bot', data);
    } catch (error) {
        if (chatBox.contains(typingDiv)) chatBox.removeChild(typingDiv);
        appendMessage('bot', 'Connection error. Is the backend running?');
    }
}

// Event Listeners
sendBtn.addEventListener('click', sendMessage);
userInput.addEventListener('keypress', (e) => { if (e.key === 'Enter') sendMessage(); });

// Si el usuario cambia el Partner ID en tu input de la cabecera
partnerInput.addEventListener('change', () => {
    partnerAccessToken = "";
    autoAuthenticate();
});

// Inicio automático
window.addEventListener('load', autoAuthenticate);
