package za.co.topitupkeyboard.utils;

public class UserException extends Exception
{

    String message;

    public UserException() {
        super();
    }

    public UserException(String message) //, Throwable cause
    {
        super(message);
        //this.message = message;
    }
}