/*
 *  Copyright (C) 2010-2012 Axel Morgner, structr <structr@structr.org>
 *
 *  This file is part of structr <http://structr.org>.
 *
 *  structr is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  structr is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with structr.  If not, see <http://www.gnu.org/licenses/>.
 */



package org.structr.web.entity;

import org.neo4j.graphdb.Direction;
import org.structr.common.PropertyView;
import org.structr.common.RelType;
import org.structr.common.property.Property;
import org.structr.common.property.StringProperty;
import org.structr.core.entity.AbstractNode;
import org.structr.core.notion.PropertyNotion;
import org.structr.core.property.CollectionProperty;

//~--- classes ----------------------------------------------------------------

/**
 * Theme entity to store information about UI customization
 *
 * @author Axel Morgner
 *
 */
public class Theme extends AbstractNode {
	
	public static final Property<String>	_customCss		= new StringProperty("customCss");
	
	public static final CollectionProperty<CrudEntity>	_crudEntities	= new CollectionProperty("crudEntities", CrudEntity.class, RelType.CONTAINS, Direction.OUTGOING, new PropertyNotion(AbstractNode.uuid), true);

	public static final org.structr.common.View publicView = new org.structr.common.View(CrudEntity.class, PropertyView.Public,
		type, name, _customCss, _crudEntities
	);

	public static final org.structr.common.View uiView = new org.structr.common.View(CrudEntity.class, PropertyView.Ui,
		type, name, _customCss, _crudEntities
	);
}
