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

import de.sdc.commands.ResultCommand;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents a server which is able to queue tasks, to distribute
 * them to the clients and receive the results
 *
 * @author Michael Pietsch (Skywalker-11)
 * @param <T> the task class
 * @param <R> the result command class
 */
public class Server<T extends Task, R extends ResultCommand> {

    private static final Logger log = LogManager.getLogger(Server.class);

    private final ServerSocket serverSocket;
    private final Map<ClientListener, Thread> listeners = new HashMap<>();

    private final Initializer initializer;
    private final TaskQueue taskQueue;
    private final CustomCommandHandler customHandler;
    private ClientAccepter accepter;

    private Thread accepterThread;

    /**
     * Initialize the server and creats a socket. No custom commands will be
     * handle by the server. An empty init command will be send to the client
     *
     * @param port the server port
     * @throws IOException the serverport could not be created
     */
    public Server(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException e) {
            log.fatal("An other server is already running");
            throw e;
        }
        log.info("Server started");
        this.taskQueue = new TaskQueue();
        this.initializer = null;
        this.customHandler = null;
    }

    /**
     * Initialize the server and creats a socket. No custom commands will be
     * handled by the server
     *
     * @param port the server port
     * @param initializer the initializer that should be used to init the
     * clients
     * @throws IOException the serverport could not be created
     */
    public Server(int port, Initializer initializer) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException e) {
            log.fatal("An other server is already running");
            throw e;
        }
        log.info("Server started");
        this.initializer = initializer;
        this.taskQueue = new TaskQueue();
        this.customHandler = null;
    }

    /**
     * initialize the server and creats a socket
     *
     * @param port the server port
     * @param initializer the initializer that should be used to init the
     * clients
     * @param customHandler handler for custom commands (commandID = CUSTOM)
     * @throws IOException the serverport could not be created
     */
    public Server(int port, Initializer initializer, CustomCommandHandler customHandler) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException e) {
            log.fatal("An other server is already running");
            throw e;
        }
        log.info("Server started");
        this.initializer = initializer;
        this.taskQueue = new TaskQueue();
        this.customHandler = customHandler;
    }

    /**
     * starts accepting the client and listening for their commands
     */
    public void start() {
        accepter = new ClientAccepter(this);
        accepterThread = new Thread(accepter, "ClientAccepter");
        accepterThread.start();
    }

    /**
     * creates a copy of the initializer
     *
     * @return a full copy of the initializer
     */
    protected synchronized Initializer getInitializerCopy() {
        return initializer == null ? null : initializer.copy();
    }

    /**
     * connects a client
     *
     * @return the clients socket
     * @throws IOException error during connection to a client
     */
    Socket acceptClient() throws IOException {
        return serverSocket.accept();
    }

    /**
     * removes a listener from the listeners list
     *
     * @param listener listener that should be removed
     */
    void removeListener(ClientListener listener) {
        listeners.remove(listener);
    }

    /**
     * adds a listener and its thread to the listeners list
     *
     * @param listener listener that should be added
     * @param thread the thread in which the listener is running
     */
    void addListener(ClientListener listener, Thread thread) {
        listeners.put(listener, thread);
    }

    /**
     * @return returns the task queue
     */
    TaskQueue<T, R> getTaskQueue() {
        return taskQueue;
    }

    /**
     * @return the handler for custom commands
     */
    CustomCommandHandler getCustomCommandHandler() {
        return customHandler;
    }

    /**
     * resets the task queue and the result list
     */
    public void resetQueue() {
        taskQueue.resetQueue();
    }

    /**
     * adds a task to the task queue
     *
     * @param task task to be added
     */
    public void addTask(T task) {
        taskQueue.addTask(task);
    }

    /**
     * @return true, if all queued task are finished, false else
     */
    public boolean allTasksFinished() {
        return taskQueue.allTasksFinished();
    }

    /**
     * @return list of the results received from the clients
     */
    public List<R> getResults() {
        return taskQueue.getResults();
    }

    /**
     * @return the number of clients that are connected to the server
     */
    public int getCurrentClientNumber() {
        return listeners.size();
    }

    /**
     * @return the percentage of tasks that are finished or -1 if no tasks are
     * added
     */
    public double getProgress() {
        return taskQueue.getProgress();
    }

    /**
     * closes connections to all clients and ends the server
     */
    public void close() {
        accepter.close();
        synchronized (listeners) {
            for (ClientListener listener : listeners.keySet()) {
                listener.close();
            }
            try {
                //wait to let the listeners close the connection
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                //ignore exeption
            }
            Thread[] listenerThreads = listeners.values().toArray(new Thread[listeners.size()]);
            for (int i = 0; i < listeners.size(); i++) {
                listenerThreads[i].interrupt();
            }
            listeners.clear();
        }
        try {
            serverSocket.close();
        } catch (IOException ex) {
            log.log(Level.ALL, ex);
        }
        accepterThread.interrupt();
    }
}
