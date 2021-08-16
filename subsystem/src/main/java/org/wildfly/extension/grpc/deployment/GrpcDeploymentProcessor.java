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
package org.wildfly.extension.grpc.deployment;

import java.util.List;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.annotation.CompositeIndex;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.wildfly.extension.grpc.GrpcService;
import org.wildfly.extension.grpc._private.GrpcLogger;

public class GrpcDeploymentProcessor implements DeploymentUnitProcessor {

    static final DotName GRPC_SERVICE = DotName.createSimple("org.wildfly.grpc.GrpcService");

    private final GrpcService grpcService;

    public GrpcDeploymentProcessor(final GrpcService grpcService) {
        this.grpcService = grpcService;
    }

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        CompositeIndex compositeIndex = deploymentUnit.getAttachment(Attachments.COMPOSITE_ANNOTATION_INDEX);
        if (compositeIndex.getAnnotations(GRPC_SERVICE).isEmpty()) {
            return;
        }

        List<AnnotationInstance> grpcServices = compositeIndex.getAnnotations(GRPC_SERVICE);
        if (grpcServices != null) {
            for (AnnotationInstance annotationInstance : grpcServices) {
                if (annotationInstance.target() instanceof ClassInfo) {
                    ClassInfo clazz = (ClassInfo) annotationInstance.target();
                    GrpcLogger.LOGGER.registerService(clazz.name().toString());
                    // TODO Start gRPC server and register services
                }
            }
        }
    }

    @Override
    public void undeploy(DeploymentUnit context) {
        // TODO Stop and remove server
    }
}
