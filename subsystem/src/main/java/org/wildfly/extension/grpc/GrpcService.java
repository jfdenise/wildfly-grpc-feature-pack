package org.wildfly.extension.grpc;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

import org.jboss.msc.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.wildfly.extension.grpc._private.GrpcLogger;

public class GrpcService implements Service {

    private final Set<ServerService> registeredServers;
    private final Consumer<GrpcService> serviceConsumer;

    GrpcService(Consumer<GrpcService> serviceConsumer) {
        this.serviceConsumer = serviceConsumer;
        this.registeredServers = new CopyOnWriteArraySet<>();
    }

    @Override
    public void start(StartContext context) {
        GrpcLogger.LOGGER.grpcStarting();
        serviceConsumer.accept(this);
    }

    @Override
    public void stop(StopContext context) {
        serviceConsumer.accept(null);
        GrpcLogger.LOGGER.grpcStopping();
    }

    protected void registerServer(ServerService server) {
        registeredServers.add(server);
    }

    protected void unregisterServer(final ServerService server) {
        registeredServers.remove(server);
    }
}
