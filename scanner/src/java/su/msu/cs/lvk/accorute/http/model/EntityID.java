/***********************************************************************
 *
 * $CVSHeader$
 *
 * This file is part of WebScarab, an Open Web Application Security
 * Project utility. For details, please see http://www.owasp.org/
 *
 * Copyright (c) 2002 - 2004 Rogan Dawes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Getting Source
 * ==============
 *
 * Source for this application is maintained at Sourceforge.net, a
 * repository for free software projects.
 *
 * For details, please see http://www.sourceforge.net/projects/owasp
 *
 */

/*
 * EntityID.java
 *
 * Created on July 13, 2004, 3:59 PM
 */

package su.msu.cs.lvk.accorute.http.model;

import java.io.Serializable;

/**
 * provides a link to a conversation in the model
 *
 * @author knoppix
 */
public class EntityID implements Comparable, Serializable {
    public static final EntityID NOT_INITIALIZED = new EntityID(0l);
    
    private Long id = 0l;

    public boolean isInitialized() {
        return id > 0;
    }

    public EntityID() {
    }

    public EntityID(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /**
     * shows a string representation of the ConversationID
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return id.toString();
    }

    /**
     * compares this ConversationID to another
     *
     * @param o the other ConversationID to compare to
     * @return true if they are equal, false otherwise
     */
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EntityID)) return false;
        return id.equals(((EntityID) o).getId());
    }

    /**
     * @return
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * compares this ConversationID to another
     *
     * @param o the other ConversationID to compare to
     * @return -1, 0 or 1 if this ConversationID is less than, equal to, or greater than the supplied parameter
     */
    public int compareTo(Object o) {
        if (o instanceof EntityID) {
            Long thatid = ((EntityID) o).getId();
            if (id - thatid > 0l) return 1;
            if (id - thatid < 0l) return -1;
            return 0;
        }
        return 1;
    }

}
