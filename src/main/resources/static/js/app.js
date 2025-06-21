let ws = new WebSocket("ws://" + window.location.host + "/chat");

ws.onopen = () => console.log("Connected");
ws.onmessage = (event) => {
    let data = JSON.parse(event.data);
    let chat = document.getElementById("chat");
    let message = document.createElement("div");
    message.className = "message";
    message.textContent = `${data.name}: ${data.message}`;
    chat.appendChild(message);
    chat.scrollTop = chat.scrollHeight;
};
ws.onerror = () => alert("Connection error");
ws.onclose = () => alert("Disconnected");

function sendMessage() {
    let name = document.getElementById("name").value.trim();
    let message = document.getElementById("message").value.trim();
    if (name && message) {
        ws.send(JSON.stringify({ name: name, message: message }));
        document.getElementById("message").value = "";
    }
}