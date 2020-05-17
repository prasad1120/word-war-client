
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

/**
 *
 * @author Prasad
 */
public class WordWarClient {

    private static Socket clientSocket = null;
    private static PrintWriter outputStreamPrinter = null;
    private static BufferedReader inputStreamReader = null;
    private static int portNumber = 8000;
    private static String host = "localhost";

    GameWindow mainGameWindow;
    Player player;
    DictionaryTrie dictionaryTrie;
    private EnterNameWindow enterName;
    int oppScore;
    String oppName;
    static WordWarClient shared;
    private CountDownLatch rematchLatch = new CountDownLatch(1);
    boolean rematch;

    public static void main(String[] args) {
        Enumeration<URL> resources = null;
        try {
            resources = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int trial = 0;
        while (resources != null && resources.hasMoreElements()) {
            try {
                if (++trial == 3) {
                    break;
                }
                URL manifestUrl = resources.nextElement();

                if (Pattern.compile(".*word-war-client.*jar!/META-INF/MANIFEST.MF").matcher(manifestUrl.getPath()).find()) {
                    Manifest manifest = new Manifest(manifestUrl.openStream());
                    Attributes mainAttributes = manifest.getMainAttributes();
                    String port = mainAttributes.getValue("Port");
                    String host = mainAttributes.getValue("Host");
                    WordWarClient.host = host;
                    portNumber = Integer.parseInt(port);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                portNumber = 8000;
            }
        }
        startGame();
    }

    private static void startGame(){
        shared = new WordWarClient();
        Helper.createAdjacencyGraph(4, 4);
        System.out.println("Now using host=" + host + ", portNumber=" + portNumber);
        setupSocketAndIO();

        try {
            if (clientSocket != null && outputStreamPrinter != null && inputStreamReader != null) {

                String temp;
                if ((temp = inputStreamReader.readLine()).equals("Server too busy. Try later.")) {
                    System.out.println("Server too busy. Try later.");
                    shared.enterName.showError("Server too busy. Try later!");
                    return;
                }

                shared.enterName.playerCount.setText("Number of players online : " + temp);
                shared.oppName = inputStreamReader.readLine();
                shared.enterName.dispose();

                WordWarClient.shared.playGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupSocketAndIO() {
        try {
            clientSocket = new Socket(host, portNumber);
            outputStreamPrinter = new PrintWriter(clientSocket.getOutputStream(), true);
            inputStreamReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            shared.enterName.showError("Server not available. Try later!");
            e.printStackTrace();
        }
    }

    private WordWarClient() {
        enterName = new EnterNameWindow();
        enterName.init();

        enterName.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (outputStreamPrinter != null)
                    outputStreamPrinter.println("_abort_");
                closeSocketAndIO();
                enterName.dispose();
                mainGameWindow.dispose();
                System.out.println("Enter name window closed");
            }
        });
        System.out.println("Enter name window shown");

        mainGameWindow = new GameWindow();
        mainGameWindow.init();
        player = new Player();

        mainGameWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                mainGameWindow.dispose();
                closeConnection();
                System.out.println("Game window closed");
            }
        });
    }

    void sendScore() {
        outputStreamPrinter.println(shared.player.getScore() + "");
        System.out.println("Sent score: " + shared.player.getScore());
    }

    void endGame() {
        outputStreamPrinter.println("_end_");
    }

    private void closeConnection() {
        endGameThreadOnServer();
        closeSocketAndIO();
    }

    private void endGameThreadOnServer() {
        outputStreamPrinter.println("left_game");
        System.out.println("Left game initiated");
    }

    private void closeSocketAndIO() {
        try {
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (outputStreamPrinter != null)
                outputStreamPrinter.close();
            if (clientSocket != null)
                clientSocket.close();
            System.out.println("Sockets closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void okPressedOnEnterName() {

        shared.player.name = enterName.inputName.getText();
        System.out.println(shared.player.name + ": name entered");
        outputStreamPrinter.println(shared.player.name);
        outputStreamPrinter.println("_start_");
        System.out.println("Start game initiated");
        enterName.showLoader();

    }

    void rematch() {
        rematch = true;
        outputStreamPrinter.println("_rematch_");
        rematchLatch.countDown();
        System.out.println("Rematch initiated");
    }

    void startNewGame() {
        outputStreamPrinter.println("new_match");
        rematchLatch.countDown();
        System.out.println("New game initiated");
    }

    void waitForOppAction() {
        try {

            label:
            while (true) {
                String s = inputStreamReader.readLine();
                System.out.println("Waiting for opp action, received = " + s);
                if (s == null) {
                    return;
                }
                switch (s) {
                    case "_rematch_":
                        rematchLatch.await();
                        if (rematch) {
                            System.out.println("Rematch starting");
                            playGame();
                            break label;
                        }
                        break;
                    case "left_game":
                        outputStreamPrinter.println("opp_left");
                        mainGameWindow.updateUIOnOppLeft();
                        break;
                    case "new_match":
                        oppName = inputStreamReader.readLine();
                        System.out.println("Opp name received: " + oppName);
                        System.out.println("New match starting");
                        playGame();
                        break label;
                }
            }
            rematch = false;
            rematchLatch = new CountDownLatch(1);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playGame() {
        try {
            player.reset();
            mainGameWindow.declareWinnerLatch = new CountDownLatch(1);


            oppScore = 0;
            String ranString = inputStreamReader.readLine();

            dictionaryTrie = new DictionaryTrie();

            mainGameWindow.startGame(ranString);

            ArrayList<String> wordList = shared.dictionaryTrie.getAllWordsFromBoard(ranString);
            System.out.println("Number of all possible words = " + wordList.size());
            System.out.println(wordList);

            String s;

            while (true) {
                s = inputStreamReader.readLine();
                System.out.println("inputMsg in playGame = " + s);
                if (s == null) {
                    return;
                }
                if (s.equals("_end_")) {
                    oppScore = Integer.parseInt(inputStreamReader.readLine());
                    break;
                } else if (s.equals("left_game")) {
                    outputStreamPrinter.println("opp_left");
                    mainGameWindow.count = 1;
                    mainGameWindow.hasOppGivenUp = true;
                    break;
                }

                oppScore = Integer.parseInt(s);
                mainGameWindow.oppScore.setText(shared.oppName + ": " + String.format("%03d", oppScore));
            }


            shared.mainGameWindow.declareWinnerLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
