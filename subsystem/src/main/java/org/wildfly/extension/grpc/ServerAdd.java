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

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.network.SocketBinding;
import org.jboss.dmr.ModelNode;

import static org.wildfly.extension.grpc.Capabilities.REF_SOCKET_BINDING;
import static org.wildfly.extension.grpc.ServerDefinition.SERVER_CAPABILITY;

public class ServerAdd extends AbstractAddStepHandler {

    ServerAdd() {
        super(new Parameters()
                .addAttribute(ServerDefinition.ATTRIBUTES)
                .addRuntimeCapability(SERVER_CAPABILITY) // TODO Find deprecation replacement
        );
    }

    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model)
            throws OperationFailedException {
        String name = context.getCurrentAddressValue();
        String bindingRef = ServerDefinition.SOCKET_BINDING.resolveModelAttribute(context, model).asString();

        final CapabilityServiceBuilder<?> csb = context.getCapabilityServiceTarget().addCapability(SERVER_CAPABILITY);
        final Consumer<ServerService> consumer = csb.provides(SERVER_CAPABILITY);
        final Supplier<GrpcService> grpcSupplier = csb.requiresCapability(Capabilities.CAPABILITY_GRPC,
                GrpcService.class);
        ServerService service = new ServerService(consumer, grpcSupplier, name);
        csb.setInstance(service);

        service.getBinding().set(csb.requiresCapability(REF_SOCKET_BINDING, SocketBinding.class, bindingRef));
        csb.install();
    }
}
