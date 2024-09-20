/*
Papadakis Fragkiskos
*/
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.registry.Registry;
import java.util.HashSet;

public class WinnerServer extends UnicastRemoteObject implements WinnerOperation {

    private static HashSet<String> winners = new HashSet<>();
    private static File winnerFile = new File("winners.dat");
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    protected WinnerServer() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            WinnerServer winserver;
            winserver = new WinnerServer();
            Registry roum = java.rmi.registry.LocateRegistry.createRegistry(1995);
            roum.rebind("//localhost/WinnerBingo", winserver);
            System.out.println("Server is up..");
            in = new ObjectInputStream(new FileInputStream(winnerFile));

            winners = (HashSet<String>) in.readObject();

        } catch (EOFException ex) {
            System.out.println("There are no winners!");
        } catch (IOException ex) {
            Logger.getLogger(WinnerServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WinnerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public HashSet<String> sendWinner(Player p) throws RemoteException {
        // me to pou ksekinaei o winnerServer exei diabasei tous proigoumenous nikites
        //ean einai kapoios nikitis prosthetei ton teleutaio nikiti
        //kanei eggrafi sto arxeio to ananeomeno hashset
        //kai epistrefei to ananeomeno hashset sto bingoserver
        try {

            out = new ObjectOutputStream(new FileOutputStream(winnerFile));
            String s = p.getUsername();
            winners.add(s);
            out.writeObject(winners);
            out.flush();
            out.close();
           
        } catch (IOException ex) {
            Logger.getLogger(WinnerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return winners;
    }

    @Override
    public HashSet<String> askList() throws RemoteException {

        return winners;
    }

}
