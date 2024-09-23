package common.command.res;

import common.command.Response;
import common.net.data.Command;
import common.net.data.Entity;
import control.App;

import java.io.Serial;

public class OK extends Response {

    @Serial
    private static final long serialVersionUID = -2605079882365807145L;

    public OK(Entity entity) {
        super(entity);
        addHeader("type", "ok");
        addHeader("connection-type", "tcp");
    }

    public OK(Command incoming, Entity sender) {
        super(sender);
        addHeader("file-name", incoming.getHeader("file-name"));
    }

    @Override
    public boolean isValid(Command command) {
        return "ok".equals(getHeader("type"));
    }

    @Override
    public void run() {
        App.instance.downloadSucccessful((String) getHeader("file-name"));
    }
}
