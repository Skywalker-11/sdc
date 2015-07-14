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

import de.sdc.commands.TaskCommand;
import de.sdc.commands.Command;
import de.sdc.commands.DisconnectCommand;
import de.sdc.commands.InitCommand;
import de.sdc.commands.ResultCommand;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Listener that sends tasks to the client and handles the responses
 *
 * @author Michael Pietsch (Skywalker-11)
 */
class ClientListener implements Runnable {

    private static final Logger log = LogManager.getLogger(ClientListener.class);
    private static final AtomicInteger nextId = new AtomicInteger(0);

    private final Socket socket;
    private final int id;
    private final BufferedOutputStream bostream;
    private final BufferedInputStream bistream;
    private final Server server;
    private final TaskQueue taskQueue;

    private Task runningTask = null;
    private boolean isRunning;

    /**
     * creates a new listener for a client socket.
     *
     * @param socket the network socket to a client
     * @param server the server object that should be used
     * @throws IOException if an exception while creating the streams occured
     */
    protected ClientListener(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.bostream = new BufferedOutputStream(socket.getOutputStream());
        this.bistream = new BufferedInputStream(socket.getInputStream());
        log.info("Connected new client ({})", socket.getRemoteSocketAddress().toString());
        this.id = nextId.getAndIncrement();
        this.server = server;
        this.taskQueue = server.getTaskQueue();
    }

    /**
     * this starts the clientlistener by listening on the socket connection for
     * incomming commands and handles them until the listener is closed
     */
    @Override
    public void run() {
        isRunning = true;
        try {
            Initializer init = server.getInitializerCopy();
            if (init != null) {
                NetworkUtil.sendObject(new InitCommand(init), bostream);
            }

            while (isRunning) {
                Command c = NetworkUtil.receiveObject(bistream);
                switch (c.getType()) {
                    case RESULT:
                        handleResultCommand((ResultCommand) c);
                        break;

                    case REQUEST_TASK:
                        handleRequestTaskCommand();
                        break;

                    case DISCONNECT:
                        disconnect();
                        break;

                    case CUSTOM:
                        handleCustomCommand(c);
                        break;

                    default:
                        log.error("received command could not be handled (commandID={}): closing connection to client", c.getType());
                        server.removeListener(this);
                        disconnect();
                        break;
                }
            }
        } catch (SocketException | EOFException e) {
            log.warn("Connection lost to Client {}", id);
            server.removeListener(this);
            disconnect();
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    /**
     * returns the id of the client
     *
     * @return id of the client
     */
    protected int getId() {
        return id;
    }

    /**
     * handles a request command of the client. This will send a task if one is
     * available
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void handleRequestTaskCommand() throws IOException {
        log.debug("Received request from {}", id);
        Command toSend;

        try {
            while (runningTask == null) {
                runningTask = taskQueue.pollTask();
                Thread.sleep(20);
            }
        } catch (InterruptedException ex) {
            log.log(Level.TRACE, ex);
        }
        TaskCommand command = new TaskCommand(runningTask);
        toSend = command;
        log.debug("Send task {}", toSend.getId());
        NetworkUtil.sendObject(toSend, bostream);
    }

    /**
     * handles the result from the client
     *
     * @param result the resultcommand from the client
     */
    private void handleResultCommand(ResultCommand result) {
        log.debug("Received {} from client {}", result.getDescription(), id);
        taskQueue.finishTask(runningTask, result);
        runningTask = null;
    }

    /**
     * handles a custom command from the client
     *
     * @param customCommand the customCommand received from the client
     * @throws IOException
     */
    private void handleCustomCommand(Command customCommand) throws IOException {
        log.debug("Received custom command from client {}", id);
        CustomCommandHandler commandHandler = server.getCustomCommandHandler();
        Command responseCommand = commandHandler.handleCustomCommand(customCommand);
        if (responseCommand != null) {
            NetworkUtil.sendObject(responseCommand, bostream);
        }
    }

    /**
     * closes the streams and the sockets to the client
     *
     * @param runningTask task that currently is executed by the client
     */
    private void disconnect() {
        try {
            bistream.close();
            bostream.close();
            socket.close();
        } catch (IOException ex) {
            log.log(Level.ALL, ex);
        }
        if (runningTask != null) {
            taskQueue.setTaskAvailable(runningTask);
        }
        log.info("client {} disconnected", id);
    }

    /**
     * sends an disconnect to the client and closes the streams and sockets
     */
    protected void close() {
        isRunning = false;
        try {
            NetworkUtil.sendObject(new DisconnectCommand(), bostream);
        } catch (IOException ex) {
            log.log(Level.ALL, ex);
        }
        disconnect();
    }
}
