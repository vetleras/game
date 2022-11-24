import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import runtime.SchedulerData;

// An interface to handle click event for the callback method  
interface UIActionHandler {
    public void sendImageHandler(String image);

    public void judgeSendWinnerID(String id);

    public void judgeSendWord(String word);

    public void continueAsJudge();
}

class Handler implements UIActionHandler {
    private SchedulerData scheduler;

    public final static String DRAWING = "WINDOW_DRAWING", WORD = "WINDOW_WORD",
            CONTINUE_AS_JUDGE = "WINDOW_CONTINUE_AS_JUDGE", WINNER_CHOSEN = "WINDOW_WINNER_CHOSEN";

    Handler(SchedulerData scheduler) {
        this.scheduler = scheduler;
    }

    public void sendImageHandler(String image) {
        scheduler.addToQueueLast(DRAWING, image);
    }

    public void judgeSendWinnerID(String id) {
        scheduler.addToQueueLast(WINNER_CHOSEN, id);
    }

    public void judgeSendWord(String word) {
        scheduler.addToQueueLast(WORD, word);
    }

    public void continueAsJudge() {
        scheduler.addToQueueLast(CONTINUE_AS_JUDGE);
    }
}

public class Window {
    private SchedulerData scheduler;
    private JFrame frame;
    private int drawingTime = 30;
    private Handler actionHandler;
    private ParticipantWindow p;

    public final static String DRAWING = "WINDOW_DRAWING", WORD = "WINDOW_WORD",
            CONTINUE_AS_JUDGE = "WINDOW_CONTINUE_AS_JUDGE", WINNER_CHOSEN = "WINDOW_WINNER_CHOSEN";

    Window(SchedulerData scheduler) {
        this.scheduler = scheduler;
        this.frame = new JFrame("Main Window");
        this.frame.setLocationRelativeTo(null);
        this.frame.setAlwaysOnTop(true);
        this.frame.setVisible(true);
        this.actionHandler = new Handler(scheduler);
        this.p = new ParticipantWindow(actionHandler);

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
    public void P_waitForWord() {
        p.resetView();
        updateView(p);
    }

    // emits DRAWING, String drawing after 30 seconds
    public void P_startDrawing(String word) {
        p = new ParticipantWindow(actionHandler);
        p.resetView();
        updateView(p);
        p.receivedWord(word, drawingTime);
    }

    // emits CONTINUE_AS_JUDGE
    public void P_displayWin() {
        p.showResult(true);
    }

    public void P_displayLoss(String id) {

        p.showResult(false, id);
    }

    /// Judge mode

    // emits WORD, String word
    public void J_chooseWord() {
        // Display list of connected participants, enter word, and button for starting
        // game
        updateView(new JudgeChooseWord(actionHandler));
    }

    // Participants are drawing, timer is counting down
    public void J_waitForDrawings() {
        JudgeWaiting judgeWaiting = new JudgeWaiting();
        updateView(judgeWaiting);
        judgeWaiting.startCountdown(drawingTime);
    }

    // emits WINNER_CHOSEN, String id
    public void J_selectWinner(ArrayList<DrawingMessage> drawings) {
        JudgeChooseWinner j = new JudgeChooseWinner(actionHandler);
        j.showDrawings(drawings);
        updateView(j);

    }

}