const API_BASE = '';
const API_URL = `${API_BASE}/api/media`;

let mediaData = [];
let currentFolder = 'media_gallery';
let selectedIds = new Set();

// Biến trạng thái phân trang
let currentPage = 1;
let pageSize = 32;

// 1. Tải danh sách ảnh thật từ MySQL khi mở trang
async function loadMediaFromServer() {
    try {
        const res = await fetch(API_URL);
        if (!res.ok) throw new Error('Không thể tải dữ liệu từ server');
        mediaData = await res.json();
        renderGrid();
    } catch (error) {
        console.error('Lỗi tải danh sách ảnh:', error);
    }
}

// 2. Render danh sách ảnh ra lưới (Có kết hợp phân trang)
function renderGrid() {
    const grid = document.getElementById('mediaGrid');
    const keyword = document.getElementById('searchInput').value.toLowerCase();
    const sortBy = document.getElementById('sortSelect').value;

    // Bước A: Lọc dữ liệu
    let filtered = mediaData.filter(item => {
        const matchFolder = (currentFolder === 'media_gallery') 
            ? item.folder !== 'category'
            : item.folder === currentFolder;
        const matchKeyword = item.title.toLowerCase().includes(keyword);
        return matchFolder && matchKeyword;
    });

    // Bước B: Sắp xếp dữ liệu
    if (sortBy === 'name') {
        filtered.sort((a, b) => a.title.localeCompare(b.title));
    } else if (sortBy === 'oldest') {
        filtered.sort((a, b) => a.id - b.id);
    } else {
        filtered.sort((a, b) => b.id - a.id);
    }

    // Bước C: Tính toán phân trang dựa trên mảng đã lọc (filtered)
    const totalRecords = filtered.length;
    const totalPages = Math.max(1, Math.ceil(totalRecords / pageSize));

    if (currentPage > totalPages) {
        currentPage = totalPages;
    }
    if (currentPage < 1) {
        currentPage = 1;
    }

    // Cập nhật thông số hiển thị trên thanh phân trang
    document.getElementById('recordCount').innerText = totalRecords;
    document.getElementById('currentPageInput').value = currentPage;
    document.getElementById('totalPagesText').innerText = totalPages;
    document.getElementById('prevPageBtn').disabled = (currentPage <= 1);
    document.getElementById('nextPageBtn').disabled = (currentPage >= totalPages);

    // Bước D: Cắt mảng chỉ lấy các phần tử thuộc trang hiện tại
    const startIndex = (currentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedItems = filtered.slice(startIndex, endIndex);

    // Bước E: Render HTML
    grid.innerHTML = '';

    paginatedItems.forEach(item => {
        const isSelected = selectedIds.has(item.id);
        const card = document.createElement('div');
        card.className = `media-card ${isSelected ? 'selected' : ''}`;
        card.onclick = () => toggleSelect(item.id);

        const fullImageUrl = item.url.startsWith('http') ? item.url : `${API_BASE}${item.url}`;

        card.innerHTML = `
            <div class="media-thumb">
                <input type="checkbox" class="media-checkbox" ${isSelected ? 'checked' : ''} onclick="event.stopPropagation(); toggleSelect(${item.id})">
                <img src="${fullImageUrl}" alt="${item.title}" loading="lazy">
            </div>
            <div class="media-info">
                <div class="media-details">
                    <div class="media-title" title="${item.title}">${item.title}</div>
                    <div class="media-meta">${item.format} &bull; ${item.width}x${item.height}</div>
                </div>
                <div class="media-more" title="Options">...</div>
            </div>
        `;
        grid.appendChild(card);
    });

    updateActionButtons();
}

// 3. Chọn thư mục bên trái
function selectFolder(folderName, element) {
    currentFolder = folderName;
    selectedIds.clear();
    currentPage = 1; // Reset về trang 1 khi đổi thư mục
    
    document.querySelectorAll('.tree-item').forEach(el => el.classList.remove('active'));
    if (element && element.classList) {
        element.classList.add('active');
    }

    const tag = document.getElementById('activeFolderTag');
    if (folderName === 'all') {
        tag.style.display = 'none';
    } else {
        tag.style.display = 'inline-flex';
        document.getElementById('currentFolderName').innerText = folderName;
    }

    renderGrid();
}

// 4. Tích chọn ảnh
function toggleSelect(id) {
    if (selectedIds.has(id)) {
        selectedIds.delete(id);
    } else {
        selectedIds.add(id);
    }
    renderGrid();
}

// 5. Cập nhật trạng thái nút Delete Images
function updateActionButtons() {
    const btnDeleteImages = document.getElementById('btnDeleteImages');
    btnDeleteImages.disabled = selectedIds.size === 0;
    btnDeleteImages.innerText = selectedIds.size > 0 ? `Delete Images (${selectedIds.size})` : 'Delete Images...';
}

// 6. Tạo thư mục mới trên cây thư mục
async function createFolder() {
    const name = prompt('Nhập tên thư mục mới:');
    if (name && name.trim() !== '') {
        const res = await fetch(`${API_URL}/folders?name=${encodeURIComponent(name)}`, {
            method: 'POST'
        });
        if (res.ok) {
            location.reload();
        }
    }
}

// 7. Xóa thư mục đang chọn
async function deleteCurrentFolder() {
    if (currentFolder === 'media_gallery' || currentFolder === 'category') {
        alert('Không thể xóa thư mục gốc mặc định!');
        return;
    }
    const activeEl = document.querySelector('.tree-item.active');
    const folderId = activeEl ? activeEl.getAttribute('data-id') : null;

    if (!folderId) {
        alert('Không tìm thấy ID thư mục!');
        return;
    }
    if (confirm(`Xóa thư mục "${currentFolder}" và toàn bộ ảnh bên trong?`)) {
        const res = await fetch(`${API_URL}/folders/${folderId}`, {
            method: 'DELETE'
        });
        if (res.ok) {
            location.reload();
        } else {
            alert('Xóa thư mục thất bại!');
        }
    }
}

// 8. Upload ảnh thật từ máy tính lên Spring Boot Server
async function handleUpload(event) {
    const files = event.target.files;
    if (!files || files.length === 0) return;

    const formData = new FormData();
    for (let file of files) {
        formData.append('files', file);
    }
    formData.append('folder', currentFolder === 'media_gallery' ? 'media_gallery' : currentFolder);

    try {
        const res = await fetch(`${API_URL}/upload`, {
            method: 'POST',
            body: formData
        });

        if (res.ok) {
            currentPage = 1; // Quay về trang 1 để thấy ảnh mới upload (nếu sort newest)
            await loadMediaFromServer();
        } else {
            alert('Upload ảnh thất bại, vui lòng kiểm tra dung lượng file hoặc server!');
        }
    } catch (error) {
        console.error('Lỗi upload:', error);
        alert('Không thể kết nối tới máy chủ Spring Boot!');
    } finally {
        event.target.value = '';
    }
}

// 9. Xóa hàng loạt ảnh đang tích chọn
async function deleteSelectedImages() {
    if (selectedIds.size === 0) return;

    if (confirm(`Bạn có chắc muốn xóa vĩnh viễn ${selectedIds.size} ảnh đã chọn?`)) {
        try {
            const res = await fetch(API_URL, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(Array.from(selectedIds))
            });

            if (res.ok) {
                selectedIds.clear();
                await loadMediaFromServer();
            } else {
                alert('Xóa ảnh thất bại!');
            }
        } catch (error) {
            console.error('Lỗi khi xóa ảnh:', error);
        }
    }
}

