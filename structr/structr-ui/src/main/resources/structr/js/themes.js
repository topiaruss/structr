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

var themes;

$(document).ready(function() {
    Structr.registerModule('themes', _Themes);
    Structr.classes.push('theme');
});

var _Themes = {
    
    type_icon : 'icon/database_table.png',

    init : function() {
        pageSize['Theme'] = 25;
        page['Theme'] = 1;
    },
	
    onload : function() {
        _Themes.init();
        if (debug) console.log('onload');
        if (palette) palette.remove();

        main.append('<table><tr><td id="themes"></td></tr></table>');
        themes = $('#themes');
        _Themes.refreshThemes();
    },
    
    refreshThemes : function() {
        themes.empty();
        if (Command.list('Theme')) {
            themes.append('<button class="add_theme_icon button"><img title="Add Theme" alt="Add Theme" src="' + _Themes.type_icon + '"> Add Theme</button>');
            $('.add_theme_icon', main).on('click', function(e) {
                e.stopPropagation();
                var entity = {};
                entity.type = 'Theme';
                return Command.create(entity);
            });
        }
        Structr.addPager(themes, 'Theme');
    },
    
    appendThemeElement : function(theme) {
		
        if (debug) console.log('appendThemeElement', theme);
        
        themes.append('<div id="_' + theme.id + '" structr_type="theme" class="node theme ' + theme.id + '_">'
            + '<img class="typeIcon" src="'+ _Themes.type_icon + '">'
            + '<b class="name_">' + theme.name + '</b> <span class="id">' + theme.id + '</span>'
            + '</div>');
        
        var div = $('#_' + theme.id);
        
        div.append('<img title="Delete Theme ' + theme.id + '" alt="Delete Theme ' + theme.id + '" class="delete_icon button" src="' + Structr.delete_icon + '">');
        $('.delete_icon', div).on('click', function(e) {
            e.stopPropagation();
            _Entities.deleteNode(this, theme);
        });
        
        _Entities.appendAccessControlIcon(div, theme);
        _Entities.appendEditPropertiesIcon(div, theme);
        _Entities.setMouseOver(div);
		
        return div;
    }
};