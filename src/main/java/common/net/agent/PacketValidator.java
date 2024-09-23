package common.net.agent;

import common.net.data.Command;
import common.net.data.Entity;
import common.net.data.Packet;

import java.util.ArrayList;
import java.util.List;

public abstract class PacketValidator {
    protected final List<Command> commandList = new ArrayList<>();

    public void validate(Packet incoming) {
        try {
            for (var command : commandList)
                if (command.isValid(incoming.command)) {
                    var clazz = command.getClass();
                    var constructor = clazz.getConstructor(Command.class, Entity.class);
                    var finalCommand = constructor.newInstance(incoming.command, incoming.entity);
                    finalCommand.run();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
