/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

// @author Fnaranjob

public class Gcode {

    ArrayList<String> Codigo = new ArrayList();
    private static final int LARGO_ANTORCHA=279;
    private static final double FACTOR_VEL=0.4;
    private static final double FACTOR_VEL_RADIO=2;
    private static final int DIST_SEG=20;
    private static final double Y_COMP_ALMA=1;
    
    private double[] get_x_izq_radio(double radio){
        double[] x=new double[4];
        double rango;
        
        x[3]=0.417*radio+14.176;
        if(radio<=20){
            x[0]=126;
        }else{
            x[0]=.5333*radio+114.47;
        }
        
        rango=x[0]-x[3];
        x[2]=x[3]+0.32*rango;
        x[1]=x[3]+0.67*rango;
       
        return x;
    }
    private double[] get_x_der_radio(double[] x_izq,double ancho, double e_ala){
        double[] x = new double[4];
        for(int i=0;i<4;i++)
            x[i]=ancho-2*e_ala-x_izq[i];
        return x;
    }
    private double[] get_z_radio(double[] x_izq,double radio){
        double[] z=new double[4];
        
        if(radio<=8){
            for(int i=0;i<4;i++)
                z[i]=3.16+0.0833*x_izq[i]-0.0022*x_izq[i]*x_izq[i];
        }else if(radio>8&&radio<=13){
            for(int i=0;i<4;i++)
                z[i]=3.03+0.0944*x_izq[i]-0.0023*x_izq[i]*x_izq[i];
        }else if(radio>13&&radio<=18){
            for(int i=0;i<4;i++)
                z[i]=3.69+0.0604*x_izq[i]-0.0019*x_izq[i]*x_izq[i];
        }else if(radio>18&&radio<=24){
            for(int i=0;i<4;i++)
                z[i]=2.96+0.0861*x_izq[i]-0.0019*x_izq[i]*x_izq[i];
        }else{
            for(int i=0;i<4;i++)
                z[i]=2.62+0.1013*x_izq[i]-0.0019*x_izq[i]*x_izq[i];
        }
       
        return z;
    }
    private double[] get_a_radio(double radio){
        double[] a=new double[4];
        a[0]=25;
        a[3]=0;
        
        if(radio<=8){
            a[1]=16.5;
            a[2]=7.7;
        }else if(radio>8&&radio<=13){
            a[1]=16.2;
            a[2]=8.2;
        }else if(radio>13&&radio<=18){
            a[1]=17.3;
            a[2]=7.7;
        }else if(radio>18&&radio<=24){
            a[1]=17.6;
            a[2]=8.8;
        }else{
            a[1]=17.0;
            a[2]=8.2;
        }
       
        return a;
    }
    
    private void corte_radio_izq_up(double radio,double feed,double e_ala,double altura_alma){
        double off_x=LARGO_ANTORCHA+e_ala;
        double off_z=LARGO_ANTORCHA-altura_alma;
        double[] x_izq = get_x_izq_radio(radio);
        //double[] x_der = get_x_der_radio(x_izq,ancho,radio);
        double[] z = get_z_radio(x_izq,radio);
        double[] a = get_a_radio(radio);
        for(int i=3;i>=0;i--)
            Codigo.add("G1 X"+String.format("%.3f",x_izq[i]+off_x)+" Z"+String.format("%.3f",z[i]+off_z)+" A"+String.format("%.3f",a[i])+" F"+String.format("%.1f",feed*FACTOR_VEL_RADIO)+"\r\n");      
        
    }
    private void corte_radio_izq_down(double radio,double feed,double e_ala,double altura_alma){
        double off_x=LARGO_ANTORCHA+e_ala;
        double off_z=LARGO_ANTORCHA-altura_alma;
        double[] x_izq = get_x_izq_radio(radio);
        //double[] x_der = get_x_der_radio(x_izq,ancho,radio);
        double[] z = get_z_radio(x_izq,radio);
        double[] a = get_a_radio(radio);
        for(int i=0;i<4;i++)
            Codigo.add("G1 X"+String.format("%.3f",x_izq[i]+off_x)+" Z"+String.format("%.3f",z[i]+off_z)+" A"+String.format("%.3f",a[i])+" F"+String.format("%.1f",feed*FACTOR_VEL_RADIO)+"\r\n");      
    }
    private void corte_radio_der_up(double ancho, double radio,double feed,double e_ala, double altura_alma){
        double off_x=LARGO_ANTORCHA+e_ala;
        double off_z=LARGO_ANTORCHA-altura_alma;
        double[] x_izq = get_x_izq_radio(radio);
        double[] x_der = get_x_der_radio(x_izq,ancho,e_ala);
        double[] z = get_z_radio(x_izq,radio);
        double[] a = get_a_radio(radio);
        for(int i=3;i>=0;i--)
            Codigo.add("G1 X"+String.format("%.3f",x_der[i]+off_x)+" Z"+String.format("%.3f",z[i]+off_z)+" A"+String.format("%.3f",-a[i])+" F"+String.format("%.1f",feed*FACTOR_VEL_RADIO)+"\r\n");      
    }
//    private void corte_radio_der_down(double ancho, double radio,double feed, double e_ala, double altura_alma){
//        double off_x=LARGO_ANTORCHA+e_ala;
//        double off_z=LARGO_ANTORCHA-altura_alma;
//        double[] x_izq = get_x_izq_radio(radio);
//        double[] x_der = get_x_der_radio(x_izq,ancho,e_ala);
//        double[] z = get_z_radio(x_izq,radio);
//        double[] a = get_a_radio(radio);
//        for(int i=0;i<4;i++)
//            Codigo.add("G1 X"+String.format("%.3f",x_der[i]+off_x)+" Z"+String.format("%.3f",z[i]+off_z)+" A"+String.format("%.3f",-a[i])+" F"+String.format("%.1f",feed*FACTOR_VEL)+"\r\n");      
//    }
    
