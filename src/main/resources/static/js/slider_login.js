const track = document.getElementById("slider-track");
const prevBtn = document.getElementById("prev-btn");
const nextBtn = document.getElementById("next-btn");
const dots = document.querySelectorAll(".dot");
const totalSlides = 3;
let currentIndex = 0;

function updateSlider() {
    track.style.transform = `translateX(-${currentIndex * 100}%)`;

    dots.forEach((dot, index) => {
        if (index === currentIndex) {
            dot.className = "dot w-8 h-1.5 bg-black rounded-full transition-all duration-300";
        } else {
            dot.className = "dot w-3 h-1.5 bg-zinc-300 rounded-full transition-all duration-300";
        }
    });
}

function nextSlideRTL() {
    currentIndex = (currentIndex + 1) % totalSlides;
    updateSlider();
}

function prevSlideRTL() {
    currentIndex = (currentIndex - 1 + totalSlides) % totalSlides;
    updateSlider();
}

function goToSlide(index) {
    currentIndex = index;
    updateSlider();
}

nextBtn.addEventListener("click", nextSlideRTL);
prevBtn.addEventListener("click", prevSlideRTL);

let autoPlay = setInterval(nextSlideRTL, 4000);

const sliderSection = document.querySelector("section");
sliderSection.addEventListener("mouseenter", () => clearInterval(autoPlay));
sliderSection.addEventListener("mouseleave", () => (autoPlay = setInterval(nextSlideRTL, 4000)));

// Dang nhap, dang ky
const authModal = document.getElementById("auth-modal");
const authModalCard = document.getElementById("auth-modal-card");
const openLoginBtn = document.getElementById("open-login-btn");
const closeAuthBtn = document.getElementById("close-auth-btn");
const formLogin = document.getElementById("form-login");
const formRegister = document.getElementById("form-register");
const tabLoginBtn = document.getElementById("tab-login-btn");
const tabRegisterBtn = document.getElementById("tab-register-btn");

function openAuthModal(tab = "login") {
    switchTab(tab);
    authModal.classList.remove("invisible", "opacity-0");
    authModal.classList.add("visible", "opacity-100");
    authModalCard.classList.remove("scale-95");
    authModalCard.classList.add("scale-100");
}

function closeAuthModal() {
    authModal.classList.remove("visible", "opacity-100");
    authModal.classList.add("invisible", "opacity-0");
    authModalCard.classList.remove("scale-100");
    authModalCard.classList.add("scale-95");
}

function switchTab(tab) {
    if (tab === "login") {
        formLogin.classList.remove("hidden");
        formRegister.classList.add("hidden");
        tabLoginBtn.className =
            "pb-3 text-sm font-extrabold uppercase tracking-wider border-b-2 border-black text-black w-1/2 text-center transition-colors";
        tabRegisterBtn.className =
            "pb-3 text-sm font-extrabold uppercase tracking-wider border-b-2 border-transparent text-zinc-400 hover:text-black w-1/2 text-center transition-colors";
    } else {
        formLogin.classList.add("hidden");
        formRegister.classList.remove("hidden");
        tabRegisterBtn.className =
            "pb-3 text-sm font-extrabold uppercase tracking-wider border-b-2 border-black text-black w-1/2 text-center transition-colors";
        tabLoginBtn.className =
            "pb-3 text-sm font-extrabold uppercase tracking-wider border-b-2 border-transparent text-zinc-400 hover:text-black w-1/2 text-center transition-colors";
    }
}

openLoginBtn?.addEventListener("click", () => openAuthModal("login"));
closeAuthBtn?.addEventListener("click", closeAuthModal);
authModal?.addEventListener("click", (e) => {
    if (e.target === authModal) closeAuthModal();
});