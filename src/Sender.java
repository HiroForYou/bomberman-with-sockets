import java.awt.event.*;

// escucha mientras la ventana (JFrame) está enfocada
public class Sender extends KeyAdapter {
   int lastKeyCodePressed;

   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_SPACE)
         Client.out.println("pressedSpace " + Game.you.x + " " + Game.you.y);
      else if (isNewKeyCode(e.getKeyCode()))
         Client.out.println("keyCodePressed " + e.getKeyCode());
   }

   public void keyReleased(KeyEvent e) {
      Client.out.println("keyCodeReleased " + e.getKeyCode());
      lastKeyCodePressed = -1; // la siguiente clave siempre será nueva
   }

   boolean isNewKeyCode(int keyCode) {
      boolean ok = (keyCode != lastKeyCodePressed) ? true : false;
      lastKeyCodePressed = keyCode;
      return ok;
   }
}