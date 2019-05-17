package Model;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.io.Serializable;

public class Question implements Serializable {
    private Integer id;
    private Constant.SUBJECT subject;
    private String text;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private Integer answer;

    public Question(Integer id, String subject, String text, String option1, String option2, String option3, String option4, Integer answer) {
        this.id = id;
        this.subject = Constant.SUBJECT.valueOf(subject);
        this.text = text;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Constant.SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = Constant.SUBJECT.valueOf(subject);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }
}