// 10. Tìm kiếm và Đặt lại bộ lọc
function filterMedia() {
    currentPage = 1; // Reset về trang 1 khi tìm kiếm hoặc đổi kiểu sắp xếp
    renderGrid();
}

function resetFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('sortSelect').value = 'newest';
    selectFolder('media_gallery', document.getElementById('defaultFolderItem'));
}

// 11. Khởi tạo các sự kiện phân trang & tải dữ liệu khi mở trang
document.addEventListener('DOMContentLoaded', () => {
    const pageSizeSelect = document.getElementById('pageSizeSelect');
    const prevBtn = document.getElementById('prevPageBtn');
    const nextBtn = document.getElementById('nextPageBtn');
    const currentPageInput = document.getElementById('currentPageInput');

    // Khởi tạo giá trị pageSize mặc định theo thẻ <select>
    pageSize = parseInt(pageSizeSelect.value, 32) || 32;

    // Đổi số lượng hiển thị (10, 20, 30)
    pageSizeSelect.addEventListener('change', (e) => {
        pageSize = parseInt(e.target.value, 32);
        currentPage = 1;
        renderGrid();
    });

    // Nút trang trước (<)
    prevBtn.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage--;
            renderGrid();
        }
    });

    // Nút trang sau (>)
    nextBtn.addEventListener('click', () => {
        currentPage++;
        renderGrid();
    });

    // Nhập số trang trực tiếp và nhấn Enter
    currentPageInput.addEventListener('change', (e) => {
        let val = parseInt(e.target.value, 32);
        currentPage = isNaN(val) || val < 1 ? 1 : val;
        renderGrid();
    });

    // Tải dữ liệu ban đầu từ Spring Boot
    loadMediaFromServer();
});
