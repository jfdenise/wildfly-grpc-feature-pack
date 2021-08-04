package org.wildfly.extension.grpc;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.dmr.ModelNode;
import org.wildfly.extension.grpc._private.GrpcLogger;
import org.wildfly.extension.grpc.deployment.GrpcDependencyProcessor;

import java.util.Collections;
import java.util.function.Consumer;

import static org.jboss.as.controller.OperationContext.Stage.RUNTIME;
import static org.jboss.as.server.deployment.Phase.DEPENDENCIES;
import static org.wildfly.extension.grpc.GrpcServerService.GRPC_SERVICE_NAME;
import static org.wildfly.extension.grpc.GrpcSubsystemDefinition.GRPC_CAPABILITY;

class GrpcSubsystemAdd extends AbstractBoottimeAddStepHandler {

    static GrpcSubsystemAdd INSTANCE = new GrpcSubsystemAdd();

    private GrpcSubsystemAdd() {
        super(Collections.emptyList());
    }

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, ModelNode model) throws OperationFailedException {
        super.performBoottime(context, operation, model);

        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(DeploymentProcessorTarget processorTarget) {
                int DEPENDENCIES_TEMPLATE = 6304;
                processorTarget.addDeploymentProcessor(GrpcExtension.SUBSYSTEM_NAME,
                        DEPENDENCIES,
                        DEPENDENCIES_TEMPLATE,
                        new GrpcDependencyProcessor());
            }
        }, RUNTIME);
        GrpcLogger.LOGGER.activatingSubsystem();

        CapabilityServiceBuilder<?> csb = context.getCapabilityServiceTarget().addCapability(GRPC_CAPABILITY);
        Consumer<GrpcServerService> consumer = csb.provides(GRPC_CAPABILITY, GRPC_SERVICE_NAME);
        csb.setInstance(new GrpcServerService(consumer));
        csb.install();
    }
}
