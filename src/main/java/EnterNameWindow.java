
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Prasad
 */
class EnterNameWindow extends JFrame {

    private JPanel enterNamePanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    JLabel label = new JLabel("Enter your name\n");
    JLabel playerCount = new JLabel();
    JTextField inputName = new JTextField();
    JButton ok = new JButton("OK");

    EnterNameWindow() {

        label.setFont(new Font("Tahoma", Font.PLAIN, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.LEFT);

        playerCount.setFont(new Font("Tahoma", Font.PLAIN, 10));
        playerCount.setAlignmentX(Component.LEFT_ALIGNMENT);
        playerCount.setHorizontalAlignment(SwingConstants.LEFT);

        ok.setFont(new Font("Tahoma", Font.PLAIN, 15));

        inputName.setFont(new Font("Tahoma", Font.PLAIN, 15));
        inputName.setSize(120, 10);

        buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.add(ok);

        enterNamePanel.setLayout(new BoxLayout(enterNamePanel, BoxLayout.Y_AXIS));
        enterNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        enterNamePanel.add(label);
        enterNamePanel.add(inputName);
        enterNamePanel.add(playerCount);
        enterNamePanel.add(buttonPanel);
        
        ok.addActionListener((event) -> WordWarClient.shared.okPressedOnEnterName());
    }

    void init() {
        setSize(300, 140);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(enterNamePanel);
        setVisible(true);
    }

    void showError(String text) {
        playerCount.setText(text);
        buttonPanel.remove(ok);
        enterNamePanel.remove(inputName);
        enterNamePanel.remove(label);
        buttonPanel.revalidate();
        buttonPanel.repaint();
        enterNamePanel.revalidate();
        enterNamePanel.repaint();
    }

    void showLoader() {
        enterNamePanel.removeAll();
        Icon imgIcon = new ImageIcon(getClass().getResource( "loader.gif"));
        JLabel label = new JLabel(imgIcon);
        enterNamePanel.setLayout(new GridBagLayout());
        enterNamePanel.add(label);
        enterNamePanel.revalidate();
        enterNamePanel.repaint();
    }
}
