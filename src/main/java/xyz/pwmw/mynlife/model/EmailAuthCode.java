package xyz.pwmw.mynlife.model;

import lombok.*;
import xyz.pwmw.mynlife.util.DateCreator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.ParseException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class EmailAuthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "INT(11)")
    private long userId;

    @Column(nullable = false)
    private int authNumber;

    @Column(nullable = false, columnDefinition = "datetime")
    private Timestamp createdDate;

    @Column(columnDefinition = "datetime")
    private Timestamp certifiedDate;

    @Column(nullable = false, columnDefinition = "TINYINT(4)")
    private boolean isCanUse = true;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String secret;

    @Column(columnDefinition = "VARCHAR(30)")
    private String whereToUse;

    public EmailAuthCode(int authNumber) throws ParseException {
        this.authNumber = authNumber;
        this.createdDate = new DateCreator().getTimestamp();
    }
}