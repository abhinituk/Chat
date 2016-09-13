package xyz.chat;

/**
 * Created by Abhishek on 13-09-2016.
 */
public class MessageFormat {
    private String name;
    private String text;

    public MessageFormat() {
        super();
    }

    public MessageFormat(String name, String message) {
        this.name=name;
        this.text=message;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return text;
    }

    public void setMessage(String message) {
        this.text = message;
    }
}
