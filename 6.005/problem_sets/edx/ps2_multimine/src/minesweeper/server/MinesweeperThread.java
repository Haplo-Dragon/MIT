package minesweeper.server;

import minesweeper.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MinesweeperThread implements Runnable {
    private final Socket socket;
    private final int id;
    private final Board board;
    private final boolean debug;
    private final String MESSAGE_WELCOME =
            "Welcome to Minesweeper. Players: %d including " +
            " you. Board: %d columns by %d rows. Type 'help' for help. " +
            "You're player #%d.";

    private final String MESSAGE_BYE = "Bye.";
    private final String MESSAGE_BOOM = "BOOM!";
    private final String MESSAGE_HELP =
            "It's Minesweeper! You're probably familiar with " +
            " the rules. Commands: 'look' to see the board. 'dig X Y' to dig a space. " +
            "'flag X Y' to flag a space, 'deflag X Y' to deflag a space. 'help' to show" +
            "this help message. 'bye' to disconnect.";

    public MinesweeperThread(Socket socket, int id, Board board, boolean debug) {
        this.socket = socket;
        this.id = id;
        this.board = board;
        this.debug = debug;
    }

    @Override
    public void run() {
        try(
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true)
                ) {
            out.println(String.format(MESSAGE_WELCOME,
                    this.id + 1,
                    this.board.getX_size(),
                    this.board.getY_size(),
                    this.id + 1));

//            for (String line = in.readLine(); line != null; line = in.readLine()) {
            boolean listening = true;
            while(listening) {
                String line = in.readLine();
//                System.out.println(String.format(
//                        "Message received from thread %d: %s",
//                        this.id, line));
                String output = handleRequest(line);
                // If the user isn't requesting a disconnect or getting blown up, show the
                // response.
                if (! (output.equals(MESSAGE_BYE) || (output.equals(MESSAGE_BOOM)))) {
                    out.println(output);
                } else if (output.equals(MESSAGE_BOOM)) {
                    // If the user is getting blown up, we'll close their connection
                    // unless debug = true.
                    if (!debug) {
                        out.println(output);
                        listening = false;
                    } else {
                        out.println(output);
                    }
                } else if (output.equals(MESSAGE_BYE)) {
                    listening = false;
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
        if ( ! (input.matches(regex))) {
            // Invalid input, so we'll show a help message.
            return MESSAGE_HELP;
        }
        String[] tokens = input.split(" ");

        switch (tokens[0]) {
            case "look":
                // 'look' request.
                // System.out.println(this.board);
                return this.board.toString();
            case "help":
                // 'help' request.
                return MESSAGE_HELP;
            case "bye":
                // 'bye' request.
                return MESSAGE_BYE;
            default:
                int x = Integer.parseInt(tokens[1]);
                int y = Integer.parseInt(tokens[2]);

                switch (tokens[0]) {
                    case "dig":
                        // 'dig x y' request
                        // If the position is invalid, do nothing and return a board
                        // message.
                        if (! (this.board.isValidPosition(x, y))) {
                            return this.board.toString();
                        }

                        // If there's a mine in the given square, the mine explodes and
                        // the user is disconnected (unless debug = true).
                        if (this.board.hasMine(x, y)) {
                            this.board.dig(x, y);
                            return MESSAGE_BOOM;
                        }

                        // If there's no mine in the given square, dig states and mine
                        // counts are updated and a board message is returned.
                        this.board.dig(x, y);
                        return this.board.toString();
                    case "flag":
                        // 'flag x y' request
                        if (this.board.isUntouched(x, y)) {
                            this.board.flag(x, y);
                        }
                        return this.board.toString();
                    case "deflag":
                        // 'deflag x y' request
                        this.board.deflag(x, y);
                        return this.board.toString();
                }
                break;
        }
        throw new UnsupportedOperationException(
                "End of handleRequest, this code should be unreachable.");
    }
}
