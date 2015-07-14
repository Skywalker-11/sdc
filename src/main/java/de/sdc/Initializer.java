/*
 * Copyright (C) 2015 Michael Pietsch (Skywalker-11)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.sdc;

import java.io.Serializable;

/**
 * abstract class that represents an object that is used to initilize the client
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public abstract class Initializer implements Serializable {

    /**
     * creates a full copy of this object and all sub objects
     *
     * @return a deep copy of this object
     */
    public abstract Initializer copy();
}
