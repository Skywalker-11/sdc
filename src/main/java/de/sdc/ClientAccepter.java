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

import java.io.IOException;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to accept new clients on the server socket and creates a
 * client listener for it.
 *
 * @author Michael Pietsch (Skywalker-11)
 */
class ClientAccepter implements Runnable {

    private static final Logger log = LogManager.getLogger(ClientAccepter.class);
    private final Server server;
    private boolean running;

    /**
     * initializes the accepter
     *
     * @param server
     */
    ClientAccepter(Server server) {
        this.server = server;
    }

    /**
     * starts the accepter. this enables the clients to connect to the server
     */
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                Socket clientSocket = server.acceptClient();
                if (running) {
                    ClientListener listener = new ClientListener(
                            clientSocket,
                            server
                    );
                    Thread listenerThread = new Thread(listener, "ClientListener" + listener.getId());
                    server.addListener(listener, listenerThread);
                    listenerThread.start();
                } else {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                if (running) {
                    //if the server should be closed an exception occures 
                    //so don't log it in that case
                    log.error("connection to client failed: {}", ex);
                }
            }
        }
    }

    /**
     * closes the accepter thread
     */
    protected void close() {
        running = false;
    }
}
