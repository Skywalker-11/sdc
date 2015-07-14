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
import de.sdc.commands.CommandType;
import de.sdc.commands.DisconnectCommand;
import de.sdc.commands.InitCommand;
import de.sdc.commands.RequestCommand;
import de.sdc.commands.ResultCommand;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the client which could receive commands from a server and forwards
 * the commands to the caller
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public class Client {

    private final Socket socket;
    private static final Logger log = LogManager.getLogger(Client.class);
    private final BufferedOutputStream bostream;
    private final BufferedInputStream bistream;

    /**
     * starts connection to server. if you want to use an init command from the
     * server receiveInitCommand should be the next method that is called on the
     * client
     *
     *
     * @param host server to connect
     * @param port server port
     * @throws IOException error during connection with server
     */
    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        bostream = new BufferedOutputStream(socket.getOutputStream());
        bistream = new BufferedInputStream(socket.getInputStream());
        log.info("connected to server");
    }

    /**
     * !!! If you want to receive an initCommand from the server this method
     * must be the first method called after creating the client object!!!
     *
     * receives an initcommand from the server. If no Initializer is specified
     * in the server no init command will be send and so this method will always
     * throw an InvalidClassException
     *
     * @return initcommand returned by the server (initializer could be null if
     * no initializer is specified in the server
     * @throws IOException error during connection with server
     *
     */
    public InitCommand receiveInitCommand() throws IOException {
        Command command = NetworkUtil.receiveObject(bistream);
        if (command.getType() != CommandType.INIT) {
            throw new InvalidClassException("Expected an init command, but received the command:" + command.getType());
        }
        log.trace("received init command: " + command.getId());
        return (InitCommand) command;
    }

    /**
     * checks if the client is connected to the server
     *
     * @return true if connection is established, false else
     */
    public boolean isConnected() {
        return !socket.isClosed();
    }

    /**
     * retrieves command from server by sending a request task
     *
     * @return command returned by server
     * @throws IOException a SocketException is thrown if an error occured with
     * the network connection. An EOFException occures if the server was shut
     * down
     */
    public Command requestCommand() throws IOException {
        RequestCommand request = new RequestCommand();
        log.trace("request command: {}", request.getId());
        NetworkUtil.sendObject(request, bostream);

        Command command = NetworkUtil.receiveObject(bistream);
        log.trace("Received command: {}", command.getId());

        return command;
    }

    /**
     * sends a command to the server
     *
     * @param command command should be send to the server
     * @throws IOException error during connection with server
     */
    public void sendCommand(Command command) throws IOException {
        log.trace("send command: {} of type {}", command.getId(), command.getType());
        NetworkUtil.sendObject(command, bostream);
    }

    /**
     * sends a command to the server
     *
     * @param command command should be send to the server
     * @throws IOException error during connection with server
     */
    public void sendResult(ResultCommand command) throws IOException {
        sendCommand(command);
    }

    /**
     * disconnects the client
     *
     * @throws IOException error during connection with server
     */
    public void disconnect() throws IOException {
        DisconnectCommand disconnect = new DisconnectCommand();
        log.trace("send disconnect command: {}", disconnect.getId());
        sendCommand(disconnect);
        close();
    }

    /**
     * closes the streams and the socket
     *
     * @throws IOException error during connection with server
     */
    public void close() throws IOException {
        bostream.close();
        bistream.close();
        socket.close();
    }
}
