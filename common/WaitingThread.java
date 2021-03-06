package common;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.TrayIcon;

/*
 * Waiting thread will sleep the time specified in the monitor by calling
 * getHours(), getMinutes(), getSeconds()
 * If the sleep was successful (i.e. no interrupts) then it will wiggle the mouse
 * using the class java.awt.Robot
 * */

public class WaitingThread extends Thread {
	private Monitor monitor;
	private Robot robot;
	String status = "Not Wiggling";
	private TrayIcon trayIcon=null; 
	
	public String getStatus()
	{
		return status;
	}

	public WaitingThread(Monitor monitor) {
		this.monitor = monitor;
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			System.err.println("Robot couldn't be created");
			System.exit(1);
		}
	}

	public void run() {
		System.out.println("Thread started");
		boolean interrupted=true;
		int secondsToSleep = (monitor.getHours()*60+monitor.getMinutes())*60+monitor.getSeconds();	
		
		while (true) 
		{
			if(interrupted)
			{
				secondsToSleep = (monitor.getHours()*60+monitor.getMinutes())*60+monitor.getSeconds();		
				status="Wiggling every "+secondsToSleep+" seconds";
				if(trayIcon!=null)
				{
					trayIcon.setToolTip(status);
				}	
				interrupted=false;
			}
			try {
				System.out.println("Going to sleep for "+secondsToSleep*1000+"ms");
				Thread.sleep(secondsToSleep*1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				interrupted=true;
			}
			System.out.println("Wiggling.");
			wiggle();
		}
	}

	private void wiggle() {


		try {
			Point point = MouseInfo.getPointerInfo().getLocation();
			int x = (int)point.getX();
			int y = (int)point.getY();
			
			robot.mouseMove(x, y+5);
			Thread.sleep(50);
			robot.mouseMove(x, y-5);
			Thread.sleep(50);
			robot.mouseMove(x, y);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	public TrayIcon getTrayIcon() {
		return trayIcon;
	}

	public void setTrayIcon(TrayIcon trayIcon) {
		this.trayIcon = trayIcon;
	}
}
