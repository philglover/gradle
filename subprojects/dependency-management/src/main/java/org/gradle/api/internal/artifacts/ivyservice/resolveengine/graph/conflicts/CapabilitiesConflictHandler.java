/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.conflicts;

import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.capabilities.CapabilityDescriptor;
import org.gradle.api.internal.artifacts.ivyservice.resolveengine.graph.builder.ComponentState;

import java.util.Collection;

public interface CapabilitiesConflictHandler extends ConflictHandler<CapabilitiesConflictHandler.Candidate, ConflictResolutionResult, CapabilitiesConflictHandler.Resolver> {
    interface Candidate {
        ComponentState getComponent();
        CapabilityDescriptor getCapabilityDescriptor();
    }

    interface ResolutionDetails extends ConflictResolutionResult {
        Collection<? extends CapabilityDescriptor> getCapabilityVersions();
        Collection<? extends CandidateDetails> getCandidates(CapabilityDescriptor capability);
        boolean hasResult();
    }

    interface CandidateDetails {
        ComponentIdentifier getId();
        void evict();
        void select();
    }

    interface Resolver {
        void resolve(ResolutionDetails details);
    }
}