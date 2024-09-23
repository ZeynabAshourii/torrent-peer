package common.command.req;

import common.command.Request;
import common.net.data.Command;
import common.net.data.Entity;
import control.App;

import java.io.Serial;

public class Get extends Request {

    @Serial
    private static final long serialVersionUID = 1187611208765459855L;

    public Get(Entity entity) {
        super(entity);
        addHeader("type", "get");
        addHeader("connection-type", "tcp");
    }

    public Get(Command incoming, Entity sender) {
        super(sender);
        addHeader("file-name", incoming.getHeader("file-name"));
    }

    @Override
    public boolean isValid(Command command) {
        return "get".equals(command.getHeader("type"));
    }

    @Override
    public void run() {
        App.instance.offerFile((String) getHeader("file-name"), entity);
    }
}
