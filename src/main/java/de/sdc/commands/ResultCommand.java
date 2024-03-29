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

/**
 * Represents a command which whill be send from the client to the server to
 * submit a result
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public abstract class ResultCommand extends Command implements Serializable {

    public ResultCommand() {
        super(CommandType.RESULT);
    }

    /**
     * @return returns a short description of the result (may will be logged)
     */
    public String getDescription() {
        return " result ";
    }
}
