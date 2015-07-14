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

import de.sdc.Server;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public class ServerStart {

    public static void main(String[] args) throws IOException {
        //create a server
        Server server = new Server(10000);

        //you can already add tasks before starting the server
        server.addTask(new SimpleTask(0));

        //starts the server. this will allow clients to connect
        server.start();
        
        //start calculation
        for (int i = 0; i < 5; i++) {
            calculate(server);
        }
        
        server.close();
    }

    /**
     * this method creates tasks, let them calculate by the clients and prints
     * the results
     *
     * @param server the server object which should be used to publish the tasks
     */
    private static void calculate(Server server) {
        //create tasks that the clients should execute
        for (int i = 0; i < 10; i++) {
            server.addTask(new SimpleTask(i));
        }

        try {
            //asks the server if all queued tasks are finished
            while (!server.allTasksFinished()) {
                //wait 1 milli second and ask again
                Thread.sleep(1500);
                System.out.println("Tasks finished:" + server.getProgress() * 100 + "%");
            }
        } catch (InterruptedException ex) {
        }

        //do something with the results (here they are printed)
        List<SimpleResultCmd> results = server.getResults();
        for (SimpleResultCmd result : results) {
            System.out.println(result.getValue());
        }
        server.resetQueue();
    }
}
