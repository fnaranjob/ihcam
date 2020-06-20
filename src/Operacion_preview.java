/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;

// @author FNaranjoB

import java.util.ArrayList;


public class Operacion_preview {
    
    ArrayList<Dibujo> dibujos = new ArrayList<>();

    public Operacion_preview() {
    }
    
    public void añadirDibujo(Dibujo d){
        dibujos.add(d);
    }
    public Dibujo getDibujo(int i){
        return dibujos.get(i);
    }
    public void borrarDibujo(int i){
        dibujos.remove(i);
    }
    public void borrarTodo(){
        dibujos.clear();
    }
    public int getTamaño(){
        return dibujos.size();
    }
    public ArrayList<Dibujo> getAll(){
        return dibujos;
    }
}
