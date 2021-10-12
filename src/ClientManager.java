import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// para cada cliente que ingresa al servidor, se crea una instancia de un nuevo hilo para manejarlo
class ClientManager extends Thread {
   static List<PrintStream> listOutClients = new ArrayList<PrintStream>();

   static void sendToAllClients(String outputLine) {
      for (PrintStream outClient : listOutClients)
         outClient.println(outputLine);
   }

   private Socket clientSocket = null;
   private Scanner in = null;
   private PrintStream out = null;
   private int id;

   CoordinatesThrower ct;
   MapUpdatesThrower mt;

   ClientManager(Socket clientSocket, int id) {
      this.id = id;
      this.clientSocket = clientSocket;
      (ct = new CoordinatesThrower(this.id)).start();
      (mt = new MapUpdatesThrower(this.id)).start();

      try {
         System.out.print("Iniciando la conexión del jugador " + this.id + "...");
         this.in = new Scanner(clientSocket.getInputStream()); // para recibir del cliente
         this.out = new PrintStream(clientSocket.getOutputStream(), true); // para enviar al cliente
      } catch (IOException e) {
         System.out.println(" erro: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");

      listOutClients.add(out);
      Server.player[id].logged = true;
      Server.player[id].alive = true;
      sendInitialSettings(); // enviar una sola cadena

      // notificar a los clientes que ya han iniciado sesión
      for (PrintStream outClient: listOutClients)
         if (outClient != this.out)
            outClient.println(id + " playerJoined");
   }

   public void run() {
      while (in.hasNextLine()) { //  conexión establecida con el cliente this.id
         String str[] = in.nextLine().split(" ");
         
         if (str[0].equals("keyCodePressed") && Server.player[id].alive) {    
            ct.keyCodePressed(Integer.parseInt(str[1]));
         } 
         else if (str[0].equals("keyCodeReleased") && Server.player[id].alive) {
            ct.keyCodeReleased(Integer.parseInt(str[1]));
         } 
         else if (str[0].equals("pressedSpace") && Server.player[id].numberOfBombs >= 1) {
            Server.player[id].numberOfBombs--;
            mt.setBombPlanted(Integer.parseInt(str[1]), Integer.parseInt(str[2]));
         }
      }
      clientDesconnected();
   }

   void sendInitialSettings() {
      out.print(id);
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            out.print(" " + Server.map[i][j].img);

      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         out.print(" " + Server.player[i].alive);

      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         out.print(" " + Server.player[i].x + " " + Server.player[i].y);
      out.print("\n");
   }

   void clientDesconnected() {
      listOutClients.remove(out);
      Server.player[id].logged = false;
      try {
         System.out.print("Cerrando la conexión del jugador " + this.id + "...");
         in.close();
         out.close();
         clientSocket.close();
      } catch (IOException e) {
         System.out.println(" error: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");
   }
}