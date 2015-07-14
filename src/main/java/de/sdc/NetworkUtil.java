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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.nustaq.serialization.FSTConfiguration;

/**
 * This class offers methods to send and receive objects over the network
 * For faster serialization FST is used.
 *
 * @author Michael Pietsch (Skywalker-11)
 */
public class NetworkUtil {

    private NetworkUtil() {
    }

    /**
     * this is a part of the java library FST of Ruediger Moeller and is used
     * for more performant serialization. Available at
     * http://ruedigermoeller.github.io/fast-serialization/
     */
    private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    /**
     * This method sends an object over the output stream
     *
     * @param <T> the class of the object what is expected to be received
     * @param toSend the object that should be sended
     * @param outputStream the output stream over which
     * @throws IOException thrown if the sending fails
     */
    protected static <T> void sendObject(T toSend, OutputStream outputStream) throws IOException {
        ObjectOutputStream oostream = new ObjectOutputStream(outputStream);
        byte[] buffer = conf.asByteArray(toSend);
        oostream.writeInt(buffer.length);
        oostream.write(buffer);
        oostream.flush();
    }

    /**
     * receives an object from an input stream
     *
     * @param <T> the object class in which the received object should be casted
     * @param inputStream the inputstream
     * @return the received and casted object
     * @throws IOException is thrown if the receiving fails
     */
    protected static <T> T receiveObject(InputStream inputStream) throws IOException {
        ObjectInputStream oistream = new ObjectInputStream(inputStream);
        int length = oistream.readInt();
        byte[] buff = new byte[length];
        while (length > 0) {
            length -= oistream.read(buff, buff.length - length, length);
        }
        return (T) conf.asObject(buff);
    }
}
