
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author Prasad
 */
class GameWindow extends JFrame {

    private String randomstring;
    private JPanel gameScreen = new JPanel();
    private JPanel playArea = new JPanel();

    private JPanel userArea = new JPanel();
    JTextArea myText = new JTextArea(6, 20);
    private JPanel foundWordsArea = new JPanel();
    JPanel wordArea = new JPanel();
    private JPanel scoreArea = new JPanel();
    private JPanel gameInfo = new JPanel();
    JLabel wordDisplay = new JLabel();
    JLabel myScore = new JLabel();
    JLabel oppScore = new JLabel();
    private JLabel stopWatch = new JLabel();
    private JPanel btnPanel = new JPanel();
    private JLabel fillerLbl = new JLabel();
    private JButton rematchBtn = new JButton("Rematch");
    private JButton newGameBtn = new JButton("Find other opponent");
    private JScrollPane myScroll = new JScrollPane(myText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    CountDownLatch declareWinnerLatch = new CountDownLatch(1);
    Timer timer;

    private final int gameDuration = 30;
    int count = gameDuration;
    boolean hasOppGivenUp;
    TileBtn[] buttons = new TileBtn[16];
    MouseMotionHandler listener = new MouseMotionHandler();

    GameWindow() {
        super("WordWar");

        myScore.setHorizontalAlignment(SwingConstants.CENTER);
        oppScore.setHorizontalAlignment(SwingConstants.CENTER);

        myText.setBackground(Helper.LIGHT_YELLOW);
        myText.setLineWrap(true);
        myText.setWrapStyleWord(true);
        myText.setEditable(false);

        fillerLbl.setFont(new Font("Tahoma", Font.BOLD, 15));
        fillerLbl.setHorizontalAlignment(SwingConstants.CENTER);

        gameScreen.setLayout(new BoxLayout(gameScreen, BoxLayout.Y_AXIS));
        gameScreen.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        gameScreen.setBackground(Helper.LIGHT_BLUE);

        stopWatch.setFont(new Font("Tahoma", Font.BOLD, 35));
        stopWatch.setHorizontalAlignment(SwingConstants.CENTER);
        stopWatch.setForeground(new Color(30, 30, 30));
        stopWatch.setText(" ");
        
        wordDisplay.setFont(new Font("Tahoma", Font.BOLD, 30));
        wordDisplay.setForeground(new Color(30, 30, 30));
        wordDisplay.setText(" ");
        wordArea.setBackground(Helper.LIGHT_BLUE);

        wordArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.gray, Color.gray));
        wordArea.setSize(400, 200);
        wordArea.add(wordDisplay);

        foundWordsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        foundWordsArea.setLayout(new BoxLayout(foundWordsArea, BoxLayout.X_AXIS));
        foundWordsArea.setBackground(Helper.LIGHT_BLUE);
        foundWordsArea.add(myScroll);

        scoreArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scoreArea.setLayout(new GridLayout(3,1));
        scoreArea.setBackground(Helper.LIGHT_BLUE);
        scoreArea.add(fillerLbl);
        scoreArea.add(myScore);
        scoreArea.add(oppScore);

        gameInfo.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        gameInfo.setLayout(new GridLayout(2, 1));
        gameInfo.setBackground(Helper.LIGHT_BLUE);
        gameInfo.add(scoreArea);
        gameInfo.add(stopWatch);

