let activeEditor = '#editorDescription';
let modalMediaList = [];
let modalSelectedUrls = new Set();

// Hàm dùng chung để khởi tạo CKEditor và gắn sự kiện mở Popup
function initCustomEditor(selector) {
    const element = document.querySelector(selector);
    if (!element) return;

    ClassicEditor
        .create(element, {
            licenseKey: 'GPL',
            image: {
                insert: {
                    type: 'block'
                },
                resizeUnit: '%',
                resizeOptions: [
                    { name: 'resizeImage:original', value: null, label: 'Original' },
                    { name: 'resizeImage:25', value: '25', label: '25%' },
                    { name: 'resizeImage:50', value: '50', label: '50%' },
                    { name: 'resizeImage:75', value: '75', label: '75%' }
                ],
                toolbar: [
                    'imageStyle:alignLeft', 
                    'imageStyle:alignCenter', 
                    'imageStyle:alignRight',  
                    'imageStyle:block',     
                    '|',
                    'resizeImage',
                    '|',
                    'toggleImageCaption',
                    'imageTextAlternative'
                ]
            }
        })
        .then(editor => {
            editor.editing.view.change(writer => {
                writer.setStyle('min-height', "250px", editor.editing.view.document.getRoot());
            });

            const toolbarEl = editor.ui.view.toolbar.element;

            // Bắt sự kiện khi bấm vào nút Insert Media (mediaEmbed) HOẶC Insert Image (uploadImage)
            const mediaButtons = toolbarEl.querySelectorAll(
                'button[data-cke-tooltip-text="media" i], button[data-cke-tooltip-text*="phương tiện" i], .ck-file-dialog-button'
            );

            mediaButtons.forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    e.stopPropagation(); // Chặn hành vi mặc định của CKEditor
                    activeEditor = editor; // Ghi nhớ chính xác khung soạn thảo nào vừa bấm nút
                    openMediaModal();
                }, true); // Bắt ở pha capture để chặn trước CKEditor
            });
        })
        .catch(error => console.error('Lỗi khởi tạo CKEditor:', error));
}

// Khởi tạo cho cả 2 ô mô tả trong form của bạn
document.addEventListener('DOMContentLoaded', () => {
    initCustomEditor('#editorShort');      
    initCustomEditor('#editorDescription');
});

// --- CÁC HÀM XỬ LÝ POPUP MEDIA GALLERY ---
async function openMediaModal() {
    const modal = document.getElementById('mediaGalleryModal');
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    
    modalSelectedUrls.clear();
    updateModalSelectionUI();

    try {
        const res = await fetch('/api/media');
        modalMediaList = await res.json();
        renderModalGrid();
    } catch (err) {
        console.error('Lỗi tải danh sách ảnh:', err);
    }
}

function closeMediaModal() {
    const modal = document.getElementById('mediaGalleryModal');
    modal.classList.add('hidden');
    modal.classList.remove('flex');
}

function renderModalGrid() {
    const grid = document.getElementById('modalMediaGrid');
    const keyword = document.getElementById('modalSearchInput').value.toLowerCase();

    const filtered = modalMediaList.filter(item => 
        item.title.toLowerCase().includes(keyword)
    );

    grid.innerHTML = filtered.map(item => {
        const isSelected = modalSelectedUrls.has(item.url);
        return `
            <div onclick="toggleModalImage('${item.url}')" 
                 class="cursor-pointer bg-white border-2 overflow-hidden transition ${isSelected ? 'border-orange-600 ring-2 ring-orange-200' : 'border-slate-200 hover:border-slate-400'}">
                <div class="aspect-square bg-slate-100 relative flex items-center justify-center overflow-hidden">
                    <img src="${item.url}" alt="${item.title}" class="object-cover w-full h-full" loading="lazy">
                    <input type="checkbox" ${isSelected ? 'checked' : ''} 
                           class="absolute top-2 left-2 w-4 h-4 pointer-events-none accent-orange-600">
                </div>
                <div class="p-2">
                    <div class="text-xs font-medium text-slate-700 truncate" title="${item.title}">${item.title}</div>
                    <div class="text-[11px] text-slate-400">${item.format} &bull; ${item.width}x${item.height}</div>
                </div>
            </div>
        `;
    }).join('');
}

function toggleModalImage(url) {
    if (modalSelectedUrls.has(url)) {
        modalSelectedUrls.delete(url);
    } else {
        modalSelectedUrls.add(url);
    }
    updateModalSelectionUI();
    renderModalGrid();
}

function updateModalSelectionUI() {
    const count = modalSelectedUrls.size;
    document.getElementById('modalSelectedCount').innerText = count;
    document.getElementById('btnInsertSelected').disabled = (count === 0);
}

// Chèn các ảnh đã chọn vào đúng khung CKEditor đang mở
function insertSelectedImagesToEditor() {
    if (modalSelectedUrls.size === 0) return;

    // 1. Đảm bảo luôn lấy được đúng instance của CKEditor đang mở trên trang
    let editor = activeEditor;
    if (!editor) {
        const ckEl = document.querySelector('.ck-editor');
        if (ckEl && ckEl.ckeditorInstance) {
            editor = ckEl.ckeditorInstance;
        }
    }

    if (!editor) {
        alert('Không tìm thấy khung soạn thảo CKEditor!');
        return;
    }

    const urls = Array.from(modalSelectedUrls);

    try {
        // Cách 1: Dùng command chuẩn 'insertImage' của CKEditor 5
        if (editor.commands.get('insertImage')) {
            editor.execute('insertImage', { source: urls });
        } 
        // Cách 2: Dùng command 'imageInsert' (một số bản CDN cũ dùng tên này)
        else if (editor.commands.get('imageInsert')) {
            editor.execute('imageInsert', { source: urls });
        } 
        // Cách 3: Chèn trực tiếp chuỗi HTML thẻ <img> vào vị trí con trỏ (chạy 100% mọi phiên bản)
        else {
            const htmlImages = urls.map(url => `<figure class="image"><img src="${url}" alt="product image"></figure>`).join('');
            const viewFragment = editor.data.processor.toView(htmlImages);
            const modelFragment = editor.data.toModel(viewFragment);
            editor.model.insertContent(modelFragment, editor.model.document.selection);
        }

        // Đưa con trỏ focus lại vào CKEditor sau khi chèn
        editor.editing.view.focus();
    } catch (error) {
        console.error('Lỗi khi chèn ảnh vào CKEditor:', error);
    }

    closeMediaModal();
}


// Upload ảnh mới trực tiếp từ trong Popup lên /api/media/upload
async function handleModalUpload(event) {
    const files = event.target.files;
    if (!files || files.length === 0) return;

    const formData = new FormData();
    for (let file of files) {
        formData.append('files', file);
    }
    formData.append('folder', 'media_gallery');

    try {
        const res = await fetch('/api/media/upload', {
            method: 'POST',
            body: formData
        });
        if (res.ok) {
            // Tải lại danh sách ảnh trong popup sau khi upload xong
            const listRes = await fetch('/api/media');
            modalMediaList = await listRes.json();
            renderModalGrid();
        } else {
            alert('Upload ảnh thất bại!');
        }
    } catch (err) {
        console.error('Lỗi upload:', err);
    } finally {
        event.target.value = '';
    }
}