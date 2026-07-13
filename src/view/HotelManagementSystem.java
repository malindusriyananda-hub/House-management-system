package view;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelManagementSystem {

    private static final Logger logger = Logger.getLogger(HotelManagementSystem.class.getName());

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Nimbus L&F not available", ex);
        }

        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
