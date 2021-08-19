package org.wildfly.extension.grpc;

import org.jboss.msc.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StopContext;
import org.wildfly.extension.grpc._private.GrpcLogger;

public class GrpcSubsystemService implements Service {

    public static final ServiceName SERVICE_NAME = ServiceName.of("io.grpc.server");
    public static final ServiceName GRPC_DEPLOYMENT = ServiceName.of("grpc-deployment");

    public static ServiceName deploymentServiceName(ServiceName deploymentServiceName) {
        return deploymentServiceName.append(GRPC_DEPLOYMENT);
    }

    GrpcSubsystemService() {
    }

    @Override
    public void start(StartContext context) {
        GrpcLogger.LOGGER.grpcStarting();
    }

    @Override
    public void stop(StopContext context) {
        GrpcLogger.LOGGER.grpcStopping();
    }
}
