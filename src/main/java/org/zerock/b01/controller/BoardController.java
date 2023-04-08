package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.upload.BoardDTO;
import org.zerock.b01.service.BoardService;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    @Value("${org.zerock.upload.path}") // import 시에 springframework으로 시작하는 Value
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {

//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
//        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);
        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);


       log.info("responseDTO = " + responseDTO);

        model.addAttribute("responseDTO", responseDTO);

    }


        @PreAuthorize("hasRole('USER')") // USER 권한 사전 체크
    @GetMapping("/register")
    public void registerGET() {

    }

    @PostMapping("/register")
    public String registerPOST(@Valid BoardDTO boardDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        log.info("board POST register..............");

        if (bindingResult.hasErrors()) {
            log.info("has error..........");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            return "redirect:/board/register";
        }

        log.info("boardDTO = " + boardDTO);

        Long bno = boardService.register(boardDTO);

        redirectAttributes.addFlashAttribute("result", bno);

        return "redirect:/board/list";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/read", "/modify"})
    public void read(Long bno,
                     PageRequestDTO pageRequestDTO,
                     Model model) {

        BoardDTO boardDTO = boardService.readOne(bno);

        log.info("boardDTO = " + boardDTO);

        model.addAttribute("dto", boardDTO);

    }

    // 현재 로그인한 사용자와 게시물 작성자가 동일한 경우에만 권한 부여
    @PreAuthorize("principal.username == #boardDTO.writer")
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                         @Valid BoardDTO boardDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        log.info("board modify POST .....");
        log.info("boardDTO = " + boardDTO);

        if (bindingResult.hasErrors()) {

            log.info("has errors.........");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            redirectAttributes.addAttribute("bno", boardDTO.getBno());

            return "redirect:/board/modify?" + link;

        }

        boardService.modify(boardDTO);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("bno", boardDTO.getBno());

        return "redirect:/board/read";
    }

    @PreAuthorize("principal.username == #boardDTO.writer")
    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO,
                         RedirectAttributes redirectAttributes) {

        Long bno = boardDTO.getBno();

        log.info("remove POST .........");
        log.info("bno = " + bno);

        boardService.remove(bno);

        // 게시물이 데이터베이스 상에서 삭제되었다면 첨부파일 삭제
        log.info(boardDTO.getFileNames());
        List<String> filenames = boardDTO.getFileNames();
        if (filenames != null && filenames.size() > 0) {
            removeFiles(filenames);
        }

        redirectAttributes.addFlashAttribute("result", "remove");

        return "redirect:/board/list";
    }

    public void removeFiles(List<String> files){

        for (String fileName:files) {

            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();


            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                //섬네일이 존재한다면
                if (contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }//end for
    }
}
