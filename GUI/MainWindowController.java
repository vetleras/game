
// All of this code moved to Window.java


/*
import javax.swing.JFrame;
import javax.swing.JPanel;

import runtime.SchedulerData;

// An interface to handle click event for the callback method  
interface UIActionHandler {
    public void sendImageHandler(boolean[][] image);
}

class Handler implements UIActionHandler {
    private SchedulerData scheduler;

    public final static String DRAWING = "WINDOW_DRAWING", WORD = "WINDOW_WORD",
            CONTINUE_AS_JUDGE = "WINDOW_CONTINUE_AS_JUDGE";
    
    Handler(SchedulerData scheduler) {
        this.scheduler = scheduler;
    }

    public void sendImageHandler(boolean[][] image) {  
        scheduler.addToQueueLast(DRAWING, image);
    }
}

public class MainWindowController {
    private SchedulerData scheduler;
    private JFrame frame;
    private int drawingTime = 30;



    private ParticipantWindow p = new ParticipantWindow(new Handler(scheduler));

    
    MainWindowController(SchedulerData scheduler) {
        this.scheduler = scheduler;
        this.frame = new JFrame ("Main Window");
        this.frame.setLocationRelativeTo(null);
        this.frame.setAlwaysOnTop(true);
        this.frame.setVisible(true);
    }

    private void updateView(JPanel newView) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(newView);
        frame.revalidate();
        frame.repaint();
        frame.pack();
    }

    /// Participant mode

    // Waiting for judge to choose word
    public void showWaitingForWordWindow() {
        p.resetView();
        updateView(p);
    }

    // emits DRAWING, boolean[][] drawing after 30 seconds
    public void showDrawingWindow(String word) {
        p.receivedWord(word, drawingTime);
    }

    // emits CONTINUE_AS_JUDGE
    public void showWinAndWordSelectWindow() {
        p.showResult(true);
    }

    public void showLoosingWindow() {
        p.showResult(false);
    }

    /// Judge mode

    // emits WORD, String word
    public void showJudgeControlWindow() {
        // Display list of connected participants, enter word, and button for starting
        // game
        updateView(new JudgeChooseWord());
    }

    // Participants are drawing, timer is counting down
    public void judgeWaitingForUserWindow() {
        int drawingTime = 30;
        
        JudgeWaiting judgeWaiting = new JudgeWaiting();
        updateView(judgeWaiting);
        judgeWaiting.startCountdown(drawingTime);
    }

    // emits Pi.WINNER_CHOSEN, String id
    public void judgeSelectingWinnerWindow() {
         // Missing view
        updateView(null);
    }

    // A new new displaying the name/tag of the player that won
    public void displayWinner(String id) {
    }

}

*/