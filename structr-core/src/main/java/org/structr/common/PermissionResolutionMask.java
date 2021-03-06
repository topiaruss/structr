/**
 * Copyright (C) 2010-2015 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.structr.core.property.PropertyKey;

/**
 *
 *
 */
public class PermissionResolutionMask {

	private static final Set<String> systemProperties = new HashSet<>(Arrays.asList(new String[] { "id", "type", }));
	private final Set<String> removedProperties       = new HashSet<>();
	private final int READ                            = 0x00000001;
	private final int WRITE                           = 0x00000002;
	private final int DELETE                          = 0x00000004;
	private final int ACCESS_CONTROL                  = 0x00000008;
	private int value                                 = 0;

	public PermissionResolutionMask() {}

	public PermissionResolutionMask(final PermissionResolutionMask toCopy) {

		removedProperties.addAll(toCopy.removedProperties);
		this.value = toCopy.value;
	}

	public boolean allowsPermission(final Permission permission) {

		if (Permission.read.equals(permission)) {
			return (value & READ) != 0;
		}

		if (Permission.write.equals(permission)) {
			return (value & WRITE) != 0;
		}

		if (Permission.delete.equals(permission)) {
			return (value & DELETE) != 0;
		}

		if (Permission.accessControl.equals(permission)) {
			return (value & ACCESS_CONTROL) != 0;
		}

		return false;
	}

	public boolean allowsProperty(final PropertyKey key) {

		final String name = key.jsonName();

		if (systemProperties.contains(name)) {
			return true;
		}

		return !removedProperties.contains(name);
	}

	public Set<String> getRemovedProperties() {
		return removedProperties;
	}

	public Set<String> propertyMask() {
		return removedProperties;
	}

	public boolean isEmpty() {
		return value == 0;
	}

	public void addRead() {
		value |= READ;
	}

	public void removeRead() {
		value ^= READ;
	}

	public void addWrite() {
		value |= WRITE;
	}

	public void removeWrite() {
		value ^= WRITE;
	}

	public void addDelete() {
		value |= DELETE;
	}

	public void removeDelete() {
		value ^= DELETE;
	}

	public void addAccessControl() {
		value |= ACCESS_CONTROL;
	}

	public void removeAccessControl() {
		value ^= ACCESS_CONTROL;
	}

	public void handleProperties(final String delta) {

		if (delta != null) {

			for (final String prop : delta.split("[, ]+")) {

				this.removedProperties.add(prop.substring(1).trim());
			}
		}
	}

	public PermissionResolutionMask copy() {
		return new PermissionResolutionMask(this);
	}

	public void restore(final PermissionResolutionMask mask) {

		removedProperties.clear();
		removedProperties.addAll(mask.removedProperties);

		this.value = mask.value;
	}
}
