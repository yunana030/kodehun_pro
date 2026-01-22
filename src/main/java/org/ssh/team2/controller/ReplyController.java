package org.ssh.team2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.ssh.team2.dto.PageRequestDTO;
import org.ssh.team2.dto.PageResponseDTO;
import org.ssh.team2.dto.ReplyDTO;
import org.ssh.team2.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/replies")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> register(@RequestBody ReplyDTO replyDTO){
        log.debug(replyDTO.toString());
        Map<String,Long> map = new HashMap<>();
        Long rno=replyService.insertReply(replyDTO);
        map.put("rno",rno);
        return map;
    }

    @GetMapping("/list/place/{placeId}")
    public PageResponseDTO<ReplyDTO> getPlaceReplies(
            @PathVariable("placeId") Long placeId, PageRequestDTO pageRequestDTO){
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard("PLACE", placeId, null, pageRequestDTO);
        return responseDTO;
    }
    @GetMapping("/list/free/{freeId}")
    public PageResponseDTO<ReplyDTO> getFreeReplies(
            @PathVariable("freeId") Long freeId, PageRequestDTO pageRequestDTO){
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard("FREE", null, freeId, pageRequestDTO);
        return responseDTO;
    }
    @GetMapping("/{rno}")
    public ReplyDTO read(@PathVariable("rno") Long rno){
        log.info("read"+rno);
        ReplyDTO replyDTO=replyService.findReplyById(rno);
        return replyDTO;
    }
    @DeleteMapping("/{rno}")
    public Map<String,Long> remove(@PathVariable("rno")Long rno){
        Map<String,Long> map=new HashMap<>();
        replyService.deleteReply(rno);
        map.put("rno",rno);
        return map;
    }
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> modify(@PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO){
        replyDTO.setRno(rno);
        replyService.modifyReply(replyDTO);
        Map<String,Long> map=new HashMap<>();
        map.put("rno",rno);
        return map;
    }
}
