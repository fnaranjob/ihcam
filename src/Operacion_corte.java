/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;

// @author FNaranjoB

public class Operacion_corte {
    
    /*
    Tipos: 
        Izq y der:
            - 0 => Vacio
            - 1 => Agujero redondo
            - 2 => Agujero oblongo vertical
            - 3 => Agujero oblongo horizontal
            - 4 => Agujero rectangular
            - 5 => Corte en L
            - 6 => Linea recta
        Sup:
            - 0 => Vacio
            - 1 => Agujero redondo
            - 2 => Agujero oblongo vertical
            - 3 => Agujero oblongo horizontal
            - 4 => Agujero rectangular
            - 5 => Corte en L izquierdo
            - 6 => Corte en L derecho
            - 7 => Destijere
            - 8 => Empalme normal
            - 9 => Empalme hembra
            - 10 => Linea recta
            - 11 => Empalme macho
    */
    
    /*
    Parametros:
        Izq y der:
            - Tipo 0 => [ND,ND,ND,ND]
            - Tipo 1 => [Y,Z,Diametro,ND]
            - Tipo 2 => [Y,Z,Diametro,Largo]
            - Tipo 3 => [Y,Z,Diametro,Largo]
            - Tipo 4 => [Y,Z,A,L]
            - Tipo 5 => [A,L,S/I,ND] => S/I = 1 esquina superior, S/I = 0 esquina inferior
            - Tipo 6 => [Yinf,Ysup,ND,ND]
        Sup:
            - Tipo 0 => [ND,ND,ND,ND]
            - Tipo 1 => [X,Y,Diametro,ND]
            - Tipo 2 => [X,Y,Diametro,Largo]
            - Tipo 3 => [X,Y,Diametro,Largo]
            - Tipo 4 => [X,Y,A,L]
            - Tipo 5 => [XI,Y,ND,ND]
            - Tipo 6 => [XD,Y,ND,ND]
            - Tipo 7 => [YI,YD,CI,CD]
            - Tipo 8 => [YI,YD,X,ND]
            - Tipo 9 => [XI,XD,Y,C]
            - Tipo 10 => [YI,YD,ND,ND]
            - Tipo 11 => [XI,XD,C,ND]
    */
    
    int tipo_izq=0,tipo_der=0,tipo_sup=0; 
    double param_izq[] = new double[4];
    double param_der[] = new double[4];
    double param_sup[] = new double[4];

    public Operacion_corte() {
    }
    
    public void Setear(int tipo_izq,int tipo_der, int tipo_sup,double[] param_izq,double[] param_der,double[] param_sup){
        this.tipo_izq = tipo_izq;
        this.tipo_der = tipo_der;
        this.tipo_sup = tipo_sup;
        this.param_izq = param_izq;
        this.param_der = param_der;
        this.param_sup = param_sup;
    }
    
    public int GetTipoIzq(){
        return tipo_izq;
    }
    public int GetTipoDer(){
        return tipo_der;
    }
    public int GetTipoSup(){
        return tipo_sup;
    }
    
    public double[] GetParamIzq(){
        return param_izq;
    }
    
    public double[] GetParamDer(){
        return param_der;
    }
    
    public double[] GetParamSup(){
        return param_sup;
    }
}
