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
package de.sdc.examples.simple;

import de.sdc.Client;
import de.sdc.commands.Command;
import de.sdc.commands.TaskCommand;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public class ClientStart {

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 10000);
        client.requestCommand();
        while (client.isConnected()) {
            //receive a command (thread will be blocked till a task is received from the server 
            Command command = client.requestCommand();
            
            switch (command.getType()) {
                case TASK: //a taskcommand was received
                    TaskCommand toExecute = (TaskCommand) command;

                    //get the task from the command
                    SimpleTask task = (SimpleTask) toExecute.getTask();

                    //get parameter from the task
                    double start = task.getStart();

                    //do what ever you want
                    double result = doSth(start);

                    //create a result command with the calculated result
                    SimpleResultCmd resultCmd = new SimpleResultCmd(result);

                    //send the result
                    client.sendResult(resultCmd);
                    break;

                case DISCONNECT: // the server sent a disconnect
                    //close the client connection
                    client.close();
                    System.out.print("Clean disconnect");
                    System.exit(0);
                    
                default:
                    throw new InvalidClassException("only task command accepted");
            }
        }
    }

    public static double doSth(double start) {
        try {
            //simulate a more complex calculation by sleeping the client
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
        }

        //do the real calculation
        return Math.pow(start, 2);
    }
}
