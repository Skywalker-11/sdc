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

import de.sdc.Task;

/**
 * This command that will be send from server to client and wrapps a task that 
 * should be executed there
 *
 * @author Michael Pietsch (Skywalker-11)
 * @param <T> task wrapped by this class
 */
public class TaskCommand<T extends Task> extends Command {

    T task;

    /**
     * 
     * @param task the task that should be executed on a client
     */
    public TaskCommand(T task) {
        super(CommandType.TASK);
        this.task = task;
    }

    /**
     * 
     * @return returns the task that is wrapped in this command
     */
    public T getTask() {
        return task;
    }
}
