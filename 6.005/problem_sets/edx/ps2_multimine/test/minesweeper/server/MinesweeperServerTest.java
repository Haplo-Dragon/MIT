/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import org.junit.Test;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO
 */
public class MinesweeperServerTest {

    private static final String LOCALHOST = "127.0.0.1";
    private static final int PORT = 4000 + new Random().nextInt(1 << 15);

    private static final int MAX_CONNECTION_ATTEMPTS = 10;

    /**
     * Start a MinesweeperServer in debug mode with a board file from BOARDS_PKG.
     *
     * @return thread running the server
     * @throws IOException if the board file cannot be found
     */
    private static Thread startMinesweeperServer(int port) throws IOException {
        final String[] args = new String[] {
                "--debug",
                "--port", Integer.toString(port)
        };
        Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
        serverThread.start();
        return serverThread;
    }

    /**
     * Connect to a MinesweeperServer and return the connected socket.
     *
     * @param server abort connection attempts if the server thread dies
     * @return socket connected to the server
     * @throws IOException if the connection fails
     */
    private static Socket connectToMinesweeperServer(Thread server, int port) throws IOException {
        int attempts = 0;
        while (true) {
            try {
                Socket socket = new Socket(LOCALHOST, port);
                socket.setSoTimeout(3000);
                return socket;
            } catch (ConnectException ce) {
                if ( ! server.isAlive()) {
                    throw new IOException("Server thread not running");
                }
                if (++attempts > MAX_CONNECTION_ATTEMPTS) {
                    throw new IOException("Exceeded max connection attempts", ce);
                }
                try { Thread.sleep(attempts * 10); } catch (InterruptedException ie) { }
            }
        }
    }

    @Test(timeout = 10000)
    public void testMinimumThreadCreation() throws IOException {
        final int port = 4000 + new Random().nextInt(1 << 15);
        Thread thread = startMinesweeperServer(port);
        Socket socket = connectToMinesweeperServer(thread, port);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        assertTrue(
                "Expected thread identification message.",
                in.readLine().startsWith("Hi, I'm the"));

        out.println("look");
        assertTrue(
                "Expected look message.",
                in.readLine().startsWith("Look request"));

        out.println("help");
        assertTrue(
                "Expected help message.",
                in.readLine().startsWith("YOU DON'T NEED HELP"));

        out.println("bye");
        socket.close();

//        out.println("look");
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//
//        out.println("dig 3 1");
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - 1 - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//        assertEquals("- - - - - - -", in.readLine());
//
//        out.println("dig 4 1");
//        assertEquals("BOOM!", in.readLine());
//
//        out.println("look"); // debug mode is on
//        assertEquals("             ", in.readLine());
//        assertEquals("             ", in.readLine());
//        assertEquals("             ", in.readLine());
//        assertEquals("             ", in.readLine());
//        assertEquals("             ", in.readLine());
//        assertEquals("1 1          ", in.readLine());
//        assertEquals("- 1          ", in.readLine());
    }

    @Test(timeout = 10000)
    public void testMultipleThreads() throws IOException {
        final int port_1 = 4000 + new Random().nextInt(1 << 15);
        Thread thread_1 = startMinesweeperServer(port_1);
        Socket socket_1 = connectToMinesweeperServer(thread_1, port_1);

        BufferedReader in_1 = new BufferedReader(new InputStreamReader(socket_1.getInputStream()));
        PrintWriter out_1 = new PrintWriter(socket_1.getOutputStream(), true);

        assertTrue(
                "Expected thread identification message.",
                in_1.readLine().startsWith("Hi, I'm the"));

        out_1.println("look");
        assertTrue(
                "Expected look message.",
                in_1.readLine().startsWith("Look request"));

        out_1.println("help");
        assertTrue(
                "Expected help message.",
                in_1.readLine().startsWith("YOU DON'T NEED HELP"));

        final int port_2 = 4000 + new Random().nextInt(1 << 15);
        Thread thread_2 = startMinesweeperServer(port_2);
        Socket socket_2 = connectToMinesweeperServer(thread_2, port_2);

        BufferedReader in_2 = new BufferedReader(new InputStreamReader(socket_2.getInputStream()));
        PrintWriter out_2 = new PrintWriter(socket_2.getOutputStream(), true);

        assertTrue(
                "Expected thread identification message.",
                in_2.readLine().startsWith("Hi, I'm the"));

        out_2.println("look");
        assertTrue(
                "Expected look message.",
                in_2.readLine().startsWith("Look request"));

        out_2.println("help");
        assertTrue(
                "Expected help message.",
                in_2.readLine().startsWith("YOU DON'T NEED HELP"));

        out_1.println("bye");
        socket_1.close();

        out_2.println("bye");
        socket_2.close();
    }

}
