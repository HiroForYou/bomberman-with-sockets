// hilo que lanza cambios graduales en el mapa que ocurren justo después de que se coloca la bomba
class MapUpdatesThrower extends Thread {
   boolean bombPlanted;
   int id, l, c;

   MapUpdatesThrower(int id) {
      this.id = id;
      this.bombPlanted = false;
   }

   void setBombPlanted(int x, int y) {
      x += Const.WIDTH_SPRITE_PLAYER / 2;
      y += 2 * Const.HEIGHT_SPRITE_PLAYER / 3;

      this.c = x / Const.SIZE_SPRITE_MAP;
      this.l = y / Const.SIZE_SPRITE_MAP;

      this.bombPlanted = true;
   }

   // cambiar el mapa en el servidor y el cliente
   static void changeMap(String keyWord, int l, int c) {
      Server.map[l][c].img = keyWord;
      ClientManager.sendToAllClients("-1 mapUpdate " + keyWord + " " + l + " " + c);
   }

   int getColumnOfMap(int x) {
      return x / Const.SIZE_SPRITE_MAP;
   }

   int getLineOfMap(int y) {
      return y / Const.SIZE_SPRITE_MAP;
   }

   // comprueba si el fuego alcanzó a algún jugador inmóvil (coordenada del centro
   // del cuerpo)
   void checkIfExplosionKilledSomeone(int linSprite, int colSprite) {
      int linPlayer, colPlayer, x, y;

      for (int id = 0; id < Const.QTY_PLAYERS; id++)
         if (Server.player[id].alive) {
            x = Server.player[id].x + Const.WIDTH_SPRITE_PLAYER / 2;
            y = Server.player[id].y + 2 * Const.HEIGHT_SPRITE_PLAYER / 3;

            colPlayer = getColumnOfMap(x);
            linPlayer = getLineOfMap(y);

            if (linSprite == linPlayer && colSprite == colPlayer) {
               Server.player[id].alive = false;
               ClientManager.sendToAllClients(id + " newStatus dead");
            }
         }
   }

   int[] whereMoveEnemy(int x, int y) {
      int xBody = x;
      int yBody = y;
      int[] newPosition = new int[2];
      newPosition[0] = x;
      newPosition[1] = y;
      if (xBody != 8) {
         if (Server.map[yBody][xBody + 1].img.contains("floor")) {
            newPosition[0] = xBody + 1;
            newPosition[1] = y;
         }

      } else {
         if (xBody != 1) {
            if (Server.map[yBody][xBody - 1].img.contains("floor")) {
               newPosition[0] = xBody - 1;
               newPosition[1] = y;
            }

         } else {
            if (yBody != 8) {
               if (Server.map[yBody + 1][xBody].img.contains("floor")) {
                  newPosition[0] = x;
                  newPosition[1] = yBody + 1;
               }

            } else {
               if (yBody != 0) {
                  if (Server.map[yBody - 1][xBody].img.contains("floor")) {
                     newPosition[0] = x;
                     newPosition[1] = yBody - 1;
                  }

               }
            }
         }
      }

      return newPosition;
   }

   public void run() {
      while (true) {
         /*
          * for (int i = 0; i < Const.xEnemy.length; i++) { Const.xEnemy[i] =
          * whereMoveEnemy(Const.xEnemy[i], Const.yEnemy[i])[0]; Const.yEnemy[i] =
          * whereMoveEnemy(Const.xEnemy[i], Const.yEnemy[i])[1]; //changeMap("enemigo",
          * Const.xEnemy[i], Const.yEnemy[i]);
          * //Server.map[Const.xEnemy[i]][Const.yEnemy[i]].img = "enemigo";
          * //System.out.println(Const.xEnemy[i] + " " + Const.yEnemy[i]); }
          */
         if (bombPlanted) {
            bombPlanted = false;

            for (String index : Const.indexBombPlanted) {
               changeMap("bomb-planted-" + index, l, c);
               try {
                  sleep(Const.RATE_BOMB_UPDATE);
               } catch (InterruptedException e) {
               }
            }

            // efectos de explosión
            new Thrower("center-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c).start();
            checkIfExplosionKilledSomeone(l, c);

            // debajo
            if (Server.map[l + 1][c].img.equals("floor-1")) {
               new Thrower("down-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l + 1, c).start();
               checkIfExplosionKilledSomeone(l + 1, c);
            } else if (Server.map[l + 1][c].img.contains("block"))
               new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l + 1, c).start();

            // la derecha
            if (Server.map[l][c + 1].img.equals("floor-1")) {
               new Thrower("right-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c + 1).start();
               checkIfExplosionKilledSomeone(l, c + 1);
            } else if (Server.map[l][c + 1].img.contains("block"))
               new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l, c + 1).start();

            // arriba
            if (Server.map[l - 1][c].img.equals("floor-1")) {
               new Thrower("up-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l - 1, c).start();
               checkIfExplosionKilledSomeone(l - 1, c);
            } else if (Server.map[l - 1][c].img.contains("block"))
               new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l - 1, c).start();

            // la izquierda
            if (Server.map[l][c - 1].img.equals("floor-1")) {
               new Thrower("left-explosion", Const.indexExplosion, Const.RATE_FIRE_UPDATE, l, c - 1).start();
               checkIfExplosionKilledSomeone(l, c - 1);
            } else if (Server.map[l][c - 1].img.contains("block"))
               new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, l, c - 1).start();

            Server.player[id].numberOfBombs++; // libera bomba
         }
         try {
            sleep(0);
         } catch (InterruptedException e) {
         }
      }
   }
}

// thread auxiliar
class Thrower extends Thread {
   String keyWord, index[];
   int l, c;
   int delay;

   Thrower(String keyWord, String index[], int delay, int l, int c) {
      this.keyWord = keyWord;
      this.index = index;
      this.delay = delay;
      this.l = l;
      this.c = c;
   }

   public void run() {
      for (String i : index) {
         MapUpdatesThrower.changeMap(keyWord + "-" + i, l, c);
         try {
            sleep(delay);
         } catch (InterruptedException e) {
         }
      }
      // situación posterior a la explosión
      MapUpdatesThrower.changeMap("floor-1", l, c);
   }
}