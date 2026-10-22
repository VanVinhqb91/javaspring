document.addEventListener("DOMContentLoaded", function() {
    const searchInput = document.getElementById("elastic-search-input");
    const suggestionBox = document.getElementById("elastic-suggestion-box");

    // Hàm Debounce kiểm soát tần suất gửi request lên hệ thống
    function debounce(func, delay) {
        let timeoutId;
        return function(...args) {
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
            timeoutId = setTimeout(() => {
                func.apply(this, args);
            }, delay);
        };
    }

    // Hàm gọi API ngầm tới Spring Boot để lấy danh sách gợi ý
    function getAutocomplete(keyword) {
        // Chỉ thực hiện gọi tìm kiếm khi người dùng nhập từ 2 ký tự trở lên
        if (keyword.length < 2) {
            suggestionBox.style.display = "none";
            return;
        }

        // Gọi tới API công khai mà chúng ta thiết lập ở tầng Backend
        fetch(`/api/public/products/suggest?keyword=${encodeURIComponent(keyword)}`)
            .then((response) => response.json())
            .then((products) => {
                suggestionBox.innerHTML = "";

                if (products && products.length > 0) {
                    products.forEach((product) => {
                        const li = document.createElement("li");
                        // Style các phần tử con theo chuẩn giao diện gọn gàng của Tailwind
                        li.className =
                            "flex items-center justify-between px-4 py-3 hover:bg-zinc-50 cursor-pointer border-b border-zinc-100 last:border-b-0 transition-colors";

                        // Định dạng hiển thị tên và giá tiền sản phẩm trực quan
                        li.innerHTML = `
                        <div class="flex flex-col pr-2">
                            <span class="text-xs font-semibold text-zinc-800 tracking-wide truncate max-w-[180px]">${product.name}</span>
                            <span class="text-[10px] text-zinc-400">Thiết bị âm thanh</span>
                        </div>
                        <span class="text-[11px] font-bold text-black bg-zinc-100 px-2 py-0.5 rounded-md flex-shrink-0">
                            ${product.price ? product.price.toLocaleString("vi-VN") + " đ" : "Liên hệ"}
                        </span>
                    `;

                        // Sự kiện khi người dùng click trực tiếp vào một dòng gợi ý
                        li.addEventListener("click", function() {
                            searchInput.value = product.name;
                            suggestionBox.style.display = "none";

                            // CHÚ Ý: Chuyển hướng người dùng sang trang chi tiết sản phẩm dựa vào ID
                            window.location.href = `/product/detail/${product.id}`;
                        });

                        suggestionBox.appendChild(li);
                    });
                    suggestionBox.style.display = "block";
                } else {
                    // Ẩn hộp nếu không tìm thấy bất kỳ sản phẩm nào tương ứng
                    suggestionBox.style.display = "none";
                }
            })
            .catch((error) => console.error("Lỗi đồng bộ hệ thống gợi ý Elastic:", error));
    }

    // Lắng nghe sự kiện người dùng nhập dữ liệu, áp dụng bộ trễ 300ms
    searchInput.addEventListener(
        "input",
        debounce(function() {
            getAutocomplete(this.value.trim());
        }, 300)
    );

    // Tự động đóng hộp gợi ý khi người dùng nhấn chuột ra khu vực bên ngoài thanh tìm kiếm
    document.addEventListener("click", function(e) {
        if (!searchInput.contains(e.target) && !suggestionBox.contains(e.target)) {
            suggestionBox.style.display = "none";
        }
    });

    // Hiển thị lại hộp gợi ý khi người dùng focus chuột lại vào ô input (nếu có sẵn chữ)
    searchInput.addEventListener("focus", function() {
        if (this.value.trim().length >= 2 && suggestionBox.children.length > 0) {
            suggestionBox.style.display = "block";
        }
    });
});