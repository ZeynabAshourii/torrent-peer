package policies;

import common.command.req.Get;
import common.command.res.Accept;
import common.command.res.FileShared;
import common.command.res.OK;
import common.command.res.Seeders;
import common.net.agent.PacketValidator;

public class Validator extends PacketValidator {
    public Validator() {
        commandList.add(new Seeders(null));
        commandList.add(new FileShared(null));
        commandList.add(new Get(null));
        commandList.add(new Accept(null));
        commandList.add(new OK(null));
    }
}
