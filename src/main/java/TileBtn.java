
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;

/**
 *
 * @author Prasad
 */

class TileBtn extends JButton {

    TileBtn(String s) {
        super(s);
    }

    void initializeBtn() {
        setOpaque(true);
        setBackground(Helper.YELLOW);
        setForeground(new Color(30, 30, 30));
        setBorderPainted(false);
        setFocusable(false);
        setFocusPainted(false);
        setFont(new Font("Tahoma", Font.BOLD, 45));
    }
    
}
