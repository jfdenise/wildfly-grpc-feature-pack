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

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.wildfly.extension.grpc.deployment.GrpcDependencyProcessor;

import static org.wildfly.extension.grpc.GrpcSubsystemDefinition.GRPC_CAPABILITY;

class GrpcSubsystemAdd extends AbstractBoottimeAddStepHandler {

    static GrpcSubsystemAdd INSTANCE = new GrpcSubsystemAdd();

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model)
            throws OperationFailedException {
        super.performBoottime(context, operation, model);

        CapabilityServiceBuilder<?> csb = context.getCapabilityServiceTarget().addCapability(GRPC_CAPABILITY);
        Consumer<GrpcService> consumer = csb.provides(GRPC_CAPABILITY, ServiceNames.GRPC);
        csb.setInstance(new GrpcService(consumer));
        csb.install();

        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(DeploymentProcessorTarget processorTarget) {
                int DEPENDENCIES_TEMPLATE = 6304;
                processorTarget.addDeploymentProcessor(GrpcExtension.SUBSYSTEM_NAME, Phase.DEPENDENCIES,
                        DEPENDENCIES_TEMPLATE, new GrpcDependencyProcessor());
            }
        }, OperationContext.Stage.RUNTIME);
    }
}
