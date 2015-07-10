/**
 * Copyright (C) 2010-2015 Morgner UG (haftungsbeschränkt)
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
package org.structr.core.graph.search;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.NumericUtils;
import org.structr.core.property.ArrayProperty;
import org.structr.core.property.PropertyKey;

/**
 *
 * @author Christian Morgner
 */
public class DoubleSearchAttribute extends PropertySearchAttribute<Double> {

	public DoubleSearchAttribute(PropertyKey<Double> key, Double value, Occur occur, boolean isExactMatch) {
		super(key, value, occur, isExactMatch);
	}

	@Override
	public String toString() {
		return "DoubleSearchAttribute()";
	}

	@Override
	public boolean canUseCypher() {
		return getValue() == null || (!getValue().isInfinite() && !getValue().isNaN());
	}

	@Override
	public String getCypherQuery(final boolean first) {

		final StringBuilder buf = new StringBuilder();
		final Double value       = getValue();

		appendOccur(buf, first);

		if (getKey() instanceof ArrayProperty) {

			buf.append(" ANY (x IN n.");
			buf.append(getKey().dbName());
			buf.append(" WHERE x");

			if (value == null) {

				buf.append(" IS NULL");

			} else if (value.isNaN()) {

				buf.append(" = ");
				buf.append(Double.longBitsToDouble(0x7ff8000000000000L));

			} else if (value.isInfinite()) {

				buf.append(" = ");
				buf.append(Double.longBitsToDouble(0x7ff0000000000000L));

			} else {

				buf.append(" = ");
				buf.append(getValue());
			}

			buf.append(")");

		} else {

			buf.append(" n.");
			buf.append(getKey().dbName());

			if (value == null) {

				buf.append(" IS NULL");

			} else if (value.isNaN()) {

				buf.append(" = ");
				buf.append(Double.longBitsToDouble(0x7ff8000000000000L));

			} else if (value.isInfinite()) {

				buf.append(" = ");
				buf.append(Double.longBitsToDouble(0x7ff0000000000000L));

			} else {

				buf.append(" = ");
				buf.append(getValue());
			}
		}

		return buf.toString();
	}

	@Override
	public String getStringValue() {

		Double value = getValue();
		if (value != null) {
			return NumericUtils.doubleToPrefixCoded(value);
		}

		return null;
	}
}
