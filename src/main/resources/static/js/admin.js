// 로그인 후 사용자 프로필 클릭 버튼 이벤트
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


function loadMemberList(page = 1, size = 5) {
    fetch(`/admin/member_list/ajax?page=${page}&size=${size}`)
        .then(res => res.text())
        .then(html => {
            document.getElementById('page-content-wrapper').innerHTML = html;
        })
        .catch(err => console.error(err));
}

function deleteUser(id) {
    if(!confirm("정말 삭제하시겠습니까?")) return;

    fetch(`/admin/member/${id}`, {
        method: 'DELETE'
    })
        .then(res => {
            if(res.ok) {
                loadMemberList(); // 삭제 후 리스트 새로고침
            } else {
                alert("삭제 실패!");
            }
        })
        .catch(err => console.error(err));
}

function loadUserDetail(id) {
    fetch(`/admin/member/${id}`)
        .then(res => {
            if (!res.ok) throw new Error('회원정보를 불러오는 중 에러 발생');
            return res.json();
        })
        .then(member => {
            // 모달 input에 값 채우기
            const editIdEl = document.getElementById('editId');
            if (!editIdEl) return;

            editIdEl.value = member.id;
            document.getElementById('editUsername').value = member.username;
            document.getElementById('editName').value = member.name;
            document.getElementById('editEmail').value = member.email;
            document.getElementById('editMphone').value = member.mphone;
            document.getElementById('editRole').value = member.role;

            // 모달 띄우기
            const editModal = new bootstrap.Modal(document.getElementById('editModal'));
            editModal.show();
        })
        .catch(err => {
            alert(err.message);
            console.error(err);
        });
}


// 수정 저장
document.addEventListener('DOMContentLoaded', function() {
    const saveBtn = document.getElementById('saveEditBtn');
    if (saveBtn) {
        saveBtn.addEventListener('click', function() {
            const editIdEl = document.getElementById('editId');
            if (!editIdEl) return; // 안전 체크

            const id = editIdEl.value;
            const dto = {
                id: Number(editIdEl.value),
                username: document.getElementById('editUsername').value,
                name: document.getElementById('editName').value,
                email: document.getElementById('editEmail').value,
                mphone: document.getElementById('editMphone').value,
                role: document.getElementById('editRole').value
            };

            fetch(`/admin/member/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(dto)
            })
                .then(res => {
                    if(res.ok) {
                        loadMemberList(); // 수정 후 리스트 새로고침
                        const editModal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                        if(editModal) editModal.hide();
                    } else {
                        alert('수정 실패!');
                    }
                })
                .catch(err => console.error(err));
        });
    }
});

function loadFreeList(page = 1, size = 5) {
    fetch(`/admin/free/list/ajax?page=${page}&size=${size}`)
        .then(res => res.text())
        .then(html => {
            document.getElementById('page-content-wrapper').innerHTML = html;
        })
        .catch(err => console.error(err));
}

function deleteFree(id) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    fetch(`/admin/free/${id}`, { method: 'DELETE' })
        .then(res => {
            if(res.ok) loadFreeList();
            else alert("삭제 실패!");
        })
        .catch(err => console.error(err));
}

function loadPlaceList(page = 1, size = 5) {
    fetch(`/admin/place/list/ajax?page=${page}&size=${size}`)
        .then(res => res.text())
        .then(html => {
            document.getElementById('page-content-wrapper').innerHTML = html;
        })
        .catch(err => console.error(err));
}

function deletePlace(id) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    fetch(`/admin/place/${id}`, { method: 'DELETE' })
        .then(res => {
            if(res.ok) loadPlaceList();
            else alert("삭제 실패!");
        })
        .catch(err => console.error(err));
}

function loadNoticeList(page=1,size=5) {
    fetch(`/admin/notice/list/ajax?page=${page}&size=${size}`)
        .then(res => res.text())
        .then(html => {
            document.getElementById('page-content-wrapper').innerHTML = html;
        })
        .catch(err => console.error(err));
}

function deleteNotice(id) {
    if (!confirm("삭제하시겠습니까?")) return;

    fetch(`/admin/notice/${id}`, { method: 'DELETE' })
        .then(res => {
            if(res.ok) {
                alert("삭제 완료");
                loadNoticeList();
            } else {
                alert("삭제 실패");
            }
        })
        .catch(err => console.error(err));
}
// 공지사항 등록
document.addEventListener('DOMContentLoaded', function () {
    const saveNoticeBtn = document.getElementById('saveNoticeBtn');
    if (saveNoticeBtn) {
        saveNoticeBtn.addEventListener('click', function () {
            const title = document.getElementById('noticeTitle').value.trim();
            const content = document.getElementById('noticeContent').value.trim();

            if (!title || !content) {
                alert("제목과 내용을 입력해주세요.");
                return;
            }

            const dto = { title, content };

            fetch('/admin/notice/register/ajax', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dto)
            })
                .then(res => {
                    if (res.ok) {
                        alert("공지사항 등록 완료!");
                        const modal = bootstrap.Modal.getInstance(document.getElementById('noticeModal'));
                        if (modal) modal.hide();
                        document.getElementById('noticeTitle').value = '';
                        document.getElementById('noticeContent').value = '';
                        loadNoticeList();
                    } else {
                        alert("등록 실패");
                    }
                })
                .catch(err => console.error(err));
        });
    }
});

function loadNoticeDetail(id) {
    fetch(`/admin/notice/${id}`)
        .then(res => res.json())
        .then(notice => {
            document.getElementById('editNoticeId').value = notice.id;
            document.getElementById('editNoticeTitle').value = notice.title;
            document.getElementById('editNoticeContent').value = notice.content;

            const modal = new bootstrap.Modal(document.getElementById('noticeEditModal'));
            modal.show();
        })
        .catch(err => console.error('공지 불러오기 실패:', err));
}
// 공지사항 수정 저장
document.addEventListener('DOMContentLoaded', function () {
    const saveEditBtn = document.getElementById('saveNoticeEditBtn');
    if (saveEditBtn) {
        saveEditBtn.addEventListener('click', function () {
            const id = document.getElementById('editNoticeId').value;
            const title = document.getElementById('editNoticeTitle').value.trim();
            const content = document.getElementById('editNoticeContent').value.trim();

            if (!title || !content) {
                alert("제목과 내용을 입력해주세요.");
                return;
            }

            const dto = { id, title, content };

            fetch(`/admin/notice/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dto)
            })
                .then(res => {
                    if (res.ok) {
                        alert("공지사항 수정 완료!");
                        const modal = bootstrap.Modal.getInstance(document.getElementById('noticeEditModal'));
                        if (modal) modal.hide();
                        loadNoticeList(); // 목록 새로고침
                    } else {
                        alert("수정 실패!");
                    }
                })
                .catch(err => console.error(err));
        });
    }
});