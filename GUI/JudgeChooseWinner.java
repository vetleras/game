import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;


public class JudgeChooseWinner extends JPanel {
	static final int MAX_NUM_PLAYERS = 10;
	static final int IMG_WIDTH = 8;
	static final int IMG_HEIGHT = 8;
	
	private JButton btnWinner[] = new JButton[MAX_NUM_PLAYERS];
	private JLabel lblImageDisplay[][][] = new JLabel[MAX_NUM_PLAYERS][IMG_WIDTH][IMG_HEIGHT];
	
	private UIActionHandler actionHandler;
	
	public JudgeChooseWinner(UIActionHandler handler) {
		this.actionHandler = handler;
		UIManager.put("Label.font", new FontUIResource(new Font("Dialog", Font.PLAIN, 20)));
		
		//adjust size and set layout
        setPreferredSize (new Dimension (410, 440));
        setLayout (null);
	}
	
	public void showDrawings(ArrayList<DrawingMessage> drawings) {
		int numPlayers = drawings.size();
		
		// Adjust layout size according to number of players
		super.setPreferredSize (new Dimension ((numPlayers+1)/2 * 200 + 10, 440));
		super.setLayout(null);
		
		for(int i = 0; i < numPlayers; i++) {
			int originX = 20 + (i/2)*200;
			int originY = 20 + (i%2)*210;
			
			// Add button for choosing winner
			btnWinner[i] = new JButton("Select");
			add(btnWinner[i]);
			btnWinner[i].setBounds(originX, originY+165, 160, 30);
			final String id = drawings.get(i).id; 
			btnWinner[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("The winner is: " + id);
                    // TODO send result back to state machine
					actionHandler.judgeSendWinnerID(id);
	            }
			});
			
			// Display image using labels
			for(int u = 0; u < IMG_WIDTH; u++)  {
	        	for(int v = 0; v < IMG_HEIGHT; v++) {
	        		lblImageDisplay[i][u][v] = new JLabel();
	        		add(lblImageDisplay[i][u][v]);
	        		lblImageDisplay[i][u][v].setBounds(originX + 20*u, originY + 20*v, 20, 20);
	        		
	        		if(drawings.get(i).drawing.charAt(u * IMG_WIDTH + v) == '0') {
	        			lblImageDisplay[i][u][v].setBackground(Color.BLACK);
	        		} else {
	        			lblImageDisplay[i][u][v].setBackground(Color.WHITE);
	        		}

	        		lblImageDisplay[i][u][v].setOpaque(true);
	        	}
			}
		}
	}

	/*
	public static void main (String[] args) {
        JFrame frame = new JFrame ("Judge Choose Winner");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        JudgeChooseWinner j = new JudgeChooseWinner();
        frame.getContentPane().add (j);
        frame.pack();
        frame.setVisible (true);
        
        Random rand = new Random();
        
        ArrayList<DrawingMessage> drawings = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
        	boolean drawing[][] = new boolean[8][8];
			for(int u = 0; u < IMG_WIDTH; u++)  {
	        	for(int v = 0; v < IMG_HEIGHT; v++) {
	        		drawing[u][v] = rand.nextBoolean();
	        	}
			}
			drawings.add(new DrawingMessage("user" + i, drawing));
        }
        
        j.showDrawings(drawings);
        
        
        frame.pack();	// Readjust window size
    }
    */
}