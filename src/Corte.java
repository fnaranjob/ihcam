/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;

// @author FnaranjoB

public class Corte {
    int indice;
    String nombre;
    Operacion_preview preview_izq = new Operacion_preview();
    Operacion_preview preview_der = new Operacion_preview();
    Operacion_preview preview_sup = new Operacion_preview();
    Operacion_corte corte = new Operacion_corte();

    public Corte() {
    }

    public void Setear_preview_izq(Operacion_preview preview){
        this.preview_izq=preview;
    }
    public void Setear_preview_der(Operacion_preview preview){
        this.preview_der=preview;
    }
    public void Setear_preview_sup(Operacion_preview preview){
        this.preview_sup=preview;
    }
    public void Setear_indice(int indice){
        this.indice=indice;
    }
    public void Setear_nombre(String nombre){
        this.nombre=nombre;
    }
    public void Setear_corte(Operacion_corte corte){
        this.corte=corte;
    }
    public Operacion_preview GetPreviewIzq(){
        return preview_izq;
    }
    public Operacion_preview GetPreviewDer(){
        return preview_der;
    }
    public Operacion_preview GetPreviewSup(){
        return preview_sup;
    }
    public Operacion_corte GetCorte(){
        return corte;
    }
    public int GetIndice(){
        return indice;
    }
    public String GetNombre(){
        return nombre;
    }
}