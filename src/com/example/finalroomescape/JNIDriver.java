package com.example.finalroomescape;

public class JNIDriver {
	
	private static int WRONLY = 1;
	private static int RDONLY = 2;
	private static int RDWR = 3;
	
private boolean mBuzFlag, mLedFlag, mBtnFlag, mSevFlag, mLcdFlag, mEEpromFlag;
int flag = 0;	
	static {
		System.loadLibrary("JNIDriver");
	}
	
	private native static int openBuzDriver(String path);
	private native static void closeBuzDriver();
	private native static void writeBuzDriver(byte[] data, int length);
	
	private native static int openLedDriver(String path);
	private native static void closeLedDriver();
	private native static void writeLedDriver(byte[] data, int length);
	
	private native static int openBtnDriver(String path);
	private native static void closeBtnDriver();
	private native static int readBtnDriver();
	
	private native static int openSevDriver(String path);
	private native static void closeSevDriver();
	private native static int writeSevDriver(byte[] data, int length);
	
	private native static int openEEDriver(String path);
	private native void closeEEDriver();
	
	private native void writeEEPROM(int addr, int data);
	private native int readEEPROM(int addr);
	
	private native static int readVrFile(String path, String type);
	
	private native static int readCdsFile(String path, String type);
	
	private native static int openLcdDriver(String path);
	private native static void closeLcdDriver();
	private native static void displayOn();
	private native static void displayOff();
	private native static void displayClear();
	private native static void cursorOn();
	private native static void cursorOff();
	private native static void cursorLeft();
	private native static void cursorRight();
	private native static void cursorHome();
	private native static void writeLine1(String str, int len);
	private native static void writeLine2(String str, int len);
	
	public JNIDriver() {
		mBuzFlag = false;
		mLedFlag = false;
		mBtnFlag = false;
		mSevFlag = false;
		mLcdFlag = false;
		mEEpromFlag = false;
	}
	// 버저
	public int openBuz(String driver) {
		if(mBuzFlag) return -1;
		
		if(openBuzDriver(driver)>0) {
			mBuzFlag = true;
			return 1;
		} else {
			return -1;
		}
	}
	public void closeBuz() {
		if(!mBuzFlag) return;
		mBuzFlag = false;
		closeBuzDriver();
	}
	
	//엘이디
	public int openLed(String driver) {
		if(mLedFlag) return -1;
		
		if(openLedDriver(driver)>0) {
			mLedFlag = true;
			return 1;
		} else {
			return -1;
		}
	}
	
	public void closeLed() {
		if(!mLedFlag) return;
		mLedFlag = false;
		closeLedDriver();
	}
	
	//스위치
	public int openBtn(String driver) {
		if(mBtnFlag) return -1;
		
		if(openBtnDriver(driver)>0) {
			mBtnFlag = true;
			return 1;
		} else {
			return -1;
		}
	}
	public void closeBtn() {
		if(!mBtnFlag) return;
		mBtnFlag = false;
		closeBtnDriver();
	}
	
	protected void finalize() throws Throwable {
		closeBuz();
		closeLed();
		closeBtn();
		super.finalize();
	}
	
	public int readBtn() {
		return readBtnDriver();
	}
	
	protected void writeBuz() {
		if(!mBuzFlag) return;
		byte[] data = {0,0,0,1};
		
		if(data!=null) writeBuzDriver(data, data.length);
	}
	
	protected void writeLed(int var) {
		if(!mLedFlag) return;
		byte[] data = {0,0,0,0};
		switch (var) {
		case 4:
			data[0] = 1;
			data[1] = 1;
			data[2] = 1;
			data[3] = 1;
			break;
		case 3:
			data[0] = 1;
			data[1] = 1;
			data[2] = 1;
			data[3] = 0;
			break;
		case 2:
			data[0] = 1;
			data[1] = 1;
			data[2] = 0;
			data[3] = 0;
			break;
		case 1:
			data[0] = 1;
			data[1] = 0;
			data[2] = 0;
			data[3] = 0;
			break;
		case 0:
			data[0] = 0;
			data[1] = 0;
			data[2] = 0;
			data[3] = 0;
			break;
		}
		writeLedDriver(data, data.length);
	}
	
