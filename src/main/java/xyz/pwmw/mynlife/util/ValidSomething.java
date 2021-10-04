package xyz.pwmw.mynlife.util;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidSomething {
    public static boolean isValidEmail(String email) {
        boolean err = false;
        // 첫번째 문자는  소문자 or 숫자 ; 그다음은 -_. 와도 됨 뒤에 com 은   2~7글자까지! 왜냐하면 도메인 끝이 company로 끝나는 도메인이 존재함.
//        String regex = "^[0-9a-zA-Z]([-_.]*?[0-9a-zA-Z])*@[0-9a-zA-Z가-힣]([-_.]?[0-9a-zA-Z가-힣])*.[a-zA-Z가-힣]{2,7}$";
        String regex = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidNumber(String number) {
        boolean err = false;
        String regex = "^[0-9]{10,11}$"; //숫자만
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(number);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidPassword(String password) {
        boolean err = false;//                                       `~!@#$%^&*()_+
        String regex = "^.*(?=^.{8,32}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[.,/\\\\!@#$%^*+=-]).*$"; //숫자만
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidName(String name) {
        boolean err = false;//
        //String regex = "^.*(?=^.{2,20}$)[가-힣0-9a-zA-Z].*$"; //숫자만\ //(?=.*\d)(?=.*[a-zA-Z])
//        String regex = "^.*(?=^.{2,20}$)([가-힣])*([a-zA-Z])*([0-9]).*$";
        if (!canUseNickName(name)) {
            return false;
        }
        String regex = "^.*(?=^.{2,14}$)([가-힣-a-z-A-Z])+[가-힣a-zA-Z0-9]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    // 비속어 필터 처리
    public static boolean canUseNickName(String sText) {
        String filter = "fuck|shit|개새끼|씨발|씨1발|씨2발|씨3발|병신|애미|뒤진|니미|시팔|호구|호갱|새키|보지|자지|짬지|고보딩지|중보딩지|초보딩지|대보딩지|섹스|성관계|뒤질|죽을|좆|개년|개새|개씨발|" +
                "고자|급식충|김치녀|꺼저|꺼져|꼬라봐|꼴불견|꼴통|느금마|느개비|니기미|니미럴|샹년|썅년|나가뒤져|닥쳐|돌았냐|뒈지다|딸딸이|또라이|돌아이|똘마니|똘추|띨빵|띨띨이|" +
                "렉카충|맘충|매국노|메갈리아|일베충|무뇌|미친|버러지|변태|따까리|보슬아치|보추|고환|븅딱|바카|빠가|빨갱이|빨통|뻐큐" +
                "빨통|삐꾸|싸가지|썩을|씹년|씹쓰레기|씹치남|씹치녀|애비충|역겹|엠창|잡놈|젖|죶|호로|패드립";
        Pattern p = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sText);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            //System.out.println(m.group());
            m.appendReplacement(sb, maskWord(m.group()));
            return false;
        }
        m.appendTail(sb);

        //System.out.println(sb.toString());
        return true;
    }

    public static boolean isValidDate(String date) {
        boolean err = false;
        String regex = "^(19|20)\\d{2}[- /.]*(0[1-9]|1[012])[- /.]*(0[1-9]|[12][0-9]|3[01])$"; // 날짜 정규식
//        String regex = "^(19|20)\\d{2}[- /.]*(0[0-9]|0[012])[- /.]*(0[0-9]|[12][0-9]|3[01])$"; // 날짜 정규식
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(date);
        if (m.matches()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                dateFormat.parse(date);
                err = true;
            } catch (Exception e) {
                err = false;
            }
        }
        return err;
    }

//    public static String validText(String sText){
//        Pattern p = Pattern.compile(badWordList, Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(sText);
//        StringBuffer sb = new StringBuffer();
//
//        while (m.find()) {
//            // System.out.println(m.group());
//            m.appendReplacement(sb, maskWord(m.group()));
//        }
//
//        m.appendTail(sb);
//
//        //System.out.println(sb.toString());
//        return stripHTML(sb.toString());
//    }
//    public static String stripHTML(String htmlStr) {
//        Pattern p = Pattern.compile("<(?:.|\\s)*?>");
//        Matcher m = p.matcher(htmlStr);
//
//        return m.replaceAll("");
//    }

    public static String maskWord(String word) {
        StringBuffer buff = new StringBuffer();
        char[] ch = word.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (i < 1) {
                buff.append(ch[i]);
            } else {
                buff.append("*");
            }
        }
        return buff.toString();
    }

}