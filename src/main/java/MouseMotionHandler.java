
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

/**
 *
 * @author Prasad
 */
public class MouseMotionHandler implements MouseMotionListener, MouseListener {

    private boolean[] reachedButtons = new boolean[16];
    StringBuffer word = new StringBuffer();
    private TileBtn prevTile = null;

    void reset() {
        reachedButtons = new boolean[16];
        word = new StringBuffer();
        prevTile = null;
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (event.getSource() instanceof TileBtn) {
            TileBtn curTile = (TileBtn) event.getSource();

            String text = curTile.getText();
            int curTileIndex = Integer.parseInt(curTile.getName());

            if (!reachedButtons[curTileIndex]) {
                reachedButtons[curTileIndex] = true;
                word.append(text);
                WordWarClient.shared.mainGameWindow.wordDisplay.setText(word.toString());
                TileBtn a = (TileBtn) event.getSource();
                a.setBackground(Helper.LIGHT_YELLOW);
                WordWarClient.shared.mainGameWindow.wordArea.setBackground(Helper.LIGHT_BLUE);
                prevTile = curTile;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {

    }

    @Override
    public void mouseClicked(MouseEvent event) {

    }

    @Override
    public void mousePressed(MouseEvent event) {

    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (word.length() <= 1) {
            word.delete(0, word.length());
            Arrays.fill(reachedButtons, false);
            prevTile = null;
            
            for (int i = 0; i < 16; i++) {
                WordWarClient.shared.mainGameWindow.buttons[i].setBackground(Helper.YELLOW);
            }
            
            return;
        }
        if (WordWarClient.shared.dictionaryTrie.search(word.toString()) == TrieSearchResult.WORD) {
            if (WordWarClient.shared.player.isWordAlreadyFound(word.toString())) {
                System.out.println(word + "= already present Score=" + WordWarClient.shared.player.getScore());
            } else {
                WordWarClient.shared.player.addFoundWord(word.toString());
                System.out.println(word + "= found ");
                WordWarClient.shared.mainGameWindow.wordDisplay.setText(word.toString());
                WordWarClient.shared.player.updateScore(word.toString());
                WordWarClient.shared.sendScore();
                WordWarClient.shared.mainGameWindow.wordArea.setBackground(Helper.LIGHT_GREEN);
                WordWarClient.shared.mainGameWindow.myScore.setText(WordWarClient.shared.player.name + ": " + String.format("%03d", WordWarClient.shared.player.getScore()));
                WordWarClient.shared.mainGameWindow.myText.setText(WordWarClient.shared.player.getFoundWords());
            }
        } else {
            System.out.println(word + "= no such word ");
            WordWarClient.shared.mainGameWindow.wordArea.setBackground(Helper.LIGHT_RED);
        }

        for (int i = 0; i < 16; i++) {
            WordWarClient.shared.mainGameWindow.buttons[i].setBackground(Helper.YELLOW);

        }
        word.delete(0, word.length());
        Arrays.fill(reachedButtons, false);
        prevTile = null;
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        if ((event.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
            if (event.getSource() instanceof TileBtn) {
                TileBtn curTile = (TileBtn) event.getSource();

                String text = curTile.getText();
                int curTileIndex = Integer.parseInt(curTile.getName());

                if (!reachedButtons[curTileIndex] && prevTile != null && Helper.areNeighbours(Integer.parseInt(prevTile.getName()), curTileIndex)) {
                    reachedButtons[curTileIndex] = true;
                    word.append(text);
                    WordWarClient.shared.mainGameWindow.wordDisplay.setText(word.toString());
                    TileBtn a = (TileBtn) event.getSource();
                    a.setBackground(Helper.LIGHT_YELLOW);
                    prevTile = curTile;
                }
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent event) {

    }
}
