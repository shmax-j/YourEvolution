package entities.MCP;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

 class CO extends Pane {
     int type;
     int x,y;
     ImageView graphic;

     CO(int x, int y) {
         this.x = x;
         this.y = y;
     }
 }
