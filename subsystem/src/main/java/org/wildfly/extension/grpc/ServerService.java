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
import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.jboss.as.network.ManagedBinding;
import org.jboss.as.network.NetworkUtils;
import org.jboss.as.network.SocketBinding;
import org.jboss.as.server.deployment.DelegatingSupplier;
import org.jboss.msc.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.wildfly.extension.grpc._demo.GreeterImpl;
import org.wildfly.extension.grpc._private.GrpcLogger;

public class ServerService implements Service {

    private final Consumer<ServerService> serverConsumer;
    private final Supplier<GrpcService> grpcService;
    private final String name;
    private final DelegatingSupplier<SocketBinding> binding;
    private Server server;

    public ServerService(Consumer<ServerService> serverConsumer, Supplier<GrpcService> grpcService,
            String name) {
        this.serverConsumer = serverConsumer;
        this.grpcService = grpcService;
        this.name = name;
        this.binding = new DelegatingSupplier<>();
    }

    @Override
    public void start(StartContext context) {
        GrpcLogger.LOGGER.serverStarting(name);

        InetSocketAddress socketAddress = binding.get().getSocketAddress();
        GrpcLogger.LOGGER.serverListening(name, NetworkUtils.formatIPAddressForURI(socketAddress.getAddress()),
                socketAddress.getPort());

        // TODO Start gRPC server asynchronously!
        try {
            server = ServerBuilder.forPort(socketAddress.getPort())
                    .addService(new GreeterImpl())
                    .build()
                    .start();
        } catch (IOException e) {
            context.failed(new StartException("Unable to start gRPC server: " + e.getMessage(), e));
        }

        grpcService.get().registerServer(this);
        binding.get().getSocketBindings().getNamedRegistry().registerBinding(new ListenerBinding(binding.get()));
        serverConsumer.accept(this);
    }

    @Override
    public void stop(StopContext stopContext) {
        GrpcLogger.LOGGER.serverStopping(name);

        if (server != null) {
            server.shutdown();
        }

        serverConsumer.accept(null);
        binding.get().getSocketBindings().getNamedRegistry().unregisterBinding(binding.get().getName());
        grpcService.get().unregisterServer(this);
    }

    DelegatingSupplier<SocketBinding> getBinding() {
        return binding;
    }

    private static class ListenerBinding implements ManagedBinding {

        private final SocketBinding binding;

        private ListenerBinding(final SocketBinding binding) {
            this.binding = binding;
        }

        @Override
        public String getSocketBindingName() {
            return binding.getName();
        }

        @Override
        public InetSocketAddress getBindAddress() {
            return binding.getSocketAddress();
        }

        @Override
        public void close() {
        }
    }
}
