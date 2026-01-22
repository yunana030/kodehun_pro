async function addReply(replyObj){
    try {
        const response = await axios.post(`/replies/`, replyObj)
        console.log(replyObj)
        return response.data
    } catch (error) {
        console.error("댓글 등록 실패:", error)
    }
}
async function getReply(rno){
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}
async function modifyReply(replyObj){
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
}
async function removeReply(rno){
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}
async function getList({type, targetId, page=1, size=5, goLast=false}){
    console.log(page)
    try {
        if (page < 1) page = 1;

        const response = await axios.get(`/replies/list/${type}/${targetId}`, {
            params: {page, size}
        })

        // 총 페이지 계산
        const total = response.data.total
        const totalPages = Math.ceil(total / size)
        const currentPage = page

        // prev/next 존재 여부
        const hasPrev = currentPage > 1
        const hasNext = currentPage < totalPages

        if (goLast) {
            const total = response.data.total
            const lastPage = parseInt(Math.ceil(total / size))
            return getList({type, targetId, page: lastPage, size})
        }
        return {
            ...response.data,
            paging: {
                currentPage,
                totalPages,
                hasPrev,
                hasNext
            }
        }
    } catch (error) {
        console.error("댓글 목록 조회 실패:", error)
    }
}