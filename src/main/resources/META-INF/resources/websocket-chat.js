// const username = prompt("Enter your username:");
const ws = new WebSocket(`ws://localhost:8080/chat/user`);
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
    messageElement.textContent = message;
    chatBox.appendChild(messageElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}
