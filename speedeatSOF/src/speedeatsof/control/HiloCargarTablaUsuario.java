
package speedeatsof.control;

import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juanR
 */
public class HiloCargarTablaUsuario extends Thread{
    
    ControladorUsuarios cu;

    public HiloCargarTablaUsuario(ControladorUsuarios cu) {
        this.cu = cu;
    }

    @Override
    public void run() {
        while(true){
            cu.cargarTabla();
            try {
                sleep(20000);
            } catch (InterruptedException ex) {
                Logger.getLogger(HiloCargarTablaUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
