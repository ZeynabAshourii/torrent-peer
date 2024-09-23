package common.command.res;

import common.command.Response;
import common.net.data.Command;
import common.net.data.Entity;
import control.App;

import java.io.Serial;

public class Accept extends Response {

    @Serial
    private static final long serialVersionUID = 5222011523240354835L;

    public Accept(Entity entity) {
        super(entity);
        addHeader("type", "accept");
        addHeader("connection-type", "tcp");
    }

    public Accept(Command incoming, Entity sender) {
        super(sender);
        addHeader("file-name", incoming.getHeader("file-name"));
        addHeader("content", incoming.getHeader("content"));
    }

    @Override
    public boolean isValid(Command command) {
        return "accept".equals(command.getHeader("type"));
    }

    @Override
    public void run() {
        App.instance.acceptFile((String) getHeader("file-name"), (byte[]) getHeader("content"), entity);
    }
}
