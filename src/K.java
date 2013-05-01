import java.awt.EventQueue;

/**
 * @author Wav
 */
public class K {
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new KFrame();
			}
		});
	}
}