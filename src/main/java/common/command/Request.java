package common.command;

import common.net.data.Command;
import common.net.data.Entity;

public abstract class Request extends Command {
    public Request(Entity entity) {
        super(entity);
    }

    /*public void run() {
        EventQueue.getInstance().publish(this);
    }*/

    public void run() {

    }
}
