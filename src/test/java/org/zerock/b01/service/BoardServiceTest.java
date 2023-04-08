package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.upload.BoardDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.upload.BoardImageDTO;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Log4j2
@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardService boardService;



    @Test
    public void testRegister() {

        log.info("boardService : " + boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("Sample Title....")
                .content("Sample Content....")
                .writer("user00")
                .build();

        Long bno = boardService.register(boardDTO);

        log.info("bno = " + bno);

    }

    @Test
    public void testUpdate() {

        log.info("Update ---------------------");

        BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("Update title")
                .content("Update content")
                .build();

        boardService.modify(boardDTO);

    }

    @Test
    public void testList() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info("responseDTO = " + responseDTO);
    }


    @Test
    public void testRegisterWithImages() {

        log.info("boardService.getClass().getName() = " + boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("File...Sample Title...")
                .content("Sample Content...")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_ccc.jpg"
                ));

        Long bno = boardService.register(boardDTO);

        log.info("bno = " + bno);
    }

    @Test
    public void testReadAll() {

        Long bno = 101L;

        BoardDTO boardDTO = boardService.readOne(bno);

        log.info("boardDTO = " + boardDTO);

        for (String fileName : boardDTO.getFileNames()) {
            log.info("fileName = " + fileName);
        }

    }

    @Test
    public void testModify() {

        // 변경에 필요한 데이터
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("Updated....101")
                .content("Updated content 101.....")
                .build();

        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID() + "_" + "zzz.jpg"));

        boardService.modify(boardDTO);
    }

    @Test
    public void testRemoveAll() {

        Long bno = 1L;

        boardService.remove(bno);

    }

    @Test
    public void testListWithAll() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO -> {

            log.info(boardListAllDTO.getBno() + " : " + boardListAllDTO.getTitle());

            if (boardListAllDTO.getBoardImages() != null) {
                for (BoardImageDTO boardImage : boardListAllDTO.getBoardImages()) {
                    log.info("boardImage = " + boardImage);
                }
            }

            log.info("================================================================");

        });

    }

}


























