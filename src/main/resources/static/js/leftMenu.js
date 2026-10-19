document.addEventListener("DOMContentLoaded", function() {
    const subLinks = document.querySelectorAll(".submenu-panel ul li a");
    const menuItems = document.querySelectorAll(".menu-item:not(.logo)");

    subLinks.forEach(link => {
        link.addEventListener("click", function() {
            // Xóa active của tất cả các mục lớn
            menuItems.forEach(item => item.classList.remove("active"));

            // Thêm active cho mục cha chứa link vừa click
            this.closest(".menu-item").classList.add("active");
        });
    });
});