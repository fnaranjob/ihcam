/*
 * Este software ha sido creado para uso exclusivo junto con la máquina
 * cortadora de plasma PP4-130605-V1 desarrollada por SupraCNC
 * cualquier otro uso de este software queda estrictamente prohibido
 */

package ihcam;

// @author FNaranjoB

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;


public class main {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        FramePrincipal F = null;
        try {
            F = new FramePrincipal();
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        F.setDefaultCloseOperation(FramePrincipal.EXIT_ON_CLOSE);
        F.setSize(800,600);
        F.setTitle("IHCAM V0.1b");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();                        //Centrar JFrame
        F.setLocation(dim.width/2-F.getSize().width/2, dim.height/2-F.getSize().height/2);
        F.setVisible(true);
        
    }
    
}
