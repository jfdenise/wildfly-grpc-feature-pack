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

package org.wildfly.extension.feature.pack.grpc.subsystem;

import static org.jboss.as.controller.PersistentResourceXMLDescription.builder;

import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.as.controller.PersistentResourceXMLParser;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SubsystemParser_1_0 extends PersistentResourceXMLParser {

    // TODO Rename to something that makes sense and update the template-subsytem.xsd namespaces to match
    public static final String NAMESPACE = "urn:wildfly:wildfly-grpc-subsystem:1.0";

    private static final PersistentResourceXMLDescription xmlDescription = builder(GrpcExtension.SUBSYSTEM_PATH, NAMESPACE)
            .build();

    @Override
    public PersistentResourceXMLDescription getParserDescription() {
        return xmlDescription;
    }

}
