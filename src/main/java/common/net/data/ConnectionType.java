package common.net.data;

import common.net.Connection;

public enum ConnectionType {
    TCP {
        @Override
        public Connection getConnection(Entity entity) {
            return entity.getTcp();
        }
    },
    UDP {
        @Override
        public Connection getConnection(Entity entity) {
            return entity.getUdp();
        }
    };

    public abstract Connection getConnection(Entity entity);
}
