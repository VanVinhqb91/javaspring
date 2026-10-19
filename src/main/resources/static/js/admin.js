const dashboardPage = document.getElementById("dashboardPage"); 
const productsPage = document.getElementById("productsPage"); 
const breadcrumb = document.getElementById("breadcrumb"); 
const menuItems = document.querySelectorAll("[data-page]"); 
menuItems.forEach(item => { item.addEventListener("click", function(e) { 
	e.preventDefault(); 
	const page = this.dataset.page; 
	if (page === "dashboard") { 
		showPage("dashboard"); 
	} 
	if (page === "products") { 
		showPage("products"); 
	} 
}); 
}); 
function showPage(page) { 
	dashboardPage.classList.remove("active-page"); 
	productsPage.classList.remove("active-page"); 
	document.querySelectorAll(".menu-item, .submenu-item").forEach(item => item.classList.remove("active")); 
	if (page === "dashboard") { 
		dashboardPage.classList.add("active-page"); 
		breadcrumb.textContent = "Dashboard"; 
		document.querySelector('[data-page="dashboard"]').classList.add("active"); 
	} 
	if (page === "products") { 
		productsPage.classList.add("active-page"); 
		breadcrumb.textContent = "Catalog / Products"; 
		document.querySelector('[data-page="products"]').classList.add("active"); 
		document.querySelector(".submenu").classList.add("open"); 
	} 
	window.scrollTo(
		{ 
			top: 0, behavior: "smooth" 
		}
	); 
} /* ================= CATALOG MENU ================= */ 
document.getElementById("catalogMenu").addEventListener("click", function() {
	 document.querySelector(".submenu").classList.toggle("open"); 
 }); 
 /* ================= SEARCH ================= */ 
 const searchInput = document.getElementById("productSearch");
 const tableRows = document.querySelectorAll("#productTable tr"); 
 searchInput.addEventListener("input", function() { 
	const keyword = this.value.toLowerCase().trim(); 
	tableRows.forEach(row => { 
		const text = row.textContent.toLowerCase(); 
		row.style.display = text.includes(keyword) ? "" : "none"; 
	}); 
}); /* ================= FILTER ================= */ 
document.getElementById("filterBtn").addEventListener("click", function() { 
	document.getElementById("filterPanel").classList.toggle("show"); 
}); 
/* ================= SELECT ALL ================= */ 
document.getElementById("selectAll").addEventListener("change", function() { 
	document.querySelectorAll("#productTable input[type='checkbox']").forEach(checkbox => { 
		checkbox.checked = this.checked; 
	}); 
}); 
/* ================= MODAL ================= */ 
const modal = document.getElementById("productModal"); 
document.getElementById("addProductBtn").addEventListener("click", function() { 
	modal.classList.add("show"); 
}); 
document.getElementById("closeModal").addEventListener("click", closeModal); 
document.getElementById("cancelModal").addEventListener("click", closeModal); 
modal.addEventListener("click", function(e) { 
	if (e.target === modal) { closeModal(); } 
}); 
function closeModal() { 
	modal.classList.remove("show"); 
} 
	
/* ================= MOBILE SIDEBAR ================= */ 
document.getElementById("mobileMenu").addEventListener("click", function() {
	document.querySelector(".sidebar").classList.toggle("mobile-open"); 
}); 
/* ================= DEFAULT PAGE ================= */ 
showPage("dashboard");
