package ihcam;

// @author FNaranjoB

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.*;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;


public class FramePrincipal extends javax.swing.JFrame {

    
    public static final int ANCHO_DISPLAY = 700;
    public static final int ALTO_DISPLAY = 200;
    
    Viga Material;
    double escalaLat;
    double escalaSup;
    double auxd;
    double xMaxLat=0;
    double xMaxSup=0;
    double yMaxLat=0;
    double yMaxSup=0;
    double[] auxParametrosIzq = new double[4];
    double[] auxParametrosDer = new double[4];
    double[] auxParametrosSup = new double[4];
    int posLateral[] = new int[2];  //[x,y]
    int posSuperior[] = new int[2]; //[x,y]
    int tamLateral[] = new int[2];  //[ancho,alto]
    int tamSuperior[] = new int[2]; //[ancho,alto]
    int tipoPerfil = 1;             //0- Ninguno, 1- IPE/HEB, 2-IPN, 3-UPN, 4-CAJA, 5-Angulo
    int aux=0;                      //auxiliar para calculos intermedios
    int indice_corte=0;             //Ubicacion de la operacion en el arrayList
    int consumible=45;              //Consumible seleccionado
    
    Operacion_preview PreviewMaterialLatIzq = new Operacion_preview(); //Almacena los dibujos que representan la viga
    Operacion_preview PreviewMaterialLatDer = new Operacion_preview();
    Operacion_preview PreviewMaterialSup = new Operacion_preview();
    Operacion_preview AuxPreview = new Operacion_preview();             //Variable de almacenaje intermedia para operaciones
    ArrayList<Operacion_preview> DisplayIzq = new ArrayList<>();       //Almacena todos los dibujos juntos
    ArrayList<Operacion_preview> DisplayDer = new ArrayList<>();
    ArrayList<Operacion_preview> DisplaySup = new ArrayList<>();
    ArrayList<Operacion_preview> OperacionesIzq = new ArrayList<>();    //Almacena los dibujos que representan todas las
    ArrayList<Operacion_preview> OperacionesDer = new ArrayList<>();    //operaciones de corte
    ArrayList<Operacion_preview> OperacionesSup = new ArrayList<>();
    ArrayList<Dibujo> BufferIzq = new ArrayList<>();                    //Arreglo de dibujos para presentar
    ArrayList<Dibujo> BufferDer = new ArrayList<>();                    //en pantalla
    ArrayList<Dibujo> BufferSup = new ArrayList<>();
    ArrayList<Corte> Cortes = new ArrayList<>();                        //Almacena toda la informacion de operaciones de corte creadas
    Corte AuxCorteGlobal=new Corte();                                         //Variable de almacenaje intermedia para operaciones de corte
    Operacion_corte AuxOpCorte = new Operacion_corte();                 //Variable de almacenaje intermedia para parametros de corte
    
    Shape OutlineLat;
    Shape OutlineSup;
    Shape OrigenIzq;
    Shape OrigenDer;
    Shape OrigenSup;
    Shape LineaGuia;
    Shape FormaAuxiliar;
    Dibujo OutlineLatDib;
    Dibujo OutlineSupDib;
    Dibujo OrigenIzqDib;
    Dibujo OrigenDerDib;
    Dibujo OrigenSupDib;
    Dibujo LineaGuiaDib;
    Dibujo FormaAuxiliarDib;
    Gcode Codigo;
      
    public FramePrincipal() throws UnsupportedLookAndFeelException {
        initComponents();
        Material = new Viga();
        PanelTabs.setEnabledAt(1,false);
        PanelTabs.setEnabledAt(2,false);
        PanelTabs.setEnabledAt(3,false);
        PanelTabs.setEnabledAt(4,false);
        PanelTabs.setEnabledAt(5,false);
        Codigo = new Gcode();
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(FramePrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void dibujarViga(int tipo){
        
        //Inicializacion (borra todo el contenido del visualizador)
        
        Codigo.borrar();
        modelo_gcode.clear();
        PreviewMaterialLatIzq.borrarTodo();
        PreviewMaterialLatDer.borrarTodo();
        PreviewMaterialSup.borrarTodo();
        DisplayIzq.clear();
        DisplayDer.clear();
        DisplaySup.clear();
        BufferIzq.clear();
        BufferDer.clear();
        BufferSup.clear();
        
        //Activar las pestañas
        
        PanelTabs.setEnabledAt(1,true);
        PanelTabs.setEnabledAt(2,true);
        if(tipo!=5)
            PanelTabs.setEnabledAt(3,true);
        else
            PanelTabs.setEnabledAt(3,false);
        PanelTabs.setEnabledAt(4,true);
        PanelTabs.setEnabledAt(5,true);

        
        //Definicion de la viga:
        
        switch(tipo){
            case 1: Material.SetearViga((double)SpinnerAnchoIPEHEB.getValue(),(double) SpinnerAltoIPEHEB.getValue(),0,(double) SpinnerRadioIPEHEB.getValue(), (double) SpinnerEAlaIPEHEB.getValue(), (double) SpinnerEAlmaIPEHEB.getValue());
                    break;  
            case 2: Material.SetearViga((double)SpinnerAnchoIPN.getValue(),(double) SpinnerAltoIPN.getValue(),(double) SpinnerRadioIPN.getValue(),(double) SpinnerRadio1IPN.getValue(), (double) SpinnerEAlaIPN.getValue(), (double) SpinnerEAlmaIPN.getValue());
                    break;  
            case 3: Material.SetearViga((double)SpinnerAnchoUPN.getValue(),(double) SpinnerAltoUPN.getValue(),(double) SpinnerRadioUPN.getValue(),(double) SpinnerRadio1UPN.getValue(), (double) SpinnerEAlaUPN.getValue(), (double) SpinnerEAlmaUPN.getValue());
                    break;  
            case 4: Material.SetearViga((double)SpinnerAnchoCAJON.getValue(),(double) SpinnerAltoCAJON.getValue(),0,0, (double) SpinnerECAJON.getValue(), (double) SpinnerECAJON.getValue());
                    break;  
            case 5: Material.SetearViga((double)SpinnerAnchoANGULO.getValue(),(double) SpinnerAnchoANGULO.getValue(),0,0, (double) SpinnerEANGULO.getValue(), (double) SpinnerEANGULO.getValue());
                    break;
        }
        
        xMaxLat=600;
        xMaxSup=600;
        yMaxLat=Material.GetAlto()*(-1);
        yMaxSup=Material.GetAncho();
        
        
        escalaLat = Material.GetEscalaLateralDisplay(ANCHO_DISPLAY, ALTO_DISPLAY);
        escalaSup = Material.GetEscalaSuperiorDisplay(ANCHO_DISPLAY, ALTO_DISPLAY);
        
        //Creacion del outline de la vista previa de la viga:
        
        tamLateral[0] =(int)(600.0*escalaLat);
        tamLateral[1] =(int)(Material.GetAlto()*escalaLat);
        posLateral[0] =26+(ANCHO_DISPLAY-tamLateral[0])/2;
        posLateral[1] =24+(ALTO_DISPLAY-tamLateral[1])/2;
        
        OutlineLat = new Rectangle2D.Double(posLateral[0],posLateral[1],tamLateral[0],tamLateral[1]);
        OutlineLatDib = new Dibujo();
        OutlineLatDib.Setear(OutlineLat, Color.BLUE, 1);
        
        tamSuperior[0] =(int)(600.0*escalaSup);
        tamSuperior[1] =(int)(Material.GetAncho()*escalaSup);
        posSuperior[0] =26+(ANCHO_DISPLAY-tamSuperior[0])/2;
        posSuperior[1] =24+(ALTO_DISPLAY-tamSuperior[1])/2;
        
        OutlineSup = new Rectangle2D.Double(posSuperior[0],posSuperior[1],tamSuperior[0],tamSuperior[1]);
        OutlineSupDib = new Dibujo();
        OutlineSupDib.Setear(OutlineSup, Color.BLUE, 1);
              
        PreviewMaterialLatIzq.añadirDibujo(OutlineLatDib);
        PreviewMaterialLatDer.añadirDibujo(OutlineLatDib);
        PreviewMaterialSup.añadirDibujo(OutlineSupDib);
        
        //Creacion del punto de origen en la vista previa:
        
        OrigenIzq = new Ellipse2D.Double(posLateral[0]-2,posLateral[1]-2, 4, 4);
        OrigenIzqDib = new Dibujo();
        OrigenIzqDib.Setear(OrigenIzq, Color.red, 2);
        
        PreviewMaterialLatIzq.añadirDibujo(OrigenIzqDib);
        
        OrigenDer = new Ellipse2D.Double(posLateral[0]+tamLateral[0]-2,posLateral[1]-2, 4, 4);
        OrigenDerDib = new Dibujo();
        OrigenDerDib.Setear(OrigenDer, Color.red, 2);
        
        PreviewMaterialLatDer.añadirDibujo(OrigenDerDib);
        
        //OrigenSup = new Ellipse2D.Double(posSuperior[0]+tamSuperior[0]-2,posSuperior[1]+tamSuperior[1]-2, 4, 4);
        OrigenSup = new Ellipse2D.Double(posSuperior[0]-2,posSuperior[1]+tamSuperior[1]-2, 4, 4);
        OrigenSupDib = new Dibujo();
        OrigenSupDib.Setear(OrigenSup, Color.red, 2);
        
        PreviewMaterialSup.añadirDibujo(OrigenSupDib);
        
        //Creacion de las aristas ocultas y radios
        
        switch(tipo){
            case 1: //IPE/HEB
                    //Arista oculta superior alma
                    auxd = posLateral[1]+(tamLateral[1]-Material.GetEspesorAlma()*escalaLat)/2;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialLatIzq.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialLatDer.añadirDibujo(LineaGuiaDib);
                    //Arista oculta inferior alma
                    auxd = posLateral[1]+(tamLateral[1]+Material.GetEspesorAlma()*escalaLat)/2;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialLatIzq.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialLatDer.añadirDibujo(LineaGuiaDib);
                    //Arista ala superior
                    auxd = posSuperior[1]+Material.GetEspesorAla()*escalaSup;
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.BLUE, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Arista ala inferior
                    auxd = posSuperior[1]+tamSuperior[1]-Material.GetEspesorAla()*escalaSup;
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.BLUE, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Radio superior
                    auxd = posSuperior[1]+Material.GetEspesorAla()*escalaSup+Material.GetRadioAlma()*escalaSup;
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.ORANGE, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Radio inferior
                    auxd = posSuperior[1]+tamSuperior[1]-Material.GetEspesorAla()*escalaSup-Material.GetRadioAlma()*escalaSup;
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.ORANGE, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    break;
            case 2: //IPN
                    //Arista oculta superior alma
                    auxd = posLateral[1]+(tamLateral[1]-Material.GetEspesorAlma()*escalaLat)/2;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialLatIzq.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialLatDer.añadirDibujo(LineaGuiaDib);
                    //Arista oculta inferior alma
                    auxd = posLateral[1]+(tamLateral[1]+Material.GetEspesorAlma()*escalaLat)/2;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialLatIzq.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialLatDer.añadirDibujo(LineaGuiaDib);
                    //Arista ala superior
                    auxd = (double)posSuperior[1]+(double)Material.GetEspesorAla()*escalaSup-0.03514*((double)Material.GetAlto()*escalaSup);
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.BLUE, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Arista ala inferior
                    auxd = (double)posSuperior[1]+(double)tamSuperior[1]-(double)Material.GetEspesorAla()*escalaSup+0.03514*((double)Material.GetAlto()*escalaSup);
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.BLUE, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Radio superior
                    auxd = (double)posSuperior[1]+(double)Material.GetEspesorAla()*escalaSup+0.8693*((double)Material.GetRadioAlma()*escalaSup)+0.03514*((double)Material.GetAlto()*escalaSup-2*(double)Material.GetEspesorAlma()*escalaSup);
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.ORANGE, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Radio inferior
                    auxd = (double)posSuperior[1]+(double)tamSuperior[1]-(double)Material.GetEspesorAla()*escalaSup-0.8693*((double)Material.GetRadioAlma()*escalaSup)-0.03514*((double)Material.GetAlto()*escalaSup-2*(double)Material.GetEspesorAlma()*escalaSup);
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.ORANGE, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    break;
            case 3: //UPN
                    //Arista oculta alma
                    auxd = posLateral[1]+Material.GetEspesorAlma()*escalaLat;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialLatIzq.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialLatDer.añadirDibujo(LineaGuiaDib);
                    //Arista ala superior
                    if(Material.GetAncho()<300)    
                        auxd = (double)posSuperior[1]+(double)Material.GetEspesorAla()*escalaSup-0.0437*((double)Material.GetAlto()*escalaSup);
                    else
                        auxd = (double)posSuperior[1]+(double)Material.GetEspesorAla()*escalaSup-0.035*((double)Material.GetAlto()*escalaSup);
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Arista ala inferior
                    if(Material.GetAncho()<300)
                        auxd = (double)posSuperior[1]+(double)tamSuperior[1]-(double)Material.GetEspesorAla()*escalaSup+0.0437*((double)Material.GetAlto()*escalaSup);
                    else
                        auxd = (double)posSuperior[1]+(double)tamSuperior[1]-(double)Material.GetEspesorAla()*escalaSup+0.035*((double)Material.GetAlto()*escalaSup);
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Radio superior
                    if(Material.GetAncho()<300)
                        auxd = (double)posSuperior[1]+(double)Material.GetEspesorAla()*escalaSup+0.9163*((double)Material.GetRadioAlma()*escalaSup)+0.08749*((double)Material.GetAlto()*escalaSup-2*(double)Material.GetEspesorAlma()*escalaSup);
                    else
                        auxd = (double)posSuperior[1]+(double)Material.GetEspesorAla()*escalaSup+0.9325*((double)Material.GetRadioAlma()*escalaSup)+0.06993*((double)Material.GetAlto()*escalaSup);
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Radio inferior
                    if(Material.GetAncho()<300)
                        auxd = (double)posSuperior[1]+(double)tamSuperior[1]-(double)Material.GetEspesorAla()*escalaSup-0.9163*((double)Material.GetRadioAlma()*escalaSup)-0.08749*((double)Material.GetAlto()*escalaSup-2*(double)Material.GetEspesorAlma()*escalaSup);
                    else
                        auxd = (double)posSuperior[1]+(double)tamSuperior[1]-(double)Material.GetEspesorAla()*escalaSup-0.9325*((double)Material.GetRadioAlma()*escalaSup)-0.06993*((double)Material.GetAlto()*escalaSup);
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    break;
            case 4: //Cajon
                    //Arista oculta superior alma
                    auxd = posLateral[1]+Material.GetEspesorAlma()*escalaLat;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialLatIzq.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialLatDer.añadirDibujo(LineaGuiaDib);
                    //Arista oculta inferior alma
                    auxd = posLateral[1]+tamLateral[1]-Material.GetEspesorAlma()*escalaLat;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialLatIzq.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialLatDer.añadirDibujo(LineaGuiaDib);
                    //Arista ala superior
                    auxd = posSuperior[1]+Material.GetEspesorAla()*escalaSup;
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    //Arista ala inferior
                    auxd = posSuperior[1]+tamSuperior[1]-Material.GetEspesorAla()*escalaSup;
                    LineaGuia = new Line2D.Double(posSuperior[0],auxd,posSuperior[0]+tamSuperior[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    break;
            case 5: //Angulo
                    //Arista oculta lateral
                    auxd = posLateral[1]+Material.GetEspesorAlma()*escalaLat;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialLatIzq.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialLatDer.añadirDibujo(LineaGuiaDib);
                    //Arista oculta superior
                    auxd = posSuperior[1]+tamSuperior[1]-Material.GetEspesorAlma()*escalaSup;
                    LineaGuia = new Line2D.Double(posLateral[0],auxd,posLateral[0]+tamLateral[0],auxd);
                    LineaGuiaDib = new Dibujo();
                    LineaGuiaDib.Setear(LineaGuia, Color.lightGray, 1);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    PreviewMaterialSup.añadirDibujo(LineaGuiaDib);
                    break;
        }
        
        
        //Cargar la vista previa de la viga en los visualizadores
        
        DisplayIzq.add(PreviewMaterialLatIzq);
        DisplayDer.add(PreviewMaterialLatDer);
        DisplaySup.add(PreviewMaterialSup);
        
        //Cargar todas las operaciones en los visualizadores
        
        for(int i=0; i<Cortes.size(); i++){
            DisplayIzq.add(Cortes.get(i).GetPreviewIzq());
            DisplayDer.add(Cortes.get(i).GetPreviewDer());
            DisplaySup.add(Cortes.get(i).GetPreviewSup());
        }
        for(int i=0; i<DisplayIzq.size();i++)
            BufferIzq.addAll(DisplayIzq.get(i).getAll());
        
        ((PanelPreview)PreviewIzq).setear(BufferIzq);
        
        for(int i=0; i<DisplayDer.size();i++)
            BufferDer.addAll(DisplayDer.get(i).getAll());
        
        ((PanelPreview)PreviewDer).setear(BufferDer);
        
        for(int i=0; i<DisplaySup.size();i++)
            BufferSup.addAll(DisplaySup.get(i).getAll());
        
        ((PanelPreview)PreviewSup).setear(BufferSup);
        
        double e_feed;
        
        if(Material.GetEspesorAla()>Material.GetEspesorAlma())
            e_feed=Material.GetEspesorAla();
        else
            e_feed=Material.GetEspesorAlma();
        
        if(e_feed<6.5){
            radio45.setEnabled(true);
            radio65.setEnabled(false);
            radio85.setEnabled(false);
            radio45.setSelected(true);
            consumible=45;
        }else if(e_feed>=6.5&&e_feed<16.5){
            radio45.setEnabled(false);
            radio65.setEnabled(true);
            radio85.setEnabled(true);
            radio65.setSelected(true);
            consumible=65;
        }else{
            radio45.setEnabled(false);
            radio65.setEnabled(false);
            radio85.setEnabled(true);
            radio85.setSelected(true);
            consumible=85;
        }
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        Agregar_dialog = new javax.swing.JDialog();
        Boton_salir = new javax.swing.JButton();
        Boton_recto = new javax.swing.JButton();
        Boton_recto_inc = new javax.swing.JButton();
        Boton_recto_desf = new javax.swing.JButton();
        Boton_recto_desf_inc = new javax.swing.JButton();
        Boton_destijere = new javax.swing.JButton();
        Boton_destijere_inc = new javax.swing.JButton();
        Boton_destijere2 = new javax.swing.JButton();
        Boton_empalme = new javax.swing.JButton();
        Boton_empalme_inc = new javax.swing.JButton();
        Boton_agujero_platina = new javax.swing.JButton();
        Boton_empalme_macho = new javax.swing.JButton();
        Boton_empalme_hembra = new javax.swing.JButton();
        Boton_redondo_ala = new javax.swing.JButton();
        Boton_oblongo_ala_V = new javax.swing.JButton();
        Boton_oblongo_ala_H = new javax.swing.JButton();
        Boton_oblongo_alma_V = new javax.swing.JButton();
        Boton_oblongo_alma_H = new javax.swing.JButton();
        Boton_redondo_alma = new javax.swing.JButton();
        Boton_rectangular_ala = new javax.swing.JButton();
        Boton_rectangular_alma = new javax.swing.JButton();
        Recto_dialog = new javax.swing.JDialog();
        jLabel10 = new javax.swing.JLabel();
        SpinnerLargoRecto = new javax.swing.JSpinner();
        Boton_ok_recto = new javax.swing.JButton();
        Boton_cancel_recto = new javax.swing.JButton();
        Panel_Medidas_recto = new javax.swing.JPanel();
        Canvas_recto = new javax.swing.JLabel();
        Recto_inc_dialog = new javax.swing.JDialog();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        SpinnerL1RectoInc = new javax.swing.JSpinner();
        SpinnerL2RectoInc = new javax.swing.JSpinner();
        Boton_ok_recto_inc = new javax.swing.JButton();
        Boton_cancel_recto_inc = new javax.swing.JButton();
        Panel_Medidas_recto_inc = new javax.swing.JPanel();
        Canvas_recto_inc = new javax.swing.JLabel();
        Recto_desf_dialog = new javax.swing.JDialog();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        SpinnerLIRectoDesf = new javax.swing.JSpinner();
        SpinnerLDRectoDesf = new javax.swing.JSpinner();
        Boton_ok_recto_desf = new javax.swing.JButton();
        Boton_cancel_recto_desf = new javax.swing.JButton();
        Panel_Medidas_recto_desf = new javax.swing.JPanel();
        Canvas_recto_desf = new javax.swing.JLabel();
        Recto_inc_desf_dialog = new javax.swing.JDialog();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        SpinnerL1IRectoIncDesf = new javax.swing.JSpinner();
        SpinnerL2IRectoIncDesf = new javax.swing.JSpinner();
        SpinnerL1DRectoIncDesf = new javax.swing.JSpinner();
        SpinnerL2DRectoIncDesf = new javax.swing.JSpinner();
        Boton_ok_recto_inc_desf = new javax.swing.JButton();
        Boton_cancel_recto_inc_desf = new javax.swing.JButton();
        Panel_Medidas_recto_inc_desf = new javax.swing.JPanel();
        Canvas_recto_inc_desf = new javax.swing.JLabel();
        Empalme_dialog = new javax.swing.JDialog();
        jLabel123 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        SpinnerLIEmpalme = new javax.swing.JSpinner();
        SpinnerLDEmpalme = new javax.swing.JSpinner();
        SpinnerXEmpalme = new javax.swing.JSpinner();
        Boton_ok_empalme = new javax.swing.JButton();
        Boton_cancel_empalme = new javax.swing.JButton();
        Panel_Medidas_empalme = new javax.swing.JPanel();
        Canvas_empalme = new javax.swing.JLabel();
        Empalme_inc_dialog = new javax.swing.JDialog();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        SpinnerL1IEmpalmeInc = new javax.swing.JSpinner();
        SpinnerL2IEmpalmeInc = new javax.swing.JSpinner();
        SpinnerL1DEmpalmeInc = new javax.swing.JSpinner();
        SpinnerL2DEmpalmeInc = new javax.swing.JSpinner();
        Boton_ok_empalme_inc = new javax.swing.JButton();
        Boton_cancel_empalme_inc = new javax.swing.JButton();
        Panel_Medidas_empalme_inc = new javax.swing.JPanel();
        Canvas_recto_empalme_inc = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        SpinnerXEmpalmeInc = new javax.swing.JSpinner();
        Destijere_dialog = new javax.swing.JDialog();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        SpinnerLIDestijere = new javax.swing.JSpinner();
        SpinnerCIDestijere = new javax.swing.JSpinner();
        SpinnerLDDestijere = new javax.swing.JSpinner();
        SpinnerCDDestijere = new javax.swing.JSpinner();
        Boton_ok_destijere = new javax.swing.JButton();
        Boton_cancel_destijere = new javax.swing.JButton();
        Panel_Medidas_destijere = new javax.swing.JPanel();
        Canvas_destijere = new javax.swing.JLabel();
        Destijere_inc_dialog = new javax.swing.JDialog();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        SpinnerL1IDestijereInc = new javax.swing.JSpinner();
        SpinnerL2IDestijereInc = new javax.swing.JSpinner();
        SpinnerCIDestijereInc = new javax.swing.JSpinner();
        SpinnerL1DDestijereInc = new javax.swing.JSpinner();
        SpinnerL2DDestijereInc = new javax.swing.JSpinner();
        SpinnerCDDestijereInc = new javax.swing.JSpinner();
        Boton_ok_destijere_inc = new javax.swing.JButton();
        Boton_cancel_destijere_inc = new javax.swing.JButton();
        Panel_Medidas_destijere_inc = new javax.swing.JPanel();
        Canvas_destijere_inc = new javax.swing.JLabel();
        Destijere2_dialog = new javax.swing.JDialog();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        SpinnerLIIDestijere2 = new javax.swing.JSpinner();
        SpinnerAIIDestijere2 = new javax.swing.JSpinner();
        SpinnerLSIDestijere2 = new javax.swing.JSpinner();
        SpinnerASIDestijere2 = new javax.swing.JSpinner();
        SpinnerLIDDestijere2 = new javax.swing.JSpinner();
        SpinnerAIDDestijere2 = new javax.swing.JSpinner();
        SpinnerLSDDestijere2 = new javax.swing.JSpinner();
        SpinnerASDDestijere2 = new javax.swing.JSpinner();
        Boton_ok_destijere2 = new javax.swing.JButton();
        Boton_cancel_destijere2 = new javax.swing.JButton();
        Panel_Medidas_destijere2 = new javax.swing.JPanel();
        Canvas_recto_destijere2 = new javax.swing.JLabel();
        Empalme_macho_dialog = new javax.swing.JDialog();
        jLabel69 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        SpinnerA1EmpalmeMacho = new javax.swing.JSpinner();
        SpinnerA2EmpalmeMacho = new javax.swing.JSpinner();
        SpinnerCEmpalmeMacho = new javax.swing.JSpinner();
        Boton_ok_empalme_macho = new javax.swing.JButton();
        Boton_cancel_empalme_macho = new javax.swing.JButton();
        Panel_Medidas_empalme_macho = new javax.swing.JPanel();
        Canvas_empalme_macho = new javax.swing.JLabel();
        Empalme_hembra_dialog = new javax.swing.JDialog();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        SpinnerLEmpalmeHembra = new javax.swing.JSpinner();
        SpinnerA1EmpalmeHembra = new javax.swing.JSpinner();
        SpinnerA2EmpalmeHembra = new javax.swing.JSpinner();
        SpinnerCEmpalmeHembra = new javax.swing.JSpinner();
        Boton_ok_empalme_hembra = new javax.swing.JButton();
        Boton_cancel_empalme_hembra = new javax.swing.JButton();
        Panel_Medidas_empalme_hembra = new javax.swing.JPanel();
        Canvas_empalme_hembra = new javax.swing.JLabel();
        Agujero_platina_dialog = new javax.swing.JDialog();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        SpinnerYIIAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerLIIAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerAIIAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerYSIAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerLSIAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerASIAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerYIDAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerLIDAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerAIDAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerYSDAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerLSDAgujeroPlatina = new javax.swing.JSpinner();
        SpinnerASDAgujeroPlatina = new javax.swing.JSpinner();
        Boton_ok_agujero_platina = new javax.swing.JButton();
        Boton_cancel_agujero_platina = new javax.swing.JButton();
        Panel_Medidas_agujero_platina = new javax.swing.JPanel();
        Canvas_recto_agujero_platina = new javax.swing.JLabel();
        Redondo_ala_dialog = new javax.swing.JDialog();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        SpinnerYRedondoAla = new javax.swing.JSpinner();
        SpinnerZRedondoAla = new javax.swing.JSpinner();
        SpinnerDRedondoAla = new javax.swing.JSpinner();
        Boton_ok_redondo_ala = new javax.swing.JButton();
        Boton_cancel_redondo_ala = new javax.swing.JButton();
        Panel_Medidas_redondo_ala = new javax.swing.JPanel();
        Canvas_redondo_ala = new javax.swing.JLabel();
        Check_izq_redondo_ala = new javax.swing.JCheckBox();
        Check_der_redondo_ala = new javax.swing.JCheckBox();
        Oblongo_ala_v_dialog = new javax.swing.JDialog();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        SpinnerYOblongoAlaV = new javax.swing.JSpinner();
        SpinnerZOblongoAlaV = new javax.swing.JSpinner();
        SpinnerDOblongoAlaV = new javax.swing.JSpinner();
        SpinnerLOblongoAlaV = new javax.swing.JSpinner();
        Boton_ok_oblongo_ala_v = new javax.swing.JButton();
        Boton_cancel_oblongo_ala_v = new javax.swing.JButton();
        Panel_Medidas_oblongo_ala_v = new javax.swing.JPanel();
        Canvas_oblongo_ala_v = new javax.swing.JLabel();
        Check_izq_oblongo_ala_v = new javax.swing.JCheckBox();
        Check_der_oblongo_ala_v = new javax.swing.JCheckBox();
        Oblongo_ala_h_dialog = new javax.swing.JDialog();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        SpinnerYOblongoAlaH = new javax.swing.JSpinner();
        SpinnerZOblongoAlaH = new javax.swing.JSpinner();
        SpinnerDOblongoAlaH = new javax.swing.JSpinner();
        SpinnerLOblongoAlaH = new javax.swing.JSpinner();
        Boton_ok_oblongo_ala_h = new javax.swing.JButton();
        Boton_cancel_oblongo_ala_h = new javax.swing.JButton();
        Panel_Medidas_oblongo_ala_h = new javax.swing.JPanel();
        Canvas_oblongo_ala_h = new javax.swing.JLabel();
        Check_izq_oblongo_ala_h = new javax.swing.JCheckBox();
        Check_der_oblongo_ala_h = new javax.swing.JCheckBox();
        Redondo_alma_dialog = new javax.swing.JDialog();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        SpinnerXRedondoAlma = new javax.swing.JSpinner();
        SpinnerYRedondoAlma = new javax.swing.JSpinner();
        SpinnerDRedondoAlma = new javax.swing.JSpinner();
        Boton_ok_redondo_alma = new javax.swing.JButton();
        Boton_cancel_redondo_alma = new javax.swing.JButton();
        Panel_Medidas_redondo_alma = new javax.swing.JPanel();
        Canvas_redondo_alma = new javax.swing.JLabel();
        Oblongo_alma_v_dialog = new javax.swing.JDialog();
        jLabel107 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        SpinnerXOblongoAlmaV = new javax.swing.JSpinner();
        SpinnerYOblongoAlmaV = new javax.swing.JSpinner();
        SpinnerDOblongoAlmaV = new javax.swing.JSpinner();
        SpinnerLOblongoAlmaV = new javax.swing.JSpinner();
        Boton_ok_oblongo_alma_v = new javax.swing.JButton();
        Boton_cancel_oblongo_alma_v = new javax.swing.JButton();
        Panel_Medidas_oblongo_alma_v = new javax.swing.JPanel();
        Canvas_oblongo_alma_v = new javax.swing.JLabel();
        Oblongo_alma_h_dialog = new javax.swing.JDialog();
        jLabel111 = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        SpinnerXOblongoAlmaH = new javax.swing.JSpinner();
        SpinnerYOblongoAlmaH = new javax.swing.JSpinner();
        SpinnerDOblongoAlmaH = new javax.swing.JSpinner();
        SpinnerLOblongoAlmaH = new javax.swing.JSpinner();
        Boton_ok_oblongo_alma_h = new javax.swing.JButton();
        Boton_cancel_oblongo_alma_h = new javax.swing.JButton();
        Panel_Medidas_oblongo_alma_h = new javax.swing.JPanel();
        Canvas_oblongo_alma_h = new javax.swing.JLabel();
        Agujero_rect_ala_dialog = new javax.swing.JDialog();
        jLabel115 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        SpinnerYAgujeroRectAla = new javax.swing.JSpinner();
        SpinnerZAgujeroRectAla = new javax.swing.JSpinner();
        SpinnerAAgujeroRectAla = new javax.swing.JSpinner();
        SpinnerLAgujeroRectAla = new javax.swing.JSpinner();
        Boton_ok_agujero_rect_ala = new javax.swing.JButton();
        Boton_cancel_agujero_rect_ala = new javax.swing.JButton();
        Panel_Medidas_agujero_rect_ala = new javax.swing.JPanel();
        Canvas_agujero_rect_ala = new javax.swing.JLabel();
        Check_izq_agujero_rect_ala = new javax.swing.JCheckBox();
        Check_der_agujero_rect_ala = new javax.swing.JCheckBox();
        Agujero_rect_alma_dialog = new javax.swing.JDialog();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        SpinnerXAgujeroRectAlma = new javax.swing.JSpinner();
        SpinnerYAgujeroRectAlma = new javax.swing.JSpinner();
        SpinnerAAgujeroRectAlma = new javax.swing.JSpinner();
        SpinnerLAgujeroRectAlma = new javax.swing.JSpinner();
        Boton_ok_agujero_rect_alma = new javax.swing.JButton();
        Boton_cancel_agujero_rect_alma = new javax.swing.JButton();
        Panel_Medidas_agujero_rect_alma = new javax.swing.JPanel();
        Canvas_agujero_rect_alma = new javax.swing.JLabel();
        JOptionPane = new javax.swing.JOptionPane();
        chooser = new javax.swing.JFileChooser();
        PanelTabs = new javax.swing.JTabbedPane();
        PanelMaterial = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        PanelTipoPerfil = new javax.swing.JPanel();
        PanelIPEHEB = new javax.swing.JPanel();
        SpinnerAnchoIPEHEB = new javax.swing.JSpinner();
        SpinnerAltoIPEHEB = new javax.swing.JSpinner();
        SpinnerRadioIPEHEB = new javax.swing.JSpinner();
        SpinnerEAlaIPEHEB = new javax.swing.JSpinner();
        SpinnerEAlmaIPEHEB = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        BotonIPEHEB = new javax.swing.JButton();
        PanelIPN = new javax.swing.JPanel();
        SpinnerAnchoIPN = new javax.swing.JSpinner();
        SpinnerAltoIPN = new javax.swing.JSpinner();
        SpinnerRadioIPN = new javax.swing.JSpinner();
        SpinnerRadio1IPN = new javax.swing.JSpinner();
        SpinnerEAlaIPN = new javax.swing.JSpinner();
        SpinnerEAlmaIPN = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        BotonIPN = new javax.swing.JButton();
        PanelUPN = new javax.swing.JPanel();
        SpinnerAnchoUPN = new javax.swing.JSpinner();
        SpinnerAltoUPN = new javax.swing.JSpinner();
        SpinnerRadioUPN = new javax.swing.JSpinner();
        SpinnerRadio1UPN = new javax.swing.JSpinner();
        SpinnerEAlaUPN = new javax.swing.JSpinner();
        SpinnerEAlmaUPN = new javax.swing.JSpinner();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        BotonUPN = new javax.swing.JButton();
        PanelANGULO = new javax.swing.JPanel();
        SpinnerAnchoANGULO = new javax.swing.JSpinner();
        SpinnerEANGULO = new javax.swing.JSpinner();
        jLabel67 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel70 = new javax.swing.JLabel();
        BotonANGULO = new javax.swing.JButton();
        PanelCAJON = new javax.swing.JPanel();
        SpinnerAnchoCAJON = new javax.swing.JSpinner();
        SpinnerAltoCAJON = new javax.swing.JSpinner();
        SpinnerECAJON = new javax.swing.JSpinner();
        jLabel68 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel73 = new javax.swing.JLabel();
        BotonCAJON = new javax.swing.JButton();
        ComboTipoPerfil = new javax.swing.JComboBox<>();
        PanelIzq = new javax.swing.JPanel();
        TituloPanelIzq = new javax.swing.JLabel();
        PanelPreviewIzq = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        PreviewIzq = new PanelPreview();
        PanelOperacionesIzq = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        PanelListaIzq = new javax.swing.JScrollPane();
        modelo_izq = new DefaultListModel();
        ListaOpIzq = new javax.swing.JList<>();
        AgregarOpIzq = new javax.swing.JButton();
        BorrarOpIzq = new javax.swing.JButton();
        BorrarAllIzq = new javax.swing.JButton();
        PanelSup = new javax.swing.JPanel();
        TituloPanelSup = new javax.swing.JLabel();
        PanelPreviewSup = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        PreviewSup = new PanelPreview();
        PanelOperacionesSup = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        PanelListaSup = new javax.swing.JScrollPane();
        modelo_sup = new DefaultListModel();
        ListaOpSup = new javax.swing.JList<>();
        AgregarOpSup = new javax.swing.JButton();
        BorrarOpSup = new javax.swing.JButton();
        BorrarAllSup = new javax.swing.JButton();
        PanelDer = new javax.swing.JPanel();
        TituloPanelDer = new javax.swing.JLabel();
        PanelPreviewDer = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        PreviewDer = new PanelPreview();
        PanelOperacionesDer = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        PanelListaDer = new javax.swing.JScrollPane();
        modelo_der = new DefaultListModel();
        ListaOpDer = new javax.swing.JList<>();
        BorrarOpDer = new javax.swing.JButton();
        BorrarAllDer = new javax.swing.JButton();
        AgregarOpDer = new javax.swing.JButton();
        PanelConsumibles = new javax.swing.JPanel();
        TituloPanelConsumibles = new javax.swing.JLabel();
        PanelPicConsumibles = new javax.swing.JPanel();
        PicConsumibles = new javax.swing.JLabel();
        radio45 = new javax.swing.JRadioButton();
        radio65 = new javax.swing.JRadioButton();
        radio85 = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_boquilla = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        SpinnerVoltajeAla = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        jLabel125 = new javax.swing.JLabel();
        SpinnerVoltajeAlma = new javax.swing.JSpinner();
        PanelGcode = new javax.swing.JPanel();
        TituloPanelGcode = new javax.swing.JLabel();
        ScrollGcode = new javax.swing.JScrollPane();
        modelo_gcode = new DefaultListModel();
        listaGcode = new javax.swing.JList<>();
        BotonGenerarGcode = new javax.swing.JButton();
        BotonGuardarGcode = new javax.swing.JButton();

        Agregar_dialog.setBackground(new java.awt.Color(153, 204, 255));
        Agregar_dialog.setMinimumSize(new java.awt.Dimension(845, 610));
        Agregar_dialog.setResizable(false);

        Boton_salir.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_salir.setText("OK");
        Boton_salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_salirActionPerformed(evt);
            }
        });

        Boton_recto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/RectoIPE.png"))); // NOI18N
        Boton_recto.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_recto.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_recto.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_recto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_rectoActionPerformed(evt);
            }
        });

        Boton_recto_inc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/RectoAlaInclinada.png"))); // NOI18N
        Boton_recto_inc.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_recto_inc.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_recto_inc.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_recto_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_recto_incActionPerformed(evt);
            }
        });

        Boton_recto_desf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/RectoDesfasado.png"))); // NOI18N
        Boton_recto_desf.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_recto_desf.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_recto_desf.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_recto_desf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_recto_desfActionPerformed(evt);
            }
        });

        Boton_recto_desf_inc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/RectoDesfAlaInclinada.png"))); // NOI18N
        Boton_recto_desf_inc.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_recto_desf_inc.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_recto_desf_inc.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_recto_desf_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_recto_desf_incActionPerformed(evt);
            }
        });

        Boton_destijere.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/Destijere.png"))); // NOI18N
        Boton_destijere.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_destijere.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_destijere.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_destijere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_destijereActionPerformed(evt);
            }
        });

        Boton_destijere_inc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/DestijereAlaInclinada.png"))); // NOI18N
        Boton_destijere_inc.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_destijere_inc.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_destijere_inc.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_destijere_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_destijere_incActionPerformed(evt);
            }
        });

        Boton_destijere2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/Destijere2.png"))); // NOI18N
        Boton_destijere2.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_destijere2.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_destijere2.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_destijere2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_destijere2ActionPerformed(evt);
            }
        });

        Boton_empalme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/Empalme.png"))); // NOI18N
        Boton_empalme.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_empalme.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_empalme.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_empalme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_empalmeActionPerformed(evt);
            }
        });

        Boton_empalme_inc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/EmpalmeAlaInclinada.png"))); // NOI18N
        Boton_empalme_inc.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_empalme_inc.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_empalme_inc.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_empalme_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_empalme_incActionPerformed(evt);
            }
        });

        Boton_agujero_platina.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/AgujeroPlatina.png"))); // NOI18N
        Boton_agujero_platina.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_agujero_platina.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_agujero_platina.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_agujero_platina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_agujero_platinaActionPerformed(evt);
            }
        });

        Boton_empalme_macho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/EmpalmeMacho.png"))); // NOI18N
        Boton_empalme_macho.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_empalme_macho.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_empalme_macho.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_empalme_macho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_empalme_machoActionPerformed(evt);
            }
        });

        Boton_empalme_hembra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/EmpalmeHembra.png"))); // NOI18N
        Boton_empalme_hembra.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_empalme_hembra.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_empalme_hembra.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_empalme_hembra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_empalme_hembraActionPerformed(evt);
            }
        });

        Boton_redondo_ala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/Redondos.png"))); // NOI18N
        Boton_redondo_ala.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_redondo_ala.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_redondo_ala.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_redondo_ala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_redondo_alaActionPerformed(evt);
            }
        });

        Boton_oblongo_ala_V.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/OblongosAlaV.png"))); // NOI18N
        Boton_oblongo_ala_V.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_ala_V.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_ala_V.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_ala_V.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_oblongo_ala_VActionPerformed(evt);
            }
        });

        Boton_oblongo_ala_H.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/OblongosAlaH.png"))); // NOI18N
        Boton_oblongo_ala_H.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_ala_H.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_ala_H.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_ala_H.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_oblongo_ala_HActionPerformed(evt);
            }
        });

        Boton_oblongo_alma_V.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/OblongosAlmaV.png"))); // NOI18N
        Boton_oblongo_alma_V.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_alma_V.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_alma_V.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_alma_V.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_oblongo_alma_VActionPerformed(evt);
            }
        });

        Boton_oblongo_alma_H.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/OblongosAlmaH.png"))); // NOI18N
        Boton_oblongo_alma_H.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_alma_H.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_alma_H.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_oblongo_alma_H.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_oblongo_alma_HActionPerformed(evt);
            }
        });

        Boton_redondo_alma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/RedondosAlma.png"))); // NOI18N
        Boton_redondo_alma.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_redondo_alma.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_redondo_alma.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_redondo_alma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_redondo_almaActionPerformed(evt);
            }
        });

        Boton_rectangular_ala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/RectangularAla.png"))); // NOI18N
        Boton_rectangular_ala.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_rectangular_ala.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_rectangular_ala.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_rectangular_ala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_rectangular_alaActionPerformed(evt);
            }
        });

        Boton_rectangular_alma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/RectangularAlma.png"))); // NOI18N
        Boton_rectangular_alma.setMaximumSize(new java.awt.Dimension(147, 112));
        Boton_rectangular_alma.setMinimumSize(new java.awt.Dimension(147, 112));
        Boton_rectangular_alma.setPreferredSize(new java.awt.Dimension(147, 112));
        Boton_rectangular_alma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_rectangular_almaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Agregar_dialogLayout = new javax.swing.GroupLayout(Agregar_dialog.getContentPane());
        Agregar_dialog.getContentPane().setLayout(Agregar_dialogLayout);
        Agregar_dialogLayout.setHorizontalGroup(
            Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Agregar_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Agregar_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_recto, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_recto_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_recto_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_recto_desf_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_empalme, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Agregar_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_empalme_inc, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_destijere, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_destijere_inc, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_destijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_empalme_macho, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Agregar_dialogLayout.createSequentialGroup()
                        .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(Agregar_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_empalme_hembra, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_agujero_platina, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Agregar_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_redondo_alma, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Boton_oblongo_alma_V, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Agregar_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_redondo_ala, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_oblongo_ala_V, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_oblongo_ala_H, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Agregar_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_oblongo_alma_H, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_rectangular_ala, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Boton_salir, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Boton_rectangular_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Agregar_dialogLayout.setVerticalGroup(
            Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Agregar_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(Boton_recto_inc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Boton_recto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Boton_recto_desf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Boton_recto_desf_inc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Boton_empalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(Boton_destijere2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Boton_destijere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Boton_destijere_inc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Boton_empalme_macho, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Boton_empalme_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Boton_empalme_hembra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Boton_redondo_ala, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Boton_oblongo_ala_V, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Boton_oblongo_ala_H, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Boton_agujero_platina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(Agregar_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(Boton_rectangular_ala, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Boton_oblongo_alma_H, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Boton_oblongo_alma_V, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Boton_redondo_alma, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Boton_rectangular_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(Boton_salir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Recto_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("L:");

        SpinnerLargoRecto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLargoRecto.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_recto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_recto.setText("Agregar");
        Boton_ok_recto.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_recto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_rectoActionPerformed(evt);
            }
        });

        Boton_cancel_recto.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_recto.setText("Cancelar");
        Boton_cancel_recto.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_recto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_rectoActionPerformed(evt);
            }
        });

        Panel_Medidas_recto.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_recto.setMinimumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_recto.setPreferredSize(new java.awt.Dimension(500, 500));

        Canvas_recto.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_recto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasRecto.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_rectoLayout = new javax.swing.GroupLayout(Panel_Medidas_recto);
        Panel_Medidas_recto.setLayout(Panel_Medidas_rectoLayout);
        Panel_Medidas_rectoLayout.setHorizontalGroup(
            Panel_Medidas_rectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_rectoLayout.setVerticalGroup(
            Panel_Medidas_rectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Recto_dialogLayout = new javax.swing.GroupLayout(Recto_dialog.getContentPane());
        Recto_dialog.getContentPane().setLayout(Recto_dialogLayout);
        Recto_dialogLayout.setHorizontalGroup(
            Recto_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Recto_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Recto_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Recto_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SpinnerLargoRecto))
                    .addGroup(Recto_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_recto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_recto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addComponent(Panel_Medidas_recto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        Recto_dialogLayout.setVerticalGroup(
            Recto_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Recto_dialogLayout.createSequentialGroup()
                .addGap(242, 242, 242)
                .addGroup(Recto_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(SpinnerLargoRecto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68)
                .addGroup(Recto_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_recto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_recto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Recto_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_recto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Recto_inc_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setText("L1:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setText("L2:");

        SpinnerL1RectoInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL1RectoInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL2RectoInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL2RectoInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_recto_inc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_recto_inc.setText("Agregar");
        Boton_ok_recto_inc.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_recto_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_recto_incActionPerformed(evt);
            }
        });

        Boton_cancel_recto_inc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_recto_inc.setText("Cancelar");
        Boton_cancel_recto_inc.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_recto_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_recto_incActionPerformed(evt);
            }
        });

        Panel_Medidas_recto_inc.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_recto_inc.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_recto_inc.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_recto_inc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasRectoInc.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_recto_incLayout = new javax.swing.GroupLayout(Panel_Medidas_recto_inc);
        Panel_Medidas_recto_inc.setLayout(Panel_Medidas_recto_incLayout);
        Panel_Medidas_recto_incLayout.setHorizontalGroup(
            Panel_Medidas_recto_incLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_inc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_recto_incLayout.setVerticalGroup(
            Panel_Medidas_recto_incLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_inc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Recto_inc_dialogLayout = new javax.swing.GroupLayout(Recto_inc_dialog.getContentPane());
        Recto_inc_dialog.getContentPane().setLayout(Recto_inc_dialogLayout);
        Recto_inc_dialogLayout.setHorizontalGroup(
            Recto_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Recto_inc_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Recto_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Recto_inc_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SpinnerL2RectoInc))
                    .addGroup(Recto_inc_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SpinnerL1RectoInc))
                    .addGroup(Recto_inc_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_recto_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_recto_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(26, 26, 26)
                .addComponent(Panel_Medidas_recto_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Recto_inc_dialogLayout.setVerticalGroup(
            Recto_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Recto_inc_dialogLayout.createSequentialGroup()
                .addGap(199, 199, 199)
                .addGroup(Recto_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(SpinnerL1RectoInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Recto_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(SpinnerL2RectoInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(72, 72, 72)
                .addGroup(Recto_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_recto_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_recto_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Recto_inc_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_recto_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Recto_desf_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setText("LI:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setText("LD:");

        SpinnerLIRectoDesf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLIRectoDesf.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLDRectoDesf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLDRectoDesf.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_recto_desf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_recto_desf.setText("Agregar");
        Boton_ok_recto_desf.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_recto_desf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_recto_desfActionPerformed(evt);
            }
        });

        Boton_cancel_recto_desf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_recto_desf.setText("Cancelar");
        Boton_cancel_recto_desf.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_recto_desf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_recto_desfActionPerformed(evt);
            }
        });

        Panel_Medidas_recto_desf.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_recto_desf.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_recto_desf.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_recto_desf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasRectoDesp.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_recto_desfLayout = new javax.swing.GroupLayout(Panel_Medidas_recto_desf);
        Panel_Medidas_recto_desf.setLayout(Panel_Medidas_recto_desfLayout);
        Panel_Medidas_recto_desfLayout.setHorizontalGroup(
            Panel_Medidas_recto_desfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_desf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_recto_desfLayout.setVerticalGroup(
            Panel_Medidas_recto_desfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_desf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Recto_desf_dialogLayout = new javax.swing.GroupLayout(Recto_desf_dialog.getContentPane());
        Recto_desf_dialog.getContentPane().setLayout(Recto_desf_dialogLayout);
        Recto_desf_dialogLayout.setHorizontalGroup(
            Recto_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Recto_desf_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Recto_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Recto_desf_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SpinnerLIRectoDesf))
                    .addGroup(Recto_desf_dialogLayout.createSequentialGroup()
                        .addGroup(Recto_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(Recto_desf_dialogLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(5, 5, 5)
                                .addComponent(SpinnerLDRectoDesf))
                            .addGroup(Recto_desf_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_ok_recto_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_cancel_recto_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(26, 26, 26)
                .addComponent(Panel_Medidas_recto_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Recto_desf_dialogLayout.setVerticalGroup(
            Recto_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Recto_desf_dialogLayout.createSequentialGroup()
                .addGap(199, 199, 199)
                .addGroup(Recto_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(SpinnerLIRectoDesf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Recto_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(SpinnerLDRectoDesf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(72, 72, 72)
                .addGroup(Recto_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_recto_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_recto_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Recto_desf_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_recto_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Recto_inc_desf_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setText("L1I:");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setText("L2I:");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel22.setText("L1D:");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setText("L2D:");

        SpinnerL1IRectoIncDesf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL1IRectoIncDesf.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL2IRectoIncDesf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL2IRectoIncDesf.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL1DRectoIncDesf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL1DRectoIncDesf.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL2DRectoIncDesf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL2DRectoIncDesf.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_recto_inc_desf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_recto_inc_desf.setText("Agregar");
        Boton_ok_recto_inc_desf.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_recto_inc_desf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_recto_inc_desfActionPerformed(evt);
            }
        });

        Boton_cancel_recto_inc_desf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_recto_inc_desf.setText("Cancelar");
        Boton_cancel_recto_inc_desf.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_recto_inc_desf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_recto_inc_desfActionPerformed(evt);
            }
        });

        Panel_Medidas_recto_inc_desf.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_recto_inc_desf.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_recto_inc_desf.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_recto_inc_desf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasRectoIncDesp.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_recto_inc_desfLayout = new javax.swing.GroupLayout(Panel_Medidas_recto_inc_desf);
        Panel_Medidas_recto_inc_desf.setLayout(Panel_Medidas_recto_inc_desfLayout);
        Panel_Medidas_recto_inc_desfLayout.setHorizontalGroup(
            Panel_Medidas_recto_inc_desfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_inc_desf, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_recto_inc_desfLayout.setVerticalGroup(
            Panel_Medidas_recto_inc_desfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_inc_desf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Recto_inc_desf_dialogLayout = new javax.swing.GroupLayout(Recto_inc_desf_dialog.getContentPane());
        Recto_inc_desf_dialog.getContentPane().setLayout(Recto_inc_desf_dialogLayout);
        Recto_inc_desf_dialogLayout.setHorizontalGroup(
            Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Recto_inc_desf_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Recto_inc_desf_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerL1IRectoIncDesf, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Recto_inc_desf_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_recto_inc_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_recto_inc_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Recto_inc_desf_dialogLayout.createSequentialGroup()
                        .addGroup(Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel24)
                            .addComponent(jLabel21))
                        .addGap(13, 13, 13)
                        .addGroup(Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SpinnerL1DRectoIncDesf, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(SpinnerL2IRectoIncDesf)
                            .addComponent(SpinnerL2DRectoIncDesf))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_recto_inc_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Recto_inc_desf_dialogLayout.setVerticalGroup(
            Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Recto_inc_desf_dialogLayout.createSequentialGroup()
                .addGap(139, 139, 139)
                .addGroup(Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(SpinnerL1IRectoIncDesf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(SpinnerL2IRectoIncDesf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(SpinnerL1DRectoIncDesf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(SpinnerL2DRectoIncDesf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(Recto_inc_desf_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_recto_inc_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_recto_inc_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Recto_inc_desf_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_recto_inc_desf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Empalme_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel123.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel123.setText("X:");

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel25.setText("LI:");

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setText("LD:");

        SpinnerLIEmpalme.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLIEmpalme.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLDEmpalme.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLDEmpalme.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerXEmpalme.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerXEmpalme.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_empalme.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_empalme.setText("Agregar");
        Boton_ok_empalme.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_empalme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_empalmeActionPerformed(evt);
            }
        });

        Boton_cancel_empalme.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_empalme.setText("Cancelar");
        Boton_cancel_empalme.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_empalme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_empalmeActionPerformed(evt);
            }
        });

        Panel_Medidas_empalme.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_empalme.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_empalme.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_empalme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasEmpalme.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_empalmeLayout = new javax.swing.GroupLayout(Panel_Medidas_empalme);
        Panel_Medidas_empalme.setLayout(Panel_Medidas_empalmeLayout);
        Panel_Medidas_empalmeLayout.setHorizontalGroup(
            Panel_Medidas_empalmeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_empalme, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_empalmeLayout.setVerticalGroup(
            Panel_Medidas_empalmeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_empalme, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Empalme_dialogLayout = new javax.swing.GroupLayout(Empalme_dialog.getContentPane());
        Empalme_dialog.getContentPane().setLayout(Empalme_dialogLayout);
        Empalme_dialogLayout.setHorizontalGroup(
            Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Empalme_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Empalme_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SpinnerLIEmpalme))
                    .addGroup(Empalme_dialogLayout.createSequentialGroup()
                        .addGroup(Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(Empalme_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_ok_empalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_cancel_empalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Empalme_dialogLayout.createSequentialGroup()
                                .addGroup(Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(Empalme_dialogLayout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addGap(5, 5, 5))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Empalme_dialogLayout.createSequentialGroup()
                                        .addComponent(jLabel123)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SpinnerXEmpalme)
                                    .addComponent(SpinnerLDEmpalme))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(26, 26, 26)
                .addComponent(Panel_Medidas_empalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Empalme_dialogLayout.setVerticalGroup(
            Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Empalme_dialogLayout.createSequentialGroup()
                .addGap(164, 164, 164)
                .addGroup(Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(SpinnerLIEmpalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(SpinnerLDEmpalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel123)
                    .addComponent(SpinnerXEmpalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68)
                .addGroup(Empalme_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_empalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_empalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Empalme_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_empalme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Empalme_inc_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel28.setText("L1I:");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setText("L2I:");

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setText("L1D:");

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setText("L2D:");

        SpinnerL1IEmpalmeInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL1IEmpalmeInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL2IEmpalmeInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL2IEmpalmeInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL1DEmpalmeInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL1DEmpalmeInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL2DEmpalmeInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL2DEmpalmeInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_empalme_inc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_empalme_inc.setText("Agregar");
        Boton_ok_empalme_inc.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_empalme_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_empalme_incActionPerformed(evt);
            }
        });

        Boton_cancel_empalme_inc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_empalme_inc.setText("Cancelar");
        Boton_cancel_empalme_inc.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_empalme_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_empalme_incActionPerformed(evt);
            }
        });

        Panel_Medidas_empalme_inc.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_empalme_inc.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_recto_empalme_inc.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_recto_empalme_inc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasEmpalmeInc.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_empalme_incLayout = new javax.swing.GroupLayout(Panel_Medidas_empalme_inc);
        Panel_Medidas_empalme_inc.setLayout(Panel_Medidas_empalme_incLayout);
        Panel_Medidas_empalme_incLayout.setHorizontalGroup(
            Panel_Medidas_empalme_incLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_Medidas_empalme_incLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Canvas_recto_empalme_inc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        Panel_Medidas_empalme_incLayout.setVerticalGroup(
            Panel_Medidas_empalme_incLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_empalme_inc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel124.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel124.setText("X:");

        SpinnerXEmpalmeInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerXEmpalmeInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        javax.swing.GroupLayout Empalme_inc_dialogLayout = new javax.swing.GroupLayout(Empalme_inc_dialog.getContentPane());
        Empalme_inc_dialog.getContentPane().setLayout(Empalme_inc_dialogLayout);
        Empalme_inc_dialogLayout.setHorizontalGroup(
            Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Empalme_inc_dialogLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Empalme_inc_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_empalme_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_empalme_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(Empalme_inc_dialogLayout.createSequentialGroup()
                            .addComponent(jLabel28)
                            .addGap(18, 18, 18)
                            .addComponent(SpinnerL1IEmpalmeInc, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(Empalme_inc_dialogLayout.createSequentialGroup()
                            .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel30)
                                .addComponent(jLabel31)
                                .addComponent(jLabel29))
                            .addGap(13, 13, 13)
                            .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(SpinnerL1DEmpalmeInc)
                                .addComponent(SpinnerL2IEmpalmeInc)
                                .addComponent(SpinnerL2DEmpalmeInc, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Empalme_inc_dialogLayout.createSequentialGroup()
                            .addComponent(jLabel124)
                            .addGap(13, 13, 13)
                            .addComponent(SpinnerXEmpalmeInc, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Panel_Medidas_empalme_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Empalme_inc_dialogLayout.setVerticalGroup(
            Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Empalme_inc_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_empalme_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(Empalme_inc_dialogLayout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(SpinnerL1IEmpalmeInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(SpinnerL2IEmpalmeInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(SpinnerL1DEmpalmeInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(SpinnerL2DEmpalmeInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel124)
                    .addComponent(SpinnerXEmpalmeInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(63, 63, 63)
                .addGroup(Empalme_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_empalme_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_empalme_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Destijere_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setText("LI:");

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel33.setText("CI:");

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel34.setText("LD:");

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel35.setText("CD:");

        SpinnerLIDestijere.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLIDestijere.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerCIDestijere.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerCIDestijere.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLDDestijere.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLDDestijere.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerCDDestijere.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerCDDestijere.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_destijere.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_destijere.setText("Agregar");
        Boton_ok_destijere.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_destijere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_destijereActionPerformed(evt);
            }
        });

        Boton_cancel_destijere.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_destijere.setText("Cancelar");
        Boton_cancel_destijere.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_destijere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_destijereActionPerformed(evt);
            }
        });

        Panel_Medidas_destijere.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_destijere.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_destijere.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_destijere.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasDestijere.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_destijereLayout = new javax.swing.GroupLayout(Panel_Medidas_destijere);
        Panel_Medidas_destijere.setLayout(Panel_Medidas_destijereLayout);
        Panel_Medidas_destijereLayout.setHorizontalGroup(
            Panel_Medidas_destijereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_destijere, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_destijereLayout.setVerticalGroup(
            Panel_Medidas_destijereLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_destijere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Destijere_dialogLayout = new javax.swing.GroupLayout(Destijere_dialog.getContentPane());
        Destijere_dialog.getContentPane().setLayout(Destijere_dialogLayout);
        Destijere_dialogLayout.setHorizontalGroup(
            Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Destijere_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Destijere_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_destijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_destijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Destijere_dialogLayout.createSequentialGroup()
                            .addComponent(jLabel32)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                            .addComponent(SpinnerLIDestijere, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Destijere_dialogLayout.createSequentialGroup()
                            .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel34)
                                .addComponent(jLabel35)
                                .addComponent(jLabel33))
                            .addGap(13, 13, 13)
                            .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(SpinnerLDDestijere, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addComponent(SpinnerCDDestijere)
                                .addComponent(SpinnerCIDestijere, javax.swing.GroupLayout.Alignment.TRAILING)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_destijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Destijere_dialogLayout.setVerticalGroup(
            Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Destijere_dialogLayout.createSequentialGroup()
                .addGap(139, 139, 139)
                .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(SpinnerLIDestijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(SpinnerCIDestijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(SpinnerLDDestijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(SpinnerCDDestijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(Destijere_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_destijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_destijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Destijere_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_destijere, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Destijere_inc_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel36.setText("L1I:");

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel37.setText("L2I:");

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel38.setText("L1D:");

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel40.setText("L2D:");

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel41.setText("CI:");

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel42.setText("CD:");

        SpinnerL1IDestijereInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL1IDestijereInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL2IDestijereInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL2IDestijereInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerCIDestijereInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerCIDestijereInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL1DDestijereInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL1DDestijereInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerL2DDestijereInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerL2DDestijereInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerCDDestijereInc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerCDDestijereInc.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_destijere_inc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_destijere_inc.setText("Agregar");
        Boton_ok_destijere_inc.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_destijere_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_destijere_incActionPerformed(evt);
            }
        });

        Boton_cancel_destijere_inc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_destijere_inc.setText("Cancelar");
        Boton_cancel_destijere_inc.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_destijere_inc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_destijere_incActionPerformed(evt);
            }
        });

        Panel_Medidas_destijere_inc.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_destijere_inc.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_destijere_inc.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_destijere_inc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasDestijereInc.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_destijere_incLayout = new javax.swing.GroupLayout(Panel_Medidas_destijere_inc);
        Panel_Medidas_destijere_inc.setLayout(Panel_Medidas_destijere_incLayout);
        Panel_Medidas_destijere_incLayout.setHorizontalGroup(
            Panel_Medidas_destijere_incLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_destijere_inc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_destijere_incLayout.setVerticalGroup(
            Panel_Medidas_destijere_incLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_destijere_inc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Destijere_inc_dialogLayout = new javax.swing.GroupLayout(Destijere_inc_dialog.getContentPane());
        Destijere_inc_dialog.getContentPane().setLayout(Destijere_inc_dialogLayout);
        Destijere_inc_dialogLayout.setHorizontalGroup(
            Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Destijere_inc_dialogLayout.createSequentialGroup()
                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Destijere_inc_dialogLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(Destijere_inc_dialogLayout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addGap(18, 18, 18)
                                .addComponent(SpinnerL1IDestijereInc, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Destijere_inc_dialogLayout.createSequentialGroup()
                                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel37)
                                    .addComponent(jLabel41))
                                .addGap(18, 18, 18)
                                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SpinnerCIDestijereInc)
                                    .addComponent(SpinnerL2IDestijereInc, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Destijere_inc_dialogLayout.createSequentialGroup()
                                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel38)
                                    .addComponent(jLabel40)
                                    .addComponent(jLabel42))
                                .addGap(13, 13, 13)
                                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SpinnerCDDestijereInc, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerL2DDestijereInc, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerL1DDestijereInc))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Destijere_inc_dialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Boton_ok_destijere_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_destijere_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addComponent(Panel_Medidas_destijere_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Destijere_inc_dialogLayout.setVerticalGroup(
            Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Destijere_inc_dialogLayout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(SpinnerL1IDestijereInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(SpinnerL2IDestijereInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(SpinnerCIDestijereInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(SpinnerL1DDestijereInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(SpinnerL2DDestijereInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(SpinnerCDDestijereInc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58)
                .addGroup(Destijere_inc_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_destijere_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_destijere_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Destijere_inc_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_destijere_inc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Destijere2_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel43.setText("LII:");

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel44.setText("AII:");

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel45.setText("LSI:");

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel46.setText("ASI:");

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel47.setText("LID:");

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel48.setText("AID:");

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel49.setText("LSD:");

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel50.setText("ASD:");

        SpinnerLIIDestijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLIIDestijere2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerAIIDestijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAIIDestijere2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLSIDestijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLSIDestijere2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerASIDestijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerASIDestijere2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLIDDestijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLIDDestijere2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerAIDDestijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAIDDestijere2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLSDDestijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLSDDestijere2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerASDDestijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerASDDestijere2.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_destijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_destijere2.setText("Agregar");
        Boton_ok_destijere2.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_destijere2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_destijere2ActionPerformed(evt);
            }
        });

        Boton_cancel_destijere2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_destijere2.setText("Cancelar");
        Boton_cancel_destijere2.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_destijere2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_destijere2ActionPerformed(evt);
            }
        });

        Panel_Medidas_destijere2.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_destijere2.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_recto_destijere2.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_recto_destijere2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasDestijere2.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_destijere2Layout = new javax.swing.GroupLayout(Panel_Medidas_destijere2);
        Panel_Medidas_destijere2.setLayout(Panel_Medidas_destijere2Layout);
        Panel_Medidas_destijere2Layout.setHorizontalGroup(
            Panel_Medidas_destijere2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_destijere2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_destijere2Layout.setVerticalGroup(
            Panel_Medidas_destijere2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_destijere2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Destijere2_dialogLayout = new javax.swing.GroupLayout(Destijere2_dialog.getContentPane());
        Destijere2_dialog.getContentPane().setLayout(Destijere2_dialogLayout);
        Destijere2_dialogLayout.setHorizontalGroup(
            Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Destijere2_dialogLayout.createSequentialGroup()
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Destijere2_dialogLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel43)
                            .addComponent(jLabel44)
                            .addComponent(jLabel45)
                            .addComponent(jLabel46)
                            .addComponent(jLabel47)
                            .addComponent(jLabel48)
                            .addComponent(jLabel49)
                            .addComponent(jLabel50))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(SpinnerLIIDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SpinnerAIIDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SpinnerLSIDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SpinnerASIDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SpinnerLIDDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SpinnerAIDDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SpinnerLSDDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SpinnerASDDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(Destijere2_dialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Boton_ok_destijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_destijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(Panel_Medidas_destijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Destijere2_dialogLayout.setVerticalGroup(
            Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Destijere2_dialogLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(SpinnerLIIDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(SpinnerAIIDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(SpinnerLSIDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(SpinnerASIDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(SpinnerLIDDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(SpinnerAIDDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(SpinnerLSDDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(SpinnerASDDestijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Destijere2_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_destijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_destijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(88, 88, 88))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Destijere2_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_destijere2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Empalme_macho_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel69.setText("A1:");

        jLabel75.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel75.setText("A2:");

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel76.setText("C:");

        SpinnerA1EmpalmeMacho.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerA1EmpalmeMacho.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerA2EmpalmeMacho.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerA2EmpalmeMacho.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerCEmpalmeMacho.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerCEmpalmeMacho.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_empalme_macho.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_empalme_macho.setText("Agregar");
        Boton_ok_empalme_macho.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_empalme_macho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_empalme_machoActionPerformed(evt);
            }
        });

        Boton_cancel_empalme_macho.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_empalme_macho.setText("Cancelar");
        Boton_cancel_empalme_macho.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_empalme_macho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_empalme_machoActionPerformed(evt);
            }
        });

        Panel_Medidas_empalme_macho.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_empalme_macho.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_empalme_macho.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_empalme_macho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasEmpalmeMacho.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_empalme_machoLayout = new javax.swing.GroupLayout(Panel_Medidas_empalme_macho);
        Panel_Medidas_empalme_macho.setLayout(Panel_Medidas_empalme_machoLayout);
        Panel_Medidas_empalme_machoLayout.setHorizontalGroup(
            Panel_Medidas_empalme_machoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_empalme_macho, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_empalme_machoLayout.setVerticalGroup(
            Panel_Medidas_empalme_machoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_empalme_macho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Empalme_macho_dialogLayout = new javax.swing.GroupLayout(Empalme_macho_dialog.getContentPane());
        Empalme_macho_dialog.getContentPane().setLayout(Empalme_macho_dialogLayout);
        Empalme_macho_dialogLayout.setHorizontalGroup(
            Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Empalme_macho_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Empalme_macho_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_empalme_macho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_empalme_macho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Empalme_macho_dialogLayout.createSequentialGroup()
                        .addGroup(Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel76)
                            .addComponent(jLabel75)
                            .addComponent(jLabel69))
                        .addGap(20, 20, 20)
                        .addGroup(Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpinnerA1EmpalmeMacho, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerA2EmpalmeMacho, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerCEmpalmeMacho, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(26, 26, 26)
                .addComponent(Panel_Medidas_empalme_macho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Empalme_macho_dialogLayout.setVerticalGroup(
            Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Empalme_macho_dialogLayout.createSequentialGroup()
                .addGap(139, 139, 139)
                .addGroup(Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(SpinnerA1EmpalmeMacho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75)
                    .addComponent(SpinnerA2EmpalmeMacho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(SpinnerCEmpalmeMacho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(93, 93, 93)
                .addGroup(Empalme_macho_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_empalme_macho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_empalme_macho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Empalme_macho_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_empalme_macho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Empalme_hembra_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel77.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel77.setText("A1:");

        jLabel78.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel78.setText("A2:");

        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel79.setText("C:");

        jLabel80.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel80.setText("L:");

        SpinnerLEmpalmeHembra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLEmpalmeHembra.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerA1EmpalmeHembra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerA1EmpalmeHembra.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerA2EmpalmeHembra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerA2EmpalmeHembra.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerCEmpalmeHembra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerCEmpalmeHembra.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_empalme_hembra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_empalme_hembra.setText("Agregar");
        Boton_ok_empalme_hembra.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_empalme_hembra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_empalme_hembraActionPerformed(evt);
            }
        });

        Boton_cancel_empalme_hembra.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_empalme_hembra.setText("Cancelar");
        Boton_cancel_empalme_hembra.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_empalme_hembra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_empalme_hembraActionPerformed(evt);
            }
        });

        Panel_Medidas_empalme_hembra.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_empalme_hembra.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_empalme_hembra.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_empalme_hembra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasEmpalmeHembra.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_empalme_hembraLayout = new javax.swing.GroupLayout(Panel_Medidas_empalme_hembra);
        Panel_Medidas_empalme_hembra.setLayout(Panel_Medidas_empalme_hembraLayout);
        Panel_Medidas_empalme_hembraLayout.setHorizontalGroup(
            Panel_Medidas_empalme_hembraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_empalme_hembra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_empalme_hembraLayout.setVerticalGroup(
            Panel_Medidas_empalme_hembraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_empalme_hembra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Empalme_hembra_dialogLayout = new javax.swing.GroupLayout(Empalme_hembra_dialog.getContentPane());
        Empalme_hembra_dialog.getContentPane().setLayout(Empalme_hembra_dialogLayout);
        Empalme_hembra_dialogLayout.setHorizontalGroup(
            Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Empalme_hembra_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Empalme_hembra_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel80)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SpinnerLEmpalmeHembra, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Empalme_hembra_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_empalme_hembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_empalme_hembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(Empalme_hembra_dialogLayout.createSequentialGroup()
                        .addGroup(Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel79)
                            .addComponent(jLabel78)
                            .addComponent(jLabel77))
                        .addGap(20, 20, 20)
                        .addGroup(Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpinnerA1EmpalmeHembra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SpinnerA2EmpalmeHembra, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerCEmpalmeHembra, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(26, 26, 26)
                .addComponent(Panel_Medidas_empalme_hembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Empalme_hembra_dialogLayout.setVerticalGroup(
            Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Empalme_hembra_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_empalme_hembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(Empalme_hembra_dialogLayout.createSequentialGroup()
                .addGap(128, 128, 128)
                .addGroup(Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel80)
                    .addComponent(SpinnerLEmpalmeHembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(SpinnerA1EmpalmeHembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(SpinnerA2EmpalmeHembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79)
                    .addComponent(SpinnerCEmpalmeHembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(93, 93, 93)
                .addGroup(Empalme_hembra_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_empalme_hembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_empalme_hembra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Agujero_platina_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel81.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel81.setText("YII:");

        jLabel82.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel82.setText("LII:");

        jLabel83.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel83.setText("AII:");

        jLabel84.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel84.setText("YSI:");

        jLabel85.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel85.setText("LSI:");

        jLabel86.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel86.setText("ASI:");

        jLabel87.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel87.setText("YID:");

        jLabel88.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel88.setText("LID:");

        jLabel89.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel89.setText("AID:");

        jLabel90.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel90.setText("YSD:");

        jLabel91.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel91.setText("LSD:");

        jLabel92.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel92.setText("ASD:");

        SpinnerYIIAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYIIAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLIIAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLIIAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerAIIAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAIIAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerYSIAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYSIAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLSIAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLSIAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerASIAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerASIAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerYIDAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYIDAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLIDAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLIDAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerAIDAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAIDAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerYSDAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYSDAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLSDAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLSDAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerASDAgujeroPlatina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerASDAgujeroPlatina.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_agujero_platina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_agujero_platina.setText("Agregar");
        Boton_ok_agujero_platina.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_agujero_platina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_agujero_platinaActionPerformed(evt);
            }
        });

        Boton_cancel_agujero_platina.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_agujero_platina.setText("Cancelar");
        Boton_cancel_agujero_platina.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_agujero_platina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_agujero_platinaActionPerformed(evt);
            }
        });

        Panel_Medidas_agujero_platina.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_agujero_platina.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_recto_agujero_platina.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_recto_agujero_platina.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasAgujeroPlatina.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_agujero_platinaLayout = new javax.swing.GroupLayout(Panel_Medidas_agujero_platina);
        Panel_Medidas_agujero_platina.setLayout(Panel_Medidas_agujero_platinaLayout);
        Panel_Medidas_agujero_platinaLayout.setHorizontalGroup(
            Panel_Medidas_agujero_platinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_agujero_platina, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_agujero_platinaLayout.setVerticalGroup(
            Panel_Medidas_agujero_platinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_recto_agujero_platina, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Agujero_platina_dialogLayout = new javax.swing.GroupLayout(Agujero_platina_dialog.getContentPane());
        Agujero_platina_dialog.getContentPane().setLayout(Agujero_platina_dialogLayout);
        Agujero_platina_dialogLayout.setHorizontalGroup(
            Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Agujero_platina_dialogLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Agujero_platina_dialogLayout.createSequentialGroup()
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel81)
                            .addComponent(jLabel82)
                            .addComponent(jLabel83)
                            .addComponent(jLabel84)
                            .addComponent(jLabel85)
                            .addComponent(jLabel86)
                            .addComponent(jLabel87)
                            .addComponent(jLabel88)
                            .addComponent(jLabel89)
                            .addComponent(jLabel90)
                            .addComponent(jLabel91)
                            .addComponent(jLabel92))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpinnerYIIAgujeroPlatina)
                            .addComponent(SpinnerLIIAgujeroPlatina)
                            .addComponent(SpinnerAIIAgujeroPlatina)
                            .addComponent(SpinnerYSIAgujeroPlatina)
                            .addComponent(SpinnerLSIAgujeroPlatina)
                            .addComponent(SpinnerASIAgujeroPlatina)
                            .addComponent(SpinnerYIDAgujeroPlatina)
                            .addComponent(SpinnerLIDAgujeroPlatina)
                            .addComponent(SpinnerAIDAgujeroPlatina)
                            .addComponent(SpinnerYSDAgujeroPlatina)
                            .addComponent(SpinnerLSDAgujeroPlatina)
                            .addComponent(SpinnerASDAgujeroPlatina))
                        .addGap(33, 33, 33)
                        .addComponent(Panel_Medidas_agujero_platina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36))
                    .addGroup(Agujero_platina_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_agujero_platina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_agujero_platina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(562, 562, 562))))
        );
        Agujero_platina_dialogLayout.setVerticalGroup(
            Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Agujero_platina_dialogLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Agujero_platina_dialogLayout.createSequentialGroup()
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel81)
                            .addComponent(SpinnerYIIAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel82)
                            .addComponent(SpinnerLIIAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel83)
                            .addComponent(SpinnerAIIAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel84)
                            .addComponent(SpinnerYSIAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel85)
                            .addComponent(SpinnerLSIAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel86)
                            .addComponent(SpinnerASIAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel87)
                            .addComponent(SpinnerYIDAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel88)
                            .addComponent(SpinnerLIDAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel89)
                            .addComponent(SpinnerAIDAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel90)
                            .addComponent(SpinnerYSDAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel91)
                            .addComponent(SpinnerLSDAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel92)
                            .addComponent(SpinnerASDAgujeroPlatina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(Agujero_platina_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Boton_ok_agujero_platina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Boton_cancel_agujero_platina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(Panel_Medidas_agujero_platina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );

        Redondo_ala_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel93.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel93.setText("Y:");

        jLabel94.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel94.setText("Z:");

        jLabel95.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel95.setText("D:");

        SpinnerYRedondoAla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYRedondoAla.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 1.0d));

        SpinnerZRedondoAla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerZRedondoAla.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 1.0d));

        SpinnerDRedondoAla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerDRedondoAla.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 1.0d));

        Boton_ok_redondo_ala.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_redondo_ala.setText("Agregar");
        Boton_ok_redondo_ala.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_redondo_ala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_redondo_alaActionPerformed(evt);
            }
        });

        Boton_cancel_redondo_ala.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_redondo_ala.setText("Cancelar");
        Boton_cancel_redondo_ala.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_redondo_ala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_redondo_alaActionPerformed(evt);
            }
        });

        Panel_Medidas_redondo_ala.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_redondo_ala.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_redondo_ala.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_redondo_ala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasRedondoAla.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_redondo_alaLayout = new javax.swing.GroupLayout(Panel_Medidas_redondo_ala);
        Panel_Medidas_redondo_ala.setLayout(Panel_Medidas_redondo_alaLayout);
        Panel_Medidas_redondo_alaLayout.setHorizontalGroup(
            Panel_Medidas_redondo_alaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_redondo_ala, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_redondo_alaLayout.setVerticalGroup(
            Panel_Medidas_redondo_alaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_redondo_ala, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Check_izq_redondo_ala.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Check_izq_redondo_ala.setSelected(true);
        Check_izq_redondo_ala.setText("Ala Izquierda");

        Check_der_redondo_ala.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Check_der_redondo_ala.setText("Ala Derecha");

        javax.swing.GroupLayout Redondo_ala_dialogLayout = new javax.swing.GroupLayout(Redondo_ala_dialog.getContentPane());
        Redondo_ala_dialog.getContentPane().setLayout(Redondo_ala_dialogLayout);
        Redondo_ala_dialogLayout.setHorizontalGroup(
            Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Redondo_ala_dialogLayout.createSequentialGroup()
                .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Redondo_ala_dialogLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Redondo_ala_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_ok_redondo_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_cancel_redondo_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Redondo_ala_dialogLayout.createSequentialGroup()
                                .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel95)
                                    .addComponent(jLabel94)
                                    .addComponent(jLabel93))
                                .addGap(20, 20, 20)
                                .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SpinnerYRedondoAla, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerZRedondoAla, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerDRedondoAla, javax.swing.GroupLayout.Alignment.TRAILING))))
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Redondo_ala_dialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Check_der_redondo_ala)
                            .addComponent(Check_izq_redondo_ala))
                        .addGap(57, 57, 57)))
                .addComponent(Panel_Medidas_redondo_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Redondo_ala_dialogLayout.setVerticalGroup(
            Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Redondo_ala_dialogLayout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(Check_izq_redondo_ala)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Check_der_redondo_ala)
                .addGap(62, 62, 62)
                .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel93)
                    .addComponent(SpinnerYRedondoAla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel94)
                    .addComponent(SpinnerZRedondoAla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel95)
                    .addComponent(SpinnerDRedondoAla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(93, 93, 93)
                .addGroup(Redondo_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_redondo_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_redondo_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Redondo_ala_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_redondo_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Oblongo_ala_v_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel96.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel96.setText("Y:");

        jLabel97.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel97.setText("Z:");

        jLabel98.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel98.setText("D:");

        jLabel99.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel99.setText("L:");

        SpinnerYOblongoAlaV.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYOblongoAlaV.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerZOblongoAlaV.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerZOblongoAlaV.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerDOblongoAlaV.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerDOblongoAlaV.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLOblongoAlaV.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLOblongoAlaV.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_oblongo_ala_v.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_oblongo_ala_v.setText("Agregar");
        Boton_ok_oblongo_ala_v.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_oblongo_ala_v.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_oblongo_ala_vActionPerformed(evt);
            }
        });

        Boton_cancel_oblongo_ala_v.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_oblongo_ala_v.setText("Cancelar");
        Boton_cancel_oblongo_ala_v.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_oblongo_ala_v.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_oblongo_ala_vActionPerformed(evt);
            }
        });

        Panel_Medidas_oblongo_ala_v.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_oblongo_ala_v.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_oblongo_ala_v.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_oblongo_ala_v.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasOblongoVAla.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_oblongo_ala_vLayout = new javax.swing.GroupLayout(Panel_Medidas_oblongo_ala_v);
        Panel_Medidas_oblongo_ala_v.setLayout(Panel_Medidas_oblongo_ala_vLayout);
        Panel_Medidas_oblongo_ala_vLayout.setHorizontalGroup(
            Panel_Medidas_oblongo_ala_vLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_oblongo_ala_v, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_oblongo_ala_vLayout.setVerticalGroup(
            Panel_Medidas_oblongo_ala_vLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_oblongo_ala_v, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Check_izq_oblongo_ala_v.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Check_izq_oblongo_ala_v.setSelected(true);
        Check_izq_oblongo_ala_v.setText("Ala Izquierda");

        Check_der_oblongo_ala_v.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Check_der_oblongo_ala_v.setText("Ala Derecha");

        javax.swing.GroupLayout Oblongo_ala_v_dialogLayout = new javax.swing.GroupLayout(Oblongo_ala_v_dialog.getContentPane());
        Oblongo_ala_v_dialog.getContentPane().setLayout(Oblongo_ala_v_dialogLayout);
        Oblongo_ala_v_dialogLayout.setHorizontalGroup(
            Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Oblongo_ala_v_dialogLayout.createSequentialGroup()
                .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Oblongo_ala_v_dialogLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Oblongo_ala_v_dialogLayout.createSequentialGroup()
                                .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel99)
                                    .addComponent(jLabel98)
                                    .addComponent(jLabel97)
                                    .addComponent(jLabel96))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SpinnerDOblongoAlaV, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerZOblongoAlaV, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerYOblongoAlaV, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerLOblongoAlaV)))
                            .addGroup(Oblongo_ala_v_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_ok_oblongo_ala_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_cancel_oblongo_ala_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27))
                    .addGroup(Oblongo_ala_v_dialogLayout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Check_der_oblongo_ala_v)
                            .addComponent(Check_izq_oblongo_ala_v))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(Panel_Medidas_oblongo_ala_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Oblongo_ala_v_dialogLayout.setVerticalGroup(
            Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Oblongo_ala_v_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_oblongo_ala_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(Oblongo_ala_v_dialogLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(Check_izq_oblongo_ala_v)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Check_der_oblongo_ala_v)
                .addGap(34, 34, 34)
                .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel96)
                    .addComponent(SpinnerYOblongoAlaV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97)
                    .addComponent(SpinnerZOblongoAlaV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel98)
                    .addComponent(SpinnerDOblongoAlaV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel99)
                    .addComponent(SpinnerLOblongoAlaV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(Oblongo_ala_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_oblongo_ala_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_oblongo_ala_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Oblongo_ala_h_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel100.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel100.setText("Y:");

        jLabel101.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel101.setText("Z:");

        jLabel102.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel102.setText("D:");

        jLabel103.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel103.setText("L:");

        SpinnerYOblongoAlaH.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYOblongoAlaH.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerZOblongoAlaH.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerZOblongoAlaH.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerDOblongoAlaH.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerDOblongoAlaH.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLOblongoAlaH.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLOblongoAlaH.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_oblongo_ala_h.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_oblongo_ala_h.setText("Agregar");
        Boton_ok_oblongo_ala_h.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_oblongo_ala_h.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_oblongo_ala_hActionPerformed(evt);
            }
        });

        Boton_cancel_oblongo_ala_h.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_oblongo_ala_h.setText("Cancelar");
        Boton_cancel_oblongo_ala_h.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_oblongo_ala_h.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_oblongo_ala_hActionPerformed(evt);
            }
        });

        Panel_Medidas_oblongo_ala_h.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_oblongo_ala_h.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_oblongo_ala_h.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_oblongo_ala_h.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasOblongoHAla.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_oblongo_ala_hLayout = new javax.swing.GroupLayout(Panel_Medidas_oblongo_ala_h);
        Panel_Medidas_oblongo_ala_h.setLayout(Panel_Medidas_oblongo_ala_hLayout);
        Panel_Medidas_oblongo_ala_hLayout.setHorizontalGroup(
            Panel_Medidas_oblongo_ala_hLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_oblongo_ala_h, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_oblongo_ala_hLayout.setVerticalGroup(
            Panel_Medidas_oblongo_ala_hLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_oblongo_ala_h, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Check_izq_oblongo_ala_h.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Check_izq_oblongo_ala_h.setSelected(true);
        Check_izq_oblongo_ala_h.setText("Ala Izquierda");

        Check_der_oblongo_ala_h.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Check_der_oblongo_ala_h.setText("Ala Derecha");
        Check_der_oblongo_ala_h.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Check_der_oblongo_ala_hActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Oblongo_ala_h_dialogLayout = new javax.swing.GroupLayout(Oblongo_ala_h_dialog.getContentPane());
        Oblongo_ala_h_dialog.getContentPane().setLayout(Oblongo_ala_h_dialogLayout);
        Oblongo_ala_h_dialogLayout.setHorizontalGroup(
            Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Oblongo_ala_h_dialogLayout.createSequentialGroup()
                .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Oblongo_ala_h_dialogLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Oblongo_ala_h_dialogLayout.createSequentialGroup()
                                .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel103)
                                    .addComponent(jLabel102)
                                    .addComponent(jLabel101)
                                    .addComponent(jLabel100))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SpinnerDOblongoAlaH, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerZOblongoAlaH, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerYOblongoAlaH, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerLOblongoAlaH)))
                            .addGroup(Oblongo_ala_h_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_ok_oblongo_ala_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_cancel_oblongo_ala_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Oblongo_ala_h_dialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Check_der_oblongo_ala_h)
                            .addComponent(Check_izq_oblongo_ala_h))
                        .addGap(61, 61, 61)))
                .addComponent(Panel_Medidas_oblongo_ala_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Oblongo_ala_h_dialogLayout.setVerticalGroup(
            Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Oblongo_ala_h_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_oblongo_ala_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(Oblongo_ala_h_dialogLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(Check_izq_oblongo_ala_h)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Check_der_oblongo_ala_h)
                .addGap(37, 37, 37)
                .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel100)
                    .addComponent(SpinnerYOblongoAlaH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel101)
                    .addComponent(SpinnerZOblongoAlaH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102)
                    .addComponent(SpinnerDOblongoAlaH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(SpinnerLOblongoAlaH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(Oblongo_ala_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_oblongo_ala_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_oblongo_ala_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Redondo_alma_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel104.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel104.setText("X:");

        jLabel105.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel105.setText("Y:");

        jLabel106.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel106.setText("D:");

        SpinnerXRedondoAlma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerXRedondoAlma.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerYRedondoAlma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYRedondoAlma.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerDRedondoAlma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerDRedondoAlma.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_redondo_alma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_redondo_alma.setText("Agregar");
        Boton_ok_redondo_alma.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_redondo_alma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_redondo_almaActionPerformed(evt);
            }
        });

        Boton_cancel_redondo_alma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_redondo_alma.setText("Cancelar");
        Boton_cancel_redondo_alma.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_redondo_alma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_redondo_almaActionPerformed(evt);
            }
        });

        Panel_Medidas_redondo_alma.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_redondo_alma.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_redondo_alma.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_redondo_alma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasRedondoAlma.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_redondo_almaLayout = new javax.swing.GroupLayout(Panel_Medidas_redondo_alma);
        Panel_Medidas_redondo_alma.setLayout(Panel_Medidas_redondo_almaLayout);
        Panel_Medidas_redondo_almaLayout.setHorizontalGroup(
            Panel_Medidas_redondo_almaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_redondo_alma, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_redondo_almaLayout.setVerticalGroup(
            Panel_Medidas_redondo_almaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_redondo_alma, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Redondo_alma_dialogLayout = new javax.swing.GroupLayout(Redondo_alma_dialog.getContentPane());
        Redondo_alma_dialog.getContentPane().setLayout(Redondo_alma_dialogLayout);
        Redondo_alma_dialogLayout.setHorizontalGroup(
            Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Redondo_alma_dialogLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Redondo_alma_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_redondo_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_redondo_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(Redondo_alma_dialogLayout.createSequentialGroup()
                        .addGroup(Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel106)
                            .addComponent(jLabel105)
                            .addComponent(jLabel104))
                        .addGap(20, 20, 20)
                        .addGroup(Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpinnerXRedondoAlma, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerYRedondoAlma, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerDRedondoAlma, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(27, 27, 27)
                .addComponent(Panel_Medidas_redondo_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Redondo_alma_dialogLayout.setVerticalGroup(
            Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Redondo_alma_dialogLayout.createSequentialGroup()
                .addGap(193, 193, 193)
                .addGroup(Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel104)
                    .addComponent(SpinnerXRedondoAlma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel105)
                    .addComponent(SpinnerYRedondoAlma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel106)
                    .addComponent(SpinnerDRedondoAlma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(93, 93, 93)
                .addGroup(Redondo_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_redondo_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_redondo_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Redondo_alma_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_redondo_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        Oblongo_alma_v_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel107.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel107.setText("X:");

        jLabel108.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel108.setText("Y:");

        jLabel109.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel109.setText("D:");

        jLabel110.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel110.setText("L:");

        SpinnerXOblongoAlmaV.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerXOblongoAlmaV.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerYOblongoAlmaV.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYOblongoAlmaV.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerDOblongoAlmaV.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerDOblongoAlmaV.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLOblongoAlmaV.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLOblongoAlmaV.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_oblongo_alma_v.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_oblongo_alma_v.setText("Agregar");
        Boton_ok_oblongo_alma_v.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_oblongo_alma_v.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_oblongo_alma_vActionPerformed(evt);
            }
        });

        Boton_cancel_oblongo_alma_v.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_oblongo_alma_v.setText("Cancelar");
        Boton_cancel_oblongo_alma_v.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_oblongo_alma_v.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_oblongo_alma_vActionPerformed(evt);
            }
        });

        Panel_Medidas_oblongo_alma_v.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_oblongo_alma_v.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_oblongo_alma_v.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_oblongo_alma_v.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasOblongoAlmaV.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_oblongo_alma_vLayout = new javax.swing.GroupLayout(Panel_Medidas_oblongo_alma_v);
        Panel_Medidas_oblongo_alma_v.setLayout(Panel_Medidas_oblongo_alma_vLayout);
        Panel_Medidas_oblongo_alma_vLayout.setHorizontalGroup(
            Panel_Medidas_oblongo_alma_vLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_oblongo_alma_v, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_oblongo_alma_vLayout.setVerticalGroup(
            Panel_Medidas_oblongo_alma_vLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_oblongo_alma_v, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Oblongo_alma_v_dialogLayout = new javax.swing.GroupLayout(Oblongo_alma_v_dialog.getContentPane());
        Oblongo_alma_v_dialog.getContentPane().setLayout(Oblongo_alma_v_dialogLayout);
        Oblongo_alma_v_dialogLayout.setHorizontalGroup(
            Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Oblongo_alma_v_dialogLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Oblongo_alma_v_dialogLayout.createSequentialGroup()
                        .addGroup(Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel110)
                            .addComponent(jLabel109)
                            .addComponent(jLabel108)
                            .addComponent(jLabel107))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpinnerDOblongoAlmaV, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerYOblongoAlmaV, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerXOblongoAlmaV, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerLOblongoAlmaV)))
                    .addGroup(Oblongo_alma_v_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_oblongo_alma_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_oblongo_alma_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addComponent(Panel_Medidas_oblongo_alma_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Oblongo_alma_v_dialogLayout.setVerticalGroup(
            Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Oblongo_alma_v_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_oblongo_alma_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(Oblongo_alma_v_dialogLayout.createSequentialGroup()
                .addGap(177, 177, 177)
                .addGroup(Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel107)
                    .addComponent(SpinnerXOblongoAlmaV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel108)
                    .addComponent(SpinnerYOblongoAlmaV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel109)
                    .addComponent(SpinnerDOblongoAlmaV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel110)
                    .addComponent(SpinnerLOblongoAlmaV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(Oblongo_alma_v_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_oblongo_alma_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_oblongo_alma_v, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Oblongo_alma_h_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel111.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel111.setText("X:");

        jLabel112.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel112.setText("Y:");

        jLabel113.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel113.setText("D:");

        jLabel114.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel114.setText("L:");

        SpinnerXOblongoAlmaH.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerXOblongoAlmaH.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerYOblongoAlmaH.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYOblongoAlmaH.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerDOblongoAlmaH.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerDOblongoAlmaH.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLOblongoAlmaH.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLOblongoAlmaH.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_oblongo_alma_h.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_oblongo_alma_h.setText("Agregar");
        Boton_ok_oblongo_alma_h.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_oblongo_alma_h.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_oblongo_alma_hActionPerformed(evt);
            }
        });

        Boton_cancel_oblongo_alma_h.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_oblongo_alma_h.setText("Cancelar");
        Boton_cancel_oblongo_alma_h.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_oblongo_alma_h.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_oblongo_alma_hActionPerformed(evt);
            }
        });

        Panel_Medidas_oblongo_alma_h.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_oblongo_alma_h.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_oblongo_alma_h.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_oblongo_alma_h.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasOblongoAlmaH.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_oblongo_alma_hLayout = new javax.swing.GroupLayout(Panel_Medidas_oblongo_alma_h);
        Panel_Medidas_oblongo_alma_h.setLayout(Panel_Medidas_oblongo_alma_hLayout);
        Panel_Medidas_oblongo_alma_hLayout.setHorizontalGroup(
            Panel_Medidas_oblongo_alma_hLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_oblongo_alma_h, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_oblongo_alma_hLayout.setVerticalGroup(
            Panel_Medidas_oblongo_alma_hLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_oblongo_alma_h, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Oblongo_alma_h_dialogLayout = new javax.swing.GroupLayout(Oblongo_alma_h_dialog.getContentPane());
        Oblongo_alma_h_dialog.getContentPane().setLayout(Oblongo_alma_h_dialogLayout);
        Oblongo_alma_h_dialogLayout.setHorizontalGroup(
            Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Oblongo_alma_h_dialogLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Oblongo_alma_h_dialogLayout.createSequentialGroup()
                        .addGroup(Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel114)
                            .addComponent(jLabel113)
                            .addComponent(jLabel112)
                            .addComponent(jLabel111))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpinnerDOblongoAlmaH, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerYOblongoAlmaH, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerXOblongoAlmaH, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerLOblongoAlmaH)))
                    .addGroup(Oblongo_alma_h_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_oblongo_alma_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_oblongo_alma_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addComponent(Panel_Medidas_oblongo_alma_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Oblongo_alma_h_dialogLayout.setVerticalGroup(
            Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Oblongo_alma_h_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_oblongo_alma_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(Oblongo_alma_h_dialogLayout.createSequentialGroup()
                .addGap(177, 177, 177)
                .addGroup(Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel111)
                    .addComponent(SpinnerXOblongoAlmaH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel112)
                    .addComponent(SpinnerYOblongoAlmaH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel113)
                    .addComponent(SpinnerDOblongoAlmaH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel114)
                    .addComponent(SpinnerLOblongoAlmaH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(Oblongo_alma_h_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_oblongo_alma_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_oblongo_alma_h, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Agujero_rect_ala_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel115.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel115.setText("Y:");

        jLabel116.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel116.setText("Z:");

        jLabel117.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel117.setText("A:");

        jLabel118.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel118.setText("L:");

        SpinnerYAgujeroRectAla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYAgujeroRectAla.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerZAgujeroRectAla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerZAgujeroRectAla.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerAAgujeroRectAla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAAgujeroRectAla.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLAgujeroRectAla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLAgujeroRectAla.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_agujero_rect_ala.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_agujero_rect_ala.setText("Agregar");
        Boton_ok_agujero_rect_ala.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_agujero_rect_ala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_agujero_rect_alaActionPerformed(evt);
            }
        });

        Boton_cancel_agujero_rect_ala.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_agujero_rect_ala.setText("Cancelar");
        Boton_cancel_agujero_rect_ala.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_agujero_rect_ala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_agujero_rect_alaActionPerformed(evt);
            }
        });

        Panel_Medidas_agujero_rect_ala.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_agujero_rect_ala.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_agujero_rect_ala.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_agujero_rect_ala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasAgujeroRectAla.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_agujero_rect_alaLayout = new javax.swing.GroupLayout(Panel_Medidas_agujero_rect_ala);
        Panel_Medidas_agujero_rect_ala.setLayout(Panel_Medidas_agujero_rect_alaLayout);
        Panel_Medidas_agujero_rect_alaLayout.setHorizontalGroup(
            Panel_Medidas_agujero_rect_alaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_agujero_rect_ala, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_agujero_rect_alaLayout.setVerticalGroup(
            Panel_Medidas_agujero_rect_alaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_agujero_rect_ala, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        Check_izq_agujero_rect_ala.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Check_izq_agujero_rect_ala.setSelected(true);
        Check_izq_agujero_rect_ala.setText("Ala Izquierda");

        Check_der_agujero_rect_ala.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Check_der_agujero_rect_ala.setText("Ala Derecha");
        Check_der_agujero_rect_ala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Check_der_agujero_rect_alaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Agujero_rect_ala_dialogLayout = new javax.swing.GroupLayout(Agujero_rect_ala_dialog.getContentPane());
        Agujero_rect_ala_dialog.getContentPane().setLayout(Agujero_rect_ala_dialogLayout);
        Agujero_rect_ala_dialogLayout.setHorizontalGroup(
            Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Agujero_rect_ala_dialogLayout.createSequentialGroup()
                .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Agujero_rect_ala_dialogLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Agujero_rect_ala_dialogLayout.createSequentialGroup()
                                .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel118)
                                    .addComponent(jLabel117)
                                    .addComponent(jLabel116)
                                    .addComponent(jLabel115))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SpinnerAAgujeroRectAla, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerZAgujeroRectAla, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerYAgujeroRectAla, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(SpinnerLAgujeroRectAla)))
                            .addGroup(Agujero_rect_ala_dialogLayout.createSequentialGroup()
                                .addComponent(Boton_ok_agujero_rect_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Boton_cancel_agujero_rect_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Agujero_rect_ala_dialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Check_der_agujero_rect_ala)
                            .addComponent(Check_izq_agujero_rect_ala))
                        .addGap(61, 61, 61)))
                .addComponent(Panel_Medidas_agujero_rect_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Agujero_rect_ala_dialogLayout.setVerticalGroup(
            Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Agujero_rect_ala_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_agujero_rect_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(Agujero_rect_ala_dialogLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(Check_izq_agujero_rect_ala)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Check_der_agujero_rect_ala)
                .addGap(37, 37, 37)
                .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel115)
                    .addComponent(SpinnerYAgujeroRectAla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel116)
                    .addComponent(SpinnerZAgujeroRectAla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel117)
                    .addComponent(SpinnerAAgujeroRectAla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel118)
                    .addComponent(SpinnerLAgujeroRectAla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(Agujero_rect_ala_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_agujero_rect_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_agujero_rect_ala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Agujero_rect_alma_dialog.setMinimumSize(new java.awt.Dimension(800, 600));

        jLabel119.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel119.setText("X:");

        jLabel120.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel120.setText("Y:");

        jLabel121.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel121.setText("A:");

        jLabel122.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel122.setText("L:");

        SpinnerXAgujeroRectAlma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerXAgujeroRectAlma.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerYAgujeroRectAlma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerYAgujeroRectAlma.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerAAgujeroRectAlma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAAgujeroRectAlma.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        SpinnerLAgujeroRectAlma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerLAgujeroRectAlma.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 500.0d, 0.1d));

        Boton_ok_agujero_rect_alma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_ok_agujero_rect_alma.setText("Agregar");
        Boton_ok_agujero_rect_alma.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_ok_agujero_rect_alma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_ok_agujero_rect_almaActionPerformed(evt);
            }
        });

        Boton_cancel_agujero_rect_alma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        Boton_cancel_agujero_rect_alma.setText("Cancelar");
        Boton_cancel_agujero_rect_alma.setPreferredSize(new java.awt.Dimension(100, 31));
        Boton_cancel_agujero_rect_alma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton_cancel_agujero_rect_almaActionPerformed(evt);
            }
        });

        Panel_Medidas_agujero_rect_alma.setMaximumSize(new java.awt.Dimension(500, 500));
        Panel_Medidas_agujero_rect_alma.setMinimumSize(new java.awt.Dimension(500, 500));

        Canvas_agujero_rect_alma.setBackground(new java.awt.Color(255, 255, 255));
        Canvas_agujero_rect_alma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/MedidasAgujeroRectAlma.png"))); // NOI18N

        javax.swing.GroupLayout Panel_Medidas_agujero_rect_almaLayout = new javax.swing.GroupLayout(Panel_Medidas_agujero_rect_alma);
        Panel_Medidas_agujero_rect_alma.setLayout(Panel_Medidas_agujero_rect_almaLayout);
        Panel_Medidas_agujero_rect_almaLayout.setHorizontalGroup(
            Panel_Medidas_agujero_rect_almaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_agujero_rect_alma, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_Medidas_agujero_rect_almaLayout.setVerticalGroup(
            Panel_Medidas_agujero_rect_almaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Canvas_agujero_rect_alma, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Agujero_rect_alma_dialogLayout = new javax.swing.GroupLayout(Agujero_rect_alma_dialog.getContentPane());
        Agujero_rect_alma_dialog.getContentPane().setLayout(Agujero_rect_alma_dialogLayout);
        Agujero_rect_alma_dialogLayout.setHorizontalGroup(
            Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Agujero_rect_alma_dialogLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Agujero_rect_alma_dialogLayout.createSequentialGroup()
                        .addGroup(Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel122)
                            .addComponent(jLabel121)
                            .addComponent(jLabel120)
                            .addComponent(jLabel119))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpinnerAAgujeroRectAlma, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerYAgujeroRectAlma, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerXAgujeroRectAlma, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(SpinnerLAgujeroRectAlma)))
                    .addGroup(Agujero_rect_alma_dialogLayout.createSequentialGroup()
                        .addComponent(Boton_ok_agujero_rect_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Boton_cancel_agujero_rect_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addComponent(Panel_Medidas_agujero_rect_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        Agujero_rect_alma_dialogLayout.setVerticalGroup(
            Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Agujero_rect_alma_dialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Panel_Medidas_agujero_rect_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(Agujero_rect_alma_dialogLayout.createSequentialGroup()
                .addGap(150, 150, 150)
                .addGroup(Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel119)
                    .addComponent(SpinnerXAgujeroRectAlma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel120)
                    .addComponent(SpinnerYAgujeroRectAlma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel121)
                    .addComponent(SpinnerAAgujeroRectAlma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel122)
                    .addComponent(SpinnerLAgujeroRectAlma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(Agujero_rect_alma_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Boton_ok_agujero_rect_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Boton_cancel_agujero_rect_alma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 600));

        PanelTabs.setBackground(new java.awt.Color(204, 204, 204));
        PanelTabs.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        PanelTabs.setMaximumSize(new java.awt.Dimension(800, 600));
        PanelTabs.setMinimumSize(new java.awt.Dimension(800, 600));
        PanelTabs.setPreferredSize(new java.awt.Dimension(800, 600));

        PanelMaterial.setBackground(new java.awt.Color(255, 255, 153));
        PanelMaterial.setMaximumSize(new java.awt.Dimension(795, 503));
        PanelMaterial.setMinimumSize(new java.awt.Dimension(795, 503));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("Tipo de perfil:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("DEFINICION DEL MATERIAL");

        PanelTipoPerfil.setBackground(new java.awt.Color(255, 255, 153));
        PanelTipoPerfil.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PanelTipoPerfil.setMaximumSize(new java.awt.Dimension(770, 400));
        PanelTipoPerfil.setMinimumSize(new java.awt.Dimension(770, 400));
        PanelTipoPerfil.setPreferredSize(new java.awt.Dimension(770, 400));
        PanelTipoPerfil.setLayout(new java.awt.CardLayout());

        PanelIPEHEB.setBackground(new java.awt.Color(255, 255, 153));

        SpinnerAnchoIPEHEB.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAnchoIPEHEB.setModel(new javax.swing.SpinnerNumberModel(100.0d, 80.0d, 600.0d, 0.1d));

        SpinnerAltoIPEHEB.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAltoIPEHEB.setModel(new javax.swing.SpinnerNumberModel(100.0d, 50.0d, 500.0d, 0.1d));

        SpinnerRadioIPEHEB.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerRadioIPEHEB.setModel(new javax.swing.SpinnerNumberModel(12.0d, 5.0d, 30.0d, 0.1d));

        SpinnerEAlaIPEHEB.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerEAlaIPEHEB.setModel(new javax.swing.SpinnerNumberModel(10.0d, 5.0d, 25.0d, 0.1d));

        SpinnerEAlmaIPEHEB.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerEAlmaIPEHEB.setModel(new javax.swing.SpinnerNumberModel(6.0d, 3.0d, 25.0d, 0.1d));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("H:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("B:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("R:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("E:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("T:");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(326, 289));
        jPanel1.setMinimumSize(new java.awt.Dimension(326, 289));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/HEB.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        BotonIPEHEB.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonIPEHEB.setText("Actualizar");
        BotonIPEHEB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonIPEHEBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelIPEHEBLayout = new javax.swing.GroupLayout(PanelIPEHEB);
        PanelIPEHEB.setLayout(PanelIPEHEBLayout);
        PanelIPEHEBLayout.setHorizontalGroup(
            PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelIPEHEBLayout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelIPEHEBLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerAnchoIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelIPEHEBLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerAltoIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelIPEHEBLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerRadioIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelIPEHEBLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerEAlaIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelIPEHEBLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerEAlmaIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BotonIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
        );
        PanelIPEHEBLayout.setVerticalGroup(
            PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelIPEHEBLayout.createSequentialGroup()
                .addContainerGap(55, Short.MAX_VALUE)
                .addGroup(PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelIPEHEBLayout.createSequentialGroup()
                        .addGroup(PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerAnchoIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerAltoIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerRadioIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerEAlaIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(PanelIPEHEBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerEAlmaIPEHEB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BotonIPEHEB))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
        );

        PanelTipoPerfil.add(PanelIPEHEB, "card2");

        PanelIPN.setBackground(new java.awt.Color(255, 255, 153));

        SpinnerAnchoIPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAnchoIPN.setModel(new javax.swing.SpinnerNumberModel(100.0d, 80.0d, 600.0d, 0.1d));

        SpinnerAltoIPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAltoIPN.setModel(new javax.swing.SpinnerNumberModel(50.0d, 40.0d, 500.0d, 0.1d));

        SpinnerRadioIPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerRadioIPN.setModel(new javax.swing.SpinnerNumberModel(4.0d, 3.0d, 25.0d, 0.1d));

        SpinnerRadio1IPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerRadio1IPN.setModel(new javax.swing.SpinnerNumberModel(2.0d, 2.0d, 20.0d, 0.1d));

        SpinnerEAlaIPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerEAlaIPN.setModel(new javax.swing.SpinnerNumberModel(6.0d, 5.0d, 30.0d, 0.1d));

        SpinnerEAlmaIPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerEAlmaIPN.setModel(new javax.swing.SpinnerNumberModel(4.0d, 3.0d, 25.0d, 0.1d));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setText("T:");

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel55.setText("B:");

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel56.setText("R:");

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel57.setText("H:");

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel58.setText("E:");

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel66.setText("R1:");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(326, 289));
        jPanel2.setMinimumSize(new java.awt.Dimension(326, 289));

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/IPN.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        BotonIPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonIPN.setText("Actualizar");
        BotonIPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonIPNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelIPNLayout = new javax.swing.GroupLayout(PanelIPN);
        PanelIPN.setLayout(PanelIPNLayout);
        PanelIPNLayout.setHorizontalGroup(
            PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelIPNLayout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(PanelIPNLayout.createSequentialGroup()
                            .addComponent(jLabel57)
                            .addGap(18, 18, 18)
                            .addComponent(SpinnerAnchoIPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(PanelIPNLayout.createSequentialGroup()
                            .addComponent(jLabel55)
                            .addGap(18, 18, 18)
                            .addComponent(SpinnerAltoIPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(PanelIPNLayout.createSequentialGroup()
                            .addComponent(jLabel56)
                            .addGap(18, 18, 18)
                            .addComponent(SpinnerRadioIPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelIPNLayout.createSequentialGroup()
                        .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(PanelIPNLayout.createSequentialGroup()
                                .addComponent(jLabel66)
                                .addGap(18, 18, 18)
                                .addComponent(SpinnerRadio1IPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(PanelIPNLayout.createSequentialGroup()
                                    .addComponent(jLabel58)
                                    .addGap(18, 18, 18)
                                    .addComponent(SpinnerEAlaIPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(PanelIPNLayout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addGap(18, 18, 18)
                                    .addComponent(SpinnerEAlmaIPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(BotonIPN, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(2, 2, 2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
        );
        PanelIPNLayout.setVerticalGroup(
            PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelIPNLayout.createSequentialGroup()
                .addContainerGap(55, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
            .addGroup(PanelIPNLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerAnchoIPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57))
                .addGap(18, 18, 18)
                .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerAltoIPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addGap(18, 18, 18)
                .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerRadioIPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56))
                .addGap(18, 18, 18)
                .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerRadio1IPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66))
                .addGap(18, 18, 18)
                .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerEAlaIPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58))
                .addGap(18, 18, 18)
                .addGroup(PanelIPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerEAlmaIPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(26, 26, 26)
                .addComponent(BotonIPN)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelTipoPerfil.add(PanelIPN, "card2");

        PanelUPN.setBackground(new java.awt.Color(255, 255, 153));

        SpinnerAnchoUPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAnchoUPN.setModel(new javax.swing.SpinnerNumberModel(100.0d, 80.0d, 600.0d, 0.1d));

        SpinnerAltoUPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAltoUPN.setModel(new javax.swing.SpinnerNumberModel(50.0d, 40.0d, 500.0d, 0.1d));

        SpinnerRadioUPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerRadioUPN.setModel(new javax.swing.SpinnerNumberModel(8.0d, 6.0d, 20.0d, 0.1d));

        SpinnerRadio1UPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerRadio1UPN.setModel(new javax.swing.SpinnerNumberModel(4.0d, 4.0d, 20.0d, 0.1d));

        SpinnerEAlaUPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerEAlaUPN.setModel(new javax.swing.SpinnerNumberModel(6.0d, 6.0d, 20.0d, 0.1d));

        SpinnerEAlmaUPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerEAlmaUPN.setModel(new javax.swing.SpinnerNumberModel(6.0d, 6.0d, 15.0d, 0.1d));

        jLabel59.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel59.setText("R:");

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel60.setText("H:");

        jLabel61.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel61.setText("T:");

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel62.setText("E:");

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel64.setText("B:");

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel65.setText("R1:");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setMaximumSize(new java.awt.Dimension(326, 289));
        jPanel3.setMinimumSize(new java.awt.Dimension(326, 289));

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/UPN.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        BotonUPN.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonUPN.setText("Actualizar");
        BotonUPN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonUPNActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelUPNLayout = new javax.swing.GroupLayout(PanelUPN);
        PanelUPN.setLayout(PanelUPNLayout);
        PanelUPNLayout.setHorizontalGroup(
            PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelUPNLayout.createSequentialGroup()
                .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelUPNLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(PanelUPNLayout.createSequentialGroup()
                                    .addComponent(jLabel60)
                                    .addGap(18, 18, 18)
                                    .addComponent(SpinnerAnchoUPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(PanelUPNLayout.createSequentialGroup()
                                    .addComponent(jLabel64)
                                    .addGap(18, 18, 18)
                                    .addComponent(SpinnerAltoUPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(PanelUPNLayout.createSequentialGroup()
                                    .addComponent(jLabel59)
                                    .addGap(18, 18, 18)
                                    .addComponent(SpinnerRadioUPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(PanelUPNLayout.createSequentialGroup()
                                    .addComponent(jLabel62)
                                    .addGap(18, 18, 18)
                                    .addComponent(SpinnerEAlaUPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(PanelUPNLayout.createSequentialGroup()
                                    .addComponent(jLabel61)
                                    .addGap(18, 18, 18)
                                    .addComponent(SpinnerEAlmaUPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(PanelUPNLayout.createSequentialGroup()
                                .addComponent(BotonUPN, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1))))
                    .addGroup(PanelUPNLayout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jLabel65)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerRadio1UPN, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
        );
        PanelUPNLayout.setVerticalGroup(
            PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelUPNLayout.createSequentialGroup()
                .addContainerGap(55, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelUPNLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerAnchoUPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60))
                .addGap(18, 18, 18)
                .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerAltoUPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64))
                .addGap(18, 18, 18)
                .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerRadioUPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59))
                .addGap(18, 18, 18)
                .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerRadio1UPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel65))
                .addGap(18, 18, 18)
                .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerEAlaUPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62))
                .addGap(18, 18, 18)
                .addGroup(PanelUPNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SpinnerEAlmaUPN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61))
                .addGap(28, 28, 28)
                .addComponent(BotonUPN)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        PanelTipoPerfil.add(PanelUPN, "card2");

        PanelANGULO.setBackground(new java.awt.Color(255, 255, 153));

        SpinnerAnchoANGULO.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAnchoANGULO.setModel(new javax.swing.SpinnerNumberModel(50.0d, 20.0d, 500.0d, 0.1d));

        SpinnerEANGULO.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerEANGULO.setModel(new javax.swing.SpinnerNumberModel(3.0d, 2.0d, 25.0d, 0.1d));

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel67.setText("A:");

        jLabel71.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel71.setText("E:");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setMaximumSize(new java.awt.Dimension(326, 289));
        jPanel4.setMinimumSize(new java.awt.Dimension(326, 289));

        jLabel70.setBackground(new java.awt.Color(255, 255, 255));
        jLabel70.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/ANGULO.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel70, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel70, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        BotonANGULO.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonANGULO.setText("Actualizar");
        BotonANGULO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonANGULOActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelANGULOLayout = new javax.swing.GroupLayout(PanelANGULO);
        PanelANGULO.setLayout(PanelANGULOLayout);
        PanelANGULOLayout.setHorizontalGroup(
            PanelANGULOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelANGULOLayout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(PanelANGULOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelANGULOLayout.createSequentialGroup()
                        .addComponent(jLabel67)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerAnchoANGULO, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelANGULOLayout.createSequentialGroup()
                        .addComponent(jLabel71)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerEANGULO, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BotonANGULO, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
        );
        PanelANGULOLayout.setVerticalGroup(
            PanelANGULOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelANGULOLayout.createSequentialGroup()
                .addContainerGap(55, Short.MAX_VALUE)
                .addGroup(PanelANGULOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelANGULOLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelANGULOLayout.createSequentialGroup()
                        .addGroup(PanelANGULOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerAnchoANGULO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel67))
                        .addGap(18, 18, 18)
                        .addGroup(PanelANGULOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerEANGULO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel71))
                        .addGap(47, 47, 47)
                        .addComponent(BotonANGULO)
                        .addGap(116, 116, 116))))
        );

        PanelTipoPerfil.add(PanelANGULO, "card2");

        PanelCAJON.setBackground(new java.awt.Color(255, 255, 153));

        SpinnerAnchoCAJON.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAnchoCAJON.setModel(new javax.swing.SpinnerNumberModel(100.0d, 50.0d, 600.0d, 0.1d));

        SpinnerAltoCAJON.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerAltoCAJON.setModel(new javax.swing.SpinnerNumberModel(100.0d, 50.0d, 500.0d, 0.1d));

        SpinnerECAJON.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerECAJON.setModel(new javax.swing.SpinnerNumberModel(3.0d, 3.0d, 25.0d, 0.1d));

        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel68.setText("A:");

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel72.setText("E:");

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel74.setText("B:");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setMaximumSize(new java.awt.Dimension(326, 289));
        jPanel5.setMinimumSize(new java.awt.Dimension(326, 289));

        jLabel73.setBackground(new java.awt.Color(255, 255, 255));
        jLabel73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/CAJON.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel73, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel73, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        BotonCAJON.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonCAJON.setText("Actualizar");
        BotonCAJON.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCAJONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelCAJONLayout = new javax.swing.GroupLayout(PanelCAJON);
        PanelCAJON.setLayout(PanelCAJONLayout);
        PanelCAJONLayout.setHorizontalGroup(
            PanelCAJONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCAJONLayout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addGroup(PanelCAJONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCAJONLayout.createSequentialGroup()
                        .addComponent(jLabel68)
                        .addGap(18, 18, 18)
                        .addComponent(SpinnerAnchoCAJON, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelCAJONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(PanelCAJONLayout.createSequentialGroup()
                            .addComponent(jLabel72)
                            .addGap(18, 18, 18)
                            .addComponent(SpinnerECAJON, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(PanelCAJONLayout.createSequentialGroup()
                            .addComponent(jLabel74)
                            .addGap(18, 18, 18)
                            .addComponent(SpinnerAltoCAJON, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelCAJONLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(BotonCAJON, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
        );
        PanelCAJONLayout.setVerticalGroup(
            PanelCAJONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCAJONLayout.createSequentialGroup()
                .addContainerGap(55, Short.MAX_VALUE)
                .addGroup(PanelCAJONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCAJONLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(PanelCAJONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerAnchoCAJON, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel68))
                        .addGap(18, 18, 18)
                        .addGroup(PanelCAJONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerAltoCAJON, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel74))
                        .addGap(18, 18, 18)
                        .addGroup(PanelCAJONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerECAJON, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel72))
                        .addGap(74, 74, 74)
                        .addComponent(BotonCAJON))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
        );

        PanelTipoPerfil.add(PanelCAJON, "card2");

        ComboTipoPerfil.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ComboTipoPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "HEB/IPE", "IPN", "UPN", "CAJON", "ANGULO" }));
        ComboTipoPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboTipoPerfilActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelMaterialLayout = new javax.swing.GroupLayout(PanelMaterial);
        PanelMaterial.setLayout(PanelMaterialLayout);
        PanelMaterialLayout.setHorizontalGroup(
            PanelMaterialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaterialLayout.createSequentialGroup()
                .addContainerGap(269, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(227, 227, 227))
            .addGroup(PanelMaterialLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ComboTipoPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(393, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaterialLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelTipoPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        PanelMaterialLayout.setVerticalGroup(
            PanelMaterialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMaterialLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelMaterialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(ComboTipoPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelTipoPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        PanelTabs.addTab("", new javax.swing.ImageIcon(getClass().getResource("/ihcam/ico5.png")), PanelMaterial); // NOI18N
        PanelMaterial.getAccessibleContext().setAccessibleDescription("");

        PanelIzq.setBackground(new java.awt.Color(153, 255, 153));
        PanelIzq.setMaximumSize(new java.awt.Dimension(795, 503));
        PanelIzq.setMinimumSize(new java.awt.Dimension(795, 503));
        PanelIzq.setPreferredSize(new java.awt.Dimension(795, 503));

        TituloPanelIzq.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        TituloPanelIzq.setText("CARA LATERAL IZQUIERDA");

        PanelPreviewIzq.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PanelPreviewIzq.setMaximumSize(new java.awt.Dimension(775, 296));
        PanelPreviewIzq.setMinimumSize(new java.awt.Dimension(775, 296));
        PanelPreviewIzq.setPreferredSize(new java.awt.Dimension(775, 296));

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setText("VISTA PREVIA");

        PreviewIzq.setBackground(new java.awt.Color(255, 255, 255));
        PreviewIzq.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PreviewIzq.setMaximumSize(new java.awt.Dimension(753, 249));
        PreviewIzq.setMinimumSize(new java.awt.Dimension(753, 249));
        PreviewIzq.setPreferredSize(new java.awt.Dimension(753, 249));

        javax.swing.GroupLayout PreviewIzqLayout = new javax.swing.GroupLayout(PreviewIzq);
        PreviewIzq.setLayout(PreviewIzqLayout);
        PreviewIzqLayout.setHorizontalGroup(
            PreviewIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PreviewIzqLayout.setVerticalGroup(
            PreviewIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelPreviewIzqLayout = new javax.swing.GroupLayout(PanelPreviewIzq);
        PanelPreviewIzq.setLayout(PanelPreviewIzqLayout);
        PanelPreviewIzqLayout.setHorizontalGroup(
            PanelPreviewIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPreviewIzqLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PreviewIzq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(PanelPreviewIzqLayout.createSequentialGroup()
                .addGap(327, 327, 327)
                .addComponent(jLabel14)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelPreviewIzqLayout.setVerticalGroup(
            PanelPreviewIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPreviewIzqLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PreviewIzq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        PanelOperacionesIzq.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PanelOperacionesIzq.setMaximumSize(new java.awt.Dimension(775, 161));
        PanelOperacionesIzq.setMinimumSize(new java.awt.Dimension(775, 161));
        PanelOperacionesIzq.setPreferredSize(new java.awt.Dimension(775, 161));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setText("OPERACIONES");

        PanelListaIzq.setMaximumSize(new java.awt.Dimension(521, 106));
        PanelListaIzq.setMinimumSize(new java.awt.Dimension(521, 106));
        PanelListaIzq.setPreferredSize(new java.awt.Dimension(521, 106));

        ListaOpIzq.setModel(modelo_izq);
        ListaOpIzq.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ListaOpIzq.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ListaOpIzq.setToolTipText("");
        ListaOpIzq.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                ListaOpIzqValueChanged(evt);
            }
        });
        PanelListaIzq.setViewportView(ListaOpIzq);

        AgregarOpIzq.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        AgregarOpIzq.setText("Agregar");
        AgregarOpIzq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AgregarOpIzqActionPerformed(evt);
            }
        });

        BorrarOpIzq.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BorrarOpIzq.setText("Borrar");
        BorrarOpIzq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarOpIzqActionPerformed(evt);
            }
        });

        BorrarAllIzq.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BorrarAllIzq.setText("Borrar Todo");
        BorrarAllIzq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarAllIzqActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelOperacionesIzqLayout = new javax.swing.GroupLayout(PanelOperacionesIzq);
        PanelOperacionesIzq.setLayout(PanelOperacionesIzqLayout);
        PanelOperacionesIzqLayout.setHorizontalGroup(
            PanelOperacionesIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelOperacionesIzqLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(PanelListaIzq, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelOperacionesIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BorrarOpIzq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BorrarAllIzq, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                    .addComponent(AgregarOpIzq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelOperacionesIzqLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(323, 323, 323))
        );
        PanelOperacionesIzqLayout.setVerticalGroup(
            PanelOperacionesIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOperacionesIzqLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelOperacionesIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelOperacionesIzqLayout.createSequentialGroup()
                        .addComponent(AgregarOpIzq)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BorrarOpIzq)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BorrarAllIzq))
                    .addComponent(PanelListaIzq, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PanelIzqLayout = new javax.swing.GroupLayout(PanelIzq);
        PanelIzq.setLayout(PanelIzqLayout);
        PanelIzqLayout.setHorizontalGroup(
            PanelIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelIzqLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelOperacionesIzq, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelPreviewIzq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelIzqLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TituloPanelIzq)
                .addGap(275, 275, 275))
        );
        PanelIzqLayout.setVerticalGroup(
            PanelIzqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelIzqLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TituloPanelIzq)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelPreviewIzq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelOperacionesIzq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        PanelTabs.addTab("", new javax.swing.ImageIcon(getClass().getResource("/ihcam/ico1.png")), PanelIzq, "Cara Izquierda"); // NOI18N

        PanelSup.setBackground(new java.awt.Color(204, 255, 255));
        PanelSup.setMaximumSize(new java.awt.Dimension(795, 503));
        PanelSup.setMinimumSize(new java.awt.Dimension(795, 503));
        PanelSup.setPreferredSize(new java.awt.Dimension(795, 503));

        TituloPanelSup.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        TituloPanelSup.setText("CARA SUPERIOR");

        PanelPreviewSup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PanelPreviewSup.setMaximumSize(new java.awt.Dimension(775, 296));
        PanelPreviewSup.setMinimumSize(new java.awt.Dimension(775, 296));
        PanelPreviewSup.setPreferredSize(new java.awt.Dimension(775, 296));

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel51.setText("VISTA PREVIA");

        PreviewSup.setBackground(new java.awt.Color(255, 255, 255));
        PreviewSup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PreviewSup.setMaximumSize(new java.awt.Dimension(753, 249));
        PreviewSup.setMinimumSize(new java.awt.Dimension(753, 249));
        PreviewSup.setPreferredSize(new java.awt.Dimension(753, 249));

        javax.swing.GroupLayout PreviewSupLayout = new javax.swing.GroupLayout(PreviewSup);
        PreviewSup.setLayout(PreviewSupLayout);
        PreviewSupLayout.setHorizontalGroup(
            PreviewSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 751, Short.MAX_VALUE)
        );
        PreviewSupLayout.setVerticalGroup(
            PreviewSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelPreviewSupLayout = new javax.swing.GroupLayout(PanelPreviewSup);
        PanelPreviewSup.setLayout(PanelPreviewSupLayout);
        PanelPreviewSupLayout.setHorizontalGroup(
            PanelPreviewSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPreviewSupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PreviewSup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(PanelPreviewSupLayout.createSequentialGroup()
                .addGap(328, 328, 328)
                .addComponent(jLabel51)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelPreviewSupLayout.setVerticalGroup(
            PanelPreviewSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPreviewSupLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PreviewSup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        PanelOperacionesSup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PanelOperacionesSup.setMaximumSize(new java.awt.Dimension(775, 161));
        PanelOperacionesSup.setMinimumSize(new java.awt.Dimension(775, 161));
        PanelOperacionesSup.setPreferredSize(new java.awt.Dimension(775, 161));

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel39.setText("OPERACIONES");

        PanelListaSup.setMaximumSize(new java.awt.Dimension(521, 106));
        PanelListaSup.setMinimumSize(new java.awt.Dimension(521, 106));
        PanelListaSup.setPreferredSize(new java.awt.Dimension(521, 106));

        ListaOpSup.setModel(modelo_sup);
        ListaOpSup.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ListaOpSup.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ListaOpSup.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                ListaOpSupValueChanged(evt);
            }
        });
        PanelListaSup.setViewportView(ListaOpSup);

        AgregarOpSup.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        AgregarOpSup.setText("Agregar");
        AgregarOpSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AgregarOpSupActionPerformed(evt);
            }
        });

        BorrarOpSup.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BorrarOpSup.setText("Borrar");
        BorrarOpSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarOpSupActionPerformed(evt);
            }
        });

        BorrarAllSup.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BorrarAllSup.setText("Borrar Todo");
        BorrarAllSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarAllSupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelOperacionesSupLayout = new javax.swing.GroupLayout(PanelOperacionesSup);
        PanelOperacionesSup.setLayout(PanelOperacionesSupLayout);
        PanelOperacionesSupLayout.setHorizontalGroup(
            PanelOperacionesSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOperacionesSupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelListaSup, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelOperacionesSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BorrarOpSup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BorrarAllSup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AgregarOpSup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelOperacionesSupLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel39)
                .addGap(324, 324, 324))
        );
        PanelOperacionesSupLayout.setVerticalGroup(
            PanelOperacionesSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOperacionesSupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelOperacionesSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelOperacionesSupLayout.createSequentialGroup()
                        .addComponent(AgregarOpSup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BorrarOpSup)
                        .addGap(7, 7, 7)
                        .addComponent(BorrarAllSup))
                    .addComponent(PanelListaSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PanelSupLayout = new javax.swing.GroupLayout(PanelSup);
        PanelSup.setLayout(PanelSupLayout);
        PanelSupLayout.setHorizontalGroup(
            PanelSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelSupLayout.createSequentialGroup()
                        .addComponent(PanelPreviewSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(PanelOperacionesSup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(PanelSupLayout.createSequentialGroup()
                .addGap(321, 321, 321)
                .addComponent(TituloPanelSup)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelSupLayout.setVerticalGroup(
            PanelSupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSupLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TituloPanelSup, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelPreviewSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelOperacionesSup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        PanelTabs.addTab("", new javax.swing.ImageIcon(getClass().getResource("/ihcam/ico3.png")), PanelSup, "Cara Superior"); // NOI18N

        PanelDer.setBackground(new java.awt.Color(255, 204, 153));
        PanelDer.setMaximumSize(new java.awt.Dimension(795, 503));
        PanelDer.setMinimumSize(new java.awt.Dimension(795, 503));
        PanelDer.setPreferredSize(new java.awt.Dimension(795, 503));

        TituloPanelDer.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        TituloPanelDer.setText("CARA LATERAL DERECHA");

        PanelPreviewDer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PanelPreviewDer.setMaximumSize(new java.awt.Dimension(775, 296));
        PanelPreviewDer.setMinimumSize(new java.awt.Dimension(775, 296));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel23.setText("VISTA PREVIA");

        PreviewDer.setBackground(new java.awt.Color(255, 255, 255));
        PreviewDer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PreviewDer.setMaximumSize(new java.awt.Dimension(753, 249));
        PreviewDer.setMinimumSize(new java.awt.Dimension(753, 249));
        PreviewDer.setPreferredSize(new java.awt.Dimension(753, 249));

        javax.swing.GroupLayout PreviewDerLayout = new javax.swing.GroupLayout(PreviewDer);
        PreviewDer.setLayout(PreviewDerLayout);
        PreviewDerLayout.setHorizontalGroup(
            PreviewDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        PreviewDerLayout.setVerticalGroup(
            PreviewDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelPreviewDerLayout = new javax.swing.GroupLayout(PanelPreviewDer);
        PanelPreviewDer.setLayout(PanelPreviewDerLayout);
        PanelPreviewDerLayout.setHorizontalGroup(
            PanelPreviewDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPreviewDerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PreviewDer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelPreviewDerLayout.createSequentialGroup()
                .addContainerGap(333, Short.MAX_VALUE)
                .addComponent(jLabel23)
                .addGap(324, 324, 324))
        );
        PanelPreviewDerLayout.setVerticalGroup(
            PanelPreviewDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPreviewDerLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PreviewDer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelOperacionesDer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PanelOperacionesDer.setMaximumSize(new java.awt.Dimension(775, 161));
        PanelOperacionesDer.setMinimumSize(new java.awt.Dimension(775, 161));
        PanelOperacionesDer.setPreferredSize(new java.awt.Dimension(775, 161));

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel27.setText("OPERACIONES");

        PanelListaDer.setMaximumSize(new java.awt.Dimension(521, 106));
        PanelListaDer.setMinimumSize(new java.awt.Dimension(521, 106));
        PanelListaDer.setPreferredSize(new java.awt.Dimension(521, 106));

        ListaOpDer.setModel(modelo_der);
        ListaOpDer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ListaOpDer.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ListaOpDer.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                ListaOpDerValueChanged(evt);
            }
        });
        PanelListaDer.setViewportView(ListaOpDer);

        BorrarOpDer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BorrarOpDer.setText("Borrar");
        BorrarOpDer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarOpDerActionPerformed(evt);
            }
        });

        BorrarAllDer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BorrarAllDer.setText("Borrar Todo");
        BorrarAllDer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BorrarAllDerActionPerformed(evt);
            }
        });

        AgregarOpDer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        AgregarOpDer.setText("Agregar");
        AgregarOpDer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AgregarOpDerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelOperacionesDerLayout = new javax.swing.GroupLayout(PanelOperacionesDer);
        PanelOperacionesDer.setLayout(PanelOperacionesDerLayout);
        PanelOperacionesDerLayout.setHorizontalGroup(
            PanelOperacionesDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOperacionesDerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelListaDer, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelOperacionesDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BorrarAllDer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AgregarOpDer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BorrarOpDer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelOperacionesDerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel27)
                .addGap(326, 326, 326))
        );
        PanelOperacionesDerLayout.setVerticalGroup(
            PanelOperacionesDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOperacionesDerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelOperacionesDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelOperacionesDerLayout.createSequentialGroup()
                        .addComponent(AgregarOpDer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BorrarOpDer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BorrarAllDer))
                    .addComponent(PanelListaDer, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout PanelDerLayout = new javax.swing.GroupLayout(PanelDer);
        PanelDer.setLayout(PanelDerLayout);
        PanelDerLayout.setHorizontalGroup(
            PanelDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDerLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(PanelPreviewDer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PanelOperacionesDer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(PanelDerLayout.createSequentialGroup()
                .addGap(285, 285, 285)
                .addComponent(TituloPanelDer)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        PanelDerLayout.setVerticalGroup(
            PanelDerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TituloPanelDer, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelPreviewDer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelOperacionesDer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        PanelTabs.addTab("", new javax.swing.ImageIcon(getClass().getResource("/ihcam/ico2.png")), PanelDer, "Cara Derecha"); // NOI18N

        PanelConsumibles.setBackground(new java.awt.Color(255, 204, 255));
        PanelConsumibles.setMaximumSize(new java.awt.Dimension(795, 503));
        PanelConsumibles.setMinimumSize(new java.awt.Dimension(795, 503));
        PanelConsumibles.setPreferredSize(new java.awt.Dimension(795, 503));

        TituloPanelConsumibles.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        TituloPanelConsumibles.setText("SELECCION DE CONSUMIBLES");

        PanelPicConsumibles.setBackground(new java.awt.Color(255, 204, 255));

        PicConsumibles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ihcam/Consumibles.PNG"))); // NOI18N

        javax.swing.GroupLayout PanelPicConsumiblesLayout = new javax.swing.GroupLayout(PanelPicConsumibles);
        PanelPicConsumibles.setLayout(PanelPicConsumiblesLayout);
        PanelPicConsumiblesLayout.setHorizontalGroup(
            PanelPicConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PicConsumibles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PanelPicConsumiblesLayout.setVerticalGroup(
            PanelPicConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PicConsumibles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        radio45.setBackground(new java.awt.Color(255, 204, 255));
        buttonGroup4.add(radio45);
        radio45.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        radio45.setSelected(true);
        radio45.setText("45 A");
        radio45.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio45ItemStateChanged(evt);
            }
        });

        radio65.setBackground(new java.awt.Color(255, 204, 255));
        buttonGroup4.add(radio65);
        radio65.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        radio65.setText("65 A");
        radio65.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio65ItemStateChanged(evt);
            }
        });

        radio85.setBackground(new java.awt.Color(255, 204, 255));
        buttonGroup4.add(radio85);
        radio85.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        radio85.setText("85 A");
        radio85.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radio85ItemStateChanged(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel11.setText("220817");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel12.setText("220953");

        label_boquilla.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        label_boquilla.setText("220941");

        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel52.setText("220842");

        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel53.setText("220857");

        SpinnerVoltajeAla.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerVoltajeAla.setModel(new javax.swing.SpinnerNumberModel(120, 80, 200, 0));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("Voltaje ala:");

        jLabel125.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel125.setText("Voltaje alma:");

        SpinnerVoltajeAlma.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        SpinnerVoltajeAlma.setModel(new javax.swing.SpinnerNumberModel(120, 80, 200, 0));

        javax.swing.GroupLayout PanelConsumiblesLayout = new javax.swing.GroupLayout(PanelConsumibles);
        PanelConsumibles.setLayout(PanelConsumiblesLayout);
        PanelConsumiblesLayout.setHorizontalGroup(
            PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                        .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PanelPicConsumibles, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelConsumiblesLayout.createSequentialGroup()
                                .addComponent(TituloPanelConsumibles)
                                .addGap(171, 171, 171)))
                        .addContainerGap(48, Short.MAX_VALUE))
                    .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel11)
                        .addGap(83, 83, 83)
                        .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                                .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(radio85)
                                    .addComponent(radio65)
                                    .addComponent(radio45))
                                .addGap(122, 122, 122)
                                .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addGap(18, 18, 18)
                                        .addComponent(SpinnerVoltajeAla, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                                        .addComponent(jLabel125)
                                        .addGap(18, 18, 18)
                                        .addComponent(SpinnerVoltajeAlma, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(72, 72, 72)
                                .addComponent(label_boquilla)
                                .addGap(63, 63, 63)
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel53)
                                .addGap(70, 70, 70))))))
        );
        PanelConsumiblesLayout.setVerticalGroup(
            PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(TituloPanelConsumibles)
                .addGap(44, 44, 44)
                .addComponent(PanelPicConsumibles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(label_boquilla)
                    .addComponent(jLabel52)
                    .addComponent(jLabel53))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                        .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerVoltajeAla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGap(18, 18, 18)
                        .addGroup(PanelConsumiblesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SpinnerVoltajeAlma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel125)))
                    .addGroup(PanelConsumiblesLayout.createSequentialGroup()
                        .addComponent(radio45)
                        .addGap(18, 18, 18)
                        .addComponent(radio65)
                        .addGap(18, 18, 18)
                        .addComponent(radio85)))
                .addGap(52, 52, 52))
        );

        PanelTabs.addTab("", new javax.swing.ImageIcon(getClass().getResource("/ihcam/ico4.png")), PanelConsumibles, "Consumibles"); // NOI18N

        PanelGcode.setBackground(new java.awt.Color(204, 204, 204));
        PanelGcode.setMaximumSize(new java.awt.Dimension(795, 503));
        PanelGcode.setMinimumSize(new java.awt.Dimension(795, 503));
        PanelGcode.setPreferredSize(new java.awt.Dimension(795, 503));

        TituloPanelGcode.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        TituloPanelGcode.setText("CODIGO G");

        listaGcode.setModel(modelo_gcode);
        listaGcode.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        listaGcode.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ScrollGcode.setViewportView(listaGcode);

        BotonGenerarGcode.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGenerarGcode.setText("Generar Codigo G");
        BotonGenerarGcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGenerarGcodeActionPerformed(evt);
            }
        });

        BotonGuardarGcode.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonGuardarGcode.setText("Guardar");
        BotonGuardarGcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarGcodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelGcodeLayout = new javax.swing.GroupLayout(PanelGcode);
        PanelGcode.setLayout(PanelGcodeLayout);
        PanelGcodeLayout.setHorizontalGroup(
            PanelGcodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGcodeLayout.createSequentialGroup()
                .addGroup(PanelGcodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelGcodeLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(ScrollGcode, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(PanelGcodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BotonGenerarGcode, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BotonGuardarGcode, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelGcodeLayout.createSequentialGroup()
                        .addGap(325, 325, 325)
                        .addComponent(TituloPanelGcode)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelGcodeLayout.setVerticalGroup(
            PanelGcodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelGcodeLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(TituloPanelGcode)
                .addGap(18, 18, 18)
                .addGroup(PanelGcodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelGcodeLayout.createSequentialGroup()
                        .addGap(321, 321, 321)
                        .addComponent(BotonGenerarGcode)
                        .addGap(18, 18, 18)
                        .addComponent(BotonGuardarGcode))
                    .addComponent(ScrollGcode, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelTabs.addTab("", new javax.swing.ImageIcon(getClass().getResource("/ihcam/ico6.png")), PanelGcode, "Codigo G"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 8, Short.MAX_VALUE)
                .addComponent(PanelTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelTabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        PanelTabs.getAccessibleContext().setAccessibleName("");
        PanelTabs.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ComboTipoPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboTipoPerfilActionPerformed
        
        PanelTipoPerfil.removeAll();
        PanelTipoPerfil.repaint();
        PanelTipoPerfil.revalidate();
        
       switch(ComboTipoPerfil.getSelectedIndex()){
            case 0:
                PanelTipoPerfil.add(PanelIPEHEB);
                tipoPerfil = 1;
                break;
            case 1:
                PanelTipoPerfil.add(PanelIPN);
                tipoPerfil = 2;
                break;
            case 2:
                PanelTipoPerfil.add(PanelUPN);
                tipoPerfil = 3;
                break;
            case 3:
                PanelTipoPerfil.add(PanelCAJON);
                tipoPerfil = 4;
                break;
            case 4:
                PanelTipoPerfil.add(PanelANGULO);
                tipoPerfil = 5;
                break;
        }
        PanelTipoPerfil.repaint();
        PanelTipoPerfil.revalidate();
        
    }//GEN-LAST:event_ComboTipoPerfilActionPerformed

    private void BorrarCortes(){
        Cortes.clear();
        dibujarViga(tipoPerfil);
        indice_corte = 0;
        modelo_izq.clear();
        modelo_der.clear();
        modelo_sup.clear();
        PanelPreviewIzq.repaint();
        PanelPreviewDer.repaint();
        PanelPreviewSup.repaint();
    }
    
    private Corte GenerarCorte(int tipo_izq, int tipo_der, int tipo_sup, double[] param_izq, double[] param_der, double[] param_sup){
        
        double[] aux_display = new double[11];
        double aux_radio;
        
        Corte AuxCorte = new Corte();
        AuxOpCorte = new Operacion_corte();
        AuxCorte.Setear_indice(indice_corte);
        

        //Generacion de preview y corte izquierdo
        
        switch (tipo_izq){
            case 1: //Agujero redondo
                    aux_display[0]=(double)posLateral[0]+param_izq[0]*escalaLat-param_izq[2]*escalaLat/2;   //Yo
                    aux_display[1]=(double)posLateral[1]+param_izq[1]*escalaLat-param_izq[2]*escalaLat/2;   //Zo
                    aux_display[2]=param_izq[2]*escalaLat;                                                  //D
                    aux_display[3]=param_izq[2]*escalaLat/2;                                                //R
                    aux_display[4]=(double)posLateral[0]+param_izq[0]*escalaLat-param_izq[2]*escalaLat/4;   //Ycentro
                    
                    FormaAuxiliar = new Arc2D.Double(aux_display[4],aux_display[1],aux_display[3],aux_display[3],270,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Ellipse2D.Double(aux_display[0],aux_display[1],aux_display[2],aux_display[2]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_izq(AuxPreview); 
                    break;
            case 2: //Agujero oblongo vertical
                    double b;
                    b=(param_izq[3]*escalaLat-param_izq[2]*escalaLat)/2;
                    aux_display[0]=(double)posLateral[0]+param_izq[0]*escalaLat;                            //x1
                    aux_display[1]=(double)posLateral[0]+param_izq[0]*escalaLat+param_izq[2]*escalaLat/2;   //x2
                    aux_display[2]=(double)posLateral[0]+param_izq[0]*escalaLat-param_izq[2]*escalaLat/2;   //x3
                    aux_display[3]=(double)posLateral[1]+param_izq[1]*escalaLat;                            //y1
                    aux_display[4]=(double)posLateral[1]+param_izq[1]*escalaLat-b;                          //y2
                    aux_display[5]=(double)posLateral[1]+param_izq[1]*escalaLat+b;                          //y3
                    aux_display[6]=(double)posLateral[1]+param_izq[1]*escalaLat-b-param_izq[2]*escalaLat/2; //Yos
                    aux_display[7]=(double)posLateral[1]+param_izq[1]*escalaLat+b-param_izq[2]*escalaLat/2; //Yoi
                    aux_display[8]=param_izq[2]*escalaLat;
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[1],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[4],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[2],aux_display[6],aux_display[8],aux_display[8],0,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[2],aux_display[7],aux_display[8],aux_display[8],180,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_izq(AuxPreview); 
                    break;
            case 3://Agujero oblongo horizontal
                    b=(param_izq[3]*escalaLat-param_izq[2]*escalaLat)/2;
                    aux_display[0]=(double)posLateral[0]+param_izq[0]*escalaLat;                            //x1
                    aux_display[1]=(double)posLateral[0]+param_izq[0]*escalaLat-b;                          //x2
                    aux_display[2]=(double)posLateral[0]+param_izq[0]*escalaLat+b;                          //x3
                    aux_display[3]=(double)posLateral[1]+param_izq[1]*escalaLat;                            //y1
                    aux_display[4]=(double)posLateral[1]+param_izq[1]*escalaLat-param_izq[2]*escalaLat/2;   //y2
                    aux_display[5]=(double)posLateral[1]+param_izq[1]*escalaLat+param_izq[2]*escalaLat/2;   //y3
                    aux_display[6]=(double)posLateral[0]+param_izq[0]*escalaLat-b-param_izq[2]*escalaLat/2; //Xos
                    aux_display[7]=(double)posLateral[0]+param_izq[0]*escalaLat+b-param_izq[2]*escalaLat/2; //Xoi
                    aux_display[8]=param_izq[2]*escalaLat;
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[0],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[2],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[5],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[6],aux_display[4],aux_display[8],aux_display[8],90,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[7],aux_display[4],aux_display[8],aux_display[8],270,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_izq(AuxPreview); 
                    break;
            case 4: //Agujero rectangular
                    aux_display[0]=(double)posLateral[0]+param_izq[0]*escalaLat+param_izq[3]*escalaLat/2;   //x1
                    aux_display[1]=(double)posLateral[0]+param_izq[0]*escalaLat;                            //x2
                    aux_display[2]=(double)posLateral[0]+param_izq[0]*escalaLat+param_izq[3]*escalaLat;   //x3
                    aux_display[3]=(double)posLateral[1]+param_izq[1]*escalaLat+param_izq[2]*escalaLat/2;   //y1
                    aux_display[4]=(double)posLateral[1]+param_izq[1]*escalaLat;                            //y2
                    aux_display[5]=(double)posLateral[1]+param_izq[1]*escalaLat+param_izq[2]*escalaLat;     //y3
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[0],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[1],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[5],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[5],aux_display[2],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[4],aux_display[1],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    
                    AuxCorte.Setear_preview_izq(AuxPreview); 
                    break;
            case 5: //Destijere 2
                    if(param_izq[2]==0){
                        aux_display[0]=(double)posLateral[0]+param_izq[1]*escalaLat;                        //x1
                        aux_display[1]=(double)posLateral[0];                                               //x2
                        aux_display[2]=(double)posLateral[1]+(double)tamLateral[1];                         //y1
                        aux_display[3]=(double)posLateral[1]+(double)tamLateral[1]-param_izq[0]*escalaLat;  //y2
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[2],aux_display[0],aux_display[3]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview = new Operacion_preview();
                        AuxPreview.borrarTodo();
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        AuxCorte.Setear_preview_izq(AuxPreview); 
                    }else{
                        aux_display[0]=(double)posLateral[0]+param_izq[1]*escalaLat;                        //x1
                        aux_display[1]=(double)posLateral[0];                                               //x2
                        aux_display[2]=(double)posLateral[1];                                               //y1
                        aux_display[3]=(double)posLateral[1]+param_izq[0]*escalaLat;                        //y2
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[2],aux_display[0],aux_display[3]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview = new Operacion_preview();
                        AuxPreview.borrarTodo();
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        AuxCorte.Setear_preview_izq(AuxPreview); 
                    }
                    break;
            case 6: //Linea recta
                    if(((tipo_sup==7)&&(((param_izq[0]+param_izq[1])/2)!=0))||(tipo_sup!=7)){
                        aux_display[0]=(double)posLateral[0]+param_izq[0]*escalaLat;
                        aux_display[1]=(double)posLateral[0]+param_izq[1]*escalaLat;
                        FormaAuxiliar = new Line2D.Double(aux_display[1],posLateral[1],aux_display[0],posLateral[1]+tamLateral[1]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview = new Operacion_preview();
                        AuxPreview.borrarTodo();
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        AuxCorte.Setear_preview_izq(AuxPreview);    
                    }
                    break;
        } 
                       
        //Generacion de preview y corte derecho
        
        switch (tipo_der){
            case 1: //Agujero redondo
                    aux_display[0]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat-param_der[2]*escalaLat/2;       //Yo
                    aux_display[1]=(double)posLateral[1]+param_der[1]*escalaLat-param_der[2]*escalaLat/2;                           //Zo
                    aux_display[2]=param_der[2]*escalaLat;                                                                          //D
                    aux_display[3]=param_der[2]*escalaLat/2;                                                                        //R
                    aux_display[4]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat-param_der[2]*escalaLat/4;   //Ycentro
                    
                    FormaAuxiliar = new Arc2D.Double(aux_display[4],aux_display[1],aux_display[3],aux_display[3],270,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Ellipse2D.Double(aux_display[0],aux_display[1],aux_display[2],aux_display[2]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_der(AuxPreview); 
                    break;
            case 2: //Agujero oblongo vertical
                    double b;
                    b=(param_der[3]*escalaLat-param_der[2]*escalaLat)/2;
                    aux_display[0]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat;      //x1
                    aux_display[1]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat+param_der[2]*escalaLat/2;   //x2
                    aux_display[2]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat-param_der[2]*escalaLat/2;   //x3
                    aux_display[3]=(double)posLateral[1]+param_der[1]*escalaLat;                            //y1
                    aux_display[4]=(double)posLateral[1]+param_der[1]*escalaLat-b;                          //y2
                    aux_display[5]=(double)posLateral[1]+param_der[1]*escalaLat+b;                          //y3
                    aux_display[6]=(double)posLateral[1]+param_der[1]*escalaLat-b-param_der[2]*escalaLat/2; //Yos
                    aux_display[7]=(double)posLateral[1]+param_der[1]*escalaLat+b-param_der[2]*escalaLat/2; //Yoi
                    aux_display[8]=param_der[2]*escalaLat;
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[1],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[4],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[2],aux_display[6],aux_display[8],aux_display[8],0,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[2],aux_display[7],aux_display[8],aux_display[8],180,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_der(AuxPreview); 
                    break;
            case 3: //Agujero oblongo horizontal
                    b=(param_der[3]*escalaLat-param_der[2]*escalaLat)/2;
                    aux_display[0]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat;                              //x1
                    aux_display[1]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat-b;                            //x2
                    aux_display[2]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat+b;                            //x3
                    aux_display[3]=(double)posLateral[1]+param_der[1]*escalaLat;                                                    //y1
                    aux_display[4]=(double)posLateral[1]+param_der[1]*escalaLat-param_der[2]*escalaLat/2;                           //y2
                    aux_display[5]=(double)posLateral[1]+param_der[1]*escalaLat+param_der[2]*escalaLat/2;                           //y3
                    aux_display[6]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat-b-param_der[2]*escalaLat/2;   //Xos
                    aux_display[7]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat+b-param_der[2]*escalaLat/2;   //Xoi
                    aux_display[8]=param_der[2]*escalaLat;
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[0],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[2],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[5],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[6],aux_display[4],aux_display[8],aux_display[8],90,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[7],aux_display[4],aux_display[8],aux_display[8],270,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_der(AuxPreview); 
                    break;
            case 4: //Agujero rectangular
                    aux_display[0]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat-param_der[3]*escalaLat/2; //x1
                    aux_display[1]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat-param_der[3]*escalaLat;   //x2
                    aux_display[2]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat;                                    //x3
                    aux_display[3]=(double)posLateral[1]+param_der[1]*escalaLat+param_der[2]*escalaLat/2;                       //y1
                    aux_display[4]=(double)posLateral[1]+param_der[1]*escalaLat;                                                //y2
                    aux_display[5]=(double)posLateral[1]+param_der[1]*escalaLat+param_der[2]*escalaLat;                         //y3
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[0],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[1],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[5],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[5],aux_display[2],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[4],aux_display[1],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    
                    AuxCorte.Setear_preview_der(AuxPreview); 
                    break;
            case 5: //Agujero rectangular
                    if(param_der[2]==0){
                        aux_display[0]=(double)posLateral[0]+(double)tamLateral[0]-param_der[1]*escalaLat;  //x1
                        aux_display[1]=(double)posLateral[0]+(double)tamLateral[0];                         //x2
                        aux_display[2]=(double)posLateral[1]+(double)tamLateral[1];                         //y1
                        aux_display[3]=(double)posLateral[1]+(double)tamLateral[1]-param_der[0]*escalaLat;  //y2
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[2],aux_display[0],aux_display[3]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview = new Operacion_preview();
                        AuxPreview.borrarTodo();
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        AuxCorte.Setear_preview_der(AuxPreview); 
                    }else{
                        aux_display[0]=(double)posLateral[0]+(double)tamLateral[0]-param_der[1]*escalaLat;  //x1
                        aux_display[1]=(double)posLateral[0]+(double)tamLateral[0];                         //x2
                        aux_display[2]=(double)posLateral[1];                                               //y1
                        aux_display[3]=(double)posLateral[1]+param_der[0]*escalaLat;                        //y2
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[2],aux_display[0],aux_display[3]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview = new Operacion_preview();
                        AuxPreview.borrarTodo();
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        AuxCorte.Setear_preview_der(AuxPreview); 
                    }
                    break;
            case 6: //Linea recta
                    if(((tipo_sup==7)&&(((param_der[0]+param_der[1])/2)!=0))||(tipo_sup!=7)){
                        aux_display[0]=(double)posLateral[0]+(double)tamLateral[0]-param_der[0]*escalaLat;
                        aux_display[1]=(double)posLateral[0]+(double)tamLateral[0]-param_der[1]*escalaLat;
                        FormaAuxiliar = new Line2D.Double(aux_display[1],posLateral[1],aux_display[0],posLateral[1]+tamLateral[1]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview = new Operacion_preview();
                        AuxPreview.borrarTodo();
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        AuxCorte.Setear_preview_der(AuxPreview); 
                    }
                    break;
        }
        
        //Generacion de preview y corte superior        
        
        switch (tipo_sup){
            case 1: //Agujero redondo
                    aux_display[0]=(double)posSuperior[0]+param_sup[1]*escalaSup-param_sup[2]*escalaSup/2;                       //Yo
                    aux_display[1]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup-param_sup[2]*escalaSup/2; //Xo
                    aux_display[2]=param_sup[2]*escalaSup;                                                                      //D
                    aux_display[3]=param_sup[2]*escalaSup/2;                                                                    //R
                    aux_display[4]=(double)posSuperior[0]+param_sup[1]*escalaSup-param_sup[2]*escalaSup/4;                       //Ycentro
                    
                    FormaAuxiliar = new Arc2D.Double(aux_display[4],aux_display[1],aux_display[3],aux_display[3],270,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Ellipse2D.Double(aux_display[0],aux_display[1],aux_display[2],aux_display[2]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_sup(AuxPreview);                    
                    break;
            case 2: //Agujero oblongo vertical
                    double b;
                    b=(param_sup[3]*escalaSup-param_sup[2]*escalaSup)/2;
                    aux_display[0]=(double)posSuperior[0]+param_sup[1]*escalaSup;                                                   //x1
                    aux_display[1]=(double)posSuperior[0]+param_sup[1]*escalaSup-b;                                                 //x2
                    aux_display[2]=(double)posSuperior[0]+param_sup[1]*escalaSup+b;                                                 //x3
                    aux_display[3]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup;                            //y1
                    aux_display[4]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup-param_sup[2]*escalaSup/2;   //y2
                    aux_display[5]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup+param_sup[2]*escalaSup/2;   //y3
                    aux_display[6]=(double)posSuperior[0]+param_sup[1]*escalaSup-b-param_sup[2]*escalaSup/2;                        //Xos
                    aux_display[7]=(double)posSuperior[0]+param_sup[1]*escalaSup+b-param_sup[2]*escalaSup/2;                        //Xoi
                    aux_display[8]=param_sup[2]*escalaSup;
                    
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[0],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[2],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[5],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[6],aux_display[4],aux_display[8],aux_display[8],90,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[7],aux_display[4],aux_display[8],aux_display[8],270,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_sup(AuxPreview); 
                    break;
            case 3: //Agujero oblongo horizontal
                    b=(param_sup[3]*escalaSup-param_sup[2]*escalaSup)/2;
                    aux_display[0]=(double)posSuperior[0]+param_sup[1]*escalaSup;                            //x1
                    aux_display[1]=(double)posSuperior[0]+param_sup[1]*escalaSup+param_sup[2]*escalaSup/2;   //x2
                    aux_display[2]=(double)posSuperior[0]+param_sup[1]*escalaSup-param_sup[2]*escalaSup/2;   //x3
                    aux_display[3]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup;     //y1
                    aux_display[4]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup-b;   //y2
                    aux_display[5]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup+b;   //y3
                    aux_display[6]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup-b-param_sup[2]*escalaSup/2; //Yos
                    aux_display[7]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup+b-param_sup[2]*escalaSup/2; //Yoi
                    aux_display[8]=param_sup[2]*escalaSup;
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[1],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[4],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[2],aux_display[6],aux_display[8],aux_display[8],0,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Arc2D.Double(aux_display[2],aux_display[7],aux_display[8],aux_display[8],180,180,Arc2D.OPEN);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_sup(AuxPreview); 
                    break;
            case 4: //Agujero rectangular
                    aux_display[0]=(double)posSuperior[0]+param_sup[1]*escalaSup+param_sup[2]*escalaSup/2;   //x1
                    aux_display[1]=(double)posSuperior[0]+param_sup[1]*escalaSup;                            //x2
                    aux_display[2]=(double)posSuperior[0]+param_sup[1]*escalaSup+param_sup[2]*escalaSup;     //x3
                    aux_display[3]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup-param_sup[3]*escalaSup/2;   //y1
                    aux_display[4]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup-param_sup[3]*escalaSup;                            //y2
                    aux_display[5]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup;     //y3
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[0],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[1],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[5],aux_display[2],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[5],aux_display[2],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[2],aux_display[4],aux_display[1],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_sup(AuxPreview); 
                    break;
            case 5:
                    break;
            case 6:
                    break;
            case 7: 
                    //Destijere
                    if(Material.GetRadioAlma()<=20){
                        aux_radio=20*escalaSup+Material.GetEspesorAla()*escalaSup;
                    }else{
                        aux_radio=Material.GetRadioAlma()*escalaSup+Material.GetEspesorAla()*escalaSup;
                    }
                    aux_display[0]=(double)posSuperior[0]+param_sup[0]*escalaSup;                                   //x1i
                    aux_display[1]=(double)posSuperior[0]+param_sup[2]*escalaSup;                                   //x2i
                    aux_display[2]=(double)posSuperior[0];                                                          //x3
                    aux_display[3]=(double)posSuperior[0]+param_sup[1]*escalaSup;                                   //x1d
                    aux_display[4]=(double)posSuperior[0]+param_sup[3]*escalaSup;                                   //x2d
                    aux_display[5]=(double)posSuperior[1]+(double)tamSuperior[1];                                   //y1i
                    aux_display[6]=(double)posSuperior[1]+(double)tamSuperior[1]-aux_radio;                         //y2i
                    aux_display[7]=(double)posSuperior[1]+(double)tamSuperior[1]-aux_radio-param_sup[2]*escalaSup;  //y3i
                    aux_display[8]=(double)posSuperior[1];                                                          //y1d
                    aux_display[9]=(double)posSuperior[1]+aux_radio;                                                //y2d
                    aux_display[10]=(double)posSuperior[1]+aux_radio+param_sup[3];                                  //y3d
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    
                    //Destijere Izquierdo
                    if(param_sup[0]!=0){
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[5],aux_display[0],aux_display[6]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[6],aux_display[1],aux_display[6]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[6],aux_display[2],aux_display[7]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        
                    }
                    //Destijere Derecho
                    if(param_sup[1]!=0){
                        FormaAuxiliar = new Line2D.Double(aux_display[3],aux_display[8],aux_display[3],aux_display[9]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        FormaAuxiliar = new Line2D.Double(aux_display[3],aux_display[9],aux_display[4],aux_display[9]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                        FormaAuxiliar = new Line2D.Double(aux_display[4],aux_display[9],aux_display[2],aux_display[10]);
                        FormaAuxiliarDib = new Dibujo();
                        FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                        AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    
                    }
                    
                    if((param_sup[0]+param_sup[1])!=0)
                        AuxCorte.Setear_preview_sup(AuxPreview); 
                    break;
            case 8:
                    //Empalme normal
                    aux_display[0]=(double)posSuperior[0]+param_sup[0]*escalaSup; //x1
                    aux_display[1]=(double)posSuperior[0]+param_sup[1]*escalaSup; //x2
                    aux_display[2]=(double)posSuperior[1]+(double)tamSuperior[1];//y1
                    aux_display[3]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[2]*escalaSup;//y2
                    aux_display[4]=(double)posSuperior[1];//y3
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[2],aux_display[0],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[3],aux_display[1],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_sup(AuxPreview); 
                    break;
            case 9: //Empalme hembra
                    aux_display[0]=(double)posSuperior[0]+param_sup[2]*escalaSup;                                   //x1
                    aux_display[1]=(double)posSuperior[0]+param_sup[2]+param_sup[3];                                //x2
                    aux_display[2]=(double)posSuperior[1]+(double)tamSuperior[1];                                   //y1
                    aux_display[3]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup;            //y2
                    aux_display[4]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[1]*escalaSup;            //y3
                    aux_display[5]=(double)posSuperior[1];                                                          //y4
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[2],aux_display[0],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);                   
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[3],aux_display[1],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[1],aux_display[4],aux_display[0],aux_display[4]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[4],aux_display[0],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    
                    AuxCorte.Setear_preview_sup(AuxPreview); 
                    break;    
            case 10://Linea recta
                    aux_display[0]=(double)posSuperior[0]+param_sup[0]*escalaSup;
                    aux_display[1]=(double)posSuperior[0]+param_sup[1]*escalaSup;
                    FormaAuxiliar = new Line2D.Double(aux_display[1],posSuperior[1],aux_display[0],posSuperior[1]+tamSuperior[1]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    AuxCorte.Setear_preview_sup(AuxPreview); 
                    break;
            case 11://Empalme macho
                    aux_display[0]=(double)posSuperior[0]+param_sup[2]*escalaSup;                                   //x1
                    aux_display[1]=(double)posSuperior[0];                                                          //x2
                    aux_display[2]=(double)posSuperior[1]+(double)tamSuperior[1];                                   //y1i
                    aux_display[3]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[0]*escalaSup;            //y2i
                    aux_display[4]=(double)posSuperior[1];                                                          //y1d
                    aux_display[5]=(double)posSuperior[1]+(double)tamSuperior[1]-param_sup[1]*escalaSup;            //y2d
                    AuxPreview = new Operacion_preview();
                    AuxPreview.borrarTodo();
                    
                    //Destijere Izquierdo
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[2],aux_display[0],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[3],aux_display[1],aux_display[3]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);

                    //Destijere Derecho
                    
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[4],aux_display[0],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    FormaAuxiliar = new Line2D.Double(aux_display[0],aux_display[5],aux_display[1],aux_display[5]);
                    FormaAuxiliarDib = new Dibujo();
                    FormaAuxiliarDib.Setear(FormaAuxiliar, Color.GREEN,3);
                    AuxPreview.añadirDibujo(FormaAuxiliarDib);
                    
                    AuxCorte.Setear_preview_sup(AuxPreview); 
                    break;    
        }
             
        //Crear parametros de corte
        
        AuxOpCorte.Setear(tipo_izq,tipo_der,tipo_sup,param_izq,param_der,param_sup);
        AuxCorte.Setear_corte(AuxOpCorte);
        return AuxCorte;
        
    }
          
    private void ActualizarDisplay(){
        dibujarViga(tipoPerfil);
        PanelPreviewIzq.repaint();
        PanelPreviewDer.repaint();
        PanelPreviewSup.repaint();
    }
            
    private void BorrarAllIzqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarAllIzqActionPerformed
        BorrarCortes();
    }//GEN-LAST:event_BorrarAllIzqActionPerformed

    private void BorrarOpIzqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarOpIzqActionPerformed
        if((modelo_izq.getSize()>0)&&(ListaOpIzq.getSelectedIndex()!=-1)){
            int n = ListaOpIzq.getSelectedIndex();
            modelo_izq.removeElementAt(n);
            modelo_der.removeElementAt(n);
            modelo_sup.removeElementAt(n);
            Cortes.remove(n);
            if(Cortes.size()>0){
                for(int i=Cortes.size()-1; i>=n; i--){
                    Cortes.get(i).Setear_indice(i);
                    modelo_izq.removeElementAt(i);
                    modelo_der.removeElementAt(i);
                    modelo_sup.removeElementAt(i);
                }
                
                for(int i=n; i<Cortes.size();i++){
                    modelo_izq.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                    modelo_der.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                    modelo_sup.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                }
            }
        }
        indice_corte--;
        ActualizarDisplay();
        
    }//GEN-LAST:event_BorrarOpIzqActionPerformed

    private void BotonIPEHEBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonIPEHEBActionPerformed
        BorrarCortes();
    }//GEN-LAST:event_BotonIPEHEBActionPerformed

    private void BotonIPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonIPNActionPerformed
        BorrarCortes();
    }//GEN-LAST:event_BotonIPNActionPerformed

    private void BotonUPNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonUPNActionPerformed
        BorrarCortes();
    }//GEN-LAST:event_BotonUPNActionPerformed

    private void BotonANGULOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonANGULOActionPerformed
        BorrarCortes();
    }//GEN-LAST:event_BotonANGULOActionPerformed

    private void BotonCAJONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCAJONActionPerformed
        BorrarCortes();
    }//GEN-LAST:event_BotonCAJONActionPerformed

    private void AgregarOpIzqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AgregarOpIzqActionPerformed
        Agregar_dialog.setVisible(true);
        Agregar_dialog.setLocationRelativeTo(this);
    }//GEN-LAST:event_AgregarOpIzqActionPerformed

    private void BorrarOpDerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarOpDerActionPerformed
        if((modelo_der.getSize()>0)&&(ListaOpDer.getSelectedIndex()!=-1)){
            int n = ListaOpIzq.getSelectedIndex();
            modelo_izq.removeElementAt(n);
            modelo_der.removeElementAt(n);
            modelo_sup.removeElementAt(n);
            Cortes.remove(n);
            if(Cortes.size()>0){
                for(int i=Cortes.size()-1; i>=n; i--){
                    Cortes.get(i).Setear_indice(i);
                    modelo_izq.removeElementAt(i);
                    modelo_der.removeElementAt(i);
                    modelo_sup.removeElementAt(i);
                }
                
                for(int i=n; i<Cortes.size();i++){
                    modelo_izq.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                    modelo_der.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                    modelo_sup.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                }
            }
        }
        indice_corte--;
        ActualizarDisplay();
    }//GEN-LAST:event_BorrarOpDerActionPerformed

    private void AgregarOpDerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AgregarOpDerActionPerformed
        Agregar_dialog.setVisible(true);
        Agregar_dialog.setLocationRelativeTo(this);
    }//GEN-LAST:event_AgregarOpDerActionPerformed

    private void Boton_salirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_salirActionPerformed
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_salirActionPerformed

    private void Boton_empalme_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_empalme_incActionPerformed
        Agregar_dialog.setVisible(false);
        Empalme_inc_dialog.setVisible(true);
        Empalme_inc_dialog.setLocationRelativeTo(this);
        Empalme_inc_dialog.setTitle("Empalme alas inclinadas");
    }//GEN-LAST:event_Boton_empalme_incActionPerformed

    private void Boton_rectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_rectoActionPerformed
        Agregar_dialog.setVisible(false);
        Recto_dialog.setVisible(true);
        Recto_dialog.setLocationRelativeTo(this);
        Recto_dialog.setTitle("Corte recto");
    }//GEN-LAST:event_Boton_rectoActionPerformed

    private void Boton_cancel_rectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_rectoActionPerformed
        Recto_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_rectoActionPerformed

    private void Boton_ok_rectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_rectoActionPerformed
        
        Recto_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
        auxParametrosIzq = new double[4];
        auxParametrosDer = new double[4];
        auxParametrosSup = new double[4];
        
        
        auxParametrosIzq[0] = (double)SpinnerLargoRecto.getValue(); //Yinf Izq
        auxParametrosIzq[1] = (double)SpinnerLargoRecto.getValue(); //Ysup Izq
        auxParametrosIzq[2] = 0;                                    //ND
        auxParametrosIzq[3] = 0;                                    //ND
        auxParametrosDer = auxParametrosIzq;
        auxParametrosSup = auxParametrosIzq;
        
        AuxCorteGlobal = new Corte();
        AuxCorteGlobal = GenerarCorte(6,6,10,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
        AuxCorteGlobal.Setear_indice(indice_corte);
        AuxCorteGlobal.Setear_nombre("Corte recto");
        Cortes.add(AuxCorteGlobal);
        
        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
        indice_corte++;
        ActualizarDisplay();
        
    }//GEN-LAST:event_Boton_ok_rectoActionPerformed

    private void Boton_ok_recto_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_recto_incActionPerformed
        
        double test_var1=(double)SpinnerL1RectoInc.getValue();
        double test_var2=(double)SpinnerL2RectoInc.getValue();

        if((test_var1>test_var2)){
        
            Recto_inc_dialog.setVisible(false);
            Agregar_dialog.setVisible(false);

            auxParametrosIzq = new double[4];
            auxParametrosDer = new double[4];
            auxParametrosSup = new double[4];

            auxParametrosIzq[0] = (double)SpinnerL1RectoInc.getValue(); //Yinf Izq
            auxParametrosIzq[1] = (double)SpinnerL2RectoInc.getValue(); //Ysup Izq
            auxParametrosIzq[2] = 0;                                    //ND
            auxParametrosIzq[3] = 0;                                    //ND
            auxParametrosDer[0] = (double)SpinnerL1RectoInc.getValue(); //Yinf Izq
            auxParametrosDer[1] = (double)SpinnerL2RectoInc.getValue(); //Ysup Izq
            auxParametrosDer[2] = 0;                                    //ND
            auxParametrosDer[3] = 0;                                    //ND
            auxParametrosSup[0] = ((double)SpinnerL1RectoInc.getValue()+(double)SpinnerL2RectoInc.getValue())/2; //X Izq
            auxParametrosSup[1] = ((double)SpinnerL1RectoInc.getValue()+(double)SpinnerL2RectoInc.getValue())/2; //X Der
            auxParametrosSup[2] = 0;                                    //ND
            auxParametrosSup[3] = 0;                                    //ND

            AuxCorteGlobal = new Corte();
            AuxCorteGlobal = GenerarCorte(6,6,10,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
            AuxCorteGlobal.Setear_indice(indice_corte);
            AuxCorteGlobal.Setear_nombre("Corte recto alas inclinadas");
            Cortes.add(AuxCorteGlobal);

            modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
            modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
            modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
            indice_corte++;
            ActualizarDisplay();
        }else{
            JOptionPane.showMessageDialog(null,"L1 debe ser mayor a L2","Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_Boton_ok_recto_incActionPerformed

    private void Boton_cancel_recto_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_recto_incActionPerformed
        Recto_inc_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_recto_incActionPerformed

    private void Boton_recto_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_recto_incActionPerformed
        Agregar_dialog.setVisible(false);
        Recto_inc_dialog.setVisible(true);
        Recto_inc_dialog.setLocationRelativeTo(this);
        Recto_inc_dialog.setTitle("Corte alas inclinadas");
    }//GEN-LAST:event_Boton_recto_incActionPerformed

    private void Boton_ok_recto_desfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_recto_desfActionPerformed
    
        double test_var1=(double)SpinnerLIRectoDesf.getValue();
        double test_var2=(double)SpinnerLDRectoDesf.getValue();

        if((test_var1!=test_var2)){
            Recto_desf_dialog.setVisible(false);
            Agregar_dialog.setVisible(false);

            auxParametrosIzq = new double[4];
            auxParametrosDer = new double[4];
            auxParametrosSup = new double[4];

            auxParametrosIzq[0] = (double)SpinnerLIRectoDesf.getValue(); //Yinf Izq
            auxParametrosIzq[1] = (double)SpinnerLIRectoDesf.getValue(); //Ysup Izq
            auxParametrosIzq[2] = 0;                                    //ND
            auxParametrosIzq[3] = 0;                                    //ND
            auxParametrosDer[0] = (double)SpinnerLDRectoDesf.getValue(); //Yinf Der
            auxParametrosDer[1] = (double)SpinnerLDRectoDesf.getValue(); //Ysup Der
            auxParametrosDer[2] = 0;                                    //ND
            auxParametrosDer[3] = 0;                                    //ND
            auxParametrosSup[0] = (double)SpinnerLIRectoDesf.getValue(); //X Izq
            auxParametrosSup[1] = (double)SpinnerLDRectoDesf.getValue(); //X Der
            auxParametrosSup[2] = 0;                                    //ND
            auxParametrosSup[3] = 0;                                    //ND

            AuxCorteGlobal = new Corte();
            AuxCorteGlobal = GenerarCorte(6,6,10,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
            AuxCorteGlobal.Setear_indice(indice_corte);
            AuxCorteGlobal.Setear_nombre("Corte recto alas desfasadas");
            Cortes.add(AuxCorteGlobal);

            modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
            modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
            modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
            indice_corte++;
            ActualizarDisplay();
        }else{
            JOptionPane.showMessageDialog(null,"LI y LD no pueden ser iguales","Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_Boton_ok_recto_desfActionPerformed

    private void Boton_cancel_recto_desfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_recto_desfActionPerformed
        Recto_desf_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_recto_desfActionPerformed

    private void Boton_recto_desfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_recto_desfActionPerformed
        Agregar_dialog.setVisible(false);
        Recto_desf_dialog.setVisible(true);
        Recto_desf_dialog.setLocationRelativeTo(this);
        Recto_desf_dialog.setTitle("Corte recto desfasado");
    }//GEN-LAST:event_Boton_recto_desfActionPerformed

    private void Boton_ok_recto_inc_desfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_recto_inc_desfActionPerformed
        
        double test_var1=(double)SpinnerL1IRectoIncDesf.getValue();
        double test_var2=(double)SpinnerL2IRectoIncDesf.getValue();
        double test_var3=(double)SpinnerL1DRectoIncDesf.getValue();
        double test_var4=(double)SpinnerL2DRectoIncDesf.getValue();
        double test_var5=(test_var1+test_var2)/2;
        double test_var6=(test_var3+test_var4)/2;

        if((test_var1>test_var2)&&(test_var3>test_var4)){
            if(test_var5!=test_var6){
                Recto_inc_desf_dialog.setVisible(false);
                Agregar_dialog.setVisible(false);

                auxParametrosIzq = new double[4];
                auxParametrosDer = new double[4];
                auxParametrosSup = new double[4];

                auxParametrosIzq[0] = (double)SpinnerL1IRectoIncDesf.getValue(); //Yinf Izq
                auxParametrosIzq[1] = (double)SpinnerL2IRectoIncDesf.getValue(); //Ysup Izq
                auxParametrosIzq[2] = 0;                                    //ND
                auxParametrosIzq[3] = 0;                                    //ND
                auxParametrosDer[0] = (double)SpinnerL1DRectoIncDesf.getValue(); //Yinf Der
                auxParametrosDer[1] = (double)SpinnerL2DRectoIncDesf.getValue(); //Ysup Der
                auxParametrosDer[2] = 0;                                    //ND
                auxParametrosDer[3] = 0;                                    //ND
                auxParametrosSup[0] = ((double)SpinnerL1IRectoIncDesf.getValue()+(double)SpinnerL2IRectoIncDesf.getValue())/2; //Y Izq
                auxParametrosSup[1] = ((double)SpinnerL1DRectoIncDesf.getValue()+(double)SpinnerL2DRectoIncDesf.getValue())/2; //Y Der
                auxParametrosSup[2] = 0;                                    //ND
                auxParametrosSup[3] = 0;                                    //ND

                AuxCorteGlobal = new Corte();
                AuxCorteGlobal = GenerarCorte(6,6,10,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                AuxCorteGlobal.Setear_indice(indice_corte);
                AuxCorteGlobal.Setear_nombre("Corte recto alas inclinadas desfasadas");
                Cortes.add(AuxCorteGlobal);

                modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                indice_corte++;
                ActualizarDisplay();
            }else{
                JOptionPane.showMessageDialog(null,"Los cortes de las alas no estan desfasados","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"L1x debe ser mayor a L2x","Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_Boton_ok_recto_inc_desfActionPerformed

    private void Boton_cancel_recto_inc_desfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_recto_inc_desfActionPerformed
        Recto_inc_desf_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_recto_inc_desfActionPerformed

    private void Boton_recto_desf_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_recto_desf_incActionPerformed
        Agregar_dialog.setVisible(false);
        Recto_inc_desf_dialog.setVisible(true);
        Recto_inc_desf_dialog.setLocationRelativeTo(this);
        Recto_inc_desf_dialog.setTitle("Corte alas inclinadas desfasado");
    }//GEN-LAST:event_Boton_recto_desf_incActionPerformed

    private void Boton_ok_empalmeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_empalmeActionPerformed
        
        double dist_seg;
        double test_var1=(double)SpinnerXEmpalme.getValue();
        double test_var2=(double)SpinnerLIEmpalme.getValue();
        double test_var3=(double)SpinnerLDEmpalme.getValue();
        
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        if((test_var1>=Material.GetEspesorAla()+dist_seg)&&(test_var1<=Material.GetAncho()-Material.GetEspesorAla()-dist_seg)){
            
            if(test_var2!=test_var3){
            
                Empalme_dialog.setVisible(false);
                Agregar_dialog.setVisible(false);

                auxParametrosIzq = new double[4];
                auxParametrosDer = new double[4];
                auxParametrosSup = new double[4];

                auxParametrosIzq[0] = (double)SpinnerLIEmpalme.getValue(); //Yinf Izq
                auxParametrosIzq[1] = (double)SpinnerLIEmpalme.getValue(); //Ysup Izq
                auxParametrosIzq[2] = 0;                                    //ND
                auxParametrosIzq[3] = 0;                                    //ND
                auxParametrosDer[0] = (double)SpinnerLDEmpalme.getValue(); //Yinf Der
                auxParametrosDer[1] = (double)SpinnerLDEmpalme.getValue(); //Ysup Der
                auxParametrosDer[2] = 0;                                    //ND
                auxParametrosDer[3] = 0;                                    //ND
                auxParametrosSup[0] = (double)SpinnerLIEmpalme.getValue(); //Y Izq
                auxParametrosSup[1] = (double)SpinnerLDEmpalme.getValue(); //Y Der
                auxParametrosSup[2] = (double)SpinnerXEmpalme.getValue(); //X transicion
                auxParametrosSup[3] = 0;                                    //ND

                AuxCorteGlobal = new Corte();
                AuxCorteGlobal = GenerarCorte(6,6,8,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                AuxCorteGlobal.Setear_indice(indice_corte);
                AuxCorteGlobal.Setear_nombre("Empalme estandar");
                Cortes.add(AuxCorteGlobal);

                modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                indice_corte++;
                ActualizarDisplay();
                
            }else{
                JOptionPane.showMessageDialog(null,"LI debe ser diferente de LD","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"X fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_Boton_ok_empalmeActionPerformed

    private void Boton_cancel_empalmeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_empalmeActionPerformed
        Empalme_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_empalmeActionPerformed

    private void Boton_empalmeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_empalmeActionPerformed
        
        Agregar_dialog.setVisible(false);
        Empalme_dialog.setVisible(true);
        Empalme_dialog.setLocationRelativeTo(this);
        Empalme_dialog.setTitle("Empalme estandar");
        
        
    }//GEN-LAST:event_Boton_empalmeActionPerformed

    private void Boton_ok_empalme_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_empalme_incActionPerformed

        double dist_seg;
        double test_var1=(double)SpinnerXEmpalmeInc.getValue();
        double test_var2=(double)SpinnerL1IEmpalmeInc.getValue();
        double test_var3=(double)SpinnerL1DEmpalmeInc.getValue();
        double test_var4=(double)SpinnerL2IEmpalmeInc.getValue();
        double test_var5=(double)SpinnerL2DEmpalmeInc.getValue();
        double test_var6=(test_var2+test_var4)/2;
        double test_var7=(test_var3+test_var5)/2;
        
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        if((test_var1>=Material.GetEspesorAla()+dist_seg)&&(test_var1<=Material.GetAncho()-Material.GetEspesorAla()-dist_seg)){
            
            if(test_var2>test_var4&&test_var3>test_var5){
                
                if(test_var6!=test_var7){
                
                    Empalme_inc_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);

                    auxParametrosIzq = new double[4];
                    auxParametrosDer = new double[4];
                    auxParametrosSup = new double[4];

                    auxParametrosIzq[0] = (double)SpinnerL1IEmpalmeInc.getValue(); //Yinf Izq
                    auxParametrosIzq[1] = (double)SpinnerL2IEmpalmeInc.getValue(); //Ysup Izq
                    auxParametrosIzq[2] = 0;                                    //ND
                    auxParametrosIzq[3] = 0;                                    //ND
                    auxParametrosDer[0] = (double)SpinnerL1DEmpalmeInc.getValue(); //Yinf Der
                    auxParametrosDer[1] = (double)SpinnerL2DEmpalmeInc.getValue(); //Ysup Der
                    auxParametrosDer[2] = 0;                                    //ND
                    auxParametrosDer[3] = 0;                                    //ND
                    auxParametrosSup[0] = ((double)SpinnerL1IEmpalmeInc.getValue()+(double)SpinnerL2IEmpalmeInc.getValue())/2; //Y Izq
                    auxParametrosSup[1] = ((double)SpinnerL1DEmpalmeInc.getValue()+(double)SpinnerL2DEmpalmeInc.getValue())/2; //Y Der
                    auxParametrosSup[2] = (double)SpinnerXEmpalmeInc.getValue(); //X transicion
                    auxParametrosSup[3] = 0;                                    //ND

                    AuxCorteGlobal = new Corte();
                    AuxCorteGlobal = GenerarCorte(6,6,8,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                    AuxCorteGlobal.Setear_indice(indice_corte);
                    AuxCorteGlobal.Setear_nombre("Empalme alas inclinadas");
                    Cortes.add(AuxCorteGlobal);

                    modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    indice_corte++;
                    ActualizarDisplay();
                
                }else{
                    JOptionPane.showMessageDialog(null,"Los cortes en las alas coinciden, deben estar desfasados","Error",JOptionPane.ERROR_MESSAGE);
                }     
            }else{
                JOptionPane.showMessageDialog(null,"L1x debe ser mayor a L2x","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"X fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_Boton_ok_empalme_incActionPerformed

    private void Boton_cancel_empalme_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_empalme_incActionPerformed
        Empalme_inc_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_empalme_incActionPerformed

    private void Boton_ok_destijereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_destijereActionPerformed
        
        double dist_seg;
        double test_var1=(double)SpinnerLIDestijere.getValue();
        double test_var2=(double)SpinnerCIDestijere.getValue();
        double test_var3=(double)SpinnerLDDestijere.getValue();
        double test_var4=(double)SpinnerCDDestijere.getValue();
        
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        if(test_var1>test_var2&&test_var3>test_var4){
            if((test_var2+test_var4)<=(Material.GetAncho()-2*Material.GetEspesorAla()-2*dist_seg)){
                if(((double)SpinnerLIDestijere.getValue()+(double)SpinnerLDDestijere.getValue())!=0){
                    Destijere_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);
                    auxParametrosIzq = new double[4];
                    auxParametrosDer = new double[4];
                    auxParametrosSup = new double[4];

                    auxParametrosIzq[0] = (double)SpinnerLIDestijere.getValue();    //Yinf Izq
                    auxParametrosIzq[1] = (double)SpinnerLIDestijere.getValue();    //Ysup Izq
                    auxParametrosIzq[2] = 0;                                        //ND
                    auxParametrosIzq[3] = 0;                                        //ND
                    auxParametrosDer[0] = (double)SpinnerLDDestijere.getValue();    //Yinf Der
                    auxParametrosDer[1] = (double)SpinnerLDDestijere.getValue();    //Ysup Der
                    auxParametrosDer[2] = 0;                                        //ND
                    auxParametrosDer[3] = 0;                                        //ND
                    auxParametrosSup[0] = (double)SpinnerLIDestijere.getValue();    //Y Izq
                    auxParametrosSup[1] = (double)SpinnerLDDestijere.getValue();    //Y Der
                    auxParametrosSup[2] = (double)SpinnerCIDestijere.getValue();    //Chaflan Izq
                    auxParametrosSup[3] = (double)SpinnerCDDestijere.getValue();    //Chaflan Der

                    AuxCorteGlobal = new Corte();
                    AuxCorteGlobal = GenerarCorte(6,6,7,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                    AuxCorteGlobal.Setear_indice(indice_corte);
                    AuxCorteGlobal.Setear_nombre("Destijere");
                    Cortes.add(AuxCorteGlobal);

                    modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    indice_corte++;
                    ActualizarDisplay();
                }
            }else{
                JOptionPane.showMessageDialog(null,"Los chaflanes se entrecruzan","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"El chaflan debe ser menor o igual al largo del destijere","Error",JOptionPane.ERROR_MESSAGE);
        } 
        
    }//GEN-LAST:event_Boton_ok_destijereActionPerformed

    private void Boton_cancel_destijereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_destijereActionPerformed
        Destijere_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_destijereActionPerformed

    private void Boton_destijereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_destijereActionPerformed
        Agregar_dialog.setVisible(false);
        Destijere_dialog.setVisible(true);
        Destijere_dialog.setLocationRelativeTo(this);
        Destijere_dialog.setTitle("Destijere");
    }//GEN-LAST:event_Boton_destijereActionPerformed

    private void Boton_ok_destijere_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_destijere_incActionPerformed
        
        double dist_seg;
        double test_var1=(double)SpinnerL1IDestijereInc.getValue();
        double test_var2=(double)SpinnerL2IDestijereInc.getValue();
        double test_var3=(double)SpinnerCIDestijereInc.getValue();
        double test_var4=(double)SpinnerL1DDestijereInc.getValue();
        double test_var5=(double)SpinnerL2DDestijereInc.getValue();
        double test_var6=(double)SpinnerCDDestijereInc.getValue();
        double test_var7=(test_var1+test_var2)/2;
        double test_var8=(test_var4+test_var5)/2;
        
        
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        if(test_var7>test_var3&&test_var8>test_var6){
            if(test_var1>test_var2&&test_var4>test_var5){
                if((test_var3+test_var6)<=(Material.GetAncho()-2*Material.GetEspesorAla()-2*dist_seg)){
                    Destijere_inc_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);

                    if(((double)SpinnerL1IDestijereInc.getValue()+(double)SpinnerL2IDestijereInc.getValue()+(double)SpinnerL1DDestijereInc.getValue()+(double)SpinnerL2DDestijereInc.getValue())!=0){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];

                        auxParametrosIzq[0] = (double)SpinnerL1IDestijereInc.getValue();    //Yinf Izq
                        auxParametrosIzq[1] = (double)SpinnerL2IDestijereInc.getValue();    //Ysup Izq
                        auxParametrosIzq[2] = 0;                                            //ND
                        auxParametrosIzq[3] = 0;                                            //ND
                        auxParametrosDer[0] = (double)SpinnerL1DDestijereInc.getValue();    //Yinf Der
                        auxParametrosDer[1] = (double)SpinnerL2DDestijereInc.getValue();    //Ysup Der
                        auxParametrosDer[2] = 0;                                            //ND
                        auxParametrosDer[3] = 0;                                            //ND
                        auxParametrosSup[0] = ((double)SpinnerL1IDestijereInc.getValue()+(double)SpinnerL2IDestijereInc.getValue())/2;    //Y Izq
                        auxParametrosSup[1] = ((double)SpinnerL1DDestijereInc.getValue()+(double)SpinnerL2DDestijereInc.getValue())/2;    //Y Der
                        auxParametrosSup[2] = (double)SpinnerCIDestijereInc.getValue();    //Chaflan Izq
                        auxParametrosSup[3] = (double)SpinnerCDDestijereInc.getValue();    //Chaflan Der

                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(6,6,7,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Destijere alas inclinadas");
                        Cortes.add(AuxCorteGlobal);

                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                        ActualizarDisplay();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Los chaflanes se entrecruzan","Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null,"L1x debe ser mayor a L2x","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"El chaflan debe ser menor o igual al largo del destijere","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_Boton_ok_destijere_incActionPerformed

    private void Boton_cancel_destijere_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_destijere_incActionPerformed
        Destijere_inc_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_destijere_incActionPerformed

    private void Boton_destijere_incActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_destijere_incActionPerformed
        Agregar_dialog.setVisible(false);
        Destijere_inc_dialog.setVisible(true);
        Destijere_inc_dialog.setLocationRelativeTo(this);
        Destijere_inc_dialog.setTitle("Destijere alas inclinadas");
    }//GEN-LAST:event_Boton_destijere_incActionPerformed

    private void Boton_ok_destijere2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_destijere2ActionPerformed
        
        double test_var1=(double)SpinnerAIIDestijere2.getValue();
        double test_var2=(double)SpinnerAIDDestijere2.getValue();
        double test_var3=(double)SpinnerASIDestijere2.getValue();
        double test_var4=(double)SpinnerASDDestijere2.getValue();
        double test_var5=(Material.GetAlto()-Material.GetEspesorAlma())/2;
        
        if(test_var1<=test_var5&&test_var2<=test_var5&&test_var3<=test_var5&&test_var4<=test_var5){
            Destijere2_dialog.setVisible(false);
            Agregar_dialog.setVisible(false);

            double a=(double)SpinnerLIIDestijere2.getValue()+(double)SpinnerAIIDestijere2.getValue()+(double)SpinnerLSIDestijere2.getValue()+(double)SpinnerASIDestijere2.getValue();
            double b=(double)SpinnerLIDDestijere2.getValue()+(double)SpinnerAIDDestijere2.getValue()+(double)SpinnerLSDDestijere2.getValue()+(double)SpinnerASDDestijere2.getValue();

            if((a!=0)||(b!=0)){

                    //Esquina inferior izquierda

                    if(((double)SpinnerLIIDestijere2.getValue()!=0)&&((double)SpinnerAIIDestijere2.getValue()!=0)){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];
                        auxParametrosIzq[0] = (double)SpinnerAIIDestijere2.getValue();    //Ainf Izq
                        auxParametrosIzq[1] = (double)SpinnerLIIDestijere2.getValue();    //Linf Izq
                        auxParametrosIzq[2] = 0;                                        
                        auxParametrosIzq[3] = 0;                                        
                        auxParametrosDer[0] = 0;    
                        auxParametrosDer[1] = 0;   
                        auxParametrosDer[2] = 0;                                     
                        auxParametrosDer[3] = 0;                                  
                        auxParametrosSup[0] = 0; 
                        auxParametrosSup[1] = 0;  
                        auxParametrosSup[2] = 0;    
                        auxParametrosSup[3] = 0; 
                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(5,0,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Destijere 2");
                        Cortes.add(AuxCorteGlobal);
                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                    }

                    //Esquina superior izquierda

                    if(((double)SpinnerLSIDestijere2.getValue()!=0)&&((double)SpinnerASIDestijere2.getValue()!=0)){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];
                        auxParametrosIzq[0] = (double)SpinnerASIDestijere2.getValue();    //Asup Izq
                        auxParametrosIzq[1] = (double)SpinnerLSIDestijere2.getValue();    //Lsup Izq
                        auxParametrosIzq[2] = 1;                                        
                        auxParametrosIzq[3] = 0;                                        
                        auxParametrosDer[0] = 0;    
                        auxParametrosDer[1] = 0;   
                        auxParametrosDer[2] = 0;                                     
                        auxParametrosDer[3] = 0;                                  
                        auxParametrosSup[0] = 0; 
                        auxParametrosSup[1] = 0;  
                        auxParametrosSup[2] = 0;    
                        auxParametrosSup[3] = 0; 
                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(5,0,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Destijere 2");
                        Cortes.add(AuxCorteGlobal);
                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                    }

                    //Esquina inferior derecha

                    if(((double)SpinnerLIDDestijere2.getValue()!=0)&&((double)SpinnerAIDDestijere2.getValue()!=0)){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];
                        auxParametrosIzq[0] = 0;
                        auxParametrosIzq[1] = 0;
                        auxParametrosIzq[2] = 0;                                        
                        auxParametrosIzq[3] = 0;                                        
                        auxParametrosDer[0] = (double)SpinnerAIDDestijere2.getValue();    //Ainf Der    
                        auxParametrosDer[1] = (double)SpinnerLIDDestijere2.getValue();    //Linf Der  
                        auxParametrosDer[2] = 0;                                     
                        auxParametrosDer[3] = 0;                                  
                        auxParametrosSup[0] = 0; 
                        auxParametrosSup[1] = 0;  
                        auxParametrosSup[2] = 0;    
                        auxParametrosSup[3] = 0; 
                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(0,5,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Destijere 2");
                        Cortes.add(AuxCorteGlobal);
                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                    }

                    //Esquina superior derecha

                    if(((double)SpinnerLSDDestijere2.getValue()!=0)&&((double)SpinnerASDDestijere2.getValue()!=0)){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];
                        auxParametrosIzq[0] = 0;
                        auxParametrosIzq[1] = 0;
                        auxParametrosIzq[2] = 0;                                        
                        auxParametrosIzq[3] = 0;                                        
                        auxParametrosDer[0] = (double)SpinnerASDDestijere2.getValue();    //Asup Der    
                        auxParametrosDer[1] = (double)SpinnerLSDDestijere2.getValue();    //Lsup Der   
                        auxParametrosDer[2] = 1;                                     
                        auxParametrosDer[3] = 0;                                  
                        auxParametrosSup[0] = 0; 
                        auxParametrosSup[1] = 0;  
                        auxParametrosSup[2] = 0;    
                        auxParametrosSup[3] = 0; 
                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(0,5,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Destijere 2");
                        Cortes.add(AuxCorteGlobal);
                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                    }
                    ActualizarDisplay();
            }
        }else{
            JOptionPane.showMessageDialog(null,"Altura máxima del destijere superada","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_Boton_ok_destijere2ActionPerformed

    private void Boton_cancel_destijere2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_destijere2ActionPerformed
        Destijere2_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_destijere2ActionPerformed

    private void Boton_destijere2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_destijere2ActionPerformed
        Agregar_dialog.setVisible(false);
        Destijere2_dialog.setVisible(true);
        Destijere2_dialog.setLocationRelativeTo(this);
        Destijere2_dialog.setTitle("Destijere 2");
    }//GEN-LAST:event_Boton_destijere2ActionPerformed

    private void Boton_ok_empalme_machoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_empalme_machoActionPerformed
        
        double dist_seg;
        double test_var1=(double)SpinnerA1EmpalmeMacho.getValue();
        double test_var2=(double)SpinnerA2EmpalmeMacho.getValue();
        double test_var3=(double)SpinnerCEmpalmeMacho.getValue();
        
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        if((test_var1>=Material.GetEspesorAla()+dist_seg)&&(test_var1<=Material.GetAncho()-Material.GetEspesorAla()-dist_seg)&&(test_var2>=Material.GetEspesorAla()+dist_seg)&&(test_var2<=Material.GetAncho()-Material.GetEspesorAla()-dist_seg)){
            if(test_var3>0){
                if(test_var1<test_var2){
                    Empalme_macho_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);

                    if((double)SpinnerCEmpalmeMacho.getValue()!=0){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];

                        auxParametrosIzq[0] = (double)SpinnerCEmpalmeMacho.getValue();      //Yinf Izq
                        auxParametrosIzq[1] = (double)SpinnerCEmpalmeMacho.getValue();      //Ysup Izq
                        auxParametrosIzq[2] = 0;                                            //ND
                        auxParametrosIzq[3] = 0;                                            //ND
                        auxParametrosDer[0] = (double)SpinnerCEmpalmeMacho.getValue();      //Yinf Der
                        auxParametrosDer[1] = (double)SpinnerCEmpalmeMacho.getValue();      //Ysup Der
                        auxParametrosDer[2] = 0;                                            //ND
                        auxParametrosDer[3] = 0;                                            //ND
                        auxParametrosSup[0] = (double)SpinnerA1EmpalmeMacho.getValue();      //X Izq
                        auxParametrosSup[1] = (double)SpinnerA2EmpalmeMacho.getValue();      //X Der
                        auxParametrosSup[2] = (double)SpinnerCEmpalmeMacho.getValue();      // Y
                        auxParametrosSup[3] = 0;                                            //ND

                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(6,6,11,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Empalme macho");
                        Cortes.add(AuxCorteGlobal);

                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                        ActualizarDisplay();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"A1 debe ser menor que A2","Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null,"C debe ser mayor a cero","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Ax fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_Boton_ok_empalme_machoActionPerformed

    private void Boton_cancel_empalme_machoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_empalme_machoActionPerformed
        Empalme_macho_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_empalme_machoActionPerformed

    private void Boton_empalme_machoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_empalme_machoActionPerformed
        Agregar_dialog.setVisible(false);
        Empalme_macho_dialog.setVisible(true);
        Empalme_macho_dialog.setLocationRelativeTo(this);
        Empalme_macho_dialog.setTitle("Empalme macho");
    }//GEN-LAST:event_Boton_empalme_machoActionPerformed

    private void Boton_ok_empalme_hembraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_empalme_hembraActionPerformed
        double dist_seg;
        double test_var1=(double)SpinnerA1EmpalmeHembra.getValue();
        double test_var2=(double)SpinnerA2EmpalmeHembra.getValue();
        double test_var3=(double)SpinnerCEmpalmeHembra.getValue();
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        if((test_var1>=Material.GetEspesorAla()+dist_seg)&&(test_var1<=Material.GetAncho()-Material.GetEspesorAla()-dist_seg)&&(test_var2>=Material.GetEspesorAla()+dist_seg)&&(test_var2<=Material.GetAncho()-Material.GetEspesorAla()-dist_seg)){
        
            if(test_var1<test_var2){
                if(test_var3>0){
                    Empalme_hembra_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);
                    if((double)SpinnerCEmpalmeHembra.getValue()!=0){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];

                        auxParametrosIzq[0] = (double)SpinnerLEmpalmeHembra.getValue();     //Yinf Izq
                        auxParametrosIzq[1] = (double)SpinnerLEmpalmeHembra.getValue();     //Ysup Izq
                        auxParametrosIzq[2] = 0;                                            //ND
                        auxParametrosIzq[3] = 0;                                            //ND
                        auxParametrosDer[0] = (double)SpinnerLEmpalmeHembra.getValue();     //Yinf Der
                        auxParametrosDer[1] = (double)SpinnerLEmpalmeHembra.getValue();     //Ysup Der
                        auxParametrosDer[2] = 0;                                            //ND
                        auxParametrosDer[3] = 0;                                            //ND
                        auxParametrosSup[0] = (double)SpinnerA1EmpalmeHembra.getValue();    //X Izq
                        auxParametrosSup[1] = (double)SpinnerA2EmpalmeHembra.getValue();    //X Der
                        auxParametrosSup[2] = (double)SpinnerLEmpalmeHembra.getValue();     //Y
                        auxParametrosSup[3] = (double)SpinnerCEmpalmeHembra.getValue();     //C

                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(6,6,9,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Empalme hembra");
                        Cortes.add(AuxCorteGlobal);

                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                        ActualizarDisplay();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"C debe ser mayor a 0","Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null,"A2 debe ser mayor a A1","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"A1 o A2 fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_Boton_ok_empalme_hembraActionPerformed

    private void Boton_cancel_empalme_hembraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_empalme_hembraActionPerformed
        Empalme_hembra_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_empalme_hembraActionPerformed

    private void Boton_empalme_hembraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_empalme_hembraActionPerformed
        Agregar_dialog.setVisible(false);
        Empalme_hembra_dialog.setVisible(true);
        Empalme_hembra_dialog.setLocationRelativeTo(this);
        Empalme_hembra_dialog.setTitle("Empalme hembra");
    }//GEN-LAST:event_Boton_empalme_hembraActionPerformed

    private void Boton_ok_agujero_platinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_agujero_platinaActionPerformed
        double test_var1=(double)SpinnerAIIAgujeroPlatina.getValue();
        double test_var2=(double)SpinnerAIDAgujeroPlatina.getValue();
        double test_var3=(double)SpinnerASIAgujeroPlatina.getValue();
        double test_var4=(double)SpinnerASDAgujeroPlatina.getValue();
        double test_var5=(Material.GetAlto()-Material.GetEspesorAlma())/2;
        
        if(test_var1<=test_var5&&test_var2<=test_var5&&test_var3<=test_var5&&test_var4<=test_var5){
            Agujero_platina_dialog.setVisible(false);
            Agregar_dialog.setVisible(false);

            double a=(double)SpinnerLIIAgujeroPlatina.getValue()+(double)SpinnerAIIAgujeroPlatina.getValue()+(double)SpinnerLSIAgujeroPlatina.getValue()+(double)SpinnerASIAgujeroPlatina.getValue();
            double b=(double)SpinnerLIDAgujeroPlatina.getValue()+(double)SpinnerAIDAgujeroPlatina.getValue()+(double)SpinnerLSDAgujeroPlatina.getValue()+(double)SpinnerASDAgujeroPlatina.getValue();

            if((a!=0)||(b!=0)){

                    //Posicion inferior izquierda

                    if(((double)SpinnerLIIAgujeroPlatina.getValue()!=0)&&((double)SpinnerAIIAgujeroPlatina.getValue()!=0)){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];
                        auxParametrosIzq[0] = (double)SpinnerYIIAgujeroPlatina.getValue();                                  //Yinf Izq
                        auxParametrosIzq[1] = (double)Material.GetAlto()/2+(double)Material.GetEspesorAlma()/2;             //Zinf Izq
                        auxParametrosIzq[2] = (double)SpinnerAIIAgujeroPlatina.getValue();                                  //Ainf Izq
                        auxParametrosIzq[3] = (double)SpinnerLIIAgujeroPlatina.getValue();                                  //Linf Izq      
                        auxParametrosDer[0] = 0;    
                        auxParametrosDer[1] = 0;   
                        auxParametrosDer[2] = 0;                                     
                        auxParametrosDer[3] = 0;                                  
                        auxParametrosSup[0] = 0; 
                        auxParametrosSup[1] = 0;  
                        auxParametrosSup[2] = 0;    
                        auxParametrosSup[3] = 0; 
                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(4,0,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Agujero platina");
                        Cortes.add(AuxCorteGlobal);
                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                    }

                    //Esquina superior izquierda

                    if(((double)SpinnerLSIAgujeroPlatina.getValue()!=0)&&((double)SpinnerASIAgujeroPlatina.getValue()!=0)){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];
                        auxParametrosIzq[0] = (double)SpinnerYSIAgujeroPlatina.getValue();                                                                  //Ysup Izq
                        auxParametrosIzq[1] = (double)Material.GetAlto()/2-(double)Material.GetEspesorAlma()/2-(double)SpinnerASIAgujeroPlatina.getValue(); //Zsup Izq
                        auxParametrosIzq[2] = (double)SpinnerASIAgujeroPlatina.getValue();                                                                  //Asup Izq
                        auxParametrosIzq[3] = (double)SpinnerLSIAgujeroPlatina.getValue();                                                                  //Lsup Izq      
                        auxParametrosDer[0] = 0;    
                        auxParametrosDer[1] = 0;   
                        auxParametrosDer[2] = 0;                                     
                        auxParametrosDer[3] = 0;                                  
                        auxParametrosSup[0] = 0; 
                        auxParametrosSup[1] = 0;  
                        auxParametrosSup[2] = 0;    
                        auxParametrosSup[3] = 0; 
                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(4,0,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Agujero platina");
                        Cortes.add(AuxCorteGlobal);
                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                    }

                    //Esquina inferior derecha

                    if(((double)SpinnerLIDAgujeroPlatina.getValue()!=0)&&((double)SpinnerAIDAgujeroPlatina.getValue()!=0)){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];
                        auxParametrosIzq[0] = 0;
                        auxParametrosIzq[1] = 0;
                        auxParametrosIzq[2] = 0;
                        auxParametrosIzq[3] = 0;     
                        auxParametrosDer[0] = (double)SpinnerYIDAgujeroPlatina.getValue();                                  //Yinf Izq    
                        auxParametrosDer[1] = (double)Material.GetAlto()/2+(double)Material.GetEspesorAlma()/2;             //Zinf Izq   
                        auxParametrosDer[2] = (double)SpinnerAIDAgujeroPlatina.getValue();                                  //Ainf Izq                                     
                        auxParametrosDer[3] = (double)SpinnerLIDAgujeroPlatina.getValue();                                  //Linf Izq                                  
                        auxParametrosSup[0] = 0; 
                        auxParametrosSup[1] = 0;  
                        auxParametrosSup[2] = 0;    
                        auxParametrosSup[3] = 0; 
                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(0,4,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Agujero platina");
                        Cortes.add(AuxCorteGlobal);
                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                    }

                    //Esquina superior derecha

                    if(((double)SpinnerLSDAgujeroPlatina.getValue()!=0)&&((double)SpinnerASDAgujeroPlatina.getValue()!=0)){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];
                        auxParametrosIzq[0] = 0;
                        auxParametrosIzq[1] = 0;
                        auxParametrosIzq[2] = 0;
                        auxParametrosIzq[3] = 0;
                        auxParametrosDer[0] = (double)SpinnerYSDAgujeroPlatina.getValue();                                                                  //Ysup Izq    
                        auxParametrosDer[1] = (double)Material.GetAlto()/2-(double)Material.GetEspesorAlma()/2-(double)SpinnerASDAgujeroPlatina.getValue(); //Zsup Izq   
                        auxParametrosDer[2] = (double)SpinnerASDAgujeroPlatina.getValue();                                                                  //Asup Izq                                     
                        auxParametrosDer[3] = (double)SpinnerLSDAgujeroPlatina.getValue();                                                                  //Lsup Izq                                        
                        auxParametrosSup[0] = 0; 
                        auxParametrosSup[1] = 0;  
                        auxParametrosSup[2] = 0;    
                        auxParametrosSup[3] = 0; 
                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(0,4,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Agujero platina");
                        Cortes.add(AuxCorteGlobal);
                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                    }

                    ActualizarDisplay();
            }
        }else{
            JOptionPane.showMessageDialog(null,"Altura máxima del agujero superada","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_Boton_ok_agujero_platinaActionPerformed

    private void Boton_cancel_agujero_platinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_agujero_platinaActionPerformed
        Agujero_platina_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_agujero_platinaActionPerformed

    private void Boton_agujero_platinaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_agujero_platinaActionPerformed
        Agregar_dialog.setVisible(false);
        Agujero_platina_dialog.setVisible(true);
        Agujero_platina_dialog.setLocationRelativeTo(this);
        Agujero_platina_dialog.setTitle("Agujero platina");
    }//GEN-LAST:event_Boton_agujero_platinaActionPerformed

    private void Boton_ok_redondo_alaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_redondo_alaActionPerformed
        double test_var1=(double)SpinnerYRedondoAla.getValue();
        double test_var2=(double)SpinnerZRedondoAla.getValue();
        double test_var3=(double)SpinnerDRedondoAla.getValue()/2;
        double test_var4=(Material.GetAlto()-Material.GetEspesorAlma())/2;
        double test_var5=(Material.GetAlto()+Material.GetEspesorAlma())/2;
        
        if((test_var1-test_var3)>=0){
            if( (((test_var2+test_var3)<=test_var4)&&((test_var2-test_var3)>=0))|| (((test_var2+test_var3)<=Material.GetAlto())&&((test_var2-test_var3)>=test_var5))){
                Redondo_ala_dialog.setVisible(false);
                Agregar_dialog.setVisible(false);

                if((double)SpinnerDRedondoAla.getValue()>=8){
                    auxParametrosIzq = new double[4];
                    auxParametrosDer = new double[4];
                    auxParametrosSup = new double[4];

                    int izq;
                    int der;

                    if(Check_izq_redondo_ala.isSelected())
                        izq=1;
                    else
                        izq=0;

                    if(Check_der_redondo_ala.isSelected())
                        der=1;
                    else
                        der=0;  

                    if(izq==1){
                        auxParametrosIzq[0] = (double)SpinnerYRedondoAla.getValue();    //Ycentro
                        auxParametrosIzq[1] = (double)SpinnerZRedondoAla.getValue();    //Zcentro
                        auxParametrosIzq[2] = (double)SpinnerDRedondoAla.getValue();    //Diametro
                        auxParametrosIzq[3] = 0;                                        //ND
                    }else{
                        auxParametrosIzq[0] = 0;                                        //Ycentro
                        auxParametrosIzq[1] = 0;                                        //Zcentro
                        auxParametrosIzq[2] = 0;                                        //Diametro
                        auxParametrosIzq[3] = 0;                                        //ND
                    }

                    if(der==1){
                        auxParametrosDer[0] = (double)SpinnerYRedondoAla.getValue();    //Ycentro
                        auxParametrosDer[1] = (double)SpinnerZRedondoAla.getValue();    //Zcentro
                        auxParametrosDer[2] = (double)SpinnerDRedondoAla.getValue();    //Diametro
                        auxParametrosDer[3] = 0;                                        //ND
                    }else{
                        auxParametrosDer[0] = 0;                                        //Ycentro
                        auxParametrosDer[1] = 0;                                        //Zcentro
                        auxParametrosDer[2] = 0;                                        //Diametro
                        auxParametrosDer[3] = 0;                                        //ND
                    }

                    auxParametrosSup[0] = 0;                                            //ND
                    auxParametrosSup[1] = 0;                                            //ND
                    auxParametrosSup[2] = 0;                                            //ND
                    auxParametrosSup[3] = 0;                                            //ND

                    AuxCorteGlobal = new Corte();

                    if(izq+der!=0){
                        AuxCorteGlobal = GenerarCorte(izq,der,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Agujero redondo ala");
                        Cortes.add(AuxCorteGlobal);

                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                        ActualizarDisplay();
                    }
                }
            }else{
                JOptionPane.showMessageDialog(null,"Agujero fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Agujero muy a la izquierda","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_Boton_ok_redondo_alaActionPerformed

    private void Boton_cancel_redondo_alaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_redondo_alaActionPerformed
        Redondo_ala_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_redondo_alaActionPerformed

    private void Boton_redondo_alaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_redondo_alaActionPerformed
        Agregar_dialog.setVisible(false);
        Redondo_ala_dialog.setVisible(true);
        Redondo_ala_dialog.setLocationRelativeTo(this);
        Redondo_ala_dialog.setTitle("Agujero redondo ala");
    }//GEN-LAST:event_Boton_redondo_alaActionPerformed

    private void Boton_ok_oblongo_ala_vActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_oblongo_ala_vActionPerformed
        
        double test_var1=(double)SpinnerYOblongoAlaV.getValue();
        double test_var2=(double)SpinnerZOblongoAlaV.getValue();
        double test_var3=(double)SpinnerDOblongoAlaV.getValue()/2;
        double test_var4=(double)SpinnerLOblongoAlaV.getValue()/2;
        double test_var5=(Material.GetAlto()-Material.GetEspesorAlma())/2;
        double test_var6=(Material.GetAlto()+Material.GetEspesorAlma())/2;
        
        if((test_var1-test_var3)>=0){
            if( (((test_var2+test_var4)<=test_var5)&&((test_var2-test_var4)>=0))|| (((test_var2+test_var4)<=Material.GetAlto())&&((test_var2-test_var4)>=test_var6))){
                if(test_var4>test_var3){
                    Oblongo_ala_v_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);

                    if((double)SpinnerDOblongoAlaV.getValue()>=8){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];

                        int izq;
                        int der;

                        if(Check_izq_oblongo_ala_v.isSelected())
                            izq=2;
                        else
                            izq=0;

                        if(Check_der_oblongo_ala_v.isSelected())
                            der=2;
                        else
                            der=0;  

                        if(izq==2){
                            auxParametrosIzq[0] = (double)SpinnerYOblongoAlaV.getValue();    //Ycentro
                            auxParametrosIzq[1] = (double)SpinnerZOblongoAlaV.getValue();    //Zcentro
                            auxParametrosIzq[2] = (double)SpinnerDOblongoAlaV.getValue();    //Diametro
                            auxParametrosIzq[3] = (double)SpinnerLOblongoAlaV.getValue();    //Largo
                        }else{
                            auxParametrosIzq[0] = 0;                                        //Ycentro
                            auxParametrosIzq[1] = 0;                                        //Zcentro
                            auxParametrosIzq[2] = 0;                                        //Diametro
                            auxParametrosIzq[3] = 0;                                        //Largo
                        }

                        if(der==2){
                            auxParametrosDer[0] = (double)SpinnerYOblongoAlaV.getValue();    //Ycentro
                            auxParametrosDer[1] = (double)SpinnerZOblongoAlaV.getValue();    //Zcentro
                            auxParametrosDer[2] = (double)SpinnerDOblongoAlaV.getValue();    //Diametro
                            auxParametrosDer[3] = (double)SpinnerLOblongoAlaV.getValue();                                        //Largo
                        }else{
                            auxParametrosDer[0] = 0;                                        //Ycentro
                            auxParametrosDer[1] = 0;                                        //Zcentro
                            auxParametrosDer[2] = 0;                                        //Diametro
                            auxParametrosDer[3] = 0;                                        //Largo
                        }

                        auxParametrosSup[0] = 0;                                            //ND
                        auxParametrosSup[1] = 0;                                            //ND
                        auxParametrosSup[2] = 0;                                            //ND
                        auxParametrosSup[3] = 0;                                            //ND

                        AuxCorteGlobal = new Corte();

                        if(izq+der!=0){
                            AuxCorteGlobal = GenerarCorte(izq,der,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                            AuxCorteGlobal.Setear_indice(indice_corte);
                            AuxCorteGlobal.Setear_nombre("Agujero oblongo vertical ala");
                            Cortes.add(AuxCorteGlobal);

                            modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                            modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                            modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                            indice_corte++;
                            ActualizarDisplay();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"L debe ser mayor a D","Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null,"Agujero fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Agujero muy a la izquierda","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_Boton_ok_oblongo_ala_vActionPerformed

    private void Boton_cancel_oblongo_ala_vActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_oblongo_ala_vActionPerformed
        Oblongo_ala_v_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_oblongo_ala_vActionPerformed

    private void Boton_oblongo_ala_VActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_oblongo_ala_VActionPerformed
        Agregar_dialog.setVisible(false);
        Oblongo_ala_v_dialog.setVisible(true);
        Oblongo_ala_v_dialog.setLocationRelativeTo(this);
        Oblongo_ala_v_dialog.setTitle("Agujero oblongo vertical ala");
    }//GEN-LAST:event_Boton_oblongo_ala_VActionPerformed

    private void Boton_ok_oblongo_ala_hActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_oblongo_ala_hActionPerformed
        double test_var1=(double)SpinnerYOblongoAlaH.getValue();
        double test_var2=(double)SpinnerZOblongoAlaH.getValue();
        double test_var3=(double)SpinnerLOblongoAlaH.getValue()/2;
        double test_var4=(double)SpinnerDOblongoAlaH.getValue()/2;
        double test_var5=(Material.GetAlto()-Material.GetEspesorAlma())/2;
        double test_var6=(Material.GetAlto()+Material.GetEspesorAlma())/2;
        
        if((test_var1-test_var3)>=0){
            if( (((test_var2+test_var4)<=test_var5)&&((test_var2-test_var4)>=0))|| (((test_var2+test_var4)<=Material.GetAlto())&&((test_var2-test_var4)>=test_var6))){
                if(test_var3>test_var4){
                    Oblongo_ala_h_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);

                    if((double)SpinnerDOblongoAlaH.getValue()>=8){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];

                        int izq;
                        int der;

                        if(Check_izq_oblongo_ala_h.isSelected())
                            izq=3;
                        else
                            izq=0;

                        if(Check_der_oblongo_ala_h.isSelected())
                            der=3;
                        else
                            der=0;  

                        if(izq==3){
                            auxParametrosIzq[0] = (double)SpinnerYOblongoAlaH.getValue();    //Ycentro
                            auxParametrosIzq[1] = (double)SpinnerZOblongoAlaH.getValue();    //Zcentro
                            auxParametrosIzq[2] = (double)SpinnerDOblongoAlaH.getValue();    //Diametro
                            auxParametrosIzq[3] = (double)SpinnerLOblongoAlaH.getValue();    //Largo
                        }else{
                            auxParametrosIzq[0] = 0;                                        //Ycentro
                            auxParametrosIzq[1] = 0;                                        //Zcentro
                            auxParametrosIzq[2] = 0;                                        //Diametro
                            auxParametrosIzq[3] = 0;                                        //Largo
                        }

                        if(der==3){
                            auxParametrosDer[0] = (double)SpinnerYOblongoAlaH.getValue();    //Ycentro
                            auxParametrosDer[1] = (double)SpinnerZOblongoAlaH.getValue();    //Zcentro
                            auxParametrosDer[2] = (double)SpinnerDOblongoAlaH.getValue();    //Diametro
                            auxParametrosDer[3] = (double)SpinnerLOblongoAlaH.getValue();                                        //Largo
                        }else{
                            auxParametrosDer[0] = 0;                                        //Ycentro
                            auxParametrosDer[1] = 0;                                        //Zcentro
                            auxParametrosDer[2] = 0;                                        //Diametro
                            auxParametrosDer[3] = 0;                                        //Largo
                        }

                        auxParametrosSup[0] = 0;                                            //ND
                        auxParametrosSup[1] = 0;                                            //ND
                        auxParametrosSup[2] = 0;                                            //ND
                        auxParametrosSup[3] = 0;                                            //ND

                        AuxCorteGlobal = new Corte();

                        if(izq+der!=0){
                            AuxCorteGlobal = GenerarCorte(izq,der,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                            AuxCorteGlobal.Setear_indice(indice_corte);
                            AuxCorteGlobal.Setear_nombre("Agujero oblongo horizontal ala");
                            Cortes.add(AuxCorteGlobal);

                            modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                            modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                            modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                            indice_corte++;
                            ActualizarDisplay();
                        }
                    }

                }else{
                    JOptionPane.showMessageDialog(null,"L debe ser mayor a D","Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null,"Agujero fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Agujero muy a la izquierda","Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_Boton_ok_oblongo_ala_hActionPerformed

    private void Boton_cancel_oblongo_ala_hActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_oblongo_ala_hActionPerformed
        Oblongo_ala_h_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_oblongo_ala_hActionPerformed

    private void Check_der_oblongo_ala_hActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Check_der_oblongo_ala_hActionPerformed
        
    }//GEN-LAST:event_Check_der_oblongo_ala_hActionPerformed

    private void Boton_oblongo_ala_HActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_oblongo_ala_HActionPerformed
        Agregar_dialog.setVisible(false);
        Oblongo_ala_h_dialog.setVisible(true);
        Oblongo_ala_h_dialog.setLocationRelativeTo(this);
        Oblongo_ala_h_dialog.setTitle("Agujero oblongo vertical ala");
    }//GEN-LAST:event_Boton_oblongo_ala_HActionPerformed

    private void Boton_ok_redondo_almaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_redondo_almaActionPerformed
        double dist_seg;
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        double test_var1=(double)SpinnerXRedondoAlma.getValue();
        double test_var2=(double)SpinnerYRedondoAlma.getValue();
        double test_var3=(double)SpinnerDRedondoAlma.getValue()/2;
        double test_var4=Material.GetEspesorAla()+dist_seg;
        double test_var5=Material.GetAncho()-Material.GetEspesorAla()-dist_seg;
        
        if((test_var2-test_var3)>=0){
            if(((test_var1-test_var3)>=test_var4)&&((test_var1+test_var3)<=(test_var5))){
                Redondo_alma_dialog.setVisible(false);
                Agregar_dialog.setVisible(false);

                if((double)SpinnerDRedondoAlma.getValue()>=8){
                    auxParametrosIzq = new double[4];
                    auxParametrosDer = new double[4];
                    auxParametrosSup = new double[4];

                    auxParametrosIzq[0] = 0;    //ND
                    auxParametrosIzq[1] = 0;    //ND
                    auxParametrosIzq[2] = 0;    //ND
                    auxParametrosIzq[3] = 0;    //ND
                    auxParametrosDer[0] = 0;    //ND
                    auxParametrosDer[1] = 0;    //ND
                    auxParametrosDer[2] = 0;    //ND
                    auxParametrosDer[3] = 0;    //ND                   
                    auxParametrosSup[0] = (double)SpinnerXRedondoAlma.getValue();   //Xcentro
                    auxParametrosSup[1] = (double)SpinnerYRedondoAlma.getValue();   //Ycentro
                    auxParametrosSup[2] = (double)SpinnerDRedondoAlma.getValue();   //Diametro
                    auxParametrosSup[3] = 0;                                        //ND

                    AuxCorteGlobal = new Corte();
                    AuxCorteGlobal = GenerarCorte(0,0,1,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                    AuxCorteGlobal.Setear_indice(indice_corte);
                    AuxCorteGlobal.Setear_nombre("Agujero redondo alma");
                    Cortes.add(AuxCorteGlobal);

                    modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    indice_corte++;
                    ActualizarDisplay();

                }
            }else{
                JOptionPane.showMessageDialog(null,"Agujero fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Agujero fuera de la viga","Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_Boton_ok_redondo_almaActionPerformed

    private void Boton_cancel_redondo_almaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_redondo_almaActionPerformed
        Redondo_alma_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_redondo_almaActionPerformed

    private void Boton_redondo_almaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_redondo_almaActionPerformed
        Agregar_dialog.setVisible(false);
        Redondo_alma_dialog.setVisible(true);
        Redondo_alma_dialog.setLocationRelativeTo(this);
        Redondo_alma_dialog.setTitle("Agujero redondo alma");
    }//GEN-LAST:event_Boton_redondo_almaActionPerformed

    private void Boton_ok_oblongo_alma_vActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_oblongo_alma_vActionPerformed
        
        double dist_seg;
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        double test_var1=(double)SpinnerXOblongoAlmaV.getValue();
        double test_var2=(double)SpinnerYOblongoAlmaV.getValue();
        double test_var3=(double)SpinnerDOblongoAlmaV.getValue()/2;
        double test_var4=(double)SpinnerLOblongoAlmaV.getValue()/2;
        double test_var5=Material.GetEspesorAla()+dist_seg;
        double test_var6=Material.GetAncho()-Material.GetEspesorAla()-dist_seg;
        
        if((test_var2-test_var4)>=0){
            if(((test_var1-test_var3)>=(test_var5))&&((test_var1+test_var3)<=(test_var6))){
                if(test_var4>test_var3){
                    Oblongo_alma_v_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);

                    if((double)SpinnerDOblongoAlmaV.getValue()>=8){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];

                        auxParametrosIzq[0] = 0;    //ND
                        auxParametrosIzq[1] = 0;    //ND
                        auxParametrosIzq[2] = 0;    //ND
                        auxParametrosIzq[3] = 0;    //ND
                        auxParametrosDer[0] = 0;    //ND
                        auxParametrosDer[1] = 0;    //ND
                        auxParametrosDer[2] = 0;    //ND
                        auxParametrosDer[3] = 0;    //ND                   
                        auxParametrosSup[0] = (double)SpinnerXOblongoAlmaV.getValue();  //Xcentro
                        auxParametrosSup[1] = (double)SpinnerYOblongoAlmaV.getValue();  //Ycentro
                        auxParametrosSup[2] = (double)SpinnerDOblongoAlmaV.getValue();  //Diametro
                        auxParametrosSup[3] = (double)SpinnerLOblongoAlmaV.getValue();  //Largo

                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(0,0,2,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Agujero oblongo vertical alma");
                        Cortes.add(AuxCorteGlobal);

                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                        ActualizarDisplay();
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"L debe ser mayor a D","Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null,"Agujero fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Agujero fuera de la viga","Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_Boton_ok_oblongo_alma_vActionPerformed

    private void Boton_cancel_oblongo_alma_vActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_oblongo_alma_vActionPerformed
        Oblongo_alma_v_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_oblongo_alma_vActionPerformed

    private void Boton_oblongo_alma_VActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_oblongo_alma_VActionPerformed
        Agregar_dialog.setVisible(false);
        Oblongo_alma_v_dialog.setVisible(true);
        Oblongo_alma_v_dialog.setLocationRelativeTo(this);
        Oblongo_alma_v_dialog.setTitle("Agujero oblongo vertical alma");
    }//GEN-LAST:event_Boton_oblongo_alma_VActionPerformed

    private void Boton_ok_oblongo_alma_hActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_oblongo_alma_hActionPerformed
        double dist_seg;
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        double test_var1=(double)SpinnerXOblongoAlmaH.getValue();
        double test_var2=(double)SpinnerYOblongoAlmaH.getValue();
        double test_var3=(double)SpinnerLOblongoAlmaH.getValue()/2;
        double test_var4=(double)SpinnerDOblongoAlmaH.getValue()/2;
        double test_var5=Material.GetEspesorAla()+dist_seg;
        double test_var6=Material.GetAncho()-Material.GetEspesorAla()-dist_seg;
        
        if((test_var2-test_var4)>=0){
            if(((test_var1-test_var3)>=(test_var5))&&((test_var1+test_var3)<=(test_var6))){
                if(test_var3>test_var4){
                    Oblongo_alma_h_dialog.setVisible(false);
                    Agregar_dialog.setVisible(false);

                    if((double)SpinnerDOblongoAlmaH.getValue()>=8){
                        auxParametrosIzq = new double[4];
                        auxParametrosDer = new double[4];
                        auxParametrosSup = new double[4];

                        auxParametrosIzq[0] = 0;    //ND
                        auxParametrosIzq[1] = 0;    //ND
                        auxParametrosIzq[2] = 0;    //ND
                        auxParametrosIzq[3] = 0;    //ND
                        auxParametrosDer[0] = 0;    //ND
                        auxParametrosDer[1] = 0;    //ND
                        auxParametrosDer[2] = 0;    //ND
                        auxParametrosDer[3] = 0;    //ND                   
                        auxParametrosSup[0] = (double)SpinnerXOblongoAlmaH.getValue();  //Xcentro
                        auxParametrosSup[1] = (double)SpinnerYOblongoAlmaH.getValue();  //Ycentro
                        auxParametrosSup[2] = (double)SpinnerDOblongoAlmaH.getValue();  //Diametro
                        auxParametrosSup[3] = (double)SpinnerLOblongoAlmaH.getValue();  //Largo

                        AuxCorteGlobal = new Corte();
                        AuxCorteGlobal = GenerarCorte(0,0,3,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                        AuxCorteGlobal.Setear_indice(indice_corte);
                        AuxCorteGlobal.Setear_nombre("Agujero oblongo horizontal alma");
                        Cortes.add(AuxCorteGlobal);

                        modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                        indice_corte++;
                        ActualizarDisplay();

                    }
                    }else{
                    JOptionPane.showMessageDialog(null,"L debe ser mayor a D","Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null,"Agujero fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Agujero fuera de la viga","Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_Boton_ok_oblongo_alma_hActionPerformed

    private void Boton_cancel_oblongo_alma_hActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_oblongo_alma_hActionPerformed
        Oblongo_alma_h_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_oblongo_alma_hActionPerformed

    private void Boton_oblongo_alma_HActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_oblongo_alma_HActionPerformed
        Agregar_dialog.setVisible(false);
        Oblongo_alma_h_dialog.setVisible(true);
        Oblongo_alma_h_dialog.setLocationRelativeTo(this);
        Oblongo_alma_h_dialog.setTitle("Agujero oblongo horizontal alma");
    }//GEN-LAST:event_Boton_oblongo_alma_HActionPerformed

    private void Boton_ok_agujero_rect_alaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_agujero_rect_alaActionPerformed
       
        double dist_seg;
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        double test_var1=(double)SpinnerYAgujeroRectAla.getValue();
        double test_var2=(double)SpinnerZAgujeroRectAla.getValue();
        double test_var3=(double)SpinnerAAgujeroRectAla.getValue();
        double test_var4=(Material.GetAlto()-Material.GetEspesorAlma())/2;
        double test_var5=(Material.GetAlto()+Material.GetEspesorAlma())/2;
        
        boolean arriba = (test_var2>=0)&&(test_var2<test_var4)&&((test_var2+test_var3)<=(test_var4));
        boolean abajo = ((test_var2>=test_var5)&&(test_var2<Material.GetAlto())&&((test_var2+test_var3)<=Material.GetAlto()));
        
        if(arriba||abajo){
            Agujero_rect_ala_dialog.setVisible(false);
            Agregar_dialog.setVisible(false);

            double a=(double)SpinnerLAgujeroRectAla.getValue()+(double)SpinnerAAgujeroRectAla.getValue();

            if(a!=0){

                auxParametrosIzq = new double[4];
                auxParametrosDer = new double[4];
                auxParametrosSup = new double[4];

                int izq;
                int der;

                if(Check_izq_agujero_rect_ala.isSelected())
                    izq=4;
                else
                    izq=0;

                if(Check_der_agujero_rect_ala.isSelected())
                    der=4;
                else
                    der=0;  

                if(((double)SpinnerLAgujeroRectAla.getValue()!=0)&&((double)SpinnerAAgujeroRectAla.getValue()!=0)){

                    if(izq!=0){
                        auxParametrosIzq[0] = (double)SpinnerYAgujeroRectAla.getValue();    //Y
                        auxParametrosIzq[1] = (double)SpinnerZAgujeroRectAla.getValue();    //Z
                        auxParametrosIzq[2] = (double)SpinnerAAgujeroRectAla.getValue();    //A
                        auxParametrosIzq[3] = (double)SpinnerLAgujeroRectAla.getValue();    //L      
                    }else{
                        auxParametrosIzq[0] = 0;    //ND
                        auxParametrosIzq[1] = 0;    //ND
                        auxParametrosIzq[2] = 0;    //ND
                        auxParametrosIzq[3] = 0;    //ND  
                    }

                    if(der!=0){
                        auxParametrosDer[0] = (double)SpinnerYAgujeroRectAla.getValue();    //Y
                        auxParametrosDer[1] = (double)SpinnerZAgujeroRectAla.getValue();    //Z
                        auxParametrosDer[2] = (double)SpinnerAAgujeroRectAla.getValue();    //A
                        auxParametrosDer[3] = (double)SpinnerLAgujeroRectAla.getValue();    //L      
                    }else{
                        auxParametrosDer[0] = 0;    //ND
                        auxParametrosDer[1] = 0;    //ND
                        auxParametrosDer[2] = 0;    //ND
                        auxParametrosDer[3] = 0;    //ND  
                    }

                    auxParametrosSup[0] = 0; 
                    auxParametrosSup[1] = 0;  
                    auxParametrosSup[2] = 0;    
                    auxParametrosSup[3] = 0; 
                    AuxCorteGlobal = new Corte();
                    AuxCorteGlobal = GenerarCorte(izq,der,0,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                    AuxCorteGlobal.Setear_indice(indice_corte);
                    AuxCorteGlobal.Setear_nombre("Agujero rectangular ala");
                    Cortes.add(AuxCorteGlobal);
                    modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    indice_corte++;

                }    
                ActualizarDisplay();
            }
        }else{
            JOptionPane.showMessageDialog(null,"Agujero fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_Boton_ok_agujero_rect_alaActionPerformed

    private void Boton_cancel_agujero_rect_alaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_agujero_rect_alaActionPerformed
        Agujero_rect_ala_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_agujero_rect_alaActionPerformed

    private void Check_der_agujero_rect_alaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Check_der_agujero_rect_alaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Check_der_agujero_rect_alaActionPerformed

    private void Boton_rectangular_alaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_rectangular_alaActionPerformed
        Agregar_dialog.setVisible(false);
        Agujero_rect_ala_dialog.setVisible(true);
        Agujero_rect_ala_dialog.setLocationRelativeTo(this);
        Agujero_rect_ala_dialog.setTitle("Agujero rectangular ala");
    }//GEN-LAST:event_Boton_rectangular_alaActionPerformed

    private void Boton_ok_agujero_rect_almaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_ok_agujero_rect_almaActionPerformed
        
        double dist_seg;
        if(Material.GetRadioAlma()<20)
            dist_seg=20;
        else
            dist_seg=Material.GetRadioAlma();
        
        double test_var1=(double)SpinnerXAgujeroRectAlma.getValue();
        double test_var3=(double)SpinnerLAgujeroRectAlma.getValue();
        double test_var4=Material.GetEspesorAla()+dist_seg;
        double test_var5=Material.GetAncho()-Material.GetEspesorAla()-dist_seg;
        
      
        if((test_var1>=test_var4)&&(test_var1<test_var5)&&((test_var1+test_var3)<=test_var5)){
        
            Agujero_rect_alma_dialog.setVisible(false);
            Agregar_dialog.setVisible(false);

            double a=(double)SpinnerLAgujeroRectAlma.getValue()+(double)SpinnerAAgujeroRectAlma.getValue();

            if(a!=0){

                auxParametrosIzq = new double[4];
                auxParametrosDer = new double[4];
                auxParametrosSup = new double[4];

                if(((double)SpinnerLAgujeroRectAlma.getValue()!=0)&&((double)SpinnerAAgujeroRectAlma.getValue()!=0)){

                    auxParametrosIzq[0] = 0;    //ND
                    auxParametrosIzq[1] = 0;    //ND
                    auxParametrosIzq[2] = 0;    //ND
                    auxParametrosIzq[3] = 0;    //ND  
                    auxParametrosDer[0] = 0;    //ND
                    auxParametrosDer[1] = 0;    //ND
                    auxParametrosDer[2] = 0;    //ND
                    auxParametrosDer[3] = 0;    //ND  

                    auxParametrosSup[0] = (double)SpinnerXAgujeroRectAlma.getValue();   //X
                    auxParametrosSup[1] = (double)SpinnerYAgujeroRectAlma.getValue();   //Y
                    auxParametrosSup[2] = (double)SpinnerAAgujeroRectAlma.getValue();   //A
                    auxParametrosSup[3] = (double)SpinnerLAgujeroRectAlma.getValue();   //L
                    AuxCorteGlobal = new Corte();
                    AuxCorteGlobal = GenerarCorte(0,0,4,auxParametrosIzq,auxParametrosDer,auxParametrosSup);
                    AuxCorteGlobal.Setear_indice(indice_corte);
                    AuxCorteGlobal.Setear_nombre("Agujero rectangular alma");
                    Cortes.add(AuxCorteGlobal);
                    modelo_izq.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_der.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    modelo_sup.addElement("OP"+indice_corte+": "+AuxCorteGlobal.GetNombre());
                    indice_corte++;

                }    
                ActualizarDisplay();
            }
        }else{
            JOptionPane.showMessageDialog(null,"Agujero fuera de rango","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_Boton_ok_agujero_rect_almaActionPerformed

    private void Boton_cancel_agujero_rect_almaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_cancel_agujero_rect_almaActionPerformed
        Agujero_rect_alma_dialog.setVisible(false);
        Agregar_dialog.setVisible(false);
    }//GEN-LAST:event_Boton_cancel_agujero_rect_almaActionPerformed

    private void Boton_rectangular_almaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton_rectangular_almaActionPerformed
        Agregar_dialog.setVisible(false);
        Agujero_rect_alma_dialog.setVisible(true);
        Agujero_rect_alma_dialog.setLocationRelativeTo(this);
        Agujero_rect_alma_dialog.setTitle("Agujero rectangular ala");
    }//GEN-LAST:event_Boton_rectangular_almaActionPerformed

    private void AgregarOpSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AgregarOpSupActionPerformed
        Agregar_dialog.setVisible(true);
        Agregar_dialog.setLocationRelativeTo(this);
    }//GEN-LAST:event_AgregarOpSupActionPerformed

    private void BorrarAllSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarAllSupActionPerformed
        BorrarCortes();
    }//GEN-LAST:event_BorrarAllSupActionPerformed

    private void BorrarAllDerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarAllDerActionPerformed
        BorrarCortes();
    }//GEN-LAST:event_BorrarAllDerActionPerformed

    private void ListaOpIzqValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_ListaOpIzqValueChanged
        if(!evt.getValueIsAdjusting()) {
            if((modelo_izq.getSize()>0)&&(ListaOpIzq.getSelectedIndex()!=-1)){
                int n=ListaOpIzq.getSelectedIndex();
                ListaOpDer.setSelectedIndex(n);
                ListaOpSup.setSelectedIndex(n);
                
                for(int j=0;j<(Cortes.get(n).GetPreviewIzq().getAll().size());j++){
                    Cortes.get(n).GetPreviewIzq().getDibujo(j).SetearColor(Color.RED);
                }
                for(int j=0;j<(Cortes.get(n).GetPreviewDer().getAll().size());j++){
                    Cortes.get(n).GetPreviewDer().getDibujo(j).SetearColor(Color.RED);
                }
                for(int j=0;j<(Cortes.get(n).GetPreviewSup().getAll().size());j++){
                    Cortes.get(n).GetPreviewSup().getDibujo(j).SetearColor(Color.RED);
                }
                for(int i=0;i<Cortes.size();i++){
                    for(int j=0;j<Cortes.get(i).GetPreviewIzq().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewIzq().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewDer().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewDer().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewSup().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewSup().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                }
            }else if((modelo_izq.getSize()>0)&&(ListaOpIzq.getSelectedIndex()==-1)){
                for(int i=0;i<Cortes.size();i++){
                    for(int j=0;j<Cortes.get(i).GetPreviewIzq().getAll().size();j++){
                        Cortes.get(i).GetPreviewIzq().getDibujo(j).SetearColor(Color.GREEN);
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewDer().getAll().size();j++){
                        Cortes.get(i).GetPreviewDer().getDibujo(j).SetearColor(Color.GREEN);
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewSup().getAll().size();j++){
                        Cortes.get(i).GetPreviewSup().getDibujo(j).SetearColor(Color.GREEN);
                    }
                }
            }
            ActualizarDisplay();
        }
    }//GEN-LAST:event_ListaOpIzqValueChanged

    private void BorrarOpSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BorrarOpSupActionPerformed
        if((modelo_sup.getSize()>0)&&(ListaOpSup.getSelectedIndex()!=-1)){
            int n = ListaOpIzq.getSelectedIndex();
            modelo_izq.removeElementAt(n);
            modelo_der.removeElementAt(n);
            modelo_sup.removeElementAt(n);
            Cortes.remove(n);
            if(Cortes.size()>0){
                for(int i=Cortes.size()-1; i>=n; i--){
                    Cortes.get(i).Setear_indice(i);
                    modelo_izq.removeElementAt(i);
                    modelo_der.removeElementAt(i);
                    modelo_sup.removeElementAt(i);
                }
                
                for(int i=n; i<Cortes.size();i++){
                    modelo_izq.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                    modelo_der.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                    modelo_sup.addElement("OP"+(Cortes.get(i).GetIndice())+": "+Cortes.get(i).GetNombre());
                }
            }
        }
        indice_corte--;
        ActualizarDisplay();
    }//GEN-LAST:event_BorrarOpSupActionPerformed

    private void ListaOpSupValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_ListaOpSupValueChanged
        if(!evt.getValueIsAdjusting()) {
            if((modelo_sup.getSize()>0)&&(ListaOpSup.getSelectedIndex()!=-1)){
                int n=ListaOpSup.getSelectedIndex();
                ListaOpIzq.setSelectedIndex(n);
                ListaOpDer.setSelectedIndex(n);
                
                for(int j=0;j<(Cortes.get(n).GetPreviewIzq().getAll().size());j++){
                    Cortes.get(n).GetPreviewIzq().getDibujo(j).SetearColor(Color.RED);
                }
                for(int j=0;j<(Cortes.get(n).GetPreviewDer().getAll().size());j++){
                    Cortes.get(n).GetPreviewDer().getDibujo(j).SetearColor(Color.RED);
                }
                for(int j=0;j<(Cortes.get(n).GetPreviewSup().getAll().size());j++){
                    Cortes.get(n).GetPreviewSup().getDibujo(j).SetearColor(Color.RED);
                }
                for(int i=0;i<Cortes.size();i++){
                    for(int j=0;j<Cortes.get(i).GetPreviewIzq().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewIzq().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewDer().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewDer().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewSup().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewSup().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                }
            }else if((modelo_izq.getSize()>0)&&(ListaOpIzq.getSelectedIndex()==-1)){
                for(int i=0;i<Cortes.size();i++){
                    for(int j=0;j<Cortes.get(i).GetPreviewIzq().getAll().size();j++){
                        Cortes.get(i).GetPreviewIzq().getDibujo(j).SetearColor(Color.GREEN);
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewDer().getAll().size();j++){
                        Cortes.get(i).GetPreviewDer().getDibujo(j).SetearColor(Color.GREEN);
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewSup().getAll().size();j++){
                        Cortes.get(i).GetPreviewSup().getDibujo(j).SetearColor(Color.GREEN);
                    }
                }
            }
            ActualizarDisplay();
        }
    }//GEN-LAST:event_ListaOpSupValueChanged

    private void ListaOpDerValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_ListaOpDerValueChanged
        if(!evt.getValueIsAdjusting()) {
            if((modelo_der.getSize()>0)&&(ListaOpDer.getSelectedIndex()!=-1)){
                int n=ListaOpDer.getSelectedIndex();
                ListaOpIzq.setSelectedIndex(n);
                ListaOpSup.setSelectedIndex(n);
                for(int j=0;j<(Cortes.get(n).GetPreviewIzq().getAll().size());j++){
                    Cortes.get(n).GetPreviewIzq().getDibujo(j).SetearColor(Color.RED);
                }
                for(int j=0;j<(Cortes.get(n).GetPreviewDer().getAll().size());j++){
                    Cortes.get(n).GetPreviewDer().getDibujo(j).SetearColor(Color.RED);
                }
                for(int j=0;j<(Cortes.get(n).GetPreviewSup().getAll().size());j++){
                    Cortes.get(n).GetPreviewSup().getDibujo(j).SetearColor(Color.RED);
                }
                for(int i=0;i<Cortes.size();i++){
                    for(int j=0;j<Cortes.get(i).GetPreviewIzq().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewIzq().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewDer().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewDer().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewSup().getAll().size();j++){
                        if(i!=n){
                            Cortes.get(i).GetPreviewSup().getDibujo(j).SetearColor(Color.GREEN);
                        }
                    }
                }
            }else if((modelo_izq.getSize()>0)&&(ListaOpIzq.getSelectedIndex()==-1)){
                for(int i=0;i<Cortes.size();i++){
                    for(int j=0;j<Cortes.get(i).GetPreviewIzq().getAll().size();j++){
                        Cortes.get(i).GetPreviewIzq().getDibujo(j).SetearColor(Color.GREEN);
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewDer().getAll().size();j++){
                        Cortes.get(i).GetPreviewDer().getDibujo(j).SetearColor(Color.GREEN);
                    }
                    for(int j=0;j<Cortes.get(i).GetPreviewSup().getAll().size();j++){
                        Cortes.get(i).GetPreviewSup().getDibujo(j).SetearColor(Color.GREEN);
                    }
                }
            }
            ActualizarDisplay();
        }
    }//GEN-LAST:event_ListaOpDerValueChanged

    private void radio45ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio45ItemStateChanged
        if(radio45.isSelected()){
            label_boquilla.setText("220941");
            consumible=45;
        }
    }//GEN-LAST:event_radio45ItemStateChanged

    private void radio65ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio65ItemStateChanged
        if(radio65.isSelected()){
            label_boquilla.setText("220819");
            consumible=65;
        }
    }//GEN-LAST:event_radio65ItemStateChanged

    private void radio85ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radio85ItemStateChanged
        if(radio85.isSelected()){
            label_boquilla.setText("220816");
            consumible=85;
        }
    }//GEN-LAST:event_radio85ItemStateChanged

    private void BotonGuardarGcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarGcodeActionPerformed
        String path;
        
        if(Codigo.getSize()>0){   
            int a= chooser.showSaveDialog(jPanel1);
            if(a==JFileChooser.APPROVE_OPTION){             
                path = chooser.getSelectedFile().getAbsolutePath();
                chooser.setFileFilter(new FileNameExtensionFilter("ngc file","ngc"));
                if(path.endsWith(".ngc")){
                    chooser.setSelectedFile(new File(path));
                }else{
                    chooser.setSelectedFile(new File(path+".ngc"));
                }

                System.out.println(chooser.getSelectedFile().getAbsoluteFile());
                if(!chooser.getSelectedFile().exists()){
                    Codigo.guardarGcode(chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(null,"Codigo guardado exitosamente!","",JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    int opcion=JOptionPane.showConfirmDialog(null,"El archivo ya existe, sobreescribir?","Advertencia",JOptionPane.YES_NO_OPTION);
                    if(opcion==JOptionPane.YES_OPTION){
                        Codigo.guardarGcode(chooser.getSelectedFile().getAbsolutePath());
                        JOptionPane.showMessageDialog(null,"Codigo guardado exitosamente!","",JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }else{
            JOptionPane.showMessageDialog(null,"No se ha generado el codigo","Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_BotonGuardarGcodeActionPerformed

    private void Generar_operaciones_izq(Tools tool_ala){
        //Agujeros redondos ala izquierda
            for(int i=0;i<Cortes.size();i++){
                Operacion_corte corte_aux=Cortes.get(i).GetCorte();
                if(corte_aux.GetTipoIzq()==1){
                    Codigo.redondo_ala(-corte_aux.GetParamIzq()[0],-corte_aux.GetParamIzq()[1],corte_aux.GetParamIzq()[2],true,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho());
                }
            }
            //Agujeros oblongos verticales ala izquierda
            for(int i=0;i<Cortes.size();i++){
                Operacion_corte corte_aux=Cortes.get(i).GetCorte();
                if(corte_aux.GetTipoIzq()==2){
                    Codigo.oblongo_ala_v(-corte_aux.GetParamIzq()[0],-corte_aux.GetParamIzq()[1],corte_aux.GetParamIzq()[2],corte_aux.GetParamIzq()[3],true,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho());
                }
            }
            //Agujeros oblongos horizontales ala izquierda
            for(int i=0;i<Cortes.size();i++){
                Operacion_corte corte_aux=Cortes.get(i).GetCorte();
                if(corte_aux.GetTipoIzq()==3){
                    Codigo.oblongo_ala_h(-corte_aux.GetParamIzq()[0],-corte_aux.GetParamIzq()[1],corte_aux.GetParamIzq()[2],corte_aux.GetParamIzq()[3],true,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho());
                }
            }
            //Agujeros rectangulares
            for(int i=0;i<Cortes.size();i++){
                Operacion_corte corte_aux=Cortes.get(i).GetCorte();
                if(corte_aux.GetTipoIzq()==4){
                    Codigo.rectangular_ala(-corte_aux.GetParamIzq()[0],-corte_aux.GetParamIzq()[1],corte_aux.GetParamIzq()[2],corte_aux.GetParamIzq()[3],true,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho());
                }
            }
            //Corte en L (destijere 2)
            for(int i=0;i<Cortes.size();i++){
                Operacion_corte corte_aux=Cortes.get(i).GetCorte();
                if(corte_aux.GetTipoIzq()==5){
                    Codigo.corte_l_ala(corte_aux.GetParamIzq()[0],corte_aux.GetParamIzq()[1],corte_aux.GetParamIzq()[2],true,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho(),Material.GetAlto());
                }
            }
            for(int i=0;i<Cortes.size();i++){
                Operacion_corte corte_aux=Cortes.get(i).GetCorte();
                if(corte_aux.GetTipoIzq()==6){
                    Codigo.recta_ala(-corte_aux.GetParamIzq()[0],-corte_aux.GetParamIzq()[1],true,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho(),Material.GetAlto(),Material.GetEspesorAlma(),Material.GetRadioAlma(),false);
                }
            }
    }
    
    private void Generar_operaciones_der(Tools tool_ala){
        //Agujeros redondos ala derecha
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            if(corte_aux.GetTipoDer()==1){
                Codigo.redondo_ala(-corte_aux.GetParamDer()[0],-corte_aux.GetParamDer()[1],corte_aux.GetParamDer()[2],false,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho());
            }
        }
        //Agujeros oblongos verticales ala derecha
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            if(corte_aux.GetTipoDer()==2){
                Codigo.oblongo_ala_v(-corte_aux.GetParamDer()[0],-corte_aux.GetParamDer()[1],corte_aux.GetParamDer()[2],corte_aux.GetParamDer()[3],false,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho());
            }
        }
        //Agujeros oblongos horizontales ala derecha
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            if(corte_aux.GetTipoDer()==3){
                Codigo.oblongo_ala_h(-corte_aux.GetParamDer()[0],-corte_aux.GetParamDer()[1],corte_aux.GetParamDer()[2],corte_aux.GetParamDer()[3],false,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho());
            }
        }
        //Agujeros rectangulares
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            if(corte_aux.GetTipoDer()==4){
                Codigo.rectangular_ala(-corte_aux.GetParamDer()[0],-corte_aux.GetParamDer()[1],corte_aux.GetParamDer()[2],corte_aux.GetParamDer()[3],false,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho());
            }
        }
        //Corte en L (destijere 2)
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            if(corte_aux.GetTipoDer()==5){
                Codigo.corte_l_ala(corte_aux.GetParamDer()[0],corte_aux.GetParamDer()[1],corte_aux.GetParamDer()[2],false,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho(),Material.GetAlto());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            if(corte_aux.GetTipoDer()==6){
                Codigo.recta_ala(-corte_aux.GetParamDer()[0],-corte_aux.GetParamDer()[1],false,tool_ala,Cortes.get(i).GetIndice(),Material.GetAncho(),Material.GetAlto(),Material.GetEspesorAlma(),Material.GetRadioAlma(),false);
            }
        }
    }
    
    private void Generar_operaciones_sup(Tools tool_alma,double altura_alma){
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Agujero redondo alma
            if(corte_aux.GetTipoSup()==1){
                Codigo.redondo_alma(corte_aux.GetParamSup()[0], -corte_aux.GetParamSup()[1], corte_aux.GetParamSup()[2], tool_alma, Cortes.get(i).GetIndice());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Agujero oblongo vertical alma
            if(corte_aux.GetTipoSup()==2){
                Codigo.oblongo_alma_v(corte_aux.GetParamSup()[0], -corte_aux.GetParamSup()[1], corte_aux.GetParamSup()[2],corte_aux.GetParamSup()[3], tool_alma, Cortes.get(i).GetIndice());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Agujero oblongo horizontal alma
            if(corte_aux.GetTipoSup()==3){
                Codigo.oblongo_alma_h(corte_aux.GetParamSup()[0], -corte_aux.GetParamSup()[1], corte_aux.GetParamSup()[2],corte_aux.GetParamSup()[3], tool_alma, Cortes.get(i).GetIndice());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Agujero rectangular alma
            if(corte_aux.GetTipoSup()==4){
                Codigo.rectangular_alma(corte_aux.GetParamSup()[0], -corte_aux.GetParamSup()[1], corte_aux.GetParamSup()[2],corte_aux.GetParamSup()[3], tool_alma, Cortes.get(i).GetIndice());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Destijere
            if(corte_aux.GetTipoSup()==7){
                Codigo.destijere(-corte_aux.GetParamSup()[0], -corte_aux.GetParamSup()[1], corte_aux.GetParamSup()[2], corte_aux.GetParamSup()[3], tool_alma, Cortes.get(i).GetIndice(), Material.GetAncho(), altura_alma, Material.GetEspesorAla(), Material.GetRadioAlma());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Empalme normal
            if(corte_aux.GetTipoSup()==8){
                Codigo.empalme_normal(-corte_aux.GetParamSup()[0], -corte_aux.GetParamSup()[1], corte_aux.GetParamSup()[2], tool_alma,Cortes.get(i).GetIndice(),Material.GetAncho(),altura_alma, Material.GetEspesorAla(), Material.GetRadioAlma());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Empalme hembra
            if(corte_aux.GetTipoSup()==9){
                Codigo.empalme_hembra(corte_aux.GetParamSup()[0], corte_aux.GetParamSup()[1], -corte_aux.GetParamSup()[2], corte_aux.GetParamSup()[3], tool_alma, Cortes.get(i).GetIndice(), Material.GetAncho(), altura_alma, Material.GetEspesorAla(), Material.GetRadioAlma());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Linea recta
            if(corte_aux.GetTipoSup()==10){
                Codigo.recta_alma(-corte_aux.GetParamSup()[0], -corte_aux.GetParamSup()[1], tool_alma, Cortes.get(i).GetIndice(), Material.GetAncho(), altura_alma, Material.GetEspesorAla(), Material.GetRadioAlma());
            }
        }
        for(int i=0;i<Cortes.size();i++){
            Operacion_corte corte_aux=Cortes.get(i).GetCorte();
            //Empalme macho
            if(corte_aux.GetTipoSup()==11){
                Codigo.empalme_macho(corte_aux.GetParamSup()[0], corte_aux.GetParamSup()[1], corte_aux.GetParamSup()[2], tool_alma, Cortes.get(i).GetIndice(), Material.GetAncho(), altura_alma, Material.GetEspesorAla(), Material.GetRadioAlma());
            }
        }
    }
    private void BotonGenerarGcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGenerarGcodeActionPerformed
        double altura_alma;
        boolean hay_corte_izq=false;
        boolean hay_corte_der=false;
        boolean hay_corte_sup=false;
        Tools tool_ala = new Tools(consumible,Material.GetEspesorAla());
        Tools tool_alma = new Tools(consumible,Material.GetEspesorAlma());
        Codigo.borrar();
        modelo_gcode.clear();
        
        //Verificar si hay cortes en las alas y el alma
        for(int i=0;i<Cortes.size();i++){
            if(Cortes.get(i).GetCorte().GetTipoIzq()!=0)
                hay_corte_izq=true;
            if(Cortes.get(i).GetCorte().GetTipoDer()!=0)
                hay_corte_der=true;
            if(Cortes.get(i).GetCorte().GetTipoSup()!=0)
                hay_corte_sup=true;
        }
      
        switch(tipoPerfil){
            case 1:
            case 2:
                altura_alma=Material.GetAlto()/2-Material.GetEspesorAlma()/2;
                break;
            default:
                altura_alma=0;
                break;
        }
       
        if(hay_corte_izq){
            Codigo.generar_encabezado(tipoPerfil,consumible,(int)SpinnerVoltajeAla.getValue(),tool_ala.GetRetardoPinchazo());
            Codigo.addLinea("(Operaciones ala izquierda)");
            Generar_operaciones_izq(tool_ala);
            if(hay_corte_der){
                Codigo.generar_salto_izq_der(Material.GetAncho());
                Codigo.addLinea("(Operaciones ala derecha)");
                Generar_operaciones_der(tool_ala);
                if(hay_corte_sup){
                    Codigo.generar_salto_der_sup(Material.GetAncho(), altura_alma, Material.GetEspesorAla(),(int)SpinnerVoltajeAlma.getValue(),tool_alma.GetRetardoPinchazo());
                    Codigo.addLinea("(Operaciones alma)");
                    //operaciones alma
                    Generar_operaciones_sup(tool_alma,altura_alma);                 
                    Codigo.generar_fin_sup(altura_alma, Material.GetEspesorAla());
                    
                }else{
                    Codigo.generar_fin_der(Material.GetAncho());
                }
            }else{
                if(hay_corte_sup){
                    Codigo.generar_salto_izq_sup(Material.GetAncho(), altura_alma, Material.GetEspesorAla(),(int)SpinnerVoltajeAlma.getValue(),tool_alma.GetRetardoPinchazo());
                    Codigo.addLinea("(Operaciones alma)");
                    //operaciones alma
                    Generar_operaciones_sup(tool_alma,altura_alma);                   
                    Codigo.generar_fin_sup(altura_alma, Material.GetEspesorAla());
                }else{
                    Codigo.generar_fin_izq();
                }
            }
        }else if(hay_corte_der){
            Codigo.generar_encabezado(tipoPerfil,consumible,(int)SpinnerVoltajeAla.getValue(),tool_ala.GetRetardoPinchazo());
            Codigo.generar_salto_izq_der(Material.GetAncho());
            Codigo.addLinea("(Operaciones ala derecha)");
            Generar_operaciones_der(tool_ala);
            if(hay_corte_sup){
                Codigo.generar_salto_der_sup(Material.GetAncho(), altura_alma,Material.GetEspesorAla(),(int)SpinnerVoltajeAlma.getValue(),tool_alma.GetRetardoPinchazo());
                //Operaciones alma
                Codigo.addLinea("(Operaciones alma)");
                Generar_operaciones_sup(tool_alma,altura_alma);
                Codigo.generar_fin_sup(altura_alma, Material.GetEspesorAla());
            }else{
                Codigo.generar_fin_der(Material.GetAncho());
            }
        }else if(hay_corte_sup){
            Codigo.generar_encabezado(tipoPerfil,consumible,(int)SpinnerVoltajeAla.getValue(),tool_ala.GetRetardoPinchazo());
            Codigo.generar_salto_izq_sup(Material.GetAncho(), altura_alma, Material.GetEspesorAla(),(int)SpinnerVoltajeAlma.getValue(),tool_alma.GetRetardoPinchazo());
            //Operaciones alma
            Codigo.addLinea("(Operaciones alma)");
            Generar_operaciones_sup(tool_alma,altura_alma);
            Codigo.generar_fin_sup(altura_alma, Material.GetEspesorAla());
        }else{
            JOptionPane.showMessageDialog(null,"No se han creado operaciones!","Error",JOptionPane.ERROR_MESSAGE);
        }
        
        //Mostrar el codigo generado en el visualizador
        for(int i=0;i<Codigo.getSize();i++){
            Codigo.actualizarDecimal();
            modelo_gcode.addElement(Codigo.getLinea(i));
        }
        
    }//GEN-LAST:event_BotonGenerarGcodeActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AgregarOpDer;
    private javax.swing.JButton AgregarOpIzq;
    private javax.swing.JButton AgregarOpSup;
    private javax.swing.JDialog Agregar_dialog;
    private javax.swing.JDialog Agujero_platina_dialog;
    private javax.swing.JDialog Agujero_rect_ala_dialog;
    private javax.swing.JDialog Agujero_rect_alma_dialog;
    private javax.swing.JButton BorrarAllDer;
    private javax.swing.JButton BorrarAllIzq;
    private javax.swing.JButton BorrarAllSup;
    private javax.swing.JButton BorrarOpDer;
    private javax.swing.JButton BorrarOpIzq;
    private javax.swing.JButton BorrarOpSup;
    private javax.swing.JButton BotonANGULO;
    private javax.swing.JButton BotonCAJON;
    private javax.swing.JButton BotonGenerarGcode;
    private javax.swing.JButton BotonGuardarGcode;
    private javax.swing.JButton BotonIPEHEB;
    private javax.swing.JButton BotonIPN;
    private javax.swing.JButton BotonUPN;
    private javax.swing.JButton Boton_agujero_platina;
    private javax.swing.JButton Boton_cancel_agujero_platina;
    private javax.swing.JButton Boton_cancel_agujero_rect_ala;
    private javax.swing.JButton Boton_cancel_agujero_rect_alma;
    private javax.swing.JButton Boton_cancel_destijere;
    private javax.swing.JButton Boton_cancel_destijere2;
    private javax.swing.JButton Boton_cancel_destijere_inc;
    private javax.swing.JButton Boton_cancel_empalme;
    private javax.swing.JButton Boton_cancel_empalme_hembra;
    private javax.swing.JButton Boton_cancel_empalme_inc;
    private javax.swing.JButton Boton_cancel_empalme_macho;
    private javax.swing.JButton Boton_cancel_oblongo_ala_h;
    private javax.swing.JButton Boton_cancel_oblongo_ala_v;
    private javax.swing.JButton Boton_cancel_oblongo_alma_h;
    private javax.swing.JButton Boton_cancel_oblongo_alma_v;
    private javax.swing.JButton Boton_cancel_recto;
    private javax.swing.JButton Boton_cancel_recto_desf;
    private javax.swing.JButton Boton_cancel_recto_inc;
    private javax.swing.JButton Boton_cancel_recto_inc_desf;
    private javax.swing.JButton Boton_cancel_redondo_ala;
    private javax.swing.JButton Boton_cancel_redondo_alma;
    private javax.swing.JButton Boton_destijere;
    private javax.swing.JButton Boton_destijere2;
    private javax.swing.JButton Boton_destijere_inc;
    private javax.swing.JButton Boton_empalme;
    private javax.swing.JButton Boton_empalme_hembra;
    private javax.swing.JButton Boton_empalme_inc;
    private javax.swing.JButton Boton_empalme_macho;
    private javax.swing.JButton Boton_oblongo_ala_H;
    private javax.swing.JButton Boton_oblongo_ala_V;
    private javax.swing.JButton Boton_oblongo_alma_H;
    private javax.swing.JButton Boton_oblongo_alma_V;
    private javax.swing.JButton Boton_ok_agujero_platina;
    private javax.swing.JButton Boton_ok_agujero_rect_ala;
    private javax.swing.JButton Boton_ok_agujero_rect_alma;
    private javax.swing.JButton Boton_ok_destijere;
    private javax.swing.JButton Boton_ok_destijere2;
    private javax.swing.JButton Boton_ok_destijere_inc;
    private javax.swing.JButton Boton_ok_empalme;
    private javax.swing.JButton Boton_ok_empalme_hembra;
    private javax.swing.JButton Boton_ok_empalme_inc;
    private javax.swing.JButton Boton_ok_empalme_macho;
    private javax.swing.JButton Boton_ok_oblongo_ala_h;
    private javax.swing.JButton Boton_ok_oblongo_ala_v;
    private javax.swing.JButton Boton_ok_oblongo_alma_h;
    private javax.swing.JButton Boton_ok_oblongo_alma_v;
    private javax.swing.JButton Boton_ok_recto;
    private javax.swing.JButton Boton_ok_recto_desf;
    private javax.swing.JButton Boton_ok_recto_inc;
    private javax.swing.JButton Boton_ok_recto_inc_desf;
    private javax.swing.JButton Boton_ok_redondo_ala;
    private javax.swing.JButton Boton_ok_redondo_alma;
    private javax.swing.JButton Boton_rectangular_ala;
    private javax.swing.JButton Boton_rectangular_alma;
    private javax.swing.JButton Boton_recto;
    private javax.swing.JButton Boton_recto_desf;
    private javax.swing.JButton Boton_recto_desf_inc;
    private javax.swing.JButton Boton_recto_inc;
    private javax.swing.JButton Boton_redondo_ala;
    private javax.swing.JButton Boton_redondo_alma;
    private javax.swing.JButton Boton_salir;
    private javax.swing.JLabel Canvas_agujero_rect_ala;
    private javax.swing.JLabel Canvas_agujero_rect_alma;
    private javax.swing.JLabel Canvas_destijere;
    private javax.swing.JLabel Canvas_destijere_inc;
    private javax.swing.JLabel Canvas_empalme;
    private javax.swing.JLabel Canvas_empalme_hembra;
    private javax.swing.JLabel Canvas_empalme_macho;
    private javax.swing.JLabel Canvas_oblongo_ala_h;
    private javax.swing.JLabel Canvas_oblongo_ala_v;
    private javax.swing.JLabel Canvas_oblongo_alma_h;
    private javax.swing.JLabel Canvas_oblongo_alma_v;
    private javax.swing.JLabel Canvas_recto;
    private javax.swing.JLabel Canvas_recto_agujero_platina;
    private javax.swing.JLabel Canvas_recto_desf;
    private javax.swing.JLabel Canvas_recto_destijere2;
    private javax.swing.JLabel Canvas_recto_empalme_inc;
    private javax.swing.JLabel Canvas_recto_inc;
    private javax.swing.JLabel Canvas_recto_inc_desf;
    private javax.swing.JLabel Canvas_redondo_ala;
    private javax.swing.JLabel Canvas_redondo_alma;
    private javax.swing.JCheckBox Check_der_agujero_rect_ala;
    private javax.swing.JCheckBox Check_der_oblongo_ala_h;
    private javax.swing.JCheckBox Check_der_oblongo_ala_v;
    private javax.swing.JCheckBox Check_der_redondo_ala;
    private javax.swing.JCheckBox Check_izq_agujero_rect_ala;
    private javax.swing.JCheckBox Check_izq_oblongo_ala_h;
    private javax.swing.JCheckBox Check_izq_oblongo_ala_v;
    private javax.swing.JCheckBox Check_izq_redondo_ala;
    private javax.swing.JComboBox<String> ComboTipoPerfil;
    private javax.swing.JDialog Destijere2_dialog;
    private javax.swing.JDialog Destijere_dialog;
    private javax.swing.JDialog Destijere_inc_dialog;
    private javax.swing.JDialog Empalme_dialog;
    private javax.swing.JDialog Empalme_hembra_dialog;
    private javax.swing.JDialog Empalme_inc_dialog;
    private javax.swing.JDialog Empalme_macho_dialog;
    private javax.swing.JOptionPane JOptionPane;
    private javax.swing.JList<String> ListaOpDer;
    private DefaultListModel modelo_der;
    private javax.swing.JList<String> ListaOpIzq;
    private DefaultListModel modelo_izq;
    private javax.swing.JList<String> ListaOpSup;
    private DefaultListModel modelo_sup;
    private javax.swing.JDialog Oblongo_ala_h_dialog;
    private javax.swing.JDialog Oblongo_ala_v_dialog;
    private javax.swing.JDialog Oblongo_alma_h_dialog;
    private javax.swing.JDialog Oblongo_alma_v_dialog;
    private javax.swing.JPanel PanelANGULO;
    private javax.swing.JPanel PanelCAJON;
    private javax.swing.JPanel PanelConsumibles;
    private javax.swing.JPanel PanelDer;
    private javax.swing.JPanel PanelGcode;
    private javax.swing.JPanel PanelIPEHEB;
    private javax.swing.JPanel PanelIPN;
    private javax.swing.JPanel PanelIzq;
    private javax.swing.JScrollPane PanelListaDer;
    private javax.swing.JScrollPane PanelListaIzq;
    private javax.swing.JScrollPane PanelListaSup;
    private javax.swing.JPanel PanelMaterial;
    private javax.swing.JPanel PanelOperacionesDer;
    private javax.swing.JPanel PanelOperacionesIzq;
    private javax.swing.JPanel PanelOperacionesSup;
    private javax.swing.JPanel PanelPicConsumibles;
    private javax.swing.JPanel PanelPreviewDer;
    private javax.swing.JPanel PanelPreviewIzq;
    private javax.swing.JPanel PanelPreviewSup;
    private javax.swing.JPanel PanelSup;
    private javax.swing.JTabbedPane PanelTabs;
    private javax.swing.JPanel PanelTipoPerfil;
    private javax.swing.JPanel PanelUPN;
    private javax.swing.JPanel Panel_Medidas_agujero_platina;
    private javax.swing.JPanel Panel_Medidas_agujero_rect_ala;
    private javax.swing.JPanel Panel_Medidas_agujero_rect_alma;
    private javax.swing.JPanel Panel_Medidas_destijere;
    private javax.swing.JPanel Panel_Medidas_destijere2;
    private javax.swing.JPanel Panel_Medidas_destijere_inc;
    private javax.swing.JPanel Panel_Medidas_empalme;
    private javax.swing.JPanel Panel_Medidas_empalme_hembra;
    private javax.swing.JPanel Panel_Medidas_empalme_inc;
    private javax.swing.JPanel Panel_Medidas_empalme_macho;
    private javax.swing.JPanel Panel_Medidas_oblongo_ala_h;
    private javax.swing.JPanel Panel_Medidas_oblongo_ala_v;
    private javax.swing.JPanel Panel_Medidas_oblongo_alma_h;
    private javax.swing.JPanel Panel_Medidas_oblongo_alma_v;
    private javax.swing.JPanel Panel_Medidas_recto;
    private javax.swing.JPanel Panel_Medidas_recto_desf;
    private javax.swing.JPanel Panel_Medidas_recto_inc;
    private javax.swing.JPanel Panel_Medidas_recto_inc_desf;
    private javax.swing.JPanel Panel_Medidas_redondo_ala;
    private javax.swing.JPanel Panel_Medidas_redondo_alma;
    private javax.swing.JLabel PicConsumibles;
    private javax.swing.JPanel PreviewDer;
    private javax.swing.JPanel PreviewIzq;
    private javax.swing.JPanel PreviewSup;
    private javax.swing.JDialog Recto_desf_dialog;
    private javax.swing.JDialog Recto_dialog;
    private javax.swing.JDialog Recto_inc_desf_dialog;
    private javax.swing.JDialog Recto_inc_dialog;
    private javax.swing.JDialog Redondo_ala_dialog;
    private javax.swing.JDialog Redondo_alma_dialog;
    private javax.swing.JScrollPane ScrollGcode;
    private javax.swing.JSpinner SpinnerA1EmpalmeHembra;
    private javax.swing.JSpinner SpinnerA1EmpalmeMacho;
    private javax.swing.JSpinner SpinnerA2EmpalmeHembra;
    private javax.swing.JSpinner SpinnerA2EmpalmeMacho;
    private javax.swing.JSpinner SpinnerAAgujeroRectAla;
    private javax.swing.JSpinner SpinnerAAgujeroRectAlma;
    private javax.swing.JSpinner SpinnerAIDAgujeroPlatina;
    private javax.swing.JSpinner SpinnerAIDDestijere2;
    private javax.swing.JSpinner SpinnerAIIAgujeroPlatina;
    private javax.swing.JSpinner SpinnerAIIDestijere2;
    private javax.swing.JSpinner SpinnerASDAgujeroPlatina;
    private javax.swing.JSpinner SpinnerASDDestijere2;
    private javax.swing.JSpinner SpinnerASIAgujeroPlatina;
    private javax.swing.JSpinner SpinnerASIDestijere2;
    private javax.swing.JSpinner SpinnerAltoCAJON;
    private javax.swing.JSpinner SpinnerAltoIPEHEB;
    private javax.swing.JSpinner SpinnerAltoIPN;
    private javax.swing.JSpinner SpinnerAltoUPN;
    private javax.swing.JSpinner SpinnerAnchoANGULO;
    private javax.swing.JSpinner SpinnerAnchoCAJON;
    private javax.swing.JSpinner SpinnerAnchoIPEHEB;
    private javax.swing.JSpinner SpinnerAnchoIPN;
    private javax.swing.JSpinner SpinnerAnchoUPN;
    private javax.swing.JSpinner SpinnerCDDestijere;
    private javax.swing.JSpinner SpinnerCDDestijereInc;
    private javax.swing.JSpinner SpinnerCEmpalmeHembra;
    private javax.swing.JSpinner SpinnerCEmpalmeMacho;
    private javax.swing.JSpinner SpinnerCIDestijere;
    private javax.swing.JSpinner SpinnerCIDestijereInc;
    private javax.swing.JSpinner SpinnerDOblongoAlaH;
    private javax.swing.JSpinner SpinnerDOblongoAlaV;
    private javax.swing.JSpinner SpinnerDOblongoAlmaH;
    private javax.swing.JSpinner SpinnerDOblongoAlmaV;
    private javax.swing.JSpinner SpinnerDRedondoAla;
    private javax.swing.JSpinner SpinnerDRedondoAlma;
    private javax.swing.JSpinner SpinnerEANGULO;
    private javax.swing.JSpinner SpinnerEAlaIPEHEB;
    private javax.swing.JSpinner SpinnerEAlaIPN;
    private javax.swing.JSpinner SpinnerEAlaUPN;
    private javax.swing.JSpinner SpinnerEAlmaIPEHEB;
    private javax.swing.JSpinner SpinnerEAlmaIPN;
    private javax.swing.JSpinner SpinnerEAlmaUPN;
    private javax.swing.JSpinner SpinnerECAJON;
    private javax.swing.JSpinner SpinnerL1DDestijereInc;
    private javax.swing.JSpinner SpinnerL1DEmpalmeInc;
    private javax.swing.JSpinner SpinnerL1DRectoIncDesf;
    private javax.swing.JSpinner SpinnerL1IDestijereInc;
    private javax.swing.JSpinner SpinnerL1IEmpalmeInc;
    private javax.swing.JSpinner SpinnerL1IRectoIncDesf;
    private javax.swing.JSpinner SpinnerL1RectoInc;
    private javax.swing.JSpinner SpinnerL2DDestijereInc;
    private javax.swing.JSpinner SpinnerL2DEmpalmeInc;
    private javax.swing.JSpinner SpinnerL2DRectoIncDesf;
    private javax.swing.JSpinner SpinnerL2IDestijereInc;
    private javax.swing.JSpinner SpinnerL2IEmpalmeInc;
    private javax.swing.JSpinner SpinnerL2IRectoIncDesf;
    private javax.swing.JSpinner SpinnerL2RectoInc;
    private javax.swing.JSpinner SpinnerLAgujeroRectAla;
    private javax.swing.JSpinner SpinnerLAgujeroRectAlma;
    private javax.swing.JSpinner SpinnerLDDestijere;
    private javax.swing.JSpinner SpinnerLDEmpalme;
    private javax.swing.JSpinner SpinnerLDRectoDesf;
    private javax.swing.JSpinner SpinnerLEmpalmeHembra;
    private javax.swing.JSpinner SpinnerLIDAgujeroPlatina;
    private javax.swing.JSpinner SpinnerLIDDestijere2;
    private javax.swing.JSpinner SpinnerLIDestijere;
    private javax.swing.JSpinner SpinnerLIEmpalme;
    private javax.swing.JSpinner SpinnerLIIAgujeroPlatina;
    private javax.swing.JSpinner SpinnerLIIDestijere2;
    private javax.swing.JSpinner SpinnerLIRectoDesf;
    private javax.swing.JSpinner SpinnerLOblongoAlaH;
    private javax.swing.JSpinner SpinnerLOblongoAlaV;
    private javax.swing.JSpinner SpinnerLOblongoAlmaH;
    private javax.swing.JSpinner SpinnerLOblongoAlmaV;
    private javax.swing.JSpinner SpinnerLSDAgujeroPlatina;
    private javax.swing.JSpinner SpinnerLSDDestijere2;
    private javax.swing.JSpinner SpinnerLSIAgujeroPlatina;
    private javax.swing.JSpinner SpinnerLSIDestijere2;
    private javax.swing.JSpinner SpinnerLargoRecto;
    private javax.swing.JSpinner SpinnerRadio1IPN;
    private javax.swing.JSpinner SpinnerRadio1UPN;
    private javax.swing.JSpinner SpinnerRadioIPEHEB;
    private javax.swing.JSpinner SpinnerRadioIPN;
    private javax.swing.JSpinner SpinnerRadioUPN;
    private javax.swing.JSpinner SpinnerVoltajeAla;
    private javax.swing.JSpinner SpinnerVoltajeAlma;
    private javax.swing.JSpinner SpinnerXAgujeroRectAlma;
    private javax.swing.JSpinner SpinnerXEmpalme;
    private javax.swing.JSpinner SpinnerXEmpalmeInc;
    private javax.swing.JSpinner SpinnerXOblongoAlmaH;
    private javax.swing.JSpinner SpinnerXOblongoAlmaV;
    private javax.swing.JSpinner SpinnerXRedondoAlma;
    private javax.swing.JSpinner SpinnerYAgujeroRectAla;
    private javax.swing.JSpinner SpinnerYAgujeroRectAlma;
    private javax.swing.JSpinner SpinnerYIDAgujeroPlatina;
    private javax.swing.JSpinner SpinnerYIIAgujeroPlatina;
    private javax.swing.JSpinner SpinnerYOblongoAlaH;
    private javax.swing.JSpinner SpinnerYOblongoAlaV;
    private javax.swing.JSpinner SpinnerYOblongoAlmaH;
    private javax.swing.JSpinner SpinnerYOblongoAlmaV;
    private javax.swing.JSpinner SpinnerYRedondoAla;
    private javax.swing.JSpinner SpinnerYRedondoAlma;
    private javax.swing.JSpinner SpinnerYSDAgujeroPlatina;
    private javax.swing.JSpinner SpinnerYSIAgujeroPlatina;
    private javax.swing.JSpinner SpinnerZAgujeroRectAla;
    private javax.swing.JSpinner SpinnerZOblongoAlaH;
    private javax.swing.JSpinner SpinnerZOblongoAlaV;
    private javax.swing.JSpinner SpinnerZRedondoAla;
    private javax.swing.JLabel TituloPanelConsumibles;
    private javax.swing.JLabel TituloPanelDer;
    private javax.swing.JLabel TituloPanelGcode;
    private javax.swing.JLabel TituloPanelIzq;
    private javax.swing.JLabel TituloPanelSup;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JFileChooser chooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel label_boquilla;
    private javax.swing.JList<String> listaGcode;
    private DefaultListModel modelo_gcode;
    private javax.swing.JRadioButton radio45;
    private javax.swing.JRadioButton radio65;
    private javax.swing.JRadioButton radio85;
    // End of variables declaration//GEN-END:variables
}
