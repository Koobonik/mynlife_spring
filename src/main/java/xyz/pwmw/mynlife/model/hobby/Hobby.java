package xyz.pwmw.mynlife.model.hobby;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
// 취미 테이블
public class Hobby {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String largeCategory;
    private String mediumCategory;
    private String smallCategory;

    @Column(unique = true, nullable = false)
    private String mainColor; // 이 취미를 대표하는 컬러, ex 수영 -> 파란색, 등산, 캠핑 -> 초록색,

    @Column(nullable = false)
    private String mainImage; // 대표 이미지

    public Hobby(){

    }
}
