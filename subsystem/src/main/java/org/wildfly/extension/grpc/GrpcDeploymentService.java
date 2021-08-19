/*
 * Copyright 2021 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.extension.grpc;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.jboss.msc.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.wildfly.extension.grpc._private.GrpcLogger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class GrpcDeploymentService implements Service {

    public static final ServiceName GRPC_DEPLOYMENT = ServiceName.of("grpc-deployment");
    private static final long SHUTDOWN_TIMEOUT = 3; // seconds
    private static final String HOST = "127.0.0.1"; // TODO make configurable
    private static final int PORT = 9555; // TODO make configurable

    public static ServiceName deploymentServiceName(ServiceName deploymentServiceName) {
        return deploymentServiceName.append(GRPC_DEPLOYMENT);
    }

    private final String name;
    private final Consumer<GrpcDeploymentService> deploymentService;
    private final Supplier<GrpcSubsystemService> subsystemService;
    private final Supplier<ExecutorService> executorService;
    private final Map<String, String> serviceClasses;
    private final ClassLoader classLoader;
    private Server server;

    public GrpcDeploymentService(String name,
            Consumer<GrpcDeploymentService> deploymentService,
            Supplier<GrpcSubsystemService> subsystemService,
            Supplier<ExecutorService> executorService,
            Map<String, String> serviceClasses,
            ClassLoader classLoader) {
        this.name = name;
        this.deploymentService = deploymentService;
        this.subsystemService = subsystemService;
        this.executorService = executorService;
        this.serviceClasses = serviceClasses;
        this.classLoader = classLoader;
    }

    @Override
    public void start(StartContext context) {
        context.asynchronous();
        executorService.get().submit(() -> {
            try {
                startServer();
                context.complete();
            } catch (Throwable e) {
                context.failed(new StartException(e));
            }
        });
        deploymentService.accept(this);
    }

    private void startServer()
            throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        GrpcLogger.LOGGER.serverListening(name, HOST, PORT);
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(PORT);
        for (String serviceClass : serviceClasses.values()) {
            serverBuilder.addService(newService(serviceClass));
            GrpcLogger.LOGGER.registerService(serviceClass);
        }
        server = serverBuilder.build().start();
    }

    @SuppressWarnings("deprecation")
    private BindableService newService(String serviceClass)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = classLoader.loadClass(serviceClass);
        Object instance = clazz.newInstance();
        if (!(instance instanceof BindableService)) {
            throw new ClassCastException("gRPC service " + serviceClass + " is not a BindableService!");
        }
        return ((BindableService) instance);
    }

    @Override
    public void stop(StopContext context) {
        GrpcLogger.LOGGER.serverStopping(name);
        if (server != null) {
            stopServer();
        }
        deploymentService.accept(null);
    }

    private void stopServer() {
        try {
            server.shutdown().awaitTermination(SHUTDOWN_TIMEOUT, SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
