package org.structr.core.graph.search.cypher;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.helpers.Predicate;
import org.neo4j.helpers.collection.Iterables;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.Result;
import org.structr.core.app.Query;
import org.structr.core.graph.NodeFactory;
import org.structr.core.graph.NodeServiceCommand;
import org.structr.core.graph.search.SearchAttribute;
import org.structr.core.graph.search.SearchAttributeGroup;
import org.structr.core.property.PropertyKey;
import org.structr.core.property.PropertyMap;

/**
 *
 * @author Christian Morgner
 */
public class CypherSearchCommand<T extends GraphObject> extends NodeServiceCommand implements Query<T> {

	private PropertyKey sortKey    = null;
	private int pageSize           = NodeFactory.DEFAULT_PAGE_SIZE;
	private int page               = NodeFactory.DEFAULT_PAGE;
	private String offsetId        = null;
	private boolean exactSearch    = true;
	private boolean sortDescending = false;
	private QueryPart root         = new QueryPart();
	private QueryPart current      = root;

	@Override
	public Result<T> getResult() throws FrameworkException {

		final GraphDatabaseService graphDb  = (GraphDatabaseService)getArgument("graphDb");
		final NodeFactory nodeFactory       = new NodeFactory(securityContext);
		final StringBuilder buf             = new StringBuilder();

		buf.append("MATCH (n)");
		buf.append(root.toString());
		buf.append(" RETURN n");

		

		final org.neo4j.graphdb.Result cypherResult = graphDb.execute(buf.toString());

		try (final ResourceIterator<T> iterator = cypherResult.columnAs("n")) {
			return nodeFactory.instantiate(Iterables.asResourceIterable(iterator));
		}
	}

	@Override
	public List<T> getAsList() throws FrameworkException {
		return getResult().getResults();
	}

	@Override
	public T getFirst() throws FrameworkException {
		return getResult().getResults().get(0);
	}

	@Override
	public boolean isExactSearch() {
		return exactSearch;
	}

	@Override
	public Query<T> sort(final PropertyKey key) {

		this.sortKey = key;
		return this;
	}

	@Override
	public Query<T> sortAscending(final PropertyKey key) {

		this.sortKey = key;
		this.sortDescending = false;
		return this;
	}

	@Override
	public Query<T> sortDescending(final PropertyKey key) {

		this.sortKey = key;
		this.sortDescending = true;
		return this;
	}

	@Override
	public Query<T> order(final boolean descending) {

		this.sortDescending = descending;
		return this;
	}

	@Override
	public Query<T> pageSize(final int pageSize) {

		this.pageSize = pageSize;
		return this;
	}

	@Override
	public Query<T> page(final int page) {

		this.page = page;
		return this;
	}

	@Override
	public Query<T> publicOnly() {
		return this;
	}

	@Override
	public Query<T> exact(final boolean exact) {

		this.exactSearch = exact;
		return this;
	}

	@Override
	public Query<T> includeDeletedAndHidden() {
		return this;
	}

	@Override
	public Query<T> publicOnly(final boolean publicOnly) {
		return this;
	}

	@Override
	public Query<T> includeDeletedAndHidden(final boolean includeDeletedAndHidden) {
		return this;
	}

	@Override
	public Query<T> offsetId(final String offsetId) {

		this.offsetId = offsetId;
		return this;
	}

	@Override
	public Query<T> uuid(final String uuid) {
		return this;
	}

	@Override
	public Query<T> andType(final Class<T> type) {
		return this;
	}

	@Override
	public Query<T> orType(final Class<T> type) {
		return this;
	}

	@Override
	public Query<T> andTypes(final Class<T> type) {
		return this;
	}

	@Override
	public Query<T> orTypes(final Class<T> type) {
		return this;
	}

	@Override
	public Query<T> andName(final String name) {

		current.add(new QueryPart(Conjunction.And));
		return this;
	}

	@Override
	public Query<T> orName(final String name) {
		return this;
	}

	@Override
	public Query<T> location(final String street, final String postalCode, final String city, final String country, final double distance) {
		return this;
	}

	@Override
	public Query<T> location(final String street, final String postalCode, final String city, final String state, final String country, final double distance) {
		return this;
	}

	@Override
	public Query<T> location(final String street, final String house, final String postalCode, final String city, final String state, final String country, final double distance) {
		return this;
	}

	@Override
	public <P> Query<T> and(final PropertyKey<P> key, final P value) {
		return this;
	}

	@Override
	public <P> Query<T> and(final PropertyKey<P> key, final P value, final boolean exact) {
		return this;
	}

	@Override
	public <P> Query<T> and(final PropertyMap attributes) {
		return this;
	}

	@Override
	public Query<T> and() {

		final QueryPart newPart = new QueryPart(current, Conjunction.Not);
		current.add(newPart);
		current = newPart;

		return this;
	}

	@Override
	public <P> Query<T> or(final PropertyKey<P> key, final P value) {
		return this;
	}

	@Override
	public <P> Query<T> or(final PropertyKey<P> key, final P value, final boolean exact) {
		return this;
	}

	@Override
	public <P> Query<T> or(final PropertyMap attributes) {
		return this;
	}

	@Override
	public Query<T> notBlank(final PropertyKey key) {
		return this;
	}

	@Override
	public <P> Query<T> andRange(final PropertyKey<P> key, final P rangeStart, final P rangeEnd) {
		return this;
	}

	@Override
	public <P> Query<T> orRange(final PropertyKey<P> key, final P rangeStart, final P rangeEnd) {
		return this;
	}

	@Override
	public Query<T> or() {

		final QueryPart newPart = new QueryPart(current, Conjunction.Or);
		current.add(newPart);
		current = newPart;

		return this;
	}

	@Override
	public Query<T> not() {

		final QueryPart newPart = new QueryPart(current, Conjunction.Not);
		current.add(newPart);
		current = newPart;

		return this;
	}

	@Override
	public Query<T> parent() {

		current = current.getParent();
		return this;
	}

	@Override
	public Query<T> attributes(List<SearchAttribute> attributes) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Predicate<GraphObject> toPredicate() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public SearchAttributeGroup getRootAttributeGroup() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	// ----- private methods -----

	// ----- nested classes -----
	private enum Conjunction {
		Not, And, Or
	}

	private class QueryPart {

		private List<QueryPart> parts = new LinkedList<>();
		private Conjunction conj      = Conjunction.And;
		private QueryPart parent      = null;
		private String query          = null;

		public QueryPart() {
		}

		public QueryPart(final Conjunction conj, final String query) {
			this(null, conj, query);
		}

		public QueryPart(final QueryPart parent, final Conjunction conj) {
			this(parent, conj, null);
		}

		public QueryPart(final QueryPart parent, final Conjunction conj, final String query) {
			this.parent = parent;
			this.conj   = conj;
			this.query  = query;
		}

		public QueryPart getParent() {
			return parent;
		}

		public void add(final QueryPart part) {
			parts.add(part);
		}

		@Override
		public String toString() {

			if (parts.isEmpty()) {

				return query;

			} else {

				final StringBuilder buf = new StringBuilder();

				for (final QueryPart part : parts) {
					buf.append(part.toString());
				}

				return buf.toString();
			}
		}
	}
}
