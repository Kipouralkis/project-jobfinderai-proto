//
// export async function sendMessage(messageText, currentMessages) {
//     const userMsg = { role: "user", content: messageText };
//
//     // Use the messages passed in from the component
//     const fullHistory = [...currentMessages, userMsg];
//
//     const res = await fetch("http://localhost:8081/chat", {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({
//             message: messageText,
//             history: fullHistory
//         })
//     });
//
//     return res.json();
// }