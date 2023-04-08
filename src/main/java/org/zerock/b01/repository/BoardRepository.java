package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.Board;
import org.zerock.b01.repository.search.BoardSearch;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    @Query(value = "select now()", nativeQuery = true)
    String getTime();

    // 2번의 select가 일어나서 발생하는 오류 (지연 로딩이기에 select이 후 커넥션이 없는 상태에서 2번째 select를 날림)
    // 지연 로딩을 사용하더라도 한 번에 조인 처리해서 select가 이루어 지도록 하는 방법
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno = :bno")
    Optional<Board> findByIdWithImages(Long bno);
}
