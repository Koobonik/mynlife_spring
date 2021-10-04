package xyz.pwmw.mynlife.util;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

@NoArgsConstructor
@Getter
public class DateCreator {

    private final Date datetime = new Date();
    // 다양한 패턴 생성
    SimpleDateFormat simpleDateFormat_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    SimpleDateFormat simpleDateFormat_yyyy_MM_dd_HH_mm_ss_SSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA);
    SimpleDateFormat simpleDateFormat_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    SimpleDateFormat simpleDateFormat_yyyyMMdd = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);


    // 위 패턴에 맞게 날짜 생성
    String createdDate_yyyy_MM_dd_HH_mm_ss = simpleDateFormat_yyyy_MM_dd_HH_mm_ss.format(datetime);
    String createdDate_yyyy_MM_dd_HH_mm_ss_SSS = simpleDateFormat_yyyy_MM_dd_HH_mm_ss_SSS.format(datetime);

    String createdDate_yyyy_MM_dd = simpleDateFormat_yyyy_MM_dd.format(datetime);
    String createdDate_yyyyMMdd = simpleDateFormat_yyyyMMdd.format(datetime);



    public Timestamp getTimestamp() throws ParseException {
        return new Timestamp(this.simpleDateFormat_yyyy_MM_dd_HH_mm_ss_SSS.parse(this.createdDate_yyyy_MM_dd_HH_mm_ss_SSS).getTime());
    }

    public Timestamp getAfterOneWeek(Timestamp timestamp){
        long oneWeek = 60*60*24*7*1000L;
        long afterOneWeek = timestamp.getTime();
        afterOneWeek += oneWeek;
        Timestamp untilDate = new Timestamp(afterOneWeek);
        return untilDate;
    }
    public Timestamp getAfterFiveMinutes(Timestamp timestamp){
        long oneWeek = 60*5*1000L;
        long afterOneWeek = timestamp.getTime();
        afterOneWeek += oneWeek;
        Timestamp untilDate = new Timestamp(afterOneWeek);
        return untilDate;
    }

    public Timestamp getAfterOneDay(Timestamp timestamp){
        long oneWeek = 60*60*24*1000L;
        long afterOneWeek = timestamp.getTime();
        afterOneWeek += oneWeek;
        Timestamp untilDate = new Timestamp(afterOneWeek);
        return untilDate;
    }

    public Timestamp getTimestamp(SimpleDateFormat simpleDateFormat, String createDate) throws ParseException {
        return new Timestamp(simpleDateFormat.parse(createDate).getTime());
    }

    // 확장성을 위해 ZonedDateTime 미리 적용.
    private final ZonedDateTime zonedDateTime = ZonedDateTime.now();
    public String getZonedDateTime(){
        System.out.println(this.zonedDateTime.toLocalDateTime());
        return this.zonedDateTime.toLocalDateTime().toString().substring(0, 19);
    }

    public Timestamp getAfterThreeMinutes(Timestamp timestamp){
        long oneWeek = 60*3*1000L;
        long afterOneWeek = timestamp.getTime();
        afterOneWeek += oneWeek;
        Timestamp untilDate = new Timestamp(afterOneWeek);
        return untilDate;
    }
}
