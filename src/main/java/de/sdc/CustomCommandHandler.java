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
import de.sdc.commands.Command;

/**
 * the implementations of this class could be used to handle custom commands
 * received from clients
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public abstract class CustomCommandHandler {

    /**
     * handles a custom command (commandID = CUSTOM) this method has to be
     * thread safe
     *
     * @param command custom command received from a client
     * @return a command that should be returned to the client, if null nothing
     * is send to the client that send the custom command
     */
    public abstract Command handleCustomCommand(Command command);
}
