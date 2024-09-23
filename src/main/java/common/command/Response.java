package common.command;

import common.net.data.Command;
import common.net.data.Entity;

public abstract class Response extends Command {
    public Response(Entity entity) {
        super(entity);
    }
}
