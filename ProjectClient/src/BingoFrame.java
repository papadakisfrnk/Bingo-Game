/*
Papadakis Fragkiskos
*/
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Label;
import static java.awt.Label.CENTER;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BingoFrame extends JFrame {

    private final int size = 5;
    private RMIClient client;
    private int[][] deltio; //disdiastatos pinakas
    private JButton plegmabuttons[][] = new JButton[size][size];
    private HashSet<String> winnerList = new HashSet<>();

    public BingoFrame(RMIClient client) {
        this.client = client;

        try {
            deltio = client.getLook_up().deltio();
        } catch (RemoteException ex) {
            Logger.getLogger(BingoFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        setTitle("Bingo Project");
        Label lb = new Label("B", CENTER);
        Label li = new Label("I", CENTER);
        Label ln = new Label("N", CENTER);
        Label lg = new Label("G", CENTER);
        Label lo = new Label("0", CENTER);
        lb.setForeground(Color.red);
        li.setForeground(Color.red);
        ln.setForeground(Color.red);
        lg.setForeground(Color.red);
        lo.setForeground(Color.red);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = getContentPane();
        pane.add(lb);
        pane.add(li);
        pane.add(ln);
        pane.add(lg);
        pane.add(lo);
        pane.setLayout(new GridLayout(0, 5));// board setup
        //dimiourgia deltiou tou xristi
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JButton bt = new JButton(String.valueOf(deltio[i][j]));
                plegmabuttons[i][j] = bt;
                pane.add(bt);
                bt.addActionListener(new ActionListener() { //gia otan patisei o xristis to koumpi
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //allagi se kokkino
                        JButton b = ((JButton) e.getSource());
                        if (!b.getBackground().equals(Color.red)) {
                            b.setBackground(Color.RED);
                            b.setOpaque(true);
                        } else {
                            b.setBackground(Color.LIGHT_GRAY);
                            b.setOpaque(false);
                        }
                    }
                });
            }
            System.out.println();
        }
        //koumpia tou paikti me sygkekirmenes litourgies
        Label inv1 = new Label("");
        Label inv2 = new Label("");
        Label inv3 = new Label("");
        JButton ihaveBingo = new JButton("Bingo");
        ihaveBingo.setBackground(Color.red);
        pane.add(ihaveBingo);
        pane.add(inv1);

        JButton cancel = new JButton("Cancel");
        pane.add(cancel);
        pane.add(inv2);

        JButton winners = new JButton("Winners");
        pane.add(winners);
        pane.add(inv3);

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { try {
                //termatismos
                client.getLook_up().removePlayer(client.loggedPlayer);
                client.getLook_up().removeListenerPlayer(client);
                
                } catch (RemoteException ex) {
                    Logger.getLogger(BingoFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }

        }
        );

        ihaveBingo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBingo();
            }

        });

        winners.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                giveWinners();
            }

        });
        setSize(500, 500);
        setVisible(true);

    }

    public void checkBingo() {
        try {
            if (client.getLook_up().isBingo(deltio, client.loggedPlayer)) { //ean epistrafei true
                JOptionPane.showMessageDialog(new JFrame(), client.loggedPlayer.getUsername() + " you have Bingo  !!");
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Sorry, " + client.loggedPlayer.getUsername() + " you don't have Bingo yet..");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(BingoFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeButton(int number) {
        //ean o arithmos pou exei klirwthei einai idios me kapoio apo ta buttons (deltio tou xristi)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (plegmabuttons[i][j].getText().equals(String.valueOf(number))) {
                    plegmabuttons[2][2].setText("FREE");
                    plegmabuttons[i][j].setBackground(Color.RED);
                    plegmabuttons[i][j].setOpaque(true);
                }

            }
        }
    }

    public void giveWinners() {
        try {
            //emfanisi twn nikitwn
            JFrame winnersFrame = new JFrame("Winner's List");
            Label winnersText = new Label("", CENTER);

            winnerList = client.getLook_up().sendtoClient(); //epistrefei to hashset apo to bingoServer
            //pou to zitaei apo ton winnerServer
            winnersText.setText(winnerList.toString());
            winnersText.setEnabled(false);

            winnersFrame.add(winnersText);
            winnersFrame.setLocation(300, 100);
            winnersFrame.setVisible(true);
            winnersFrame.setSize(300, 300);
        } catch (RemoteException ex) {
            Logger.getLogger(BingoFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void displayWinner(Player win) {
        JFrame winnerFrame = new JFrame("We have Bingo!");
        Label winnerText = new Label("", CENTER);
        //emfanisi tou nikiti stous joined players
        winnerText.setText("The winner is " + win.getUsername() + "!!!");
        winnerText.setEnabled(false);

        winnerFrame.add(winnerText);
        winnerFrame.setLocation(500, 200);
        winnerFrame.setVisible(true);
        winnerFrame.setSize(300, 300);

    }

}
