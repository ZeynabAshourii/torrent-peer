package common.command.req;

import common.command.Request;
import common.net.data.Entity;

import java.io.Serial;

public class GetSeeders extends Request {

    @Serial
    private static final long serialVersionUID = -1136858753128091726L;

    public GetSeeders(Entity entity) {
        super(entity);
    }
}