	protected void writeLedSwitch(int read) {
		if(!mLedFlag) return;
		byte[] led = {0,0,0,0};
		if(read == 1) {
			led[0] = 1;
			led[2] = 1;
		}
		if(read == 2) {
			led[1] = 1;
			led[3] = 1;
		}
		if(read == 4) {
			led[0] = 0;
			led[1] = 0;
			led[2] = 0;
			led[3] = 0;
		}
		//1번과 2번 동시 클릭
		if(read == 3) {
			led[0] = 1;
			led[1] = 1;
		}
		
		if(read == 5) {
			led[0] = 1;
			led[2] = 1;
		}
		
		if(read == 6) {
			led[1] = 1;
			led[2] = 1;
		}
		
		if(read == 7) {
			led[0] = 1;
			led[1] = 1;
			led[2] = 1;
		}
		writeLedDriver(led, led.length);
	
	}
	
	protected void writeBuzzer(int var) {
		if(!mBuzFlag) return;
		byte[] data = {0,0,0,0};
		if(var == 0) {
			data[3] = 0;
		}
		else if(var == 1) {
			data[3] = 1;
		}
		if(data!=null) writeBuzDriver(data, data.length);
	}
	
	// Seven
	public int openSev(String driver) {
		if(mSevFlag) return -1;
		
		if(openSevDriver(driver)>0) {
			mSevFlag = true;
			return 1;
		} else {
			return -1;
		}
	}
	
	public void closeSev() {
		if(!mSevFlag) return;
		mSevFlag = false;
		closeSevDriver();
	}
		
	protected void writeSev(byte b1, byte b2, byte b3, byte b4) {
		if(!mSevFlag) return;
		
		byte[] data = {b1,b2,b3,b4};
		
		writeSevDriver(data, data.length);
	}
	
	// Vr
	public int readVr(String path, String type) {	
		return readVrFile(path, type);
	}
	
	// CDS
	public int readCds(String path, String type) {	
		return readCdsFile(path, type);
	}
	
	
	// EEPROM
	public int openEE(String driver) {
		if(mEEpromFlag) return -1;
		
		if(openEEDriver(driver)>0) {
			mEEpromFlag = true;
			return 1;
		} else {
			return -1;
		}
	}
	
	public void closeEE() {
		if(!mEEpromFlag) return;
		mEEpromFlag = false;
		closeEEDriver();
	}
	
	public void writeEE(int addr, int data) {
		if(!mEEpromFlag) return;
		writeEEPROM(addr, data);	
	}
	
	public int readEE(int addr) {
		if(!mEEpromFlag) return -1;
		return readEEPROM(addr);	
	}
	
	
	public int openLcd(String driver) {
		if(mLcdFlag) return -1;
		
		if(openLcdDriver(driver)>0) {
			mLcdFlag = true;
			return 1;
		} else {
			return -1;
		}
	}
	
	public void closeLcd() {
		if(!mLcdFlag) return;
		mLcdFlag = false;
		closeLcdDriver();
	}
	
	public void setDisplayVisible(boolean b) {
		if(b) displayOn();
		else displayOff();
	}
	
	public void clearDisplay() {
		displayClear();
	}
	
	public void setCursorVisible(boolean b) {
		if(b) cursorOn();
		else cursorOff();
	}
	
	public void setCursorLeft() {
		cursorLeft();
	}
	
	public void setCursorRight() {
		cursorRight();
	}
	
	public void setCursorHome() {
		cursorHome();
	}
	
	public void setText(int num, String str) {
		if(num == 1) writeLine1(str, str.length());
		else if(num == 2) writeLine2(str, str.length());
	}
}