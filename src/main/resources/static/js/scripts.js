/*!
* Start Bootstrap - Simple Sidebar v6.0.6 (https://startbootstrap.com/template/simple-sidebar)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-simple-sidebar/blob/master/LICENSE)
*/
// 
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {

        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

    // 회원 누르면 회원 정보 모달 띄우기
    (function(){
        const btn = document.getElementById('userMenuBtn');
        const menu = document.getElementById('userMenu');
        if(!btn || !menu) return;

        const open = ()=>{ menu.classList.add('show'); btn.setAttribute('aria-expanded','true'); }
        const close = ()=>{ menu.classList.remove('show'); btn.setAttribute('aria-expanded','false'); }

        btn.addEventListener('click', (e)=>{
        e.stopPropagation();
        menu.classList.toggle('show');
        btn.setAttribute('aria-expanded', menu.classList.contains('show') ? 'true' : 'false');
    });

    document.addEventListener('click', (e)=>{
        if(menu.classList.contains('show') && !menu.contains(e.target) && e.target !== btn) close();
    });

    document.addEventListener('keydown', (e)=>{ if(e.key === 'Escape') close(); });
    })();

    (() => {
        const box = document.querySelector('.userbox');
        const btn = document.getElementById('userMenuBtn');
        if (box && btn) {
        btn.addEventListener('click', () => box.classList.toggle('open'));
        document.addEventListener('click', (e) => {
        if (!box.contains(e.target)) box.classList.remove('open');
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') box.classList.remove('open');
    });
    }
    })();

    // 프로필 이미지 파일 버튼
    (()=>{ const t=document.getElementById('avatarThumb');
        const i=document.getElementById('avatarInput');
        const f=document.getElementById('avatarForm');
        if(t&&i&&f){ t.addEventListener('click',()=>i.click());
            i.addEventListener('change',()=>{ if(i.files && i.files[0]) f.submit(); });
        }})();

});
