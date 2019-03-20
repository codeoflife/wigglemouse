package common;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class TextDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public WaitingThread waitingThread;

	private JLabel label;
	public JLabel showTimeLabel;
	private JButton button;

	/* Time text fields */
	public JTextField hoursTextField;
	public JTextField minutesTextField;
	public JTextField secondsTextField;

	SystemTray tray;
	TrayIcon trayIcon;

	private void hideToSystemTray(final JDialog frame) {
		if (!SystemTray.isSupported())
			return;
		System.out.println("System tray supported");
		tray = SystemTray.getSystemTray();

		ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Exiting....");
				System.exit(0);
			}
		};
		PopupMenu popup = new PopupMenu();
		MenuItem defaultItem = new MenuItem("Exit");
		defaultItem.addActionListener(exitListener);
		popup.add(defaultItem);
	
		defaultItem = new MenuItem("Open");
		defaultItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(true);
				//frame.setExtendedState(JFrame.NORMAL);
			}
		});
		popup.add(defaultItem);
		//Image image = Toolkit.getDefaultToolkit().getImage("mouse.png");
		Image image=null;
		try {
			image = ImageIO.read(TextDialog.class.getResource("mouse.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		trayIcon = new TrayIcon(image, waitingThread.getStatus(), popup);
		trayIcon.setImageAutoSize(true);
		setIconImage(image);
		ActionListener listener = new ActionListener()
	    {
	        public void actionPerformed(ActionEvent e)
	        {
				frame.setVisible(true);
				//frame.setExtendedState(JFrame.NORMAL);
	        }
	    };	
	    trayIcon.addActionListener(listener);		
		//setVisible(true);
		//setSize(300, 200);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }		
	}

	public TextDialog(Monitor monitor, WaitingThread waitingThread) {
		//super("WiggleMouse");
		this.setTitle("WiggleMouse");
		this.waitingThread = waitingThread;

		/*
		 * This will make the program look like any other program and not have the ugly default swing look
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Couldn't set native look and feel");
		}

		/* Creating widgets */
		this.label = new JLabel("Time to wait before mouse wiggle:");
		String showTime = monitor.getHours() + "h:" + monitor.getMinutes() + "m:" + monitor.getSeconds() + "s";
		this.showTimeLabel = new JLabel(showTime);
		this.hoursTextField = new JTextField(monitor.getHours() + "", 2);
		this.minutesTextField = new JTextField(monitor.getMinutes() + "", 2);
		this.secondsTextField = new JTextField(monitor.getSeconds() + "", 2);

		this.button = new JButton("Set time");

		/* Layout stuff */
		this.setLayout(new GridLayout(3, 1));
		JPanel mainLabelPanel = new JPanel();
		JPanel showTimeLabelPanel = new JPanel();
		JPanel textFieldPanel = new JPanel();
		mainLabelPanel.setLayout(new FlowLayout());
		showTimeLabelPanel.setLayout(new FlowLayout());
		textFieldPanel.setLayout(new FlowLayout());

		this.add(mainLabelPanel);
		this.add(showTimeLabelPanel);
		this.add(textFieldPanel);

		mainLabelPanel.add(label);
		showTimeLabelPanel.add(showTimeLabel);
		textFieldPanel.add(hoursTextField);
		textFieldPanel.add(minutesTextField);
		textFieldPanel.add(secondsTextField);
		textFieldPanel.add(button);

		/* Misc layout */
		Font curFont = showTimeLabel.getFont();
		showTimeLabel.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 20));
		this.setSize(250, 200);

		/* Listeners */
		SetTimeListener timeListener = new SetTimeListener(this, monitor);
		this.button.addActionListener(timeListener);
		this.hoursTextField.addActionListener(timeListener);
		this.minutesTextField.addActionListener(timeListener);
		this.secondsTextField.addActionListener(timeListener);
		this.addWindowListener(new FrameListener());

		/* Show it */
		// this.setVisible(true);
		// minutesTextField.requestFocus();
		hideToSystemTray(this);
		this.trayIcon.setToolTip(waitingThread.status);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		

	}

	public TrayIcon getTrayIcon() {
		return trayIcon;
	}

	public void setTrayIcon(TrayIcon trayIcon) {
		this.trayIcon = trayIcon;
	}
}

/* Listeners */

class SetTimeListener implements ActionListener {
	private TextDialog textDialog;
	private Monitor monitor;

	public SetTimeListener(TextDialog textDialog, Monitor monitor) {
		this.textDialog = textDialog;
		this.monitor = monitor;
	}

	public void actionPerformed(ActionEvent event) {
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		try {
			seconds = Integer.parseInt(textDialog.secondsTextField.getText());
			minutes = seconds / 60;
			seconds = seconds % 60;
		} catch (NumberFormatException e) {
			seconds = 1;
		}

		try {
			minutes += Integer.parseInt(textDialog.minutesTextField.getText());
			hours = minutes / 60;
			minutes = minutes % 60;
		} catch (Exception e) {
			minutes = 0;
		}

		try {
			hours += Integer.parseInt(textDialog.hoursTextField.getText());
			if (hours > 100) {
				hours = 100;
			}
		} catch (NumberFormatException e) {
			hours = 0;
		}

		/* Otherwise it will do some crazy wiggling */
		if (hours == 0 && minutes == 0 && seconds == 0) {
			seconds = 1;
		}

		textDialog.hoursTextField.setText(hours + "");
		textDialog.minutesTextField.setText(minutes + "");
		textDialog.secondsTextField.setText(seconds + "");

		String showTime = hours + "h:" + minutes + "m:" + seconds + "s";
		textDialog.showTimeLabel.setText(showTime);

		monitor.setTime(hours, minutes, seconds);
		textDialog.waitingThread.setMonitor(monitor);
		textDialog.waitingThread.interrupt();
		textDialog.trayIcon.setToolTip(textDialog.waitingThread.status);
	}

}

class FrameListener extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		//System.exit(0);
	}
}
