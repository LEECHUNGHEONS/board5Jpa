package com.ch.board5.controller;


import com.ch.board5.dto.BoardDTO;
import com.ch.board5.dto.CommentDTO;
import com.ch.board5.service.BoardService;
import com.ch.board5.service.CommentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Getter
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;


    @GetMapping("/save")
    public String saveForm(){
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        boardService. save(boardDTO);
        return "index";
    }

    @GetMapping("/")
    public String findAll(Model model){
        //db에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList",boardDTOList);
        return "list";

    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page = 1) Pageable pageable) {
        // 해당게시글의 조회수를 하나 올리고 , 게시글 데이터를 detail.html에 출력
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        // 댓글 목록 가져오기
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList",commentDTOList);
        model.addAttribute("board",boardDTO);
        model.addAttribute("page",pageable.getPageNumber());
        return "detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id,Model model){
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate",boardDTO);
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model){
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board",board);
        return "detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board/";
    }

    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model ){
//        pageble.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        // page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 3
        // 현재 사용자가 7페이지
        // 7 8 9
        // 보여지는 페이지 갯수 3개
        // 총 페이지 갯수 8개

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "paging";
    }














//    // 게시글 생성을 위한 POST 요청 처리
//    @PostMapping("/save")
//    public ResponseEntity<String> save(@RequestBody BoardDTO boardDTO) {
//        boardService.save(boardDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 생성되었습니다.");
//    }
//
//    // 전체 게시글 목록 조회를 위한 GET 요청 처리
//    @GetMapping("/")
//    public ResponseEntity<List<BoardDTO>> findAll() {
//        List<BoardDTO> boardDTOList = boardService.findAll();
//        return ResponseEntity.ok(boardDTOList);
//    }




}