        userArea.setBackground(Helper.LIGHT_BLUE);
        userArea.setLayout(new BoxLayout(userArea, BoxLayout.X_AXIS));
        userArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.gray, Color.gray));
        userArea.setSize(400, 200);
        userArea.add(foundWordsArea);
        userArea.add(gameInfo);

        gameScreen.add(wordArea);
        gameScreen.add(playArea);
        gameScreen.add(userArea);

        playArea.setLayout(new GridLayout(4, 4, 25, 25));
        playArea.setBackground(Helper.LIGHT_BLUE);
        playArea.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        add(gameScreen);

        rematchBtn.addActionListener((event) -> {

            showLoaderInGameInfoPanel();
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WordWarClient.shared.rematch();
            }).start();
        });

        newGameBtn.addActionListener((event) -> {
            new Thread(() -> WordWarClient.shared.startNewGame()).start();
            showLoaderInGameInfoPanel();
        });
    }

    private void showLoaderInGameInfoPanel() {
        gameInfo.removeAll();
        Icon imgIcon = new ImageIcon(getClass().getResource("loader.gif"));
        JLabel label = new JLabel(imgIcon);
        gameInfo.setLayout(new GridBagLayout());
        gameInfo.add(label);
        gameInfo.revalidate();
        gameInfo.repaint();
    }
    
    void init(){
        this.setSize(500, 650);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setBoard() {
        System.out.println("Random string = " + randomstring);
        for (int i = 0; i < 16; i++) {
            if (buttons[i] == null) {
                buttons[i] = new TileBtn(Character.toString(randomstring.charAt(i)));
                buttons[i].setName(Integer.toString(i));
                playArea.add(buttons[i]);
            }
        }

        for (int i = 0; i < 16; i++) {
            buttons[i].initializeBtn();
            buttons[i].addMouseMotionListener(listener);
            buttons[i].addMouseListener(listener);
            buttons[i].setText(Character.toString((randomstring.charAt(i))));
        }
    }

    private void runStopWatch() {
        timer = new Timer(1000, ((event) -> {
            count--;
            if (count == 0) {
                stopWatch.setText(" " + count + " ");
                timer.stop();
                disableBtns();
                if (!hasOppGivenUp) {
                    WordWarClient.shared.endGame();
                    WordWarClient.shared.sendScore();
                }
                try {
                    declareWinner();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                stopWatch.setText(" " + (count) + " ");
            }
        }));
        timer.setInitialDelay(0);
        timer.start();
    }

    private void declareWinner() throws InterruptedException {
        gameInfo.remove(stopWatch);
        declareWinnerLatch.await();

        myText.setText("All possible words: " + WordWarClient.shared.dictionaryTrie.allWordsFromBoard.size() + "\n" + WordWarClient.shared.dictionaryTrie.allWordsFromBoard.toString());
        myText.setCaretPosition(0);

        initBtnPanel();

        gameInfo.add(btnPanel);

        myScore.setText(WordWarClient.shared.player.name + ": " + WordWarClient.shared.player.getScore());
        oppScore.setText(WordWarClient.shared.oppName + ": " + WordWarClient.shared.oppScore);

        if (hasOppGivenUp) {
            fillerLbl.setText("YOU WON! " + WordWarClient.shared.oppName + " has given up!");
            setBgColorToUserArea(Helper.LIGHT_GREEN);
            btnPanel.remove(rematchBtn);
        } else if (WordWarClient.shared.player.getScore() > WordWarClient.shared.oppScore) {
            fillerLbl.setText("YOU WON!");
            setBgColorToUserArea(Helper.LIGHT_GREEN);
        } else if (WordWarClient.shared.player.getScore() < WordWarClient.shared.oppScore){
            fillerLbl.setText("YOU LOST!");
            setBgColorToUserArea(Helper.LIGHT_RED);
        } else {
            fillerLbl.setText("IT'S A TIE!");
        }

        gameInfo.revalidate();
        gameInfo.repaint();
        new Thread(() -> WordWarClient.shared.waitForOppAction()).start();

    }

    void startGame(String randomString) {
        myText.setText("");
        count = gameDuration;
        hasOppGivenUp = false;
        myScore.setText(WordWarClient.shared.player.name + ": 000");
        oppScore.setText(WordWarClient.shared.oppName + ": 000");
        this.randomstring = randomString;

        fillerLbl.setText("");

        gameInfo.removeAll();
        gameInfo.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        gameInfo.setLayout(new GridLayout(2, 1));

        setBgColorToUserArea(Helper.LIGHT_BLUE);

        wordDisplay.setText(" ");
        wordArea.setBackground(Helper.LIGHT_BLUE);

        stopWatch.setText(" ");
        gameInfo.add(scoreArea);
        gameInfo.add(stopWatch);

        setBoard();
        setVisible(true);
        gameInfo.revalidate();
        gameInfo.repaint();
        runStopWatch();
    }

    private void disableBtns() {
        listener.reset();
        for (TileBtn btn: buttons) {
            btn.removeMouseListener(listener);
            btn.removeMouseMotionListener(listener);
            btn.setForeground(Color.gray);
            btn.setBackground(Helper.LIGHT_YELLOW);
        }
    }

    void updateUIOnOppLeft() {
        gameInfo.removeAll();
        gameInfo.setLayout(new GridLayout(2, 1));
        gameInfo.add(scoreArea);
        btnPanel.removeAll();
        btnPanel.add(newGameBtn);
        gameInfo.add(btnPanel);
        gameInfo.revalidate();
        gameInfo.repaint();
    }

    private void initBtnPanel() {
        btnPanel.setLayout(new GridLayout(2, 1));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnPanel.add(rematchBtn);
        btnPanel.add(newGameBtn);
        btnPanel.setBackground(Helper.LIGHT_BLUE);
    }

    private void setBgColorToUserArea(Color color) {
        userArea.setBackground(color);
        foundWordsArea.setBackground(color);
        gameInfo.setBackground(color);
        scoreArea.setBackground(color);
        btnPanel.setBackground(color);
    }
}