    private double[] get_ini_radio_izq(double radio){ //0-x,1-z,2-a
        double x,z,a;
        double[] output=new double[3];
        a=25;
        if(radio<=20){
            x=126;
        }else{
            x=.5333*radio+114.47;
        }
        if(radio<=8){
                z=3.16+0.0833*x-0.0022*x*x;
        }else if(radio>8&&radio<=13){
                z=3.03+0.0944*x-0.0023*x*x;
        }else if(radio>13&&radio<=18){
                z=3.69+0.0604*x-0.0019*x*x;
        }else if(radio>18&&radio<=24){
                z=2.96+0.0861*x-0.0019*x*x;
        }else{
                z=2.62+0.1013*x-0.0019*x*x;
        }
        output[0]=x;
        output[1]=z;
        output[2]=a;
        return output;
    }
    
    public Gcode() {
        Codigo.clear();
    }
    
    public void borrar(){
        Codigo.clear();
    } 
    public int getSize(){
        int size = Codigo.size();
        return size;
    }
    public String getLinea(int indice){
        String Linea;
        Linea = Codigo.get(indice);
        return Linea;
    }
    public void addLinea(String texto){
        Codigo.add("\r\n"+texto+"\r\n");
        Codigo.add("\r\n");
    }
    public void actualizarDecimal(){
        for(int i=0;i<Codigo.size();i++){
            String aux = Codigo.get(i);
            aux = aux.replace(',','.');
            Codigo.set(i, aux);
        }
    }
    
    public void generar_encabezado(int tipo,int consumible,int volt_ala,double retardo_pinchazo){
        
        //int retardo_thc_aux = (int)Math.floor(retardo_thc*10)+500;
        String auxiliar;
        Codigo.clear();
        
        Codigo.add("(Codigo para plasma SupraCNC PP4-130601-V1)\r\n");
        Codigo.add("(Creado por IHCam V1.0b)\r\n");
        switch(tipo){
            case 1:
                auxiliar="IPE/HEB)\r\n";
                break;
            case 2:
                auxiliar="IPN)\r\n";
                break;
            case 3:
                auxiliar="UPN)\r\n";
                break;
            case 4:
                auxiliar="CAJON)\r\n";
                break;
            case 5:
                auxiliar="ANGULO)\r\n";
                break;
            default:
                auxiliar="Perfil desconocido\r\n";
                break;
        }
        Codigo.add("(Perfil tipo: "+auxiliar);
        Codigo.add("(Generador plasma: POWERMAX 85)\r\n");
        Codigo.add("(Consumible: "+consumible+"A)\r\n");
        Codigo.add("G21 (Unidades: mm)\r\n");
        Codigo.add("F10\r\n");
        Codigo.add("G90 G40\r\n");
        Codigo.add("G64 P0.500 Q0.050 (Tolerancia de interpolacion=0.5)\r\n");
        Codigo.add("M68 E0 Q10\r\n");
        Codigo.add("M68 E0 Q900 (Control de altura global ON)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("M68 E0 Q3"+volt_ala+" (Voltaje Ala = "+volt_ala+"V)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("M68 E0 Q5"+Math.round(retardo_pinchazo*20)+" (Retardo DTHC = "+retardo_pinchazo*2+" seg)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("M68 E0 Q711 (DTHCIV nivel 1)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("\r\n");

    }
    public void generar_salto_izq_der(double ancho_viga){
        Codigo.add("(Transicion al ala derecha)\r\n");
        Codigo.add("G0 X-20 Z0\r\n");
        //Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("Z120\r\n");
        Codigo.add("X"+(2*LARGO_ANTORCHA+10+ancho_viga)+"\r\n");
        Codigo.add("A90\r\n");
        Codigo.add("Z0\r\n");
        Codigo.add("\r\n");
    }
    public void generar_salto_izq_sup(double ancho_viga, double altura_alma, double e_ala, int volt_alma,double retardo_pinchazo){
        
        double z_inicial;
        double z_final;
        double x_inicial;
        double x_final;
        double a_intermedio;
        
        a_intermedio=Math.acos((LARGO_ANTORCHA+10-altura_alma)/(LARGO_ANTORCHA+10));
        x_inicial=-(LARGO_ANTORCHA+10)*Math.sin(a_intermedio)+ancho_viga-e_ala+LARGO_ANTORCHA;
        z_final=LARGO_ANTORCHA+10-altura_alma;
        a_intermedio=Math.toDegrees(a_intermedio);
        
        if(z_final>=120)            //evita topar el ala al pasar por encima
            z_inicial=z_final;
        else
            z_inicial=120;
        
        if(x_inicial>=(40+e_ala+LARGO_ANTORCHA))    //evita topar la antorcha con el ala izquierda al finalizar el movimiento
            x_final=x_inicial;
        else
            x_final=40+e_ala+LARGO_ANTORCHA;
        
        Codigo.add("(Transicion al alma)\r\n");
        Codigo.add("M0 (Retirar escoria)\r\n");
        Codigo.add("M68 E0 Q3"+volt_alma+" (Voltaje Alma = "+volt_alma+"V)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("M68 E0 Q5"+Math.round(retardo_pinchazo*20)+" (Retardo DTHC = "+retardo_pinchazo*2+" seg)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("G0 X-20 Z0\r\n");
        //Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("Z"+String.format("%.3f",z_inicial)+"\r\n");
        Codigo.add("X"+String.format("%.3f",x_inicial)+"\r\n");
        Codigo.add("A"+String.format("%.2f",(-a_intermedio+5))+"\r\n");
        Codigo.add("X"+String.format("%.3f",x_final)+"Z"+String.format("%.3f",z_final)+"A0\r\n");
        Codigo.add("\r\n");
        
    }
    public void generar_salto_der_sup(double ancho_viga, double altura_alma, double e_ala, int volt_alma,double retardo_pinchazo){
        
        double z_inicial;
        double z_final;
        double x_inicial;
        double x_final;
        double a_intermedio;
        
        a_intermedio=Math.acos((LARGO_ANTORCHA+10-altura_alma)/(LARGO_ANTORCHA+10));
        System.out.println("a= "+a_intermedio);
        x_inicial=(LARGO_ANTORCHA+10)*Math.sin(a_intermedio)-ancho_viga+e_ala+LARGO_ANTORCHA+ancho_viga;
        z_final=LARGO_ANTORCHA+10-altura_alma;
        a_intermedio=Math.toDegrees(a_intermedio);
        
        if(z_final>=120)            //evita topar el ala al pasar por encima
            z_inicial=z_final;
        else
            z_inicial=120;
        
        if(x_inicial<=(ancho_viga-40-e_ala+LARGO_ANTORCHA))    //evita topar la antorcha con el ala izquierda al finalizar el movimiento
            x_final=x_inicial;
        else
            x_final=ancho_viga-40-e_ala+LARGO_ANTORCHA;
        
        Codigo.add("(Transicion al alma)\r\n");
        Codigo.add("M0 (Retirar escoria)\r\n");
        Codigo.add("M68 E0 Q3"+volt_alma+" (Voltaje Alma = "+volt_alma+"V)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("M68 E0 Q5"+Math.round(retardo_pinchazo*20)+" (Retardo DTHC = "+retardo_pinchazo*2+" seg)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("G0 X"+(2*LARGO_ANTORCHA+20+ancho_viga)+"Z0\r\n");
        //Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("Z"+String.format("%.3f",z_inicial)+"\r\n");
        Codigo.add("X"+String.format("%.3f",x_inicial)+"\r\n");
        Codigo.add("A"+String.format("%.2f",(a_intermedio-5))+"\r\n");
        Codigo.add("X"+String.format("%.3f",x_final)+"Z"+String.format("%.3f",z_final)+"A0\r\n");
        Codigo.add("\r\n");
       
    }
    public void generar_fin_izq(){
        Codigo.add("(Finalizacion del programa)\r\n");
        Codigo.add("G0 X-20 Y0 Z0\r\n");
        Codigo.add("M68 E0 Q900\r\n");
        Codigo.add("G4 P.2\r\n");
        Codigo.add("M68 E0 Q10 (DTHC off)\r\n");
        Codigo.add("(msg,Programa Terminado!)\r\n");
        Codigo.add("M5 M30");
    }
    public void generar_fin_der(double ancho_viga){
        Codigo.add("(Finalizacion del programa)\r\n");
        Codigo.add("G0 X"+String.format("%.3f",(2*LARGO_ANTORCHA+20+ancho_viga))+"\r\n");
        Codigo.add("Z120\r\n");
        Codigo.add("X-20 Y0\r\n");
        Codigo.add("A-90\r\n");
        Codigo.add("Z0\r\n");
        Codigo.add("M68 E0 Q900\r\n");
        Codigo.add("G4 P.2\r\n");
        Codigo.add("M68 E0 Q10 (DTHC off)\r\n");
        Codigo.add("(msg,Programa Terminado!)\r\n");
        Codigo.add("M5 M30");
    }
    public void generar_fin_sup(double altura_alma,double e_ala){
        
        double x=LARGO_ANTORCHA+40+e_ala;
        double z=LARGO_ANTORCHA+10-altura_alma;
        
        Codigo.add("(Finalizacion del programa)\r\n");
        Codigo.add("G0 Z"+String.format("%.3f",z)+"\r\n");
        //Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("X"+String.format("%.3f",x)+"\r\n");
        Codigo.add("X-20 Y0 A-90\r\n");
        Codigo.add("Z0\r\n");
        Codigo.add("M68 E0 Q900\r\n");
        Codigo.add("G4 P.2\r\n");
        Codigo.add("M68 E0 Q10 (DTHC off)\r\n");
        Codigo.add("(msg,Programa Terminado!)\r\n");
        Codigo.add("M5 M30");
    }
    
    public void redondo_ala(double y_centro, double z_centro, double diametro, boolean izquierda,Tools tool,int indice,double ancho_viga){
        
        double diametro_aux=diametro-tool.GetKerf();
        
        Codigo.add("(OP:"+indice+" - Agujero redondo ala)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        if(izquierda){
            Codigo.add("G90 G0 X-10\r\n");
        }else{
            Codigo.add("G90 G0 X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        }
        Codigo.add("G19\r\n"); //Plano YZ
        Codigo.add("Y"+String.format("%.3f",y_centro)+" Z"+String.format("%.3f",z_centro)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaPinchazo())+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P"+String.format("%.1f",tool.GetRetardoPinchazo())+"\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        //Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        
        
        if(izquierda){
            Codigo.add("G2 Y"+String.format("%.3f",y_centro)+" Z"+String.format("%.3f",(z_centro+diametro_aux/2))+"J0 K"+String.format("%.3f",(diametro_aux/4))+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("G2 Y"+String.format("%.3f",y_centro)+" Z"+String.format("%.3f",(z_centro+diametro_aux/2))+"J0 K"+String.format("%.3f",(-diametro_aux/2))+"\r\n");
        }else{
            Codigo.add("G3 Y"+String.format("%.3f",y_centro)+" Z"+String.format("%.3f",(z_centro+diametro_aux/2))+"J0 K"+String.format("%.3f",(diametro_aux/4))+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("G3 Y"+String.format("%.3f",y_centro)+" Z"+String.format("%.3f",(z_centro+diametro_aux/2))+"J0 K"+String.format("%.3f",(-diametro_aux/2))+"\r\n");
        }
        
        Codigo.add("G0 Y"+String.format("%.3f",y_centro+0.01)+"\r\n");
        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        //Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        //Codigo.add("G4 P.1\r\n");
        if(izquierda)
            Codigo.add("X-10\r\n");
        else
            Codigo.add("X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        Codigo.add("G17\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void redondo_alma(double x_centro, double y_centro, double diametro,Tools tool,int indice){
        
        double diametro_aux=diametro-tool.GetKerf();
        
        Codigo.add("(OP:"+indice+" - Agujero redondo alma)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("G17\r\n"); //Plano XY
        Codigo.add("G0 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA)+" y"+String.format("%.3f",y_centro+Y_COMP_ALMA)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaPinchazo())+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P"+String.format("%.1f",tool.GetRetardoPinchazo())+"\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        //Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        Codigo.add("G3 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_centro+diametro_aux/2+Y_COMP_ALMA)+" I0 J"+String.format("%.3f",(diametro_aux/4))+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
        Codigo.add("G3 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_centro+diametro_aux/2+Y_COMP_ALMA)+" I0 J"+String.format("%.3f",(-diametro_aux/2))+"\r\n");
        Codigo.add("G0 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA+0.01)+"\r\n");
        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        //Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        //Codigo.add("G4 P.1\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void oblongo_ala_v(double y_centro, double z_centro, double diametro,double largo, boolean izquierda,Tools tool,int indice,double ancho_viga){
        
        double diametro_aux=diametro-tool.GetKerf();
        double largo_aux=largo-tool.GetKerf();
        double b=(largo_aux-diametro_aux)/2;
        double y_der=y_centro-diametro_aux/2;
        double y_izq=y_centro+diametro_aux/2;
        double z_up=z_centro+b;
        double z_down=z_centro-b;
        
        Codigo.add("(OP:"+indice+" - Agujero oblongo ala)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        if(izquierda){
            Codigo.add("G90 G0 X-10\r\n");
        }else{
            Codigo.add("G90 G0 X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        }
        Codigo.add("G19\r\n"); //Plano YZ
        Codigo.add("Y"+String.format("%.3f",y_centro)+" Z"+String.format("%.3f",z_centro)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaPinchazo())+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P"+String.format("%.1f",tool.GetRetardoPinchazo())+"\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        //Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        
        if(izquierda){
            Codigo.add("G1 Y"+String.format("%.3f",y_der)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("Z"+String.format("%.3f",z_up)+"\r\n");
            Codigo.add("G2 Y"+String.format("%.3f",y_izq)+" Z"+String.format("%.3f",z_up)+" J"+String.format("%.3f",diametro_aux/2)+" K0\r\n");
            Codigo.add("G1 Z"+String.format("%.3f",z_down)+"\r\n");
            Codigo.add("G2 Y"+String.format("%.3f",y_der)+" Z"+String.format("%.3f",z_down)+" J"+String.format("%.3f",-diametro_aux/2)+" K0\r\n");
            Codigo.add("G1 Z"+String.format("%.3f",z_centro+1)+"\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",z_centro+1.01)+"\r\n");  
        }else{
            Codigo.add("G1 Y"+String.format("%.3f",y_der)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("Z"+String.format("%.3f",z_down)+"\r\n");
            Codigo.add("G3 Y"+String.format("%.3f",y_izq)+" Z"+String.format("%.3f",z_down)+" J"+String.format("%.3f",diametro_aux/2)+" K0\r\n");
            Codigo.add("G1 Z"+String.format("%.3f",z_up)+"\r\n");
            Codigo.add("G3 Y"+String.format("%.3f",y_der)+" Z"+String.format("%.3f",z_up)+" J"+String.format("%.3f",-diametro_aux/2)+" K0\r\n");
            Codigo.add("G1 Z"+String.format("%.3f",z_centro-1)+"\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",z_centro-1.01)+"\r\n");
        }
        
        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        //Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        //Codigo.add("G4 P.1\r\n");
        if(izquierda)
            Codigo.add("X-10\r\n");
        else
            Codigo.add("X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        Codigo.add("G17\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void oblongo_alma_v(double x_centro, double y_centro, double diametro,double largo,Tools tool,int indice){
        double diametro_aux=diametro-tool.GetKerf();
        double largo_aux=largo-tool.GetKerf();
        double b=(largo_aux-diametro_aux)/2;
        double x_der=x_centro+diametro_aux/2;
        double x_izq=x_centro-diametro_aux/2;
        double y_up=y_centro+b;
        double y_down=y_centro-b;
        
        Codigo.add("(OP:"+indice+" - Agujero oblongo alma)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("G17\r\n"); //Plano XY
        Codigo.add("G0 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_centro+Y_COMP_ALMA)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaPinchazo())+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P"+String.format("%.1f",tool.GetRetardoPinchazo())+"\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        //Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        Codigo.add("G1 X"+String.format("%.3f",x_der+LARGO_ANTORCHA)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
        Codigo.add("Y"+String.format("%.3f",y_up+Y_COMP_ALMA)+"\r\n");
        Codigo.add("G2 X"+String.format("%.3f",x_izq+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_up+Y_COMP_ALMA)+" I"+String.format("%.3f",-diametro_aux/2)+" J0\r\n");
        Codigo.add("G1 Y"+String.format("%.3f",y_down+Y_COMP_ALMA)+"\r\n");
        Codigo.add("G2 X"+String.format("%.3f",x_der+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_down+Y_COMP_ALMA)+" I"+String.format("%.3f",diametro_aux/2)+" J0\r\n");
        Codigo.add("G1 Y"+String.format("%.3f",y_centro+1+Y_COMP_ALMA)+"\r\n");
        Codigo.add("G0 Y"+String.format("%.3f",y_centro+1.01+Y_COMP_ALMA)+"\r\n");    
        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        //Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        //Codigo.add("G4 P.1\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void oblongo_ala_h(double y_centro, double z_centro, double diametro,double largo, boolean izquierda,Tools tool,int indice,double ancho_viga){
        
        double diametro_aux=diametro-tool.GetKerf();
        double largo_aux=largo-tool.GetKerf();
        double b=(largo_aux-diametro_aux)/2;
        double y_der=y_centro+b;
        double y_izq=y_centro-b;
        double z_up=z_centro+diametro_aux/2;
        double z_down=z_centro-diametro_aux/2;
        
        Codigo.add("(OP:"+indice+" - Agujero oblongo ala)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        if(izquierda){
            Codigo.add("G90 G0 X-10\r\n");
        }else{
            Codigo.add("G90 G0 X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        }
        Codigo.add("G19\r\n"); //Plano YZ
        Codigo.add("Y"+String.format("%.3f",y_centro)+" Z"+String.format("%.3f",z_centro)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaPinchazo())+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P"+String.format("%.1f",tool.GetRetardoPinchazo())+"\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        //Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        
        if(izquierda){
            Codigo.add("G1 Z"+String.format("%.3f",z_up)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_der)+"\r\n");
            Codigo.add("G2 Y"+String.format("%.3f",y_der)+" Z"+String.format("%.3f",z_down)+" J0 K"+String.format("%.3f",-diametro_aux/2)+"\r\n");
            Codigo.add("G1 Y"+String.format("%.3f",y_izq)+"\r\n");
            Codigo.add("G2 Y"+String.format("%.3f",y_izq)+" Z"+String.format("%.3f",z_up)+" J0 K"+String.format("%.3f",diametro_aux/2)+"\r\n");
            Codigo.add("G1 Y"+String.format("%.3f",y_centro+1)+"\r\n");
            Codigo.add("G0 Y"+String.format("%.3f",y_centro+1.01)+"\r\n");
        }else{
            Codigo.add("G1 Z"+String.format("%.3f",z_up)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_izq)+"\r\n");
            Codigo.add("G3 Y"+String.format("%.3f",y_izq)+" Z"+String.format("%.3f",z_down)+" J0 K"+String.format("%.3f",-diametro_aux/2)+"\r\n");
            Codigo.add("G1 Y"+String.format("%.3f",y_der)+"\r\n");
            Codigo.add("G3 Y"+String.format("%.3f",y_der)+" Z"+String.format("%.3f",z_up)+" J0 K"+String.format("%.3f",diametro_aux/2)+"\r\n");
            Codigo.add("G1 Y"+String.format("%.3f",y_centro-1)+"\r\n");
            Codigo.add("G0 Y"+String.format("%.3f",y_centro-1.01)+"\r\n");      
        }
        
        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        //Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        //Codigo.add("G4 P.1\r\n");
        if(izquierda)
            Codigo.add("X-10\r\n");
        else
            Codigo.add("X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        Codigo.add("G17\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void oblongo_alma_h(double x_centro, double y_centro, double diametro,double largo,Tools tool,int indice){
        double diametro_aux=diametro-tool.GetKerf();
        double largo_aux=largo-tool.GetKerf();
        double b=(largo_aux-diametro_aux)/2;
        double x_der=x_centro+b;
        double x_izq=x_centro-b;
        double y_up=y_centro+diametro_aux/2;
        double y_down=y_centro-diametro_aux/2;
        
        Codigo.add("(OP:"+indice+" - Agujero oblongo alma)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("G17\r\n"); //Plano XY
        Codigo.add("G0 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_centro+Y_COMP_ALMA)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaPinchazo())+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P"+String.format("%.1f",tool.GetRetardoPinchazo())+"\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        //Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        Codigo.add("G1 Y"+String.format("%.3f",y_up+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
        Codigo.add("X"+String.format("%.3f",x_izq+LARGO_ANTORCHA)+"\r\n");
        Codigo.add("G2 X"+String.format("%.3f",x_izq+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_down+Y_COMP_ALMA)+" I0 J"+String.format("%.3f",-diametro_aux/2)+"\r\n");
        Codigo.add("G1 X"+String.format("%.3f",x_der+LARGO_ANTORCHA)+"\r\n");
        Codigo.add("G2 X"+String.format("%.3f",x_der+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_up+Y_COMP_ALMA)+" I0 J"+String.format("%.3f",diametro_aux/2)+"\r\n");
        Codigo.add("G1 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA-1)+"\r\n");
        Codigo.add("G0 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA-1.01)+"\r\n");    
        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        //Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        //Codigo.add("G4 P.1\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
    }
    public void rectangular_ala(double y, double z, double alto,double largo, boolean izquierda,Tools tool,int indice,double ancho_viga){
        
        double alto_aux=alto-tool.GetKerf();
        double largo_aux=largo-tool.GetKerf();
        double y_centro=y-largo/2;
        double z_centro=z-alto/2;
        double y_izq=y_centro-largo_aux/2;
        double y_der=y_centro+largo_aux/2;
        double z_down=z_centro-alto_aux/2;
        double z_up=z_centro+alto_aux/2;
        
        Codigo.add("(OP:"+indice+" - Agujero rectangular ala)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        if(izquierda){
            Codigo.add("G90 G0 X-10\r\n");
        }else{
            Codigo.add("G90 G0 X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        }
        Codigo.add("G19\r\n"); //Plano YZ
        Codigo.add("Y"+String.format("%.3f",y_centro)+" Z"+String.format("%.3f",z_centro)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaPinchazo())+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P"+String.format("%.1f",tool.GetRetardoPinchazo())+"\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        //Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        
        if(izquierda){
            Codigo.add("G1 Z"+String.format("%.3f",z_up)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_der)+"\r\n");
            Codigo.add("Z"+String.format("%.3f",z_down)+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_izq)+"\r\n");
            Codigo.add("Z"+String.format("%.3f",z_up)+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_centro+1)+"\r\n");
            Codigo.add("G0 Y"+String.format("%.3f",y_centro+1.01)+"\r\n");
        }else{
            Codigo.add("G1 Z"+String.format("%.3f",z_up)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_izq)+"\r\n");
            Codigo.add("Z"+String.format("%.3f",z_down)+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_der)+"\r\n");
            Codigo.add("Z"+String.format("%.3f",z_up)+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_centro-1)+"\r\n");
            Codigo.add("G0 Y"+String.format("%.3f",y_centro-1.01)+"\r\n");      
        }
        
        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        //Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        //Codigo.add("G4 P.1\r\n");
        if(izquierda)
            Codigo.add("X-10\r\n");
        else
            Codigo.add("X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        Codigo.add("G17\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void rectangular_alma(double x, double y, double alto,double largo, Tools tool,int indice){
        
        double alto_aux=alto-tool.GetKerf();
        double largo_aux=largo-tool.GetKerf();
        double x_centro=x-largo/2;
        double y_centro=y-alto/2;
        double x_izq=x_centro-largo_aux/2;
        double x_der=x_centro+largo_aux/2;
        double y_down=y_centro-alto_aux/2;
        double y_up=y_centro+alto_aux/2;
        
        Codigo.add("(OP:"+indice+" - Agujero rectangular ala)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        Codigo.add("G17\r\n"); //Plano XY
        Codigo.add("G0 X"+String.format("%.3f",x_centro+LARGO_ANTORCHA)+" Y"+String.format("%.3f",y_centro+Y_COMP_ALMA)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaPinchazo())+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P"+String.format("%.1f",tool.GetRetardoPinchazo())+"\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        //Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        Codigo.add("G1 X"+String.format("%.3f",x_der+LARGO_ANTORCHA)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
        Codigo.add("Y"+String.format("%.3f",y_up+Y_COMP_ALMA)+"\r\n");
        Codigo.add("X"+String.format("%.3f",x_izq+LARGO_ANTORCHA)+"\r\n");
        Codigo.add("Y"+String.format("%.3f",y_down+Y_COMP_ALMA)+"\r\n");
        Codigo.add("X"+String.format("%.3f",x_der+LARGO_ANTORCHA)+"\r\n");
        Codigo.add("Y"+String.format("%.3f",y_centro+1+Y_COMP_ALMA)+"\r\n");
        Codigo.add("G0 Y"+String.format("%.3f",y_centro+1.01+Y_COMP_ALMA)+"\r\n");
        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        //Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        //Codigo.add("G4 P.1\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void corte_l_ala(double alto, double largo, double superior, boolean izquierda,Tools tool,int indice,double ancho_viga, double alto_viga){
        
         
        Codigo.add("(OP:"+indice+" - Corte en L ala)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        if(izquierda){
            Codigo.add("G90 G0 X-10\r\n");
        }else{
            Codigo.add("G90 G0 X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        }
        Codigo.add("G19\r\n"); //Plano YZ
        if(superior==1){
            Codigo.add("Y"+String.format("%.3f",-largo)+" Z-4\r\n");
            Codigo.add("o<refb> call\r\n");
            Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
            Codigo.add("Z4\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 Z"+String.format("%.3f",-alto)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("Y4\r\n");
            Codigo.add("G0 Y4.01\r\n");      
        }else{
            Codigo.add("Y"+String.format("%.3f",-largo)+" Z"+String.format("%.3f",-alto_viga+4)+"\r\n");
            Codigo.add("o<refb> call\r\n");
            Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
            Codigo.add("Z"+String.format("%.3f",-alto_viga-4)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 Z"+String.format("%.3f",-alto_viga+alto)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("Y4\r\n");
            Codigo.add("G0 Y4.01\r\n");   
        }

        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        if(izquierda)
            Codigo.add("X-10\r\n");
        else
            Codigo.add("X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        Codigo.add("G17\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void recta_ala(double y_inf, double y_sup, boolean izquierda,Tools tool,int indice,double ancho_viga, double alto_viga, double e_alma,double radio,boolean THC){
        
        double meq=alto_viga/(y_sup-y_inf);
        double beq=-alto_viga-meq*y_inf;
        double z1=-alto_viga-4;
        double z2=-alto_viga/2-e_alma/2-radio/2;
        double z3=-alto_viga/2+e_alma/2+radio/2;
        double z4=4;
        double y1,y2,y3,y4;
        
        if(y_inf!=y_sup){
            y1=(z1-beq)/meq;
            y2=(z2-beq)/meq;
            y3=(z3-beq)/meq;
            y4=(z4-beq)/meq;
        }else{
            y1=y_inf;
            y2=y1;
            y3=y1;
            y4=y1;
        }
        
        Codigo.add("(OP:"+indice+" - Corte recto ala)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        if(izquierda){
            Codigo.add("G90 G0 X-10\r\n");
        }else{
            Codigo.add("G90 G0 X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        }
        Codigo.add("G19\r\n"); //Plano YZ

        Codigo.add("Y"+String.format("%.3f",y_inf)+" Z"+String.format("%.3f",-alto_viga+4)+"\r\n");
        Codigo.add("o<refb> call\r\n");
        Codigo.add("G0 B"+String.format("%.1f",tool.GetAlturaCorte())+"\r\n");
        Codigo.add("Y"+String.format("%.3f",y1)+" Z"+String.format("%.3f",z1)+"\r\n");
        Codigo.add("M3 S100\r\n");
        Codigo.add("G4 P0.4\r\n");
        Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
        
        if(y_inf!=y_sup){
            Codigo.add("G1 Y"+String.format("%.3f",y2)+" Z"+String.format("%.3f",z2)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("Y"+String.format("%.3f",y3)+" Z"+String.format("%.3f",z3)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("Y"+String.format("%.3f",y4)+" Z"+String.format("%.3f",z4)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
        }else{
            Codigo.add("G1 Z"+String.format("%.3f",z2)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("Z"+String.format("%.3f",z3)+" F"+String.format("%d",Math.round(tool.GetFeed()*FACTOR_VEL))+"\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("Z"+String.format("%.3f",z4)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
        }
              
        Codigo.add("G0 Z"+String.format("%.3f",z4+0.01));

        Codigo.add("M5\r\n");
        Codigo.add("o<b_endcut> call\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G4 P.1\r\n");
        if(izquierda)
            Codigo.add("X-10\r\n");
        else
            Codigo.add("X"+(2*LARGO_ANTORCHA+ancho_viga+10)+"\r\n");
        Codigo.add("G17\r\n");
        Codigo.add("o<b_touchoff> call\r\n");
        Codigo.add("\r\n");
        
    }
    public void destijere(double y_izq, double y_der, double c_izq, double c_der, Tools tool, int indice, double ancho, double altura_alma, double e_ala, double radio){
        double off_x=LARGO_ANTORCHA+e_ala;
        double off_z=LARGO_ANTORCHA-altura_alma;
        double x_seg_izq, x_seg_der;
        boolean tiene_radio;
        
        tiene_radio = radio>0;
        
        if(radio>DIST_SEG){
            x_seg_izq=radio;
            x_seg_der=ancho-2*e_ala-radio;
        }
        else{
            x_seg_izq=DIST_SEG;
            x_seg_der=ancho-2*e_ala-DIST_SEG;
        }
        
        Codigo.add("(OP:"+indice+" - Destijere)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G17\r\n"); //Plano XY

        if(y_izq<0){
            Codigo.add("G0 X"+String.format("%.3f",x_seg_izq+c_izq+4+off_x)+" Y4\r\n");
            Codigo.add("Z"+String.format("%.3f",3+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 X"+String.format("%.3f",x_seg_izq+off_x)+" Y"+String.format("%.3f",-c_izq+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_izq+Y_COMP_ALMA)+"\r\n");
            if(tiene_radio){
                Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
                corte_radio_izq_up(radio,tool.GetFeed(),e_ala,altura_alma);
            }else{
                Codigo.add("X"+String.format("%.3f",off_x-4-e_ala)+"\r\n");
            }
            Codigo.add("G0 Y"+String.format("%.3f",y_izq+0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",x_seg_izq+off_x)+" A0\r\n");
            Codigo.add("o<b_touchoff> call\r\n");
            Codigo.add("\r\n");
        }
        if(y_der<0){
            Codigo.add("G0 X"+String.format("%.3f",x_seg_der-c_der-4+off_x)+" Y4\r\n");
            Codigo.add("Z"+String.format("%.3f",3+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 X"+String.format("%.3f",x_seg_der+off_x)+" Y"+String.format("%.3f",-c_der+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_der+Y_COMP_ALMA)+"\r\n");
            if(tiene_radio){
                Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
                corte_radio_der_up(ancho,radio,tool.GetFeed(),e_ala,altura_alma);
            }else
                Codigo.add("X"+String.format("%.3f",ancho+off_x+4-e_ala)+"\r\n");
            Codigo.add("G0 Y"+String.format("%.3f",y_der+0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",x_seg_der+off_x)+" A0\r\n");
            Codigo.add("o<b_touchoff> call\r\n");
            Codigo.add("\r\n");
        }
            
        
        
    }
    public void empalme_normal(double y_izq, double y_der, double x, Tools tool, int indice, double ancho, double altura_alma, double e_ala, double radio){
        double off_x=LARGO_ANTORCHA+e_ala;
        double off_z=LARGO_ANTORCHA-altura_alma;
        double x_seg_der;
        boolean tiene_radio;
        double[] coor_ini;
        coor_ini=get_ini_radio_izq(radio);
        
        
        tiene_radio = radio>0;

        if(radio>DIST_SEG){
            x_seg_der=ancho-2*e_ala-radio;
        }
        else{
            x_seg_der=ancho-2*e_ala-DIST_SEG;
        }
        
        Codigo.add("(OP:"+indice+" - Empalme estandar)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G17\r\n"); //Plano XY

        if(tiene_radio){
            Codigo.add("G0 X"+String.format("%.3f",ancho/2+off_x-e_ala)+" Y"+String.format("%.3f",y_izq+Y_COMP_ALMA)+"\r\n");
            Codigo.add("X"+String.format("%.3f",coor_ini[0]+off_x)+" A"+String.format("%.3f",coor_ini[2])+"\r\n");
            Codigo.add("Z"+String.format("%.3f",coor_ini[1]+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            corte_radio_izq_down(radio,tool.GetFeed(),e_ala,altura_alma);
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 X"+String.format("%.3f",x+off_x)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_der)+"\r\n");
            Codigo.add("X"+String.format("%.3f",x_seg_der+off_x)+"\r\n");
            Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
            corte_radio_der_up(ancho,radio,tool.GetFeed(),e_ala,altura_alma);
            Codigo.add("G0 Y"+String.format("%.3f",y_der+0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G91 G0 X-30\r\n");
            Codigo.add("G90 G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2+off_x-e_ala)+" A0\r\n");
            Codigo.add("o<b_touchoff> call\r\n");
            Codigo.add("\r\n");
        }else{
            Codigo.add("G0 X"+String.format("%.3f",off_x-e_ala-4)+" Y"+String.format("%.3f",y_izq+Y_COMP_ALMA)+"\r\n");
            Codigo.add("Z"+String.format("%.3f",off_z+3)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 X"+String.format("%.3f",x+off_x)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y_der+Y_COMP_ALMA)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho+off_x+e_ala+4)+"\r\n");
            Codigo.add("G0 X"+String.format("%.3f",ancho+off_x+e_ala+4.01)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2+off_x-e_ala)+" A0\r\n");
            Codigo.add("o<b_touchoff> call\r\n");
            Codigo.add("\r\n");
        }
    }
    public void empalme_hembra(double x_izq, double x_der, double y,double c, Tools tool, int indice, double ancho, double altura_alma, double e_ala, double radio){
        double off_x=LARGO_ANTORCHA+e_ala;
        double off_z=LARGO_ANTORCHA-altura_alma;
        double x_seg_der;
        boolean tiene_radio;
        double[] coor_ini;
        coor_ini=get_ini_radio_izq(radio);
        
        tiene_radio = radio>0;

        if(radio>DIST_SEG){
            x_seg_der=ancho-2*e_ala-radio;
        }
        else{
            x_seg_der=ancho-2*e_ala-DIST_SEG;
        }
        
        Codigo.add("(OP:"+indice+" - Empalme hembra)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G17\r\n"); //Plano XY

        if(tiene_radio){
            Codigo.add("G0 X"+String.format("%.3f",ancho/2+off_x-e_ala)+" Y"+String.format("%.3f",y+Y_COMP_ALMA)+"\r\n");
            Codigo.add("X"+String.format("%.3f",coor_ini[0]+off_x)+" A"+String.format("%.3f",coor_ini[2])+"\r\n");
            Codigo.add("Z"+String.format("%.3f",coor_ini[1]+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            corte_radio_izq_down(radio,tool.GetFeed(),e_ala,altura_alma);
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 X"+String.format("%.3f",x_izq+off_x)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y-c)+"\r\n");
            Codigo.add("X"+String.format("%.3f",x_der+off_x)+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y+Y_COMP_ALMA)+"\r\n");
            Codigo.add("X"+String.format("%.3f",x_seg_der+off_x)+"\r\n");
            Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
            corte_radio_der_up(ancho,radio,tool.GetFeed(),e_ala,altura_alma);
            Codigo.add("G0 Y"+String.format("%.3f",y+0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G91 G0 X-30\r\n");
            Codigo.add("G90 G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2+off_x-e_ala)+" A0\r\n");
            Codigo.add("o<b_touchoff> call\r\n");
            Codigo.add("\r\n");
        }else{
            Codigo.add("G0 X"+String.format("%.3f",off_x-e_ala-4)+" Y"+String.format("%.3f",y+Y_COMP_ALMA)+"\r\n");
            Codigo.add("Z"+String.format("%.3f",off_z+3)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 X"+String.format("%.3f",x_izq+off_x)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y-c+Y_COMP_ALMA)+"\r\n");
            Codigo.add("X"+String.format("%.3f",x_der+off_x)+"\r\n");
            Codigo.add("Y"+String.format("%.3f",y+Y_COMP_ALMA)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho+off_x+e_ala+4)+"\r\n");
            Codigo.add("G0 X"+String.format("%.3f",ancho+off_x+e_ala+4.01)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2+off_x-e_ala)+" A0\r\n");
            Codigo.add("o<b_touchoff> call\r\n");
            Codigo.add("\r\n");
        }
    }  
    public void recta_alma(double y_izq, double y_der, Tools tool, int indice, double ancho, double altura_alma, double e_ala, double radio){
        double off_x=LARGO_ANTORCHA+e_ala;
        double off_z=LARGO_ANTORCHA-altura_alma;
        double x_seg_der;
        boolean tiene_radio;
        double[] coor_ini;
        coor_ini=get_ini_radio_izq(radio);
        
        tiene_radio = radio>0;

        if(radio>DIST_SEG){
            x_seg_der=ancho-2*e_ala-radio;
        }
        else{
            x_seg_der=ancho-2*e_ala-DIST_SEG;
        }
        
        Codigo.add("(OP:"+indice+" - Corte recto alma)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G17\r\n"); //Plano XY

        if(tiene_radio){
            Codigo.add("G0 X"+String.format("%.3f",ancho/2+off_x-e_ala)+" Y"+String.format("%.3f",y_izq+Y_COMP_ALMA)+"\r\n");
            Codigo.add("X"+String.format("%.3f",coor_ini[0]+off_x)+" A"+String.format("%.3f",coor_ini[2])+"\r\n");
            Codigo.add("Z"+String.format("%.3f",coor_ini[1]+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            corte_radio_izq_down(radio,tool.GetFeed(),e_ala,altura_alma);
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 X"+String.format("%.3f",x_seg_der+off_x)+" Y"+String.format("%.3f",y_der+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
            corte_radio_der_up(ancho,radio,tool.GetFeed(),e_ala,altura_alma);
            Codigo.add("G0 Y"+String.format("%.3f",y_der+0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G91 G0 X-30\r\n");
            Codigo.add("G90 G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2+off_x-e_ala)+" A0\r\n");
            Codigo.add("o<b_touchoff> call\r\n");
            Codigo.add("\r\n");
        }else{
            Codigo.add("G0 X"+String.format("%.3f",off_x-e_ala-4)+" Y"+String.format("%.3f",y_izq+Y_COMP_ALMA)+"\r\n");
            Codigo.add("Z"+String.format("%.3f",off_z+3)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 X"+String.format("%.3f",ancho+off_x+e_ala+4)+" Y"+String.format("%.3f",y_der+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("G0 X"+String.format("%.3f",ancho+off_x+e_ala+4.01)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2+off_x-e_ala)+" A0\r\n");
            Codigo.add("o<b_touchoff> call\r\n");
            Codigo.add("\r\n");
        }
    }
    public void empalme_macho(double x_izq, double x_der, double c,Tools tool, int indice, double ancho, double altura_alma, double e_ala, double radio){
        double off_x=LARGO_ANTORCHA+e_ala;
        double off_z=LARGO_ANTORCHA-altura_alma;
        double x_seg_izq, x_seg_der;
        boolean tiene_radio;
        
        tiene_radio = radio>0;
        
        if(radio>DIST_SEG){
            x_seg_izq=radio;
            x_seg_der=ancho-2*e_ala-radio;
        }
        else{
            x_seg_izq=DIST_SEG;
            x_seg_der=ancho-2*e_ala-DIST_SEG;
        }
        
        Codigo.add("(OP:"+indice+" - Empalme macho)\r\n");
        Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
        Codigo.add("G17\r\n"); //Plano XY

        if(tiene_radio){
            //Corte izquierdo
            Codigo.add("G0 X"+String.format("%.3f",x_izq+off_x)+" Y4\r\n");
            Codigo.add("Z"+String.format("%.3f",3+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 Y"+String.format("%.3f",-c+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("X"+String.format("%.3f",x_seg_izq+off_x)+"\r\n");
            Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
            corte_radio_izq_up(radio,tool.GetFeed(),e_ala,altura_alma);
            Codigo.add("G0 Y"+String.format("%.3f",-c-0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2-e_ala+off_x)+" A0\r\n");
            //Corte derecho
            Codigo.add("G0 X"+String.format("%.3f",x_der+off_x)+" Y4\r\n");
            Codigo.add("Z"+String.format("%.3f",3+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 Y"+String.format("%.3f",-c+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("X"+String.format("%.3f",x_seg_der+off_x)+"\r\n");
            Codigo.add("M67 E0 Q10 (DTHC OFF)\r\n");
            corte_radio_der_up(ancho,radio,tool.GetFeed(),e_ala,altura_alma);
            Codigo.add("G0 Y"+String.format("%.3f",-c-0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2-e_ala+off_x)+" A0\r\n");
            Codigo.add("\r\n");
        }else{
            //Corte izquierdo
            Codigo.add("G0 X"+String.format("%.3f",x_izq+off_x)+" Y4\r\n");
            Codigo.add("Z"+String.format("%.3f",3+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 Y"+String.format("%.3f",-c+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("X"+String.format("%.3f",off_x-e_ala-4)+"\r\n");
            Codigo.add("G0 Y"+String.format("%.3f",-c-0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2-e_ala+off_x)+"\r\n");
            //Corte derecho
            Codigo.add("G0 X"+String.format("%.3f",x_der+off_x)+" Y4\r\n");
            Codigo.add("Z"+String.format("%.3f",3+off_z)+"\r\n");
            Codigo.add("M3 S100\r\n");
            Codigo.add("G4 P0.4\r\n");
            Codigo.add("M67 E0 Q20 (DTHC ON)\r\n");
            Codigo.add("G1 Y"+String.format("%.3f",-c+Y_COMP_ALMA)+" F"+String.format("%d",Math.round(tool.GetFeed()))+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho+off_x-e_ala+4)+"\r\n");
            Codigo.add("G0 Y"+String.format("%.3f",-c-0.01+Y_COMP_ALMA)+"\r\n");
            Codigo.add("M5\r\n");
            Codigo.add("o<b_endcut> call\r\n");
            Codigo.add("M68 E0 Q10 (DTHC OFF)\r\n");
            Codigo.add("G4 P.1\r\n");
            Codigo.add("G0 Z"+String.format("%.3f",10+off_z)+"\r\n");
            Codigo.add("X"+String.format("%.3f",ancho/2-e_ala+off_x)+"\r\n");
            Codigo.add("\r\n");
        }
               
    }
    
    public void guardarGcode(String nombre){
        
        Writer writer = null;

        try {

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nombre), "utf-8"));
            for(int indice=0;indice<Codigo.size();indice++)
                writer.write(Codigo.get(indice));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (writer!=null)
                try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
    }
    
}