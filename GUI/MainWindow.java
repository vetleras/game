
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow {
    
    private JFrame frame;

    public MainWindow() {}

    public void showJudgeWindow() {
        updateView(new JudgeChooseWord());
    }

    public void updateView(JPanel newView) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(newView);
        frame.revalidate();
        frame.repaint();
        frame.pack();
    }

    public void startup() {
        frame = new JFrame();

        //frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}