package minesweeper.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MinesweeperThread implements Runnable {
    private final Socket socket;
    private final String id;
    private final String MESSAGE_THREAD_ID = "Hi, I'm the number %s thread!";

    private final String MESSAGE_BYE = "Bye.";
    private final String MESSAGE_BOOM = "BOOM!";

    public MinesweeperThread(Socket socket, String id) {
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void run() {
        try(
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true)
                ) {
            out.println(String.format(MESSAGE_THREAD_ID, this.id));

            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String output = handleRequest(line);
                if (! (output.equals(MESSAGE_BYE) || (output.equals(MESSAGE_BOOM)))) {
                    out.println(output);
                }
            }

            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Handler for client input, performing requested operations and returning an output message.
     *
     * @param input message from client
     * @return message to client, or null if none
     */
    private String handleRequest(String input) {
        final String regex = "(look)|(help)|(bye)|"
                + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
        if ( ! input.matches(regex)) {
            // invalid input
            // TODO Problem 5
            return "Invalid input.";
        }
        String[] tokens = input.split(" ");

        switch (tokens[0]) {
            case "look":
                // 'look' request
                // TODO Problem 5
                return "Look request.";
            case "help":
                // 'help' request
                // TODO Problem 5
                return "YOU DON'T NEED HELP SHUT IT";
            case "bye":
                // 'bye' request
                // TODO Problem 5
                return MESSAGE_BYE;
            default:
                int x = Integer.parseInt(tokens[1]);
                int y = Integer.parseInt(tokens[2]);

                switch (tokens[0]) {
                    case "dig":
                        // 'dig x y' request
                        // TODO Problem 5
                        return "Dig request.";
                    case "flag":
                        // 'flag x y' request
                        // TODO Problem 5
                        return "Flag X Y request.";
                    case "deflag":
                        // 'deflag x y' request
                        // TODO Problem 5
                        return "Deflag X Y request.";
                }
                break;
        }
        throw new UnsupportedOperationException(
                "End of handleRequest, this code should be unreachable.");
    }
}
