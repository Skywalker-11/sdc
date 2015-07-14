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
package de.sdc.commands;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The implementations of this class represents a command that is send over the
 * network and maybe encapsulates other object that should be shared between
 * client and server.
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public abstract class Command implements Serializable {

    private static final AtomicLong nextId = new AtomicLong(0);

    private final CommandType type;
    private final long id;

    protected Command(CommandType type) {
        this.type = type;
        this.id = nextId.getAndIncrement();
    }

    /**
     *
     * @return the type of the command
     */
    public CommandType getType() {
        return type;
    }

    /**
     *
     * @return the id of the command
     */
    public long getId() {
        return id;
    }
}
