/*

Tooplate 2143 Inner Peace

https://www.tooplate.com/view/2143-inner-peace

Free HTML CSS Template

*/

// JavaScript Document

// Mobile menu toggle
function toggleMenu() {
    const menuToggle = document.querySelector('.menu-toggle');
    const navLinks = document.querySelector('.nav-links');
    if (menuToggle && navLinks) {
        menuToggle.classList.toggle('active');
        navLinks.classList.toggle('active');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const menuLinks = document.querySelectorAll('.nav-link');

    // 1. 페이지 로드 시 Home을 기본 active로 설정 (Home은 /main)
    const currentPath = window.location.pathname;
    menuLinks.forEach(link => {
        link.classList.remove('active');
        const href = link.getAttribute('href');

        // Home (/main) 또는 루트(/)일 때 Home active
        if ((href === '/main' || href === '/') && (currentPath === '/main' || currentPath === '/')) {
            link.classList.add('active');
        }
        // 다른 페이지면 href와 현재 경로가 일치하는지 확인
        else if (href.startsWith('/') && currentPath.startsWith(href)) {
            link.classList.add('active');
        }
    });

    // 2. 클릭 시 active 클래스 이동
    menuLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // 만약 #으로 시작하는 앵커면 스크롤만 하고 active는 URL 기반으로
            const href = this.getAttribute('href');

            // 모든 링크에서 active 제거
            menuLinks.forEach(l => l.classList.remove('active'));
            // 클릭한 링크에 active 추가
            this.classList.add('active');

            // 모바일 메뉴 닫기 (기존 기능 유지)
            const menuToggle = document.querySelector('.menu-toggle');
            const navLinksContainer = document.querySelector('.nav-links');
            if (menuToggle && navLinksContainer) {
                menuToggle.classList.remove('active');
                navLinksContainer.classList.remove('active');
            }
        });
    });

    // // Tab functionality
    // window.showTab = function(tabName) {
    //     const tabs = document.querySelectorAll('.tab-content');
    //     const buttons = document.querySelectorAll('.tab-btn');
    //
    //     tabs.forEach(tab => {
    //         tab.classList.remove('active');
    //     });
    //
    //     buttons.forEach(btn => {
    //         btn.classList.remove('active');
    //     });
    //
    //     const targetTab = document.getElementById(tabName);
    //     if (targetTab) {
    //         targetTab.classList.add('active');
    //     }
    //
    //     // Find and activate the clicked button
    //     buttons.forEach(btn => {
    //         if (btn.textContent.toLowerCase().includes(tabName.toLowerCase())) {
    //             btn.classList.add('active');
    //         }
    //     });
    // };
    //
    // // Form submission handler
    // const contactForm = document.querySelector('.contact-form form');
    // if (contactForm) {
    //     contactForm.addEventListener('submit', (e) => {
    //         e.preventDefault();
    //         alert('Thank you for reaching out! We will get back to you soon.');
    //         e.target.reset();
    //     });
    // }
});