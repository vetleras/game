import mqtt.MQTTclient;
import runtime.IStateMachineData;
import runtime.SchedulerData;

public class Game implements IStateMachineData {
    private static enum State {
        P_WaitingOnWord,
        P_Drawing,
        P_WaitingOnResult,
        P_DisplayingWin,
        P_DisplayingLoss,

        J_WaitingOnParticipantsAndWord,
        J_CountingDown,
        J_WaitingOnJudge,
        J_DisplayingResult,
    }

    public final String WORD_FROM_WINDOW = "WORD_FROM_WINDOW",
            WORD_FROM_JUDGE = "WORD_FROM_JUDGE", TIMER = "TIMER", WIN = "WIN", CLOSE_WINDOW = "CLOSE_WINDOW",
            WINNER_SELECTED = "WINNER_SELECTED", REGISTER_PARTICIPANT = "REGISTER_PARTICIPANT";

    private State state;
    private String id;

    private Window window;

    private MQTTclient client;

    public Game(String id, boolean judge) {
        this.id = id;
        SchedulerData scheduler = new SchedulerData(this);
        this.client = new MQTTclient(MQTTclient.BrokerURI, id, false, scheduler);
        if (judge) {
            window.chooseWord();
            this.state = State.J_WaitingOnParticipantsAndWord;
        } else {
            window.waitForWord();
            this.state = State.P_WaitingOnWord;
        }
    }

    @Override
    public int fire(String event, Object object, SchedulerData scheduler) {
        switch (state) {
            case P_WaitingOnWord:
                if (event == WORD_FROM_JUDGE) {
                    String word = (String) object;
                    window.startDrawing(word, 30);
                    state = State.P_Drawing;
                    return EXECUTE_TRANSITION;
                }

            case P_Drawing:
                if (event == TIMER) {
                    // Get drawing from window
                    String drawing = "";
                    client.sendMessage(MQTTclient.Topic.ParticipantToJudge, id + ":" + drawing);

                    state = State.P_WaitingOnResult;
                    return EXECUTE_TRANSITION;
                }

            case P_WaitingOnResult:
                if (event == "WIN") {
                    String winner = (String) object;
                    if (id == winner) {
                        window.displayWin();
                        state = State.P_DisplayingWin;
                    } else {
                        window.displayLoss(winner);
                        state = State.P_DisplayingLoss;
                    }
                    return EXECUTE_TRANSITION;
                }

            case P_DisplayingWin:
                if (event == CLOSE_WINDOW) {
                    window.chooseWord();
                    state = State.J_WaitingOnParticipantsAndWord;
                    return EXECUTE_TRANSITION;
                }

            case P_DisplayingLoss:
                if (event == WORD_FROM_JUDGE) {
                    String word = (String) object;
                    window.startDrawing(word, 30);
                    state = State.P_Drawing;
                    return EXECUTE_TRANSITION;
                }

            case J_WaitingOnParticipantsAndWord:
                if (event == WORD_FROM_WINDOW) {
                    String word = (String) object;
                    window.countDown(word);
                    client.sendMessage(MQTTclient.Topic.JudgeToParticipant, WORD_FROM_JUDGE + ":" + word);
                    state = State.J_CountingDown;
                    return EXECUTE_TRANSITION;
                } else if (event == MQTTclient.MESSAGEARRIVED) {
                    String mqttPayload = (String)object;
                    int seperatorPosition = mqttPayload.indexOf(":");
                    if(seperatorPosition != -1) {
                        String cmd = mqttPayload.substring(0, seperatorPosition);
                        String data = mqttPayload.substring(seperatorPosition + 1);
                        // TODO: Add participants name in the list
                        // window.addParticipant();
                    }
                }

            case J_CountingDown:
                if (event == TIMER) {
                    // TODO: Get drawings and send to selectWinner window
                    window.selectWinner();
                    state = State.J_WaitingOnJudge;
                    return EXECUTE_TRANSITION;
                } else if (event == MQTTclient.MESSAGEARRIVED) {
                    String mqttPayload = (String)object;
                    int seperatorPosition = mqttPayload.indexOf(":");
                    if(seperatorPosition != -1) {
                        String cmd = mqttPayload.substring(0, seperatorPosition);
                        String data = mqttPayload.substring(seperatorPosition + 1);
                        // TODO: Store received drawings
                        // window.addParticipant();
                    }
                }

            case J_WaitingOnJudge:
                if (event == WINNER_SELECTED) {
                    String winnerId = (String) object;
                    client.sendMessage(MQTTclient.Topic.JudgeToParticipant, WIN + ":" + winnerId);
                    state = State.J_DisplayingResult;
                    return EXECUTE_TRANSITION;
                }

            case J_DisplayingResult:
                if (event == CLOSE_WINDOW) {
                    window.waitForWord();
                    state = State.P_WaitingOnWord;
                    return EXECUTE_TRANSITION;
                }

            default:
                return DISCARD_EVENT;
        }

    }

    public static void main(String[] args) {
        try {
            String id = args[1];
            boolean judge = args[2] == "-j";
            new Game(id, judge);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage:\n ... [id] -j|p");
        }
    }
}