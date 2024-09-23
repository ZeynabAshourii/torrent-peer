package common.command.res;

import common.command.Response;
import common.net.data.Command;
import common.net.data.Entity;
import control.App;

import java.io.Serial;
import java.util.AbstractMap;
import java.util.List;

@SuppressWarnings("all")
public class Seeders extends Response {

    @Serial
    private static final long serialVersionUID = 8173856641895933717L;

    public Seeders(Entity entity) {
        super(entity);
    }

    public Seeders(Command incoming,Entity sender) {
        super(sender);
        addHeader("file-name", incoming.getHeader("file-name"));
        addHeader("list", incoming.getHeader("list"));
    }

    @Override
    public boolean isValid(Command command) {
        return "seeders".equals(command.getHeader("type"));
    }

    @Override
    public void run() {
        List<AbstractMap.SimpleEntry> list = ((List<String>)getHeader("list")).
                stream().parallel().map(e -> new AbstractMap.SimpleEntry(e.split(":")[0], Integer.parseInt(e
                        .split(":")[1]))).toList();
        App.instance.pickSeeder(entity, (String) getHeader("file-name"), list);
    }
}
