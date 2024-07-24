package application.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -5056721412305133185L;
    private String loginName;
    private String masterPassword;
    private String securityQuestion;
    private String securityAnswer;

    public User() {
    }

    public String getLoginName() {
        return loginName;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public String getQuestion() {
        return securityQuestion;
    }
    
    public String getAnswer() {
    	return securityAnswer;
    }

    public void setLoginName(String newLoginName) {
        this.loginName = newLoginName;
    }

    public void setMasterPassword(String newPassword) {
        this.masterPassword = newPassword;
    }

    public void setSecurityQuestion(String question) {
        this.securityQuestion = question;
    }
    
    public void setAnswer(String answer) {
        this.securityAnswer = answer;
    }
    
}
