package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testInsert() {

        IntStream.rangeClosed(1, 100).forEach(i -> {
                    Board board = Board.builder()
                            .title("title.....")
                            .content("content.......")
                            .writer("user" + (i % 10))
                            .build();

                    Board result = boardRepository.save(board);
            log.info("result.getBno() = " + result.getBno());
        });
    }

    @Test
    public void testSelect() {
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        log.info("board = " + board);

    }

    @Test
    public void testUpdate() {
        Long bno = 100L;

        Board board = boardRepository.findById(bno).orElseThrow();

        board.change("update...title 100", "update ...content 100");

        boardRepository.save(board);

        board = boardRepository.findById(bno).orElseThrow();

        log.info("board = " + board);
    }

    @Test
    public void testDelete() {
        Long bno = 1L;

        boardRepository.deleteById(bno);
    }

    @Test
    public void testPaging() {

        // 1page order by bno desc
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count : " + result.getTotalElements());
        log.info("total pages : " + result.getTotalPages());
        log.info("result.getNumber() = " + result.getNumber());
        log.info("result.getSize() = " + result.getSize());

        List<Board> todoList = result.getContent();

        todoList.forEach(board -> log.info("board = " + board));
    }

    @Test
    public void testSearch1() {

        //2 page order by bno desc
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());

        boardRepository.search1(pageable);
    }

    @Test
    public void testSearchAll() {

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);


    }

    @Test
    public void testSearchAll2() {

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        // total pages
        log.info("result.getTotalPages() = " + result.getTotalPages());

        // page size
        log.info("result.getSize() = " + result.getSize());

        //pageNumber
        log.info("result.getNumber() = " + result.getNumber());

        //prev next
        log.info("result.hasPrevious() = " + result.hasPrevious());
        log.info("result.hasNext() = " + result.hasNext());

        result.getContent().forEach(board -> log.info("board = " + board));

    }

    @Test
    public void testSearchReplyCount() {

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        log.info("result.getTotalPages() = " + result.getTotalPages());
        log.info("result.getSize() = " + result.getSize());
        log.info("result.getNumber() = " + result.getNumber());
        log.info("result.hasNext() = " + result.hasNext());
        log.info("result.hasPrevious() = " + result.hasPrevious());
        result.getContent().forEach(board -> log.info("board = " + board));

    }

    @Test
    public void testInsertWithImages() {

        Board board = Board.builder()
                .title("Image Test")
                .content("첨부 파일 테스트")
                .writer("tester")
                .build();

        for (int i = 0; i < 3; i++) {

            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");

        }

        boardRepository.save(board);

    }

    @Test
//    @Transactional
    public void testFailReadWithImages() {

        // 반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findById(1L);

        Board board = result.orElseThrow();

        log.info("board = " + board);
        log.info("--------------------------------");
        log.info("board.getImageSet() = " + board.getImageSet());

    }

    @Test
    public void testReadWithImages() {

        // 반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        log.info("board = " + board);
        log.info("--------------------------------");
        log.info("board.getImageSet() = " + board.getImageSet());

    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages() {

        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();
        
        // 기존 첨부 파일들을 삭제
        board.clearImage();
        
        // 새로운 첨부 파일들
        for (int i = 0; i < 2; i++) {

            board.addImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");
        }

        boardRepository.save(board);


    }

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {

        Long bno = 1L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);

    }

    @Test
    public void testInsertAll() {

        for (int i = 1; i <= 100; i++) {

            Board board = Board.builder()
                    .title("Title.." + i)
                    .content("Content.." + i)
                    .writer("writer.." + i)
                    .build();

            for (int j = 0; j < 3; j++) {

                if (i % 5 == 0) {
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(), i+"file" + j + ".jpg");
            }
            boardRepository.save(board);
        }

    }

    @Transactional
    @Test
    public void testSearchImageReplyCount() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

//        boardRepository.searchWithAll(null, null, pageable);

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null, null, pageable);

        log.info("---------------------------------");
        log.info("result.getTotalElements() = " + result.getTotalElements());

        result.getContent().forEach(boardListAllDTO -> log.info("boardListAllDTO = " + boardListAllDTO));
    }


}