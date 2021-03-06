// accedido por servidor y cliente
interface Const {
   // solo 4, no es necesario instanciar muchas cosas
   final static int QTY_PLAYERS = 4;

   final static int LIN = 9, COL = 9; // siempre ímpar
   final static int RESIZE = 4; // tamaño del pixel

   final static int SIZE_SPRITE_MAP = 16 * RESIZE;
   final static int WIDTH_SPRITE_PLAYER = 22 * RESIZE;
   final static int HEIGHT_SPRITE_PLAYER = 33 * RESIZE;

   // diferencia de píxeles entre el mapa y el sprite del jugador
   final static int VAR_X_SPRITES = 3 * RESIZE;
   final static int VAR_Y_SPRITES = 16 * RESIZE;

   final static int RATE_BOMB_UPDATE = 90;
   final static int RATE_BLOCK_UPDATE = 100;
   final static int RATE_FIRE_UPDATE = 35;
   final static int RATE_PLAYER_STATUS_UPDATE = 90;
   final static int RATE_COORDINATES_UPDATE = 27;

   final static String indexBombPlanted[] = { "1", "2", "3", "2", "1", "2", "3", "2", "1", "2", "3", "2", "1", "2",
         "red-3", "red-2", "red-1", "red-2", "red-3", "red-2", "red-3", "red-2", "red-3", "red-2", "red-3" };
   final static String indexExplosion[] = { "1", "2", "3", "4", "5", "4", "3", "4", "5", "4", "3", "4", "5", "4", "3",
         "4", "5", "4", "3", "2", "1" };
   final static String indexBlockOnFire[] = { "1", "2", "1", "2", "1", "2", "3", "4", "5", "6" };

   int nEnemy = 5;
   int[] xEnemy = new int[nEnemy];
   int[] yEnemy = new int[nEnemy];

}

class Coordinate {
   public int x, y;
   String img;

   Coordinate(int x, int y) {
      this.x = x;
      this.y = y;
   }

   Coordinate(int x, int y, String img) {
      this.x = x;
      this.y = y;
      this.img = img;
   }
}