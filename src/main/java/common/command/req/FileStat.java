package common.command.req;

import common.command.Request;
import common.net.data.Entity;

import java.io.Serial;

public class FileStat extends Request {

    @Serial
    private static final long serialVersionUID = 141545784986322584L;

    public FileStat(Entity entity) {
        super(entity);
        addHeader("type", "file-stat");
        addHeader("connection-type", "udp");
    }
}
