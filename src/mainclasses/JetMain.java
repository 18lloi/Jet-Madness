package mainclasses;

import javax.swing.JFrame;

public class JetMain {
	// creates frame and starts game
	public static void main(String[] args) {
		JFrame window = new JFrame("Jet Madness");
		window.setContentPane(new Panel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}
