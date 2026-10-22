// --- ĐỊNH NGHĨA THƯ VIỆN STOMP CHẠY ĐỘC LẬP NỘI BỘ ---
const chatHistory = new Map();
window.Stomp = {
    over: function (wsConnection) {
        var client = { ws: wsConnection, counter: 0, connected: false, subscriptions: {} };
        
		client.connect = function (headers, connectCallback) {
		    client.ws.onopen = function () { client.ws.send("CONNECT\naccept-version:1.1,1.2\n\n\0"); };
		    if (client.ws.readyState === 1) { client.ws.send("CONNECT\naccept-version:1.1,1.2\n\n\0"); }
		    
		    client.ws.onmessage = function (evt) {
		        var data = evt.data || "";
		        if (data.startsWith("CONNECTED")) {
		            client.connected = true;
		            if (connectCallback) connectCallback();
		        } else {
		            // DUYỆT QUA TẤT CẢ CÁC KÊNH ĐÃ ĐĂNG KÝ ĐỂ PHÂN PHỐI TIN NHẮN (KHÔNG BỊ GHI ĐÈ)
		            Object.keys(client.subscriptions).forEach(function (subId) {
		                client.subscriptions[subId](evt);
		            });
		        }
		    };
		};

		client.subscribe = function (destination, callback) {
		    var id = "sub-" + client.counter++;
		    client.ws.send("SUBSCRIBE\nid:" + id + "\ndestination:" + destination + "\n\n\0");
		    
		    client.subscriptions[id] = function (evt) {
		        var data = evt.data || "";
		        
		        // 1. Tìm chính xác vị trí bắt đầu của chuỗi JSON bằng ký tự '{'
		        var jsonStartIndex = data.indexOf("{");
		        var jsonEndIndex = data.lastIndexOf("}");
		        
		        if (jsonStartIndex !== -1 && jsonEndIndex !== -1) {
		            // 2. Cắt đúng khung dữ liệu JSON sạch, loại bỏ hoàn toàn các ký tự rác \0 hoặc header của STOMP
		            var bodyData = data.substring(jsonStartIndex, jsonEndIndex + 1);
		            
		            if (callback) {
		                // Trả về dữ liệu sạch cho hàm gọi xử lý
		                callback({ body: bodyData });
		            }
		        }
		    };
		    return { id: id };
		};
		
		client.send = function (destination, headers, body) {
		    client.ws.send("SEND\ndestination:" + destination + "\n\n" + body + "\0");
		};
		return client;
    }
};

// --- LOGIC XỬ LÝ CHAT CHÍNH ---
let stompClient = null;
let selectedRoomId = null; 
const activeRooms = new Set(); 

function connectAdminDashboard() {
    // Tự động lấy cấu trúc protocol (http/https) và tên miền hiện tại của trình duyệt để nối vào endpoint
    const baseEndpoint = window.location.protocol + "//" + window.location.host + "/ws-chat";
    
    const socket = new SockJS(baseEndpoint);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        console.log("Admin Dashboard đã kết nối WebSocket thành công!");
        
        // 1. Đăng ký lắng nghe nhận tin nhắn của các phòng chat như cũ
        stompClient.subscribe('/topic/room.*', function (response) {
            const message = JSON.parse(response.body);
            handleIncomingSystemMessage(message);
        });

        // 2. ĐĂNG KÝ MỚI: Lắng nghe kênh nổ phòng chat mới thời gian thực khi Admin đang mở máy trực trực tuyến
        stompClient.subscribe('/topic/admin.newRoom', function(messageOutput) {
            var newRoomData = JSON.parse(messageOutput.body);
            console.log("Phát hiện khách vãng lai mới gõ câu đầu tiên:", newRoomData.roomId);
            if (!activeRooms.has(newRoomData.roomId)) {
                activeRooms.add(newRoomData.roomId);
                updateCustomerSidebar(); // Gọi hàm tự động vẽ phòng mới lên sidebar Admin tức thì
            }
        });

        // 3. KÍCH HOẠT: Đồng bộ hốt toàn bộ danh sách phòng khách đang chờ trên RAM hệ thống về Sidebar luôn
        loadActiveRoomsFromServer();
    });
}

function handleIncomingSystemMessage(message) {
    const senderId = message.sender;
	const roomId = senderId === 'ADMIN_STAFF' ? selectedRoomId : senderId;

	if (!roomId) return;
	
	if (!activeRooms.has(roomId) && senderId !== 'ADMIN_STAFF') {
	    activeRooms.add(roomId);
	    updateCustomerSidebar();
	}
	
	if (!chatHistory.has(roomId)) {
	    chatHistory.set(roomId, []);
	}
	chatHistory.get(roomId).push(message);

	// Nếu tin nhắn thuộc về người đang được chọn xem -> Hiển thị ngay lên màn hình
	if (roomId === selectedRoomId) {
	    displayMessageInArea(message);
	}
}

