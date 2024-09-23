package common.command.req;

import common.command.Request;
import common.net.data.Entity;

import java.io.Serial;

public class Seed extends Request {

    @Serial
    private static final long serialVersionUID = 2225373047916203135L;

    public Seed(Entity entity) {
        super(entity);
    }
}
