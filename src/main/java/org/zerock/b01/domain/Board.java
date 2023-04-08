package org.zerock.b01.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 500, nullable = false)
    private String title;
    @Column(length = 2000, nullable = false)
    private String content;
    @Column(length = 50, nullable = false)
    private String writer;

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }


    @OneToMany(mappedBy = "board", // BoardImage의 board변수
            cascade = {CascadeType.ALL}, // 영속성 전이 : 상위 엔티티 모든 상태 변경을 하위 엔티티에 적용
            fetch = FetchType.LAZY, // Lazy
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20) // N + 1 방지 - N 번에 해당하는 쿼리를 모아서 한번에 처리
    private Set<BoardImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName) {

        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);

    }

    public void clearImage() {

        imageSet.forEach(boardImage -> boardImage.changeBoard(null));

        this.imageSet.clear();
    }
}
