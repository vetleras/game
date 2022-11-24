
/*
 * import java.util.ArrayList;
 * import java.util.Timer;
 * import java.util.TimerTask;
 * 
 * import mqtt.MQTTclient;
 * import runtime.SchedulerData;
 * 
 * public class Keepalive {
 * private ArrayList<String> activeEntities;
 * private ArrayList<String> previousEntities;
 * private MQTTclient client;
 * private String id;
 * private SchedulerData scheduler;
 * 
 * private Timer timer;
 * 
 * public Keepalive(MQTTclient client, String id, SchedulerData scheduler) {
 * this.client = client;
 * this.id = id;
 * this.activeEntities = new ArrayList<String>();
 * this.previousEntities = new ArrayList<String>();
 * this.scheduler = scheduler;
 * 
 * activeEntities.add(id);
 * 
 * client.subscribeToTopic(MQTTclient.TOPICS.get(MQTTclient.Topic.Keepalive));
 * client.sendMessage(MQTTclient.Topic.Keepalive, id);
 * }
 * 
 * public void start() {
 * timer = new Timer();
 * Task task = new Task();
 * timer.scheduleAtFixedRate(task, 0, 5000);
 * }
 * 
 * public void stop() {
 * timer.cancel();
 * }
 * 
 * public void receiveKeepalive(String entityId) {
 * addEntity(entityId);
 * }
 * 
 * 
 * Compares a list of received IDs to the ones registered in the previous
 * keepalive-window.
 * 
 * @param receivedEntities list of entities received in current windo
 * 
 * @return true if lists are equal, else false
 * 
 * private boolean
 * compareEntities(ArrayList<String>receivedEntities,ArrayList<String>previous){
 * ArrayList<String>tmpCompareList=(ArrayList<String>)(receivedEntities.clone())
 * ;tmpCompareList.removeAll(previous);
 * 
 * return tmpCompareList.size()==0;}
 * 
 * private
 * ArrayList<String>getEntityDifference(ArrayList<String>active,ArrayList<String
 * >previous){ArrayList<String>activeCopy=(ArrayList<String>)(active.clone());
 * ArrayList<String>previousCopy=(ArrayList<String>)(previous.clone());if(
 * compareEntities(activeCopy,previousCopy)){return new ArrayList<String>();}
 * 
 * if(activeCopy.size()>previousCopy.size()){activeCopy.removeAll(previousCopy);
 * return activeCopy;}
 * 
 * previousCopy.removeAll(activeCopy);return previousCopy;}
 * 
 * private void addEntity(String id){activeEntities.add(id);}
 * 
 * private void
 * setPreviousEntities(ArrayList<String>newPreviousEntities){this.activeEntities
 * =newPreviousEntities;}
 * 
 * private class Task extends TimerTask {
 * 
 * Compares with keepalives received in the current timer window with the one in
 * the
 * previous window. If not alike, inform Game by adding event to scheduler along
 * with the difference. Handling of entities becoming active or unactive is up
 * to
 * implementer.
 * 
 * @Override
 * public void run() {
 * ArrayList<String> difference = getEntityDifference(activeEntities,
 * previousEntities);
 * boolean shrunk = previousEntities.size() > activeEntities.size();
 * 
 * if (shrunk) {
 * scheduler.addToQueueLast(Game.KEEPALIVE_ENTITIES_INACTIVE, difference);
 * } else {
 * if (previousEntities.size() != activeEntities.size()) {
 * scheduler.addToQueueLast(Game.KEEPALIVE_NEW_ENTITIES, difference);
 * }
 * }
 * 
 * setPreviousEntities(activeEntities);
 * activeEntities.clear();
 * 
 * client.sendMessage(MQTTclient.Topic.Keepalive, id);
 * }
 * 
 * }
 * 
 * }
 * 
 */
