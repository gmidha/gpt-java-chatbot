// const username = prompt("Enter your username:");
const wsProtocol = window.location.protocol === "https:" ? "wss" : "ws";
const wsHost = window.location.hostname || 'localhost';
const wsPort = window.location.port ? `:${window.location.port}` : '';
const ws = new WebSocket(`${wsProtocol}://${wsHost}${wsPort}/chat/user`);

const chatBox = document.getElementById("chat-box");
const userInput = document.getElementById("user-input");
const sendBtn = document.getElementById("send-btn");
const chatContainer = document.getElementById("chat-container");
const chatToggle = document.getElementById("chat-toggle");
const fullscreenToggle = document.getElementById("fullscreen-toggle");

chatToggle.addEventListener("click", () => {
    chatContainer.style.display = chatContainer.style.display === "none" ? "block" : "none";
});

fullscreenToggle.addEventListener("click", () => {
    chatContainer.classList.toggle("full-screen");
});

ws.onmessage = (event) => {
    appendMessage(event.data, "bot");
};

sendBtn.addEventListener("click", () => {
    sendMessage();
});

userInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
        sendMessage();
    }
});

function sendMessage() {
    const message = userInput.value.trim();
    if (message) {
        appendMessage(message, "user");
        ws.send(message);
        userInput.value = "";
    }
}

function appendMessage(message, sender) {
    const messageElement = document.createElement("div");
    messageElement.classList.add("message", sender);

    // Render Markdown for bot responses
    if (sender === "bot") {
        messageElement.innerHTML = marked.parse(message);
    } else {
        messageElement.textContent = message;
    }

    chatBox.appendChild(messageElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}
