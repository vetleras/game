import java.util.ArrayList;

import mqtt.MQTTclient;
import runtime.IStateMachineData;
import runtime.SchedulerData;

public class Game implements IStateMachineData {
    private static enum State {
        P_WaitingOnWord,
        P_Drawing,
        P_WaitingForResult,
        P_DisplayingWin,
        P_DisplayingLoss,

        J_WaitingOnParticipantsAndWord,
        J_CountingDown,
        J_WaitingOnJudge,
    }

    public final static String WORD_FROM_JUDGE = "WORD_FROM_JUDGE", DRAWING = "DRAWING",
            WINNER_CHOSEN = "WINNER_CHOSEN";

    // -------------------------- KEEPALIVE --------------------------
    public final static String KEEPALIVE_ENTITIES_INACTIVE = "KEEPALIVE_ENTITIES_INACTIVE",
            KEEPALIVE_NEW_ENTITIES = "KEEPALIVE_NEW_ENTITY";
    // -------------------------- END KEEPALIVE --------------------------

    private State state;
    private String id;

    private Window window;
    private MQTTclient client;
    private ArrayList<DrawingMessage> drawings = new ArrayList<>();
    private static int PARTICIPANT_COUNT = 3;

    public Game(String id, boolean judge) {
        this.id = id;
        SchedulerData scheduler = new SchedulerData(this);
        this.client = new MQTTclient(MQTTclient.BrokerURI, id, false, scheduler);
        this.window = new Window(scheduler);
        scheduler.start();
        if (judge) {
            window.J_chooseWord();
            this.state = State.J_WaitingOnParticipantsAndWord;
        } else {
            window.P_waitForWord();
            this.state = State.P_WaitingOnWord;
        }
    }

    @Override
    public int fire(String event, Object object, SchedulerData scheduler) {
        switch (state) {
            case P_WaitingOnWord:
                if (event.equals(WORD_FROM_JUDGE)) {
                    String word = (String) object;
                    window.P_startDrawing(word);
                    state = State.P_Drawing;
                    return EXECUTE_TRANSITION;
                }
                return DISCARD_EVENT;

            case P_Drawing:
                if (event.equals(Window.DRAWING)) {
                    String drawing = (String) object;
                    client.send(DRAWING, id + " " + drawing);
                    state = State.P_WaitingForResult;
                    return EXECUTE_TRANSITION;
                }
                return DISCARD_EVENT;

            case P_WaitingForResult:
                if (event.equals(WINNER_CHOSEN)) {
                    String winner = (String) object;
                    if (winner.equals(id)) {
                        window.P_displayWin();
                        state = State.P_DisplayingWin;
                    } else {
                        window.P_displayLoss(winner);
                        state = State.P_DisplayingLoss;
                    }
                    return EXECUTE_TRANSITION;
                }
                return DISCARD_EVENT;

            case P_DisplayingWin:
                if (event.equals(Window.CONTINUE_AS_JUDGE)) {
                    window.J_chooseWord();
                    state = State.J_WaitingOnParticipantsAndWord;
                    return EXECUTE_TRANSITION;
                }
                return DISCARD_EVENT;

            case P_DisplayingLoss:
                if (event.equals(WORD_FROM_JUDGE)) {
                    String word = (String) object;
                    window.P_startDrawing(word);
                    state = State.P_Drawing;
                    return EXECUTE_TRANSITION;
                }
                return DISCARD_EVENT;

            case J_WaitingOnParticipantsAndWord:
                if (event.equals(Window.WORD)) {
                    String word = (String) object;
                    client.send(WORD_FROM_JUDGE, word);
                    window.J_waitForDrawings();
                    state = State.J_CountingDown;
                    return EXECUTE_TRANSITION;
                }
                return DISCARD_EVENT;

            case J_CountingDown:
                if (event.equals(DRAWING)) {
                    String[] args = ((String) object).split(" ", 2);
                    drawings.add(new DrawingMessage(args[0], args[1]));
                    if (drawings.size() == PARTICIPANT_COUNT) {
                        window.J_selectWinner(drawings);
                        drawings.clear();
                        state = State.J_WaitingOnJudge;
                    }
                    return EXECUTE_TRANSITION;
                }
                return DISCARD_EVENT;

            case J_WaitingOnJudge:
                if (event.equals(Window.WINNER_CHOSEN)) {
                    String id = (String) object;
                    client.send(WINNER_CHOSEN, id);
                    window.P_waitForWord();
                    state = State.P_WaitingOnWord;
                    return EXECUTE_TRANSITION;
                }
                return DISCARD_EVENT;

            default:
                return DISCARD_EVENT;
        }
    }

    public static void main(String[] args) {
        try {
            String id = args[0];
            boolean judge = args[1].equals("-j");
            new Game(id, judge);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage:\n ... [id] -j|p");
        }
    }
}