#include <jni.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include <stdio.h>

int fdb = 0;
int fdled = 0;
int fdbtn = 0;
int fdSev = 0;
int fdlcd = 0;
int fdee = 0;

#define EEPROM_WREN	0x6 // set Write Enable Latch
#define EEPROM_WRDI	0x4 // reset Write Enable Latch
#define EEPROM_RDSR	0x5 // set status Register
#define EEPROM_WRSR	0x1 // set status Register
#define EEPROM_READ	0x3 // Read Data from Memory Array
#define EEPROM_WRITE	0x2 // Write Data from Memory Array


#define TEXTLCD_BASE 0x56
#define TEXTLCD_FUNCTION_SET		_IO(TEXTLCD_BASE, 0x31)

#define TEXTLCD_DISPLAY_ON				_IO(TEXTLCD_BASE, 0x32)
#define TEXTLCD_DISPLAY_OFF			_IO(TEXTLCD_BASE, 0x33)
#define TEXTLCD_DISPLAY_CURSOR_ON		_IO(TEXTLCD_BASE, 0x34)
#define TEXTLCD_DISPLAY_CURSOR_OFF		_IO(TEXTLCD_BASE, 0x35)

#define TEXTLCD_CURSOR_SHIFT_RIGHT		_IO(TEXTLCD_BASE, 0x36)
#define TEXTLCD_CURSOR_SHIFT_LEFT		_IO(TEXTLCD_BASE, 0x37)

#define TEXTLCD_ENTRY_MODE_SET		_IO(TEXTLCD_BASE, 0x38)
#define TEXTLCD_RETURN_HOME				_IO(TEXTLCD_BASE, 0x39)
#define TEXTLCD_CLEAR								_IO(TEXTLCD_BASE, 0x3a)

#define TEXTLCD_DD_ADDRESS_1			_IO(TEXTLCD_BASE, 0x3b)
#define TEXTLCD_DD_ADDRESS_2			_IO(TEXTLCD_BASE, 0x3c)
#define TEXTLCD_WRITE_BYTE					_IO(TEXTLCD_BASE, 0x3d)

//BUZZER

JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_openBuzDriver(JNIEnv * env, jclass class, jstring path){
	jboolean iscopy;
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	fdb = open(path_utf, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if(fdb <0) return -1;
	else return 1;
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_closeBuzDriver(JNIEnv * env, jobject obj){
	if(fdb >0) close(fdb);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_writeBuzDriver(JNIEnv * env, jobject obj, jbyteArray arr, jint count){
	jbyte* chars = (*env)->GetByteArrayElements(env, arr, 0);
	if(fdb >0) write(fdb, (unsigned char*)chars, count);
	(*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}


//LED

JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_openLedDriver(JNIEnv* env, jclass class, jstring path) {
	jboolean iscopy;

	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	fdled = open(path_utf, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if(fdled < 0) return -1;
	else return 1;
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_closeLedDriver(JNIEnv* env, jobject obj){
	if(fdled>0) close(fdled);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_writeLedDriver(JNIEnv* env, jobject obj, jbyteArray arr, jint count){
	jbyte* chars = (*env)->GetByteArrayElements(env, arr, 0);
	if(fdled>0) write(fdled, (unsigned char*)chars, count);
	(*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}



//  switch
JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_openBtnDriver(JNIEnv* env, jclass class, jstring path) {
	jboolean iscopy;
	//문자열을 받아오는 코드
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	//파일을 여는(리눅스에서는 파일을 여는것으로 제어)코드
	fdbtn = open(path_utf, O_RDONLY);
	//메모리할당을 해제해주는 코드
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if(fdbtn < 0) return -1;
	else return 1;
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_closeBtnDriver(JNIEnv* env, jobject obj) {
	if(fdbtn > 0) close(fdbtn);
}

JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_readBtnDriver(JNIEnv* env, jobject obj) {
	int ch = 0;

	if(fdbtn > 0) {
		read(fdbtn, &ch, 1);
	}

	return ch;
}


// Seven
JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_openSevDriver(JNIEnv* env, jclass class, jstring path) {
	jboolean iscopy;

	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	fdSev = open(path_utf, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if(fdSev < 0) return -1;
	else return 1;
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_closeSevDriver(JNIEnv* env, jobject obj){
	if(fdSev>0) close(fdSev);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_writeSevDriver(JNIEnv* env, jobject obj, jbyteArray arr, jint count){
	jbyte* chars = (*env)->GetByteArrayElements(env, arr, 0);
	if(fdSev>0) write(fdSev, (unsigned char*)chars, count);
	(*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}


// 가변저항
JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_readVrFile(JNIEnv * env, jclass class, jstring path, jstring type){
	int data;
	jboolean iscopy;
	FILE* fp;
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	const char *type_utf = (*env)->GetStringUTFChars(env, type, &iscopy);

	fp = fopen(path_utf,type_utf);
	if(fp>0) fscanf(fp,"%d",&data);
	fclose(fp);

	(*env)->ReleaseStringUTFChars(env, path, path_utf);
	(*env)->ReleaseStringUTFChars(env, type, type_utf);

	return data;
}

// Cds
JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_readCdsFile(JNIEnv * env, jclass class, jstring path, jstring type){
	int data;
	jboolean iscopy;
	FILE* fp;
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	const char *type_utf = (*env)->GetStringUTFChars(env, type, &iscopy);

	fp = fopen(path_utf,type_utf);
	if(fp>0) fscanf(fp,"%d",&data);
	fclose(fp);

	(*env)->ReleaseStringUTFChars(env, path, path_utf);
	(*env)->ReleaseStringUTFChars(env, type, type_utf);

	return data;
}



// EEPROM
JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_openEEDriver(JNIEnv* env, jclass class, jstring path) {
	jboolean iscopy;

	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	fdee = open(path_utf, O_RDWR);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if(fdee < 0) return -1;
	else return 1;
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_closeEEDriver(JNIEnv* env, jobject obj) {
	if(fdee >0) close(fdee);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_writeEEPROM(JNIEnv* env, jobject obj, jint addr, jint data) {

	uint8_t tx_packet_write[] = {
			EEPROM_WRITE, 0x00, 0x00, 0xaa
	};

	uint8_t tx_packet_wren[] = {
			EEPROM_WREN
	};

	uint8_t tx_packet_wrdi[] = {
			EEPROM_WRDI
	};

	if(addr > 2047) addr = 2047;
	else if(addr < 0) addr = 0;

	if(data > 255) data = 255;
	else if(data < 0) data = 0;

	tx_packet_write[1] = (addr >> 8);
	tx_packet_write[2] = (addr & 0xff)+1;
	tx_packet_write[3] = data;

	write(fdee, tx_packet_wren, 1);
	usleep(1*1000);

	write(fdee, tx_packet_write, 4);
	usleep(2*1000);

	write(fdee, tx_packet_wrdi, 1);
}

JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_readEEPROM(JNIEnv* env, jobject obj, jint addr) {
	int data;

	uint8_t tx_packet[] = {
			EEPROM_READ, 0x00, 0x00, 0x00
	};

	if(addr > 2047) addr = 2047;
	else if(addr <0) addr = 0;

	tx_packet[1] = (addr >> 8);
	tx_packet[2] = (addr & 0xff);

	data = read(fdee, tx_packet, 4);
	if(data < 1) return -1;
	else return data;
}



// LCD
JNIEXPORT jint JNICALL Java_com_example_finalroomescape_JNIDriver_openLcdDriver(JNIEnv* env, jclass class, jstring path) {
	jboolean iscopy;

	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	fdlcd = open(path_utf, O_RDWR);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	if(fdlcd < 0) return -1;
	else return 1;
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_closeLcdDriver(JNIEnv* env, jclass class) {
	if(fdlcd >0) close(fdlcd);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_displayOn(JNIEnv* env, jclass class) {
	ioctl(fdlcd, TEXTLCD_DISPLAY_ON, NULL);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_displayOff(JNIEnv* env, jclass class) {
	ioctl(fdlcd, TEXTLCD_DISPLAY_OFF, NULL);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_displayClear(JNIEnv* env, jclass class) {
	ioctl(fdlcd, TEXTLCD_CLEAR, NULL);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_cursorOn(JNIEnv* env, jclass class) {
	ioctl(fdlcd, TEXTLCD_DISPLAY_CURSOR_ON, NULL);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_cursorOff(JNIEnv* env, jclass class) {
	ioctl(fdlcd, TEXTLCD_DISPLAY_CURSOR_OFF, NULL);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_cursorLeft(JNIEnv* env, jclass class) {
	ioctl(fdlcd, TEXTLCD_CURSOR_SHIFT_LEFT, NULL);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_cursorRight(JNIEnv* env, jclass class) {
	ioctl(fdlcd, TEXTLCD_CURSOR_SHIFT_RIGHT, NULL);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_cursorHome(JNIEnv* env, jclass class) {
	ioctl(fdlcd, TEXTLCD_RETURN_HOME, NULL);
}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_writeLine1(JNIEnv* env, jclass class, jstring str, jint len) {
	jboolean iscopy;
	int i = 0;

	const char *str_utf = (*env)->GetStringUTFChars(env, str, &iscopy);

	ioctl(fdlcd, TEXTLCD_DD_ADDRESS_1, NULL);
	for(i=0; i<len; i++)
		ioctl(fdlcd, TEXTLCD_WRITE_BYTE, str_utf[i]);

		(*env)->ReleaseStringUTFChars(env, str, str_utf);



}

JNIEXPORT void JNICALL Java_com_example_finalroomescape_JNIDriver_writeLine2(JNIEnv* env, jclass class, jstring str, jint len) {
	jboolean iscopy;
	int i = 0;

	const char *str_utf = (*env)->GetStringUTFChars(env, str, &iscopy);

	ioctl(fdlcd, TEXTLCD_DD_ADDRESS_2, NULL);
	for(i=0; i<len; i++)
		ioctl(fdlcd, TEXTLCD_WRITE_BYTE, str_utf[i]);

		(*env)->ReleaseStringUTFChars(env, str, str_utf);


}
