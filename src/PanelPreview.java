/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import javax.swing.JPanel;


public class PanelPreview extends JPanel {
    
    private ArrayList<Dibujo> dibujos = new ArrayList<>();
    
    public PanelPreview() {
    }
  
    @Override
    public void paintComponent(Graphics g){
        
        super.paintComponent(g);
        int top = dibujos.size();
        Graphics2D g2d = (Graphics2D) g;
        for(int i=0; i<top; i++){
            Dibujo auxDib = dibujos.get(i);
            Shape s = auxDib.getShape();
            g2d.setStroke(new BasicStroke((int)auxDib.getStroke()));
            g2d.setColor(auxDib.getColor());
            g2d.draw(s);
        }

    }
    
    public void setear(ArrayList<Dibujo> dibujos){
        this.dibujos=dibujos;
    }
    

    
    
}


 

