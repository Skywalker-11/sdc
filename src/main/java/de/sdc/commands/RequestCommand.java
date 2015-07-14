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

/**
 * Represents a request for a new task that a client could send to the server.
 * The server will respond with a task once one is available.
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public class RequestCommand extends Command {

    public RequestCommand() {
        super(CommandType.REQUEST_TASK);
    }
}
