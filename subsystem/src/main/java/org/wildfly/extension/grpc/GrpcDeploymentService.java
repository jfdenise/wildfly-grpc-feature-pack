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
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.wildfly.extension.grpc._private.GrpcLogger;

public class GrpcDeploymentService implements Service {

    private static final int PORT = 9555;

    private final Consumer<GrpcDeploymentService> deploymentService;
    private final Supplier<GrpcSubsystemService> subsystemService;
    private final Supplier<ExecutorService> executorService;
    private final String name;
    private final Map<String, String> serviceClasses;
    private final ClassLoader classLoader;
    private Server server;

    public GrpcDeploymentService(Consumer<GrpcDeploymentService> deploymentService,
            Supplier<GrpcSubsystemService> subsystemService,
            Supplier<ExecutorService> executorService,
            String name,
            Map<String, String> serviceClasses,
            ClassLoader classLoader) {
        this.deploymentService = deploymentService;
        this.subsystemService = subsystemService;
        this.executorService = executorService;
        this.name = name;
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

        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(PORT);
        for (String serviceClass : serviceClasses.values()) {
            serverBuilder.addService(newService(serviceClass));
            GrpcLogger.LOGGER.registerService(serviceClass);
        }
        server = serverBuilder.build().start();
        GrpcLogger.LOGGER.serverStarting(name);
        GrpcLogger.LOGGER.serverListening(name, "127.0.0.1", PORT);
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
    public void stop(StopContext stopContext) {
        GrpcLogger.LOGGER.serverStopping(name);

        if (server != null) {
            server.shutdown();
        }

        deploymentService.accept(null);
    }

    public Consumer<GrpcDeploymentService> getDeploymentService() {
        return deploymentService;
    }
}
