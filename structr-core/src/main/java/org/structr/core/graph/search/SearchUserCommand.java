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

import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.structr.core.property.PropertyKey;
import org.structr.core.graph.NodeService.NodeIndex;
import org.structr.core.graph.NodeServiceCommand;
import org.structr.common.error.FrameworkException;
import org.structr.core.graph.NodeFactory;

//~--- classes ----------------------------------------------------------------

/**
 * Searches for a user node by her/his name in the database and returns the result.
 *
 * @author Axel Morgner
 */
public class SearchUserCommand extends NodeServiceCommand {

	public Object execute(final NodeIndex indexName, final PropertyKey<String> key, final String value) throws FrameworkException {

		final NodeFactory nodeFactory = new NodeFactory(securityContext);
		final Index<Node> index       = getIndexFromArguments(indexName, arguments);

		// see: http://docs.neo4j.org/chunked/milestone/indexing-create-advanced.html
		try (final IndexHits<Node> indexHits = index.query( key.dbName(), "\"" + value + "\"" )) {

			for (final Node n : indexHits) {

				final Object u = nodeFactory.instantiate(n);
				if (u != null) {

					return u;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private Index<Node> getIndexFromArguments(final NodeIndex idx, final Map<String, Object> args) {
		final Index<Node> index = (Index<Node>) args.get(idx.name());
		return index;
	}
}
