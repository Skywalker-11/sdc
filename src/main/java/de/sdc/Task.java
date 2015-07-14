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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A task will be send from the server to the clients and defines what they
 * should calculate
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public abstract class Task implements Serializable {

    private static final AtomicInteger nextId = new AtomicInteger(0);
    private final int id;

    public Task() {
        this.id = nextId.getAndIncrement();
    }

    public int getId() {
        return id;
    }
}
