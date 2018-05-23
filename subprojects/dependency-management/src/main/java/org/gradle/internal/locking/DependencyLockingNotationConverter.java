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

package org.gradle.internal.locking;

import org.gradle.api.artifacts.DependencyConstraint;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.internal.artifacts.dependencies.DefaultDependencyConstraint;
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier;

class DependencyLockingNotationConverter {

    private final boolean updating;

    public DependencyLockingNotationConverter(boolean updating) {
        this.updating = updating;
    }

    DependencyConstraint convertToDependencyConstraint(String module) {
        int groupNameSeparatorIndex = module.indexOf(':');
        int nameVersionSeparatorIndex = module.lastIndexOf(':');
        if (groupNameSeparatorIndex < 0 || nameVersionSeparatorIndex == groupNameSeparatorIndex) {
            throw new IllegalArgumentException("The module notation does not respect the lock file format of 'group:name:version' - received '" + module + "'");
        }
        DefaultDependencyConstraint constraint;
        if (updating) {
            constraint = createPreferConstraint(module, groupNameSeparatorIndex, nameVersionSeparatorIndex);
        } else {
            constraint = createStrictConstraint(module, groupNameSeparatorIndex, nameVersionSeparatorIndex);
        }
                 return constraint;
    }

    private DefaultDependencyConstraint createStrictConstraint(String module, int groupNameSeparatorIndex, int nameVersionSeparatorIndex) {
        DefaultDependencyConstraint constraint;
        constraint = DefaultDependencyConstraint.strictConstraint(module.substring(0, groupNameSeparatorIndex),
            module.substring(groupNameSeparatorIndex + 1, nameVersionSeparatorIndex),
            module.substring(nameVersionSeparatorIndex + 1));
        constraint.because("dependency was locked to version '" + constraint.getVersion() + "'");
        return constraint;
    }

    private DefaultDependencyConstraint createPreferConstraint(String module, int groupNameSeparatorIndex, int nameVersionSeparatorIndex) {
        DefaultDependencyConstraint constraint = new DefaultDependencyConstraint(module.substring(0, groupNameSeparatorIndex),
            module.substring(groupNameSeparatorIndex + 1, nameVersionSeparatorIndex),
            module.substring(nameVersionSeparatorIndex + 1));
        constraint.because("dependency was locked to version '" + constraint.getVersion() + "' (update mode)");
        return constraint;
    }

    ModuleComponentIdentifier convertFromLockNotation(String notation) {
        int groupNameSeparatorIndex = notation.indexOf(':');
        int nameVersionSeparatorIndex = notation.lastIndexOf(':');
        if (groupNameSeparatorIndex < 0 || nameVersionSeparatorIndex == groupNameSeparatorIndex) {
            throw new IllegalArgumentException("The module notation does not respect the lock file format of 'group:name:version' - received '" + notation + "'");
        }
        return DefaultModuleComponentIdentifier.newId(notation.substring(0, groupNameSeparatorIndex),
            notation.substring(groupNameSeparatorIndex + 1, nameVersionSeparatorIndex),
            notation.substring(nameVersionSeparatorIndex + 1));
    }

    String convertToLockNotation(ModuleComponentIdentifier id) {
        return id.getGroup() + ":" + id.getModule() + ":" + id.getVersion();
    }
}
