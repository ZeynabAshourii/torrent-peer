package common.command.res;

import common.command.Response;
import common.net.data.Command;
import common.net.data.Entity;
import control.App;

import java.io.Serial;

public class FileShared extends Response {

    @Serial
    private static final long serialVersionUID = 8538370964445195288L;

    public FileShared(Entity entity) {
        super(entity);
    }

    public FileShared(Command incoming, Entity sender) {
        super(sender);
        addHeader("file-name", incoming.getHeader("file-name"));
    }

    @Override
    public boolean isValid(Command command) {
        return "file-shared".equals(command.getHeader("type"));
    }

    @Override
    public void run() {
        App.instance.fileShared(entity, (String) getHeader("file-name"));
    }
}
