package common.net.agent;

public class HostUnreachableException extends Exception{
    public HostUnreachableException() {
        super("Remote host is down!");
    }
}
