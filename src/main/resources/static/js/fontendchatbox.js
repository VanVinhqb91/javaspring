
// --- 1. ĐỊNH NGHĨA THƯ VIỆN STOMP CHUẨN CHẠY ĐỘC LẬP (ĐỒNG BỘ VỚI RABBITMQ) ---
window.Stomp = {
    over: function(wsConnection) {
        var client = { ws: wsConnection, counter: 0, connected: false, subscriptions: {} };

        client.connect = function(headers, connectCallback) {
            client.ws.onopen = function() { client.ws.send("CONNECT\naccept-version:1.1,1.2\n\n\0"); };
            if (client.ws.readyState === 1) { client.ws.send("CONNECT\naccept-version:1.1,1.2\n\n\0"); }

            client.ws.onmessage = function(evt) {
                var data = evt.data || "";
                if (data.startsWith("CONNECTED")) {
                    client.connected = true;
                    if (connectCallback) connectCallback();
                } else {
                    // Phân phối tin nhắn cho các kênh lắng nghe
                    Object.keys(client.subscriptions).forEach(function(subId) {
                        client.subscriptions[subId](evt);
                    });
                }
            };
        };

        client.subscribe = function(destination, callback) {
            var id = "sub-" + client.counter++;
            client.ws.send("SUBSCRIBE\nid:" + id + "\ndestination:" + destination + "\n\n\0");

            client.subscriptions[id] = function(evt) {
                var data = evt.data || "";
                // Bóc tách JSON chính xác tuyệt đối bằng ký tự móc nhọn
                var jsonStartIndex = data.indexOf("{");
                var jsonEndIndex = data.lastIndexOf("}");
                if (jsonStartIndex !== -1 && jsonEndIndex !== -1) {
                    var bodyData = data.substring(jsonStartIndex, jsonEndIndex + 1);
                    if (callback) callback({ body: bodyData });
                }
            };
            return { id: id };
        };

        client.send = function(destination, headers, body) {
            client.ws.send("SEND\ndestination:" + destination + "\n\n" + body + "\0");
        };
        return client;
    }
};

// --- 2. LOGIC XỬ LÝ CHAT PHÍA KHÁCH HÀNG ---
let stompClient = null;
let roomId = null;

// Tự tạo mã định danh duy nhất (Guest ID) nếu chưa có trong LocalStorage
function initGuestSession() {
    roomId = localStorage.getItem('GUEST_CHAT_ID');
    if (!roomId) {
        roomId = 'GUEST_' + Math.random().toString(36).substring(2, 10).toUpperCase();
        localStorage.setItem('GUEST_CHAT_ID', roomId);
    }
    document.getElementById('guestLabel').textContent = "Mã số của bạn: " + roomId;
}

// Bật / Ẩn hộp chat khi bấm vào nút
function toggleChat() {
    const chatBox = document.getElementById('chatBox');
    chatBox.classList.toggle('open');
}

// Thiết lập kết nối WebSocket thông qua STOMP
function connectWebSocket() {
    initGuestSession();

    // Kết nối động dựa theo tên miền hiện hành để tránh lỗi CORS
    const baseEndpoint = window.location.protocol + "//" + window.location.host + "/ws-chat";
    const socket = new SockJS(baseEndpoint);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function() {
        // Đăng ký nhận tin nhắn từ phòng riêng của Khách này (Dùng dấu chấm phân cấp của RabbitMQ)
        stompClient.subscribe('/topic/room.' + roomId, function(response) {
            const message = JSON.parse(response.body);
            displayMessage(message);
        });

        // Gửi tín hiệu thông báo cho bên Admin biết có khách vãng lai mới vào phòng
        const joinMessage = { sender: roomId, content: '', type: 'JOIN' };
        stompClient.send("/app/chat.toRoom/" + roomId, {}, JSON.stringify(joinMessage));
    });
}

// Gửi tin nhắn lên Server Spring Boot
function sendChatMessage() {
    const inputElement = document.getElementById('btnInput');
    const content = inputElement.value.trim();

    if (content && stompClient) {
        const chatMessage = {
            sender: roomId,
            content: content,
            type: 'CHAT'
        };

        // Bắn tin nhắn lên Endpoint xử lý của Spring Boot theo ID phòng
        stompClient.send("/app/chat.toRoom/" + roomId, {}, JSON.stringify(chatMessage));
        inputElement.value = ''; // Xóa sạch ô nhập sau khi gửi
    }
}

// Hỗ trợ bấm phím Enter để gửi tin nhanh
function handleKeyPress(event) {
    if (event.key === 'Enter') {
        sendChatMessage();
    }
}

// Tạo phần tử HTML để đưa tin nhắn hiển thị lên màn hình
function displayMessage(message) {
    const messageArea = document.getElementById('chatMessages');
    if (!messageArea) return;

    const msgElement = document.createElement('div');

    // 1. Tạo thẻ span nhỏ chứa nhãn tên người gửi
    const nameLabel = document.createElement('span');
    nameLabel.style.fontSize = '10px';
    nameLabel.style.display = 'block';
    nameLabel.style.marginBottom = '4px'; // Tăng khoảng cách một chút cho đẹp

    // 2. Tạo một thẻ span/div riêng biệt để chứa nội dung văn bản chat (Tránh dùng thẳng textContent lên thẻ cha)
    const textContainer = document.createElement('div');
    textContainer.style.display = 'inline-block';
    textContainer.style.wordBreak = 'break-word';

    if (message.type === 'CHAT') {
        // Nếu người gửi trùng với mã Guest hiện tại -> Tin nhắn của Khách (Bên phải)
        if (message.sender === roomId) {
            msgElement.className = "message customer";
            nameLabel.textContent = "Bạn (Khách Hàng)";
            textContainer.textContent = message.content;
        }
        // Nếu là phản hồi từ Admin -> Tin nhắn của Nhân viên (Bên trái)
        else {
            msgElement.className = "message admin";
            nameLabel.textContent = "Admin Support";
            textContainer.textContent = message.content;
        }

        // Bỏ nhãn tên và nội dung chữ vào trong bong bóng chat theo đúng thứ tự
        msgElement.appendChild(nameLabel);
        msgElement.appendChild(textContainer);
    }
    // 3. Đẩy toàn bộ bong bóng chat hoàn chỉnh vào khung hiển thị
    messageArea.appendChild(msgElement);

    // Tự động cuộn xuống mốc tin nhắn mới nhất
    messageArea.scrollTop = messageArea.scrollHeight;
}

// Tự động kích hoạt kết nối WebSocket ngay khi trang web được tải xong
window.onload = connectWebSocket;