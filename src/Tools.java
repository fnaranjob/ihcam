/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;


//@author FNaranjoB

public class Tools {
    
    int t=45;
    double kerf=2;
    double altura_corte=1.5;
    double altura_pinchazo=4;
    double retardo_pinchazo=1;
    double retardo_dthc=2;
    double feed=0;

    public Tools(int t,double espesor) {
        this.t=t;
        int int_espesor=(int)Math.round(espesor);
        
        //Ancho de corte
        
        switch (t) {
            case 45:
                switch(int_espesor){
                    case 1:
                        kerf=1.1;
                        break;
                    case 2:
                        kerf=1.4;
                        break;
                    case 3:
                        kerf=1.5;
                        break;
                    case 4:
                    case 5:
                        kerf=1.6;
                        break;
                    case 6:
                    case 7:
                        kerf=1.7;
                        break;
                }
                break;
            case 65:
                switch(int_espesor){
                    case 6:
                    case 7:
                        kerf=1.8;
                        break;
                    case 8:
                    case 9:
                        kerf=1.9;
                        break;
                    case 10:
                        kerf=2.0;
                        break;
                    case 11:
                        kerf=2.1;
                        break;
                    case 12:
                    case 13:
                    case 14:
                        kerf=2.2;
                        break;
                    case 15:
                    case 16:
                        kerf=2.3;
                        break;
                }
                break;
            default:
                switch(int_espesor){
                    case 8:
                    case 9:
                        kerf=1.9;
                        break;
                    case 10:
                        kerf=2.0;
                        break;
                    case 11:
                        kerf=2.1;
                        break;
                    case 12:
                    case 13:
                        kerf=2.2;
                        break;
                    case 14:
                    case 15:
                        kerf=2.3;
                        break;
                    case 16:
                    case 17:
                        kerf=2.4;
                        break;
                    case 18:
                    case 19:
                        kerf=2.5;
                        break;
                    case 20:
                    case 21:
                    case 22:
                        kerf=2.6;
                        break;
                    case 23:
                    case 24:
                        kerf=2.7;
                        break;
                    case 25:
                    case 26:
                    case 27:
                        kerf=2.8;
                        break;
                    case 28:
                    case 29:
                        kerf=2.9;
                        break;
                    case 30:
                        kerf=3.0;
                        break;
                }
                break;
        }
       
        //Altura de corte
        
        altura_corte=1.5;
        
        //Altura de pinchazo
        
        switch (t) {
            case 45:
                altura_pinchazo=3.8;
                break;
            case 65:
                if(espesor>=6&&espesor<=8)
                    altura_pinchazo=3.8;
                else if(espesor>8&&espesor<=12)
                    altura_pinchazo=4.5;
                else
                    altura_pinchazo=6;
                break;
            default:
                if(espesor>=8&&espesor<=10)
                    altura_pinchazo=3.8;
                else if(espesor>10&&espesor<=16)
                    altura_pinchazo=4.5;
                else
                    altura_pinchazo=6;
                break;
        }
        
        //Retardo de pinchazo
        
        switch (t) {
            case 45:
                if(espesor<=1)
                    retardo_pinchazo=0.0;
                else if(espesor>1&&espesor<=1.5)
                    retardo_pinchazo=0.1;
                else if(espesor>1.5&&espesor<=2.5)
                    retardo_pinchazo=0.3;
                else if(espesor>2.5&&espesor<=4)
                    retardo_pinchazo=0.4;
                else
                    retardo_pinchazo=0.5;
                break;
            case 65:
                if(espesor<=8.5)
                    retardo_pinchazo=0.5;
                else if(espesor>8.5&&espesor<=10)
                    retardo_pinchazo=0.7;
                else if(espesor>10&&espesor<=13)
                    retardo_pinchazo=1.2;
                else if(espesor>13&&espesor<=16)
                    retardo_pinchazo=2.0;
                else
                    retardo_pinchazo=2.5;
                break;
            default:
                if(espesor<=10)
                    retardo_pinchazo=0.5;
                else if(espesor>10&&espesor<=12)
                    retardo_pinchazo=0.7;
                else if(espesor>12&&espesor<=16)
                    retardo_pinchazo=1.0;
                else if(espesor>16&&espesor<=20)
                    retardo_pinchazo=1.5;
                else if(espesor>20&&espesor<=25)
                    retardo_pinchazo=2.0;
                else
                    retardo_pinchazo=2.5;
                break;
        }
        
        //Retardo control de altura
        
        retardo_dthc = retardo_pinchazo+1.5;
        
        //Velocidad de avance
        
        switch (t) {
            case 45:
                if(espesor<=3.5)
                    feed=3000;
                else if(espesor>3.5&&espesor<=4.5)
                    feed=2200;
                else
                    feed=1300;
                break;
            case 65:
                if(espesor<=6.5)
                    feed=2500;
                else if(espesor>6.5&&espesor<=8.5)
                    feed=1600;
                else if(espesor>8.5&&espesor<=10.5)
                    feed=1000;
                else if(espesor>10.5&&espesor<=12.5)
                    feed=800;
                else if(espesor>12.5&&espesor<=16.5)
                    feed=550;
                else 
                    feed=350;
                break;
            default:
                if(espesor<=8.5)
                    feed=2500;
                else if(espesor>8.5&&espesor<=10.5)
                    feed=1600;
                else if(espesor>10.5&&espesor<=12.5)
                    feed=1200;
                else if(espesor>12.5&&espesor<=16.5)
                    feed=850;
                else if(espesor>16.5&&espesor<=20.5)
                    feed=550;
                else if(espesor>20.5&&espesor<=25.5)
                    feed=350;
                else 
                    feed=200;
                break;
        }
    }
    
    public double GetKerf(){
        return kerf;
    }
    
    public double GetAlturaCorte(){
        return altura_corte;
    }
    
    public double GetAlturaPinchazo(){
        return altura_pinchazo;
    }
    
    public double GetRetardoPinchazo(){
        return retardo_pinchazo;
    }
    
    public double GetRetardoTHC(){
        return retardo_dthc;
    }
    
    public double GetFeed(){
        return feed;
    }
    
}
