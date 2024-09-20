/*
Papadakis Fragkiskos
 */
import java.io.*;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BingoServer extends UnicastRemoteObject implements Operations, Runnable {

    private static ArrayList<Listener> join_list = new ArrayList<>(); //gia tin xrisi callback
    private static ArrayList<String> already_logged = new ArrayList<>(); //gia na min sundethei o idios user ksana
    private HashMap<String, String> map = new HashMap<>(); //gia elegxo tou login kai register
    private File file = new File("Profiles.dat"); //apothikeusi twn eggegramenwn
    private static HashSet<Integer> bingoSet = new HashSet<>(); //katoxyrwsi twn arithmwn pou stelnei o BingoServer
    private static WinnerOperation look_up; //epikoinwnia mesw tis diepafis winnerOperation tou Bingoserver me ton WinnerServer
    private HashSet<String> returnList = new HashSet<>(); //hastset me tous nikites

    protected BingoServer() throws RemoteException {

        super();

        if (!file.exists()) { //an den yparxei to arxeio ginetai dimiourgia
            ObjectOutputStream output = null;
            try {

                output = new ObjectOutputStream(new FileOutputStream(file));
                Player player1 = new Player("karkasis", "123");
                Player player2 = new Player("papadakis", "123");
                Player player3 = new Player("frank", "123");
                map.put(player1.getUsername(), player1.getPassword());
                map.put(player2.getUsername(), player2.getPassword());
                map.put(player3.getUsername(), player3.getPassword());
                output.writeObject(map);
                output.flush();
                output.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    output.close();
                } catch (IOException ex) {
                    Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public static void main(String[] args) {

        try {
            //sundesi me ton WinnerServer kai dimiourgia tou Bingo RMI server
            Registry reg = java.rmi.registry.LocateRegistry.getRegistry(1995);//diaforetiki porta
            look_up = (WinnerOperation) reg.lookup("//localhost/WinnerBingo");
            BingoServer server;
            server = new BingoServer();
            Registry r = java.rmi.registry.LocateRegistry.createRegistry(1099);//default port
            r.rebind("//localhost/Bingo", server);
            System.out.println("Waiting to Connect");

        } catch (RemoteException ex) {
            Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public synchronized boolean register(Player profile) throws RemoteException {
        //synchronized methodo gia na min mporesoun na syndethoun duo atoma tautoxrona me to idio onoma 
        try {
            //apothikeusi xristi se arxeio
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("Profiles.dat"));
            map = (HashMap<String, String>) input.readObject();
            if (!map.containsKey(profile.getUsername())) { //an den yparxei idi to kleidi(username) sto hashmap
                map.put(profile.getUsername(), profile.getPassword());
                ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
                output.writeObject(map);
                output.flush();
                output.close();
                System.out.println(profile.getUsername() + " just registered!");
                return true;
            } else {
                System.out.println("There is already this username");
                return false;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public synchronized boolean login(Player profile) throws RemoteException {
        //synchronized methodos gia na min ginei log in se parapanw apo 1 paikti me to idio username kai password tautoxrona
        ObjectInputStream input = null;
        try {
            //sundesi tou xristi apo to arxeio
            input = new ObjectInputStream(new FileInputStream("Profiles.dat"));
            map = (HashMap<String, String>) input.readObject();
            //elegxos an to kleidi antistoixei me to value kai yparxei sto map
            if (map.containsKey(profile.getUsername()) && map.get(profile.getUsername()).equals(profile.getPassword())) {
                if (!already_logged.contains(profile.getUsername())) { //an den yparxei sti lista to username tou xristi pou paei na sundethei
                    already_logged.add(profile.getUsername());//tin prwti fora pou tha mpei sugkekrimenos xristis tha prostethei stin lista
                    System.out.println(profile.getUsername() + " logged in");

                    return true;
                }
            } else {

                System.out.println("Lathos Kwdikos/Username");
                return false;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    @Override
    public void joinGame(Listener p) throws RemoteException {
        join_list.add(p);

        if (!join_list.isEmpty()) {
            //otan mpei xristis ksekinaei to thread gia na dwsei tyxaous(lotaria)
            BingoServer server = new BingoServer();
            Thread threadRand = new Thread(server);

            threadRand.start();
        }
    }

    public void run() {
        Random rand = new Random();
        Integer x;
        bingoSet.add(0);
        for (;;) { //atermoni dimiourgia tyxaiou arithou apo 1 mexri to 75 
            try {
                Thread.sleep(1000); //1000 milisec gia paragwgi neous tuxaiou
                do {
                    //orio timwn
                    x = rand.nextInt((75 - 1) + 1);
                } while (bingoSet.contains(x));
                bingoSet.add(x); //stelnei monadiko arithmo kathe fora
                sendNumber(x);
                if (bingoSet.size() == 75) { //ean gemisei to bingoset diladi exoun klirwthei kai oi 75 arithmoi tote
                    //emfanizoume me tin xrisi callback oti i lotaria/draw exei teleiwsei 
                    for (Listener c : join_list) {
                        c.lotaryOver();
                        System.exit(0);
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(BingoServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized boolean isBingo(int[][] deltio, Player p
    ) { //methodos gia elegxo bingo
        //synchronized methodos gia tin periptwsi pou patisoun dio xristes bingo tautoxrona

        if (bingoSet.contains(deltio[0][0]) && bingoSet.contains(deltio[0][4]) && bingoSet.contains(deltio[4][0]) && bingoSet.contains(deltio[4][4])) {
            sendtoWinner(p);
            return true;
        } //elegxos gia tis gwnies tou bingo
        if (bingoSet.contains(deltio[0][0]) && bingoSet.contains(deltio[1][1]) && bingoSet.contains(deltio[2][2]) && bingoSet.contains(deltio[3][3]) && bingoSet.contains(deltio[4][4])) {
            sendtoWinner(p);
            return true;
        }//elegxos gia tin mia diagwnio apo aristera pros katw deksia
        if (bingoSet.contains(deltio[0][4]) && bingoSet.contains(deltio[1][3]) && bingoSet.contains(deltio[2][2]) && bingoSet.contains(deltio[3][1]) && bingoSet.contains(deltio[4][0])) {
            sendtoWinner(p);
            return true;
        }//elegxos gia tin mia diagwnio apo deksia pros katw aristera
        int check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[i][0])) {
                check++;
            }

        }//prwti katheti stili
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[i][1])) {
                check++;
            }

        }//deuteri katheti stili
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[i][2])) {
                check++;
            }

        }//triti katheti stili
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[i][3])) {
                check++;
            }

        }//tetarti katheti stili
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[i][4])) {
                check++;
            }

        }//pempti katheti stili
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[0][i])) {
                check++;
            }

        }//prwti grammi
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[1][i])) {
                check++;
            }

        }//deuteri grammi
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[2][i])) {
                check++;
            }

        }//triti grammi
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[3][i])) {
                check++;
            }

        }//tetarti grammi
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }
        check = 0;
        for (int i = 0; i < 5; i++) {
            if (bingoSet.contains(deltio[4][i])) {
                check++;
            }

        }//pempti grammi
        if (check == 5) {
            sendtoWinner(p);
            return true;
        }

        return false;

    }

    private void sendNumber(int number) { //callback methodos
        //stelnoume ton idio arithmo se osous xristes pou exoun kanei log in (einai diladi stin join_list)
        for (Listener c : join_list) {
            try {
                c.sendRandom(number);
            } catch (RemoteException ex) {
                join_list.remove(c);

            }
        }
    }

    @Override
    public void removePlayer(Player p) throws RemoteException {
        already_logged.remove(p.getUsername()); //afairei apo tin lista stin periptwsi pou thelei na ksanampei
        //gia na min meinei stin lista already_logged
    }

    @Override
    public int[][] deltio() throws RemoteException { //dimiourgia tou deltiou diaforetiko gia kathe xristi

        Random r = new Random();
        Integer x;
        HashSet<Integer> monadiko = new HashSet<>();
        int[][] deltio = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                do {
                    deltio[i][j] = r.nextInt(15) + (15 * j) + 1; //sugkekrimeno orio arithmwn gia kathe stili
                } while (monadiko.contains(deltio[i][j])); //monadikos arithmos gia kathe stili
                monadiko.add(deltio[i][j]);

                deltio[2][2] = 0; //free

            }
        }
        return deltio;
    }

    public void sendtoWinner(Player p) {
        try {

            //i sigkekrimeni methodos kalite kathe fora pou kapoios exei patisei kai exei bingo
            //apo tin methodo isBingo mazi me tin prosthiki tou atomou sto newWinner
            returnList = look_up.sendWinner(p);

        } catch (RemoteException ex) {
            Logger.getLogger(BingoServer.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        //otan krithei o nikiteis stelnei se olous tou user pou einai stin join_list ton nikiti kai o guros stamataei    
        for (Listener c : join_list) {
            try {
                c.thereIsWinner(p);
            } catch (RemoteException ex) {
                join_list.remove(c);
            }

        }

    }

    @Override
    public HashSet<String> sendtoClient() throws RemoteException { //opote zitaei o xristis
        returnList = look_up.askList();
        return returnList;
    }

    @Override
    public void removeListenerPlayer(Listener p) throws RemoteException {
        join_list.remove(p); //afaireite ama kanei cancel o xristis gia na ksanatrekei to thread threadRand otan syndethei allos xristis 
    }

}
