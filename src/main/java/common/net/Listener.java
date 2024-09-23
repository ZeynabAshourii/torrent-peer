package common.net;

import java.io.IOException;

public abstract class Listener {
    protected boolean started;
    public abstract Connection listen(int portNumber) throws IOException;
    public abstract void close() throws IOException;
    protected abstract void init(int portNumber) throws IOException;
}