function updateCustomerSidebar() {
    const listContainer = document.getElementById('customerList');
    if (!listContainer) return;
    
    listContainer.innerHTML = '';
    document.getElementById('countActive').textContent = activeRooms.size - 1;

    activeRooms.forEach(roomId => {
		if (roomId) {
			const li = document.createElement('li');
			li.className = `customer-item ${roomId === selectedRoomId ? 'active' : ''}`;
			li.onclick = () => selectCustomerRoom(roomId);
			li.innerHTML = `<span>👤 Khách vãng lai (${roomId})</span><span class="badge-online"></span>`;
			listContainer.appendChild(li);
		}
    });
}

function loadActiveRoomsFromServer() {
    fetch('/api/chat/active-rooms')
        .then(response => response.json())
        .then(rooms => {
            console.log("Dữ liệu Server trả về:", rooms);
            
            // 1. Nạp dữ liệu vào Set activeRooms để quản lý
            Object.keys(rooms).forEach(roomId => {
                if (!activeRooms.has(roomId)) {
                    activeRooms.add(roomId);
                }
            });

            // 2. GIẢI PHÁP ÉP HIỂN THỊ CHUẨN XÁC
            let listContainer = document.getElementById('customerList');
            if (!listContainer) {
                listContainer = document.querySelector('.sidebar-menu, ul, .customer-list');
            }

            let countElement = document.getElementById('countActive');
            if (!countElement) {
                countElement = document.querySelector('.badge, span, #count');
            }

            // 3. THỰC HIỆN VÀ VÁ ĐOẠN CODE BỊ THIẾU CŨ ĐỂ VẼ LÊN SÀN
            if (listContainer) {
                listContainer.innerHTML = '';
                if (countElement) {
                    countElement.textContent = activeRooms.size;
                }

                activeRooms.forEach(roomId => {
					if (roomId) {
						const li = document.createElement('li');
						li.className = `customer-item ${roomId === selectedRoomId ? 'active' : ''}`;
						li.style.cursor = 'pointer';
						li.onclick = () => selectCustomerRoom(roomId);
						li.innerHTML = `<span>👤 Khách vãng lai (${roomId.substring(6, 14)})</span><span class="badge-online"></span>`;
						listContainer.appendChild(li);
					}
                });
            } else {
                console.error("LỖI GIAO DIỆN: Không tìm thấy bất kỳ thẻ HTML nào để chèn danh sách khách!");
            }
        })
        .catch(error => console.error("Lỗi đồng bộ danh sách phòng từ RAM Server:", error));
}

function selectCustomerRoom(roomId) {
    selectedRoomId = roomId;
    updateCustomerSidebar();

    document.getElementById('adminInput').disabled = false;
    document.getElementById('sendBtn').disabled = false;
    document.getElementById('activeChatHeader').textContent = "💬 Đang hỗ trợ: Khách vãng lai (" + roomId + ")";

    const messageArea = document.getElementById('chatMessages');
    messageArea.innerHTML = '<div class="message system">Hệ thống: Đã kết nối vào phòng chat riêng của khách.</div>';
	
	// ĐỌC VÀ ĐẨY NGƯỢC LẠI TOÀN BỘ TIN NHẮN CŨ TRONG KHO LƯU TRỮ TẠM LÊN MÀN HÌNH
	if (chatHistory.has(roomId)) {
	    const history = chatHistory.get(roomId);
	    history.forEach(msg => {
	        displayMessageInArea(msg); // Gọi hàm sinh bong bóng chat tương ứng cho từng tin nhắn cũ
	    });
	}
}

function sendAdminReply() {
    const inputElement = document.getElementById('adminInput');
    const content = inputElement.value.trim();

    if (content && stompClient && selectedRoomId) {
        const adminMessage = { sender: 'ADMIN_STAFF', content: content, type: 'CHAT' };
        stompClient.send("/app/chat.toRoom/" + selectedRoomId, {}, JSON.stringify(adminMessage));
        inputElement.value = ''; 
    }
}

// Bắt sự kiện bấm nút Enter trên bàn phím
document.getElementById('adminInput').addEventListener('keypress', function(event) {
    if (event.key === 'Enter') { sendAdminReply(); }
});

function displayMessageInArea(message) {
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

    if (message.type === 'JOIN') {
        msgElement.className = "message system";
        msgElement.textContent = `Hệ thống: Khách hàng ${message.sender} đã trực tuyến.`;
		// Tin nhắn hệ thống không cần hiện chữ Bạn hoặc Admin
		msgElement.appendChild(textContainer);
    } else {
        if (message.sender === 'ADMIN_STAFF') {
            msgElement.className = "message admin";
            textContainer.textContent = message.content;
			nameLabel.textContent = "Admin Support";
        } else {
            msgElement.className = "message customer";
            textContainer.textContent = message.content;
			nameLabel.textContent = `Khách: ${message.sender}`;
        }
		// Bỏ nhãn tên và nội dung chữ vào trong bong bóng chat theo đúng thứ tự
		msgElement.appendChild(nameLabel);
		msgElement.appendChild(textContainer);
    }
    messageArea.appendChild(msgElement);
    messageArea.scrollTop = messageArea.scrollHeight; 
}

// Kích hoạt hàm chạy khi bật trang
window.onload = connectAdminDashboard;
