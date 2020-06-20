/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;

// @author FNaranjoB

import java.awt.Color;
import java.awt.Shape;

public class Dibujo{

    private Shape s;
    private Color c;
    private int stroke;
    
    public Dibujo() {
        
    }
    
    public void Setear(Shape s, Color c, int stroke){
        this.s=s;
        this.c=c;
        this.stroke=stroke;
    }
    public void SetearColor(Color c){
        this.c=c;
    }
    public Shape getShape(){
        return s;
    }
    public Color getColor(){
        return c;
    }
    public int getStroke(){
        return stroke;
    }
}
