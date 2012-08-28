#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.neo4j.graphdb.RelationshipType;

public enum RelType implements RelationshipType {

	KNOWS, LIVES_IN
}
