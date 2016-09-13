package xyz.chat;

/**
 * Created by Abhishek on 13-09-2016.
 */
public class MessageFormat {
    private String name;
    private String message;


    public MessageFormat(String name,String message) {
        this.name=name;
        this.message=message;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
