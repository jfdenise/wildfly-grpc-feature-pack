package org.wildfly.extension.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.jboss.msc.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.wildfly.extension.grpc._private.GrpcLogger;

import java.io.IOException;
import java.util.function.Consumer;

public class GrpcServerService implements Service {

    public static final ServiceName GRPC_SERVICE_NAME = ServiceName.of("io.grpc.server");

    private final Consumer<GrpcServerService> serviceConsumer;
    private Server server;

    GrpcServerService(final Consumer<GrpcServerService> serviceConsumer) {
        this.serviceConsumer = serviceConsumer;
    }

    @Override
    public void start(StartContext context) {
        // TODO Start asynchronously!
        try {
            int port = 50051;
            server = ServerBuilder.forPort(port)
                    .build()
                    .start();
            serviceConsumer.accept(this);
            GrpcLogger.LOGGER.serverListening("127.0.0.1", port);
        } catch (IOException e) {
            context.failed(new StartException("Unable to start gRPC server: " + e.getMessage(), e));
        }
    }

    @Override
    public void stop(StopContext context) {
        if (server != null) {
            server.shutdown();
            GrpcLogger.LOGGER.serverStopping();
        }
        serviceConsumer.accept(null);
    }
}
