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

import org.structr.common.PropertyView;
import org.structr.core.entity.AbstractNode;
import org.structr.core.property.Property;
import org.structr.core.property.StringProperty;

//~--- classes ----------------------------------------------------------------

/**
 * Holds information about customization of the CRUD UI
 *
 * @author Axel Morgner
 *
 */
public class CrudEntity extends AbstractNode {
	
	public static final Property<String>	_forType		= new StringProperty("forType");
	public static final Property<String>	_columns			= new StringProperty("columns");

	public static final org.structr.common.View publicView = new org.structr.common.View(CrudEntity.class, PropertyView.Public,
		type, name, _forType, _columns
	);
	
	public static final org.structr.common.View uiView = new org.structr.common.View(CrudEntity.class, PropertyView.Ui,
		type, name, _forType, _columns
	);
}
