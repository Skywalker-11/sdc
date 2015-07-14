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

import de.sdc.Initializer;

/**
 * This is a command that could be send to the client to set it up. If a setup
 * should done an object of this class will be send to the client as the very
 * first command.
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public class InitCommand extends Command {

    Initializer initializer;

    public InitCommand(Initializer gameIni) {
        super(CommandType.INIT);
        this.initializer = gameIni;
    }

    public Initializer getGameIni() {
        return initializer;
    }
}
