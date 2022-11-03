import runtime.SchedulerData;

public class Window {
    private SchedulerData scheduler;

    Window(SchedulerData schedueler) {
        this.scheduler = schedueler;

    }

    /// Participant mode

    // also use timer and emit word
    public void waitForWord() {

    }

    public void startDrawing(String word, int time) {

    }

    public void displayWin() {}

    public void displayLoss(String winner) {}

    public void countDown(String word) {}

    /// Judge mode
    public void chooseWord() {
        // Display list of connected participants, enter word, and button for starting game
    }

    public void waitForDrawings() {
        // Participants are drawing, timer is counting down
    }

    public void selectWinner() {
        // Display all drawings and select winner
    }
}
