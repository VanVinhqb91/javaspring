document.addEventListener("DOMContentLoaded", function () {
    const tabItems = document.querySelectorAll(".tab-item");
    const tabContents = document.querySelectorAll(".tab-content");

    tabItems.forEach(item => {
        item.addEventListener("click", function () {
            // 1. Xử lý đổi trạng thái active trên Sidebar Menu
            tabItems.forEach(tab => tab.classList.remove("active"));
            this.classList.add("active");

            // 2. Ẩn toàn bộ nội dung các tab cũ
            tabContents.forEach(content => content.classList.remove("active"));

            // 3. Lấy giá trị data-tab của mục vừa click (ví dụ: "invoices")
            const targetTabId = this.getAttribute("data-tab");

            // 4. Tìm khối nội dung tương ứng (ví dụ: #tab-invoices) và hiển thị lên
            const targetContent = document.getElementById(`tab-${targetTabId}`);
            if (targetContent) {
                targetContent.classList.add("active");
            }
        });
    });
});

// Popup Billing, Shipping Address
const API_BASE_URL = '/api/v1/address'; // Đường dẫn API tương đối, chạy trực tiếp trên localhost

document.addEventListener("DOMContentLoaded", function() {
    // 1. Sự kiện thay đổi dropdown Tỉnh -> Load Huyện
    document.getElementById('modalProvince').addEventListener('change', function() {
        const provinceId = this.value;
        const districtSelect = document.getElementById('modalDistrict');
        const wardSelect = document.getElementById('modalWard');
        
        districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
        wardSelect.innerHTML = '<option value="">-- Chọn Xã/Phường --</option>';
        wardSelect.disabled = true;
        districtSelect.classList.add('bg-gray-100');
        wardSelect.classList.add('bg-gray-100');
        
        if (!provinceId) {
            districtSelect.disabled = true;
            return;
        }
        
        fetch(`${API_BASE_URL}/districts?province_id=${provinceId}`)
            .then(res => res.json())
            .then(data => {
                data.forEach(d => {
                    districtSelect.innerHTML += `<option value="${d.id}">${d.name}</option>`;
                });
                districtSelect.disabled = false;
                districtSelect.classList.remove('bg-gray-100');
            });
    });

    // 2. Sự kiện thay đổi dropdown Huyện -> Load Xã
    document.getElementById('modalDistrict').addEventListener('change', function() {
        const districtId = this.value;
        const wardSelect = document.getElementById('modalWard');
        
        wardSelect.innerHTML = '<option value="">-- Chọn Xã/Phường --</option>';
        wardSelect.disabled = true;
        wardSelect.classList.add('bg-gray-100');
        
        if (!districtId) {
            return;
        }
        
        fetch(`${API_BASE_URL}/wards?district_id=${districtId}`)
            .then(res => res.json())
            .then(data => {
                data.forEach(w => {
                    wardSelect.innerHTML += `<option value="${w.id}">${w.name}</option>`;
                });
                wardSelect.disabled = false;
                wardSelect.classList.remove('bg-gray-100');
            });
    });
});

let currentAddressType  = '';
let currentOrderId = null;

// Hàm mở Modal và kích hoạt class Tailwind
async function openAddressModal(type, orderId, savedAddress) {
    currentAddressType = type;
    currentOrderId = orderId;
    
    document.getElementById('currentAddressType').value = type;
    let addName = (type === "billing") ? "Billing Address" : "Shipping Address";
    document.getElementById('nameSB').innerText = addName;
    
    const provinceSelect = document.getElementById('modalProvince');
    const districtSelect = document.getElementById('modalDistrict');
    const wardSelect = document.getElementById('modalWard');
    const detailInput = document.getElementById('modalDetailAddress');
    
    // Reset lại toàn bộ form về ban đầu
    provinceSelect.innerHTML = '<option value="">-- Chọn Tỉnh/Thành phố --</option>';
    document.getElementById('modalDistrict').innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
    document.getElementById('modalDistrict').disabled = true;
    document.getElementById('modalDistrict').classList.add('bg-gray-100');
    document.getElementById('modalWard').innerHTML = '<option value="">-- Chọn Xã/Phường --</option>';
    document.getElementById('modalWard').disabled = true;
    document.getElementById('modalWard').classList.add('bg-gray-100');
    document.getElementById('modalDetailAddress').value = '';

    try {
        // 3. Lúc này CHỈ CẦN tải danh sách tất cả các Tỉnh (Dữ liệu đơn hàng đã có sẵn ở tham số rồi!)
        const provincesRes = await fetch(`${API_BASE_URL}/provinces`);
        const provinces = await provincesRes.json();

        // 4. Đổ danh sách Tỉnh vào dropdown
        provinces.forEach(p => {
            provinceSelect.innerHTML += `<option value="${p.id}">${p.name}</option>`;
        });

        // 5. Hiển thị Modal lên màn hình (Sử dụng class Tailwind)
        const modal = document.getElementById('addressModal');
        modal.classList.remove('hidden');
        modal.classList.add('flex');

        // 6. TỰ ĐỘNG CHỌN SẴN DỮ LIỆU CŨ TỪ THAM SỐ TRUYỀN SANG
        if (savedAddress && savedAddress.provinceId) {
            // Chọn sẵn Tỉnh cũ
            provinceSelect.value = savedAddress.provinceId;

            // Kích hoạt gọi API tải danh sách Huyện của Tỉnh cũ đó
            const districtsRes = await fetch(`${API_BASE_URL}/districts?province_id=${savedAddress.provinceId}`);
            const districts = await districtsRes.json();
            
            districts.forEach(d => {
                document.getElementById('modalDistrict').innerHTML += `<option value="${d.id}">${d.name}</option>`;
            });
            districtSelect.disabled = false;
            districtSelect.classList.remove('bg-gray-100');
            
            // Chọn sẵn Huyện cũ
            if (savedAddress.districtId) {
                districtSelect.value = savedAddress.districtId;

                // Kích hoạt gọi API tải danh sách Xã của Huyện cũ đó
                const wardsRes = await fetch(`${API_BASE_URL}/wards?district_id=${savedAddress.districtId}`);
                const wards = await wardsRes.json();
                
                wards.forEach(w => {
                    document.getElementById('modalWard').innerHTML += `<option value="${w.id}">${w.name}</option>`;
                });
                wardSelect.disabled = false;
                wardSelect.classList.remove('bg-gray-100');
                
                // Chọn sẵn Xã cũ
                if (savedAddress.wardId) {
                    wardSelect.value = savedAddress.wardId;
                }
            }

            // Điền sẵn Số nhà / Thôn / Xóm cũ
            if (savedAddress.detailAddress) {
                detailInput.value = savedAddress.detailAddress;
            }
        }

    } catch (error) {
        console.error("Lỗi khi kết nối dữ liệu địa lý:", error);
    }
}

// Hàm ẩn Modal
function closeAddressModal() {
    const modal = document.getElementById('addressModal');
    modal.classList.remove('flex');
    modal.classList.add('hidden');
}

// Hàm lưu dữ liệu tạm thời ra giao diện bên ngoài
function saveAddress() {
    const type = document.getElementById('currentAddressType').value;
    
    const provinceSelect = document.getElementById('modalProvince');
    const districtSelect = document.getElementById('modalDistrict');
    const wardSelect = document.getElementById('modalWard');
    const detailInput = document.getElementById('modalDetailAddress');
    
    if(!wardSelect.value || !detailInput.value.trim()) {
        alert("Vui lòng chọn đầy đủ Province/District/Ward và nhập tên thôn/xóm!");
        return;
    }

    const provinceId = provinceSelect.options[provinceSelect.selectedIndex].value;
    const districtId = districtSelect.options[districtSelect.selectedIndex].value;
    const wardId     = wardSelect.options[wardSelect.selectedIndex].value;
                
    const provinceText = provinceSelect.options[provinceSelect.selectedIndex].text;
    const districtText = districtSelect.options[districtSelect.selectedIndex].text;
    const wardText     = wardSelect.options[wardSelect.selectedIndex].text;
    const detailText   = detailInput.value.trim();

    const fullAddressStr = `${detailText}, ${wardText}, ${districtText}, ${provinceText}`;

    const addressSnapshot = {
        provinceId: parseInt(provinceId),
        districtId: parseInt(districtId),
        wardId: parseInt(wardId),
        provinceName: provinceText,
        districtName: districtText,
        wardName: wardText,
        detailAddress: detailText,
        fullAddress: fullAddressStr
    };
    
    // Gửi yêu cầu PUT thẳng lên Spring Boot để ghi đè vào đúng trường JSON của order_id
    fetch(`${API_BASE_URL}/order/${currentOrderId}/update-address?type=${currentAddressType}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(addressSnapshot)
    })
    .then(res => {
        if (res.ok) return res.json();
        throw new Error("Lỗi cập nhật DB");
    })
    .then(data => {
        closeAddressModal();
        window.location.reload();
    })
    .catch(err => {
        alert("Gặp lỗi khi lưu vào database!");
        console.error(err);
    });
}