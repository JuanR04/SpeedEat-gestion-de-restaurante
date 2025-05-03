
package speedeatsof.control;

import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juanR
 */
public class HiloCargarTablaProducto extends Thread{
    
    ControladorProducto cp;

    public HiloCargarTablaProducto(ControladorProducto cp) {
        this.cp = cp;
    }

    @Override
    public void run() {
        while(true){
            cp.cargarTabla();
            try {
                sleep(20000);
            } catch (InterruptedException ex) {
                Logger.getLogger(HiloCargarTablaUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
