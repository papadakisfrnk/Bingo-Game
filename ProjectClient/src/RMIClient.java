/*
Papadakis Fragkiskos
*/
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class RMIClient extends UnicastRemoteObject implements Listener {

    protected static Operations look_up;
    private static RMIClient client;
    private JFrame bnumbers = new JFrame("RMI Server Numbers");
    private JLabel num = new JLabel();
    private JLabel lotary = new JLabel("The Bingo Server has draw: ");
    private BingoFrame bf;
    protected static Player loggedPlayer; //sundedemenos user

    protected RMIClient() throws RemoteException {
        super();
    }

    public Operations getLook_up() {
        return look_up;
    }

    @Override
    public void sendRandom(int number) throws RemoteException {
        //me tin xrisi tou callback o server stelnei tyxaio arithmo se olous tou xristes 
        bnumbers.setLayout(new GridBagLayout());
        bnumbers.setSize(300, 200);
        //bnumbers.setLocation(300, 400);
        num.setText(String.valueOf(number));

        num.repaint();
        num.revalidate();
        bnumbers.add(lotary);
        bnumbers.add(num);
        bnumbers.setVisible(true);
        bf.changeButton(number);
        bf.changeButton(0); //free number
    }

    public static void main(String[] args) {

        try {
            client = new RMIClient();
            //sundesi me ton rmi bingo server
            Registry r = java.rmi.registry.LocateRegistry.getRegistry(1099);
            look_up = (Operations) r.lookup("//localhost/Bingo");
            JFrame frame = new JFrame("Login/Sign Up");
            JPanel panel = new JPanel();
            JButton login = new JButton("Login");
            JButton register = new JButton("Register");
            JLabel explain = new JLabel("You have to Login or Register to play Bingo.");

            frame.add(panel);
            panel.add(explain);
            panel.add(login);
            panel.add(register);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setSize(300, 200);
            frame.setVisible(true);

            login.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(false);

                    client.loginPanel(); //kalei tin methodo tis othonis login

                }
            }
            );
            register.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(false);

                    registerPanel(); //kalei tin methodo tis othonis register

                }
            }
            );

        } catch (NotBoundException | RemoteException ex) {
        }
    }

    public void loginPanel() { //methodos login me frames
        //grafika tou login
        JFrame loginFrame = new JFrame("Login");
        JPanel loginPanel = new JPanel();
        JTextArea usrText = new JTextArea(1, 10);
        JTextArea passwdText = new JTextArea(1, 10);
        JButton log = new JButton("Log in");
        JLabel usr = new JLabel("Username:");
        JLabel passwd = new JLabel("Password:");

        loginPanel.add(usr);
        loginPanel.add(usrText);
        loginPanel.add(passwd);
        loginPanel.add(passwdText);
        loginPanel.add(log);
        loginFrame.add(loginPanel);

        log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginFrame.setVisible(false);
                String getusr = usrText.getText(); //pairnei to text apo ton xristi (username)
                String getpasswd = passwdText.getText(); //pairnei to password apo to xristi(password)
                loggedPlayer = new Player(getusr, getpasswd);
                try {

                    if (look_up.login(loggedPlayer) == true) { //an o bingo server epistrepsei true emfanise to bingo frame
                        RMIClient.this.viewFrame();

                        look_up.joinGame(client);
                    } else {
                        //an epistrafei false apo to BingoServer den exei ginei sundesi
                        Component parentComponent = null;
                        JOptionPane.showMessageDialog(parentComponent, "Wrong Username/Password", "Not logged in", JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    }

                } catch (RemoteException ex) {
                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setSize(400, 200);
        loginFrame.setVisible(true);

    }

    public static void registerPanel() { //methodos register me frames
        //grafika tou register
        JFrame regiFrame = new JFrame("Sign Up");
        JPanel regiPanel = new JPanel();
        JTextArea usrText = new JTextArea(1, 10);
        JTextArea passwdText = new JTextArea(1, 10);
        JButton reg = new JButton("Register");
        JLabel usr = new JLabel("Username:");
        JLabel passwd = new JLabel("Password:");

        regiPanel.add(usr);
        regiPanel.add(usrText);
        regiPanel.add(passwd);
        regiPanel.add(passwdText);
        regiPanel.add(reg);
        regiFrame.add(regiPanel);

        reg.addActionListener(new ActionListener() {
            //pairnei to text apo to xristi kai ta stelnei ston BingoServer
            @Override
            public void actionPerformed(ActionEvent e) {
                regiFrame.setVisible(false);
                String getusr = usrText.getText();
                String getpasswd = passwdText.getText();
                loggedPlayer = new Player(getusr, getpasswd);

                try {

                    if (look_up.register(loggedPlayer) == true) {
                        RMIClient client = new RMIClient();
                        look_up.joinGame(client);
                        client.viewFrame();
                        System.out.println("You have Registered " + loggedPlayer.getUsername());
                    } else {
                        //an epistrafei false tote yparxei idi to username
                        Component parent = null;
                        JOptionPane.showMessageDialog(parent, "This username is already registered", "Change your username", JOptionPane.WARNING_MESSAGE);
                        System.exit(0);
                    }

                } catch (RemoteException ex) {
                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        regiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        regiFrame.setLocationRelativeTo(null);
        regiFrame.setSize(400, 200);
        regiFrame.setVisible(true);
    }

    public void viewFrame() {
        bf = new BingoFrame(this);
    }

    @Override
    public void thereIsWinner(Player win) throws RemoteException { //me xrisi callback  klirwnei to nikiti stous ypoloipous 

        bf.displayWinner(win);
    }

    @Override
    public void lotaryOver() throws RemoteException {
        Component parent = null;
       JOptionPane.showMessageDialog(parent, "The Draw has Over!");
    }

}
