
package com.example.finalroomescape;




import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static int TURNON = 0;
	private static int TURNOFF = 1;

	private static int HEADGET = 1;
	private static int HEARTGET = 2;
	private static int NOTHAVE = 0;
	
	private static int PENCHGET = 1;
	private static int NOTPENCH = 0;
	
	private static int DIARYKEYGET = 1;
	private static int NOTDIARYKEY = 0;
	
	private static int FLASH = 1;
	private static int NOMAL = 0;
	
	private static int DRAWUPSIDEDOWN = 1;
	private static int NOTDRAW = 0;
	
	private static int LIGHTGET = 1;
	private static int NOTLIGHT = 0;
	
	private static int CELLGET = 1;
	
	private static int CLOCK = 1;
	
	private static int HEADOPEN = 1;
	
	private static int HEARTOPEN =1;
	
	
	int flagLight = TURNON;
	int safestate = NOTHAVE;
	int penchstate = NOTPENCH;
	int diarystate = NOTDIARYKEY;
	int drawstate = NOTDRAW;
	int flashstate = NOMAL;
	int blacklight = NOTLIGHT;
	int cellstate = 0;
	int clockstate = 0;
	int mirrornum = 0;
	int headopen = 0;
	int heartopen = 0;
	int cellClick = 0;
	
	
	//-----select dialog ---------
	int selectTv = 0;
	int selectWash = 0;
	int selectMirror = 0;
	int selectDoll = 0;
	
	
	JNIDriver mDriver = new JNIDriver();
	ViewPager mPager;
	ImageButton doorbtn, toilbtn, toilbook, mirrorbtn, clock, safebook, womanbtn;
	LinearLayout back;
	Dialog nomaldialog, imagedialog, nightdialog, selectdialog, radiodialog, headsafedialog, heartsafedialog, clockdialog;
	EditText n1, n2, n3, n4;
	
	int count = 600;
	int chance = 4;
	int backstop = 0;
	
	
	int i = 0;
	int tvcount;
	int lcdcount;
	int bookcount = 1;
	int headsafecount = 0;
	int heartsafecount = 0;
	
	//-----------normaldialog----------
	int normalpench = 0;
	int normaldiarybook = 0;
	int normaldiaryopen = 0;
	int imageSafe = 0;
	
	// --------getAfter----------------
	int afterPench = 0;
	
	// --------number----------
	byte[] headnum = {0,0,0,0};
	byte[] heartnum = {0,0,0};
	
	//--------flashbackground-----
	int flashback = 0;
	int stopimage = 0;
	
	ImageView img;
	CountDownTimer timer_1, timer_2, timer_3, timer_4, timer_5;
	
	View.OnClickListener mClick;
	
	private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){   // Message id 가 0 이면
            	if(count == 0) {
					mDriver.setText(1, "---You are Die---");
					mDriver.setText(2, "-----------------");
					mDriver.writeLed(0);
            		finish();
            	}
				byte b1 = (byte)(count%10000/1000);
				byte b2 = (byte)(count%1000/100);
				byte b3 = (byte)(count%100/10);
				byte b4 = (byte)(count%10);
				mDriver.writeSev(b1, b2, b3, b4);
				count--;
				
				int data = mDriver.readCds("/sys/devices/12d10000.adc/iio:device0/in_voltage0_raw", "r");
				if(data<0) Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_LONG).show();
				Log.i("msg", "CDS:"+data);
				if(data<3000 && data>0) {
					back.setBackgroundResource(R.drawable.black_wall);
					flagLight = TURNOFF;
					flashstate = FLASH;
				} 
				if(data>=3000) {
					if(backstop == 0)
					back.setBackgroundResource(R.drawable.wall);
					
					if(flashstate == FLASH) {
						if(stopimage == 0) {
							mirrorbtn.setImageResource(R.drawable.mirror_blood_states);
							toilbtn.setImageResource(R.drawable.toilet_blood_states);
							toilbook.setImageResource(R.drawable.shelf_blood_states);
							stopimage = 1;
						}
						
						
							if(safestate == HEARTGET  || safestate == HEADGET ) {
								womanbtn.setEnabled(false);
								womanbtn.setVisibility(View.INVISIBLE);
							} else {
								womanbtn.setEnabled(true);
								womanbtn.setVisibility(View.VISIBLE);
							}
					}
					flagLight = TURNON;
				}
				
				
				
            } else if(msg.what == 1) {
  
            }
        }

};

class CountThread extends Thread{
    @Override
    public void run() {
        while(true){
            // 메인에서 생성된 Handler 객체의 sendEmpryMessage 를 통해 Message 전달
            handler.sendEmptyMessage(0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } // end while
    } // end run()
} // end class CountThread

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		back = (LinearLayout)findViewById(R.id.layout);
		
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(new BkPagerAdapter(getApplicationContext()));
		mPager.setCurrentItem(1);
		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
		
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				updateImage();
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		mClick = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(v.getId()) {
				case R.id.yesBtn:
					if(selectTv == 1) {
						tvdial();
					} else if(selectWash == 1) {
						imagedialog(R.drawable.diary_key_extends);
						diarystate = DIARYKEYGET;
					} else if(selectMirror == 1) {
						mirrorbtn.setImageResource(R.drawable.mirror_number_states);
						mirrornum = 1;
					} else if(selectDoll == 1) {
						imagedialog(R.drawable.cell_extends);
						cellstate = CELLGET;
					}
					selectdialog.dismiss();
					selectDoll= 0;
					selectTv = 0;
					selectWash = 0;
					selectMirror = 0;
					break;
				case R.id.noBtn:
					selectdialog.dismiss();
					selectDoll= 0;
					selectTv = 0;
					selectWash = 0;
					selectMirror = 0;
					break;
				case R.id.radioBtn:
					int valueVr = mDriver.readVr("/sys/devices/12d10000.adc/iio:device0/in_voltage3_raw", "r");
					if(mDriver.readBtn() == 7 && valueVr < 500 && valueVr > 100) {
						try {
							mDriver.writeBuz();
							Thread.sleep(50);
							mDriver.writeBuz();
							Thread.sleep(600);
							mDriver.writeBuz();
							Thread.sleep(600);
							mDriver.writeBuz();
							success();
							} catch(InterruptedException e) {};
					} else if(mDriver.readBtn() == 6 &&  valueVr < 2000 && valueVr > 1600) {
						try {
							mDriver.writeBuz();
							Thread.sleep(600);
							mDriver.writeBuz();
							Thread.sleep(600);
							mDriver.writeBuz();
							Thread.sleep(50);
							mDriver.writeBuz();
							success();
							} catch(InterruptedException e) {};
					} else if(mDriver.readBtn() == 3 && valueVr < 3700 && valueVr > 3300) {
						try {
							mDriver.writeBuz();
							Thread.sleep(600);
							mDriver.writeBuz();
							Thread.sleep(50);
							mDriver.writeBuz();
							Thread.sleep(600);
							mDriver.writeBuz();
							success();
							} catch(InterruptedException e) {};
					}
					
					else {
						gameOver();
					
					}
					radiodialog.dismiss();
					break;
				case R.id.headBtn:
					
					int valueVrhead = mDriver.readVr("/sys/devices/12d10000.adc/iio:device0/in_voltage3_raw", "r");
					Log.i("head", "head:"+valueVrhead);
					if(valueVrhead < 2900 && valueVrhead > 2600 && headsafecount == 0) {
						headnum[0] = 1;
					}
					if(valueVrhead < 500 && valueVrhead > 100 && headsafecount == 1) {
						headnum[1] = 1;
					}
					if(valueVrhead < 4050 && valueVrhead > 3800 && headsafecount == 2) {
						headnum[2] = 1;
					}
					if(valueVrhead < 1500 && valueVrhead > 1100 && headsafecount == 3) {
						headnum[3] = 1;
					}
					if(headsafecount == 0) {
						headsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.head_safe_one_input);
					} else if(headsafecount ==1) {
						headsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.head_safe_two_input);
					} else if(headsafecount ==2) {
						headsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.head_safe_three_input);
					} else if(headsafecount ==3) {
						headsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.head_safe_full_input);	
					} 
						headsafecount++;
						if(headsafecount >= 4)  {
							headsafecount = 0;
							if(headnum[0] == 1 && headnum[1] == 1 && headnum[2] == 1 && headnum[3] == 1) {
									imageSafe = 1;
									imagedialog(R.drawable.inner_head);
									timer_1 = new CountDownTimer(3000, 1000) {  
							            @Override  
							            public void onFinish() { 
							            	imagedialog.dismiss();
							            	imagedialog(R.drawable.inner_head_non);
							            	imageSafe = 0;
							            	headopen = HEADOPEN;
							            	womanbtn.setImageResource(R.drawable.woman_head_states);
							            	success();
							            }  
							      
							            @Override  
							            public void onTick(long millisUntilFinished) {  
							            	//img.setImageResource(R.drawable.tv_green);
							            }  
							        };
							        timer_1.start();
							     
							        headnum[0] = 0;
							        headnum[1] = 0;
							        headnum[2] = 0;
							        headnum[3] = 0;
							} else {
								gameOver();
							}
							headsafedialog.dismiss();
						}
						Log.i("head", "head:"+headnum[0]+headnum[1]+headnum[2]+headnum[3]);	
					break;
				case R.id.heartBtn:
					int valueVr2 = mDriver.readVr("/sys/devices/12d10000.adc/iio:device0/in_voltage3_raw", "r");
					Log.i("head", "heart:"+valueVr2);
					if(valueVr2 < 500 && valueVr2 > 100 && heartsafecount == 0) {
						heartnum[0] = 1;
					}
					if(valueVr2 < 500 && valueVr2 > 100 && heartsafecount == 1) {
						heartnum[1] = 1;
					}
					if(valueVr2 < 1500 && valueVr2 > 1100 && heartsafecount == 2) {
						heartnum[2] = 1;
					}
					if(heartsafecount == 0) {
						heartsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.heart_safe_one_input);
					} else if(heartsafecount ==1) {
						heartsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.heart_safe_two_input);
					} else if(heartsafecount ==2) {
						heartsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.heart_safe_full_input);
					} 
						heartsafecount++;
						if(heartsafecount >= 3)  {
							headsafecount = 0;
							if(heartnum[0] == 1 && heartnum[1] == 1 && heartnum[2] == 1) {
									imageSafe = 1;
									imagedialog(R.drawable.inner_heart);
									timer_1 = new CountDownTimer(3000, 1000) {  
							            @Override  
							            public void onFinish() { 
							            	imagedialog.dismiss();
							            	imagedialog(R.drawable.inner_heart_non);
							            	imageSafe = 0;
							            	heartopen = HEARTOPEN;
							            	womanbtn.setImageResource(R.drawable.woman_heart_states);
							            	success();
							            }  
							      
							            @Override  
							            public void onTick(long millisUntilFinished) {  
							            	//img.setImageResource(R.drawable.tv_green);
							            }  
							        };
							        timer_1.start();
							     
							        heartnum[0] = 0;
							        heartnum[1] = 0;
							        heartnum[2] = 0;
							} else {
								gameOver();
							}
							heartsafedialog.dismiss();
						}
						Log.i("head", "head:"+heartnum[0]+heartnum[1]+heartnum[2]);	
					break;
				case R.id.clockCheckBtn:
					if(n1.getText().toString().equals("2") && n2.getText().toString().equals("1") && n3.getText().toString().equals("5") && n4.getText().toString().equals("6")) {
						clockstate = CLOCK;
						nomaldialog("복도에서 소리가 들렸다.");
						clock.setImageResource(R.drawable.clock3_states);
						success();
					} else {
						gameOver();
					}
					Log.i("head", "clock:"+n1.getText()+n2.getText()+n3.getText()+n4.getText());	
					clockdialog.dismiss();
				}
			}
		};
		
	    CountThread thread = new CountThread();
        thread.setDaemon(true);
        thread.start();
	}
	
	private OnClickListener mButtonClick = new OnClickListener() {		//클릭 이벤트 객체
		public void onClick(View v) {
			switch(v.getId()) {
//-----------------------------------------LivingRoom----------------------------------------------------
				case R.id.doll1Btn:
					if(flagLight == TURNON) {
						if(cellstate == CELLGET) {
							nomaldialog("더 이상 얻을것은 안보인다.");
						} else {
							if(flashstate == FLASH) {
								selectDoll = 1;
								selectdialog("건전지가 있다. 가져갈까?");
							} else {
								nomaldialog("안이 비어있다.");
							}
						}
					}
					else {
						nomaldialog("어두워서 보이지않는다.");
					}
				break;
				
			case R.id.doll2Btn:
				if(flagLight == TURNON) {
					imagedialog(R.drawable.dollpaper_sw2);
				}
				else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.doll3Btn:
				if(flagLight == TURNON) {
					imagedialog(R.drawable.dollpaper_sw1);
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.doll4Btn:
				if(flagLight == TURNON) {
					imagedialog(R.drawable.dollpaper_sw3);
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.doorBtn:
				if(flagLight == TURNON) {
					if(safestate == HEADGET) {
						backstop = 1;
						doorbtn.setImageResource(R.drawable.open_door);
						back.setBackgroundResource(R.drawable.red_wall);
						timer_1 = new CountDownTimer(3000, 1000) {  
				            @Override  
				            public void onFinish() { 
				            	mDriver.writeBuz();
				            	mDriver.writeBuz();
								mDriver.setText(1, "---You are Die---");
								mDriver.setText(2, "-----------------");
								finish();
				            }  
				      
				            @Override  
				            public void onTick(long millisUntilFinished) {  
				            	//img.setImageResource(R.drawable.tv_green);
				            }  
				        };
				        timer_1.start();
					}else if(safestate == HEARTGET) {
						backstop = 1;
						doorbtn.setImageResource(R.drawable.open_door2);
						back.setBackgroundResource(R.drawable.wall);
						timer_1 = new CountDownTimer(3000, 1000) {  
				            @Override  
				            public void onFinish() { 
								mDriver.setText(1, "----C-L-E-A-R---");
								mDriver.setText(2, "-----------------");
								finish();
				            }  
				      
				            @Override  
				            public void onTick(long millisUntilFinished) {  
				            	//img.setImageResource(R.drawable.tv_green);
				            }  
				        };
				        timer_1.start();
					}else {
						nomaldialog("잠겨있다.");
					}
					
				} else {
					if(blacklight == LIGHTGET) {
						imagedialog(R.drawable.door_extends);
					} else {
						nomaldialog("어두워서 보이지않는다.");
					}
				}
				break;
				
			case R.id.photoBtn:
				if(flagLight == TURNON) {
					if(drawstate == DRAWUPSIDEDOWN) {
						imagedialog(R.drawable.manual);
					} else {
						nomaldialog("오래된 그림이다.");
					}
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
					
			case R.id.lightBtn:
				if(flagLight == TURNON) {
					imagedialog(R.drawable.light_hint);
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.deskBtn:
				if(flagLight == TURNON) {
					if(blacklight == LIGHTGET) {
						nomaldialog("더 이상 아무것도없다.");
					} else {
					if(clockstate == CLOCK) {
						blacklight = LIGHTGET;
						imagedialog(R.drawable.light_extends);	
						}
					else nomaldialog("잠겨있다."); }
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
// ---------------------------------------Room-------------------------------------------------------				
		
			case R.id.clockbtn:
				if(flagLight == TURNON) {
					if(cellstate == CELLGET) {
						cellClick = 1;
						if(clockstate == CLOCK) {
							nomaldialog("작동하지 않는다.");
						} else {
							clock.setImageResource(R.drawable.clock2_states);
							clockdialog();
						}
					} else {
						nomaldialog("전기가 들어오지 않는다.");
					}
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.bedbtn:
				if(flagLight == TURNON) {
					radiodialog();
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.tvbtn:
				if(flagLight == TURNON) {
					selectTv = 1;
					selectdialog("TV를 켤까?");
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.safeheadbtn:
				if(flagLight == TURNON) {
					if(heartopen == HEARTOPEN || headopen == HEADOPEN) 
						nomaldialog("금고가 망가졌다.");
					else
					headsafedialog();
					
				} else {
					if(blacklight == LIGHTGET) {
						imagedialog(R.drawable.safe_head_extends);
					} else
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.bookbtn:
				if(flagLight == TURNON) {
					if(bookcount < 3) {
						nomaldialog("책이 잔뜩 있다.");
						bookcount++;
					} else if(bookcount == 3){
						safebook.setImageResource(R.drawable.book_safe_blood_states);
						nomaldialog("금고가 있다.");
						bookcount++;
					} else {
						if(headopen == HEADOPEN || heartopen == HEARTOPEN) 
							nomaldialog("금고가 망가졌다.");
						else
						heartsafedialog();
					}
				} else {
					if(bookcount >=3 && blacklight == LIGHTGET) {
						imagedialog(R.drawable.safe_extends);
					} else 
					nomaldialog("어두워서 보이지않는다.");
					
				}
				break;
// ---------------------------------------------------------------Toilet-------------------------------------------------				
			case R.id.womanbtn:
				if(flagLight == TURNON) {
							if(headopen == HEADOPEN) {	
								            	womanbtn.setEnabled(false);
								            	womanbtn.setVisibility(View.INVISIBLE);
								            	imagedialog(R.drawable.door_key_extends);
								            	safestate = HEADGET;
							}
							if(heartopen == HEARTOPEN) {
													womanbtn.setEnabled(false);
									            	womanbtn.setVisibility(View.INVISIBLE);
									            	imagedialog(R.drawable.door_key_extends);
									            	safestate = HEARTGET;
							}
							else if(headopen == 0 && heartopen == 0){
									nomaldialog("시체..인가?.");
							}
					
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.shelfbtn:
				if(flagLight == TURNON) {
					if(flashstate == FLASH) {
						if(diarystate == DIARYKEYGET) {
							normaldiaryopen = 1;
							nomaldialog("열쇠로 열수있을것같다.");
						} else {
							normaldiarybook = 1;
							nomaldialog("책 한권에 피가묻어있다.");
						}
					} else {
						nomaldialog("낡은 책장이다.");
					}
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.mirrorbtn:
				if(flagLight == TURNON) {
					if(flashstate == FLASH) {
						if(mirrornum == 1) {
							nomaldialog("더 이상 지워지지 않는다.");
						} else {
							selectMirror = 1;
							selectdialog("얼룩을 지울까?");
						}
					} else {
						nomaldialog("거울이다.");
					}
					
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;

			case R.id.toilbtn:
				if(flagLight == TURNON) {
					if(flashstate == FLASH) {
						if(afterPench == 0) {
							normalpench = 1;
							nomaldialog("변기물안에 뭔가가 있다.");
							afterPench = 1;
						} else {
							nomaldialog("더 이상 얻을것은 없어보인다.");
						}
					} else {
						nomaldialog("낡은 변기다.");
					}
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;
				
			case R.id.washbtn:
				if(flagLight == TURNON) {
					if(penchstate == PENCHGET) {
						if(diarystate != DIARYKEYGET) {
							selectWash = 1;
							selectdialog("세면대를 해체할까?");
						} else {
							nomaldialog("더 이상 얻을것은 없어보인다.");
						}
					} else {
						nomaldialog("배수구안에 뭔가가있다 \n그러나 너무좁다.");
					}
				} else {
					nomaldialog("어두워서 보이지않는다.");
				}
				break;									
			}
												
		}

	};
	
	public void updateImage() {
		if(flashstate == FLASH) {
			toilbtn.setImageResource(R.drawable.toilet_blood_states);
			toilbook.setImageResource(R.drawable.shelf_blood_states);
		
			if(mirrornum == 1)
				mirrorbtn.setImageResource(R.drawable.mirror_number_states);
			else 
				mirrorbtn.setImageResource(R.drawable.mirror_blood_states);
			
				womanbtn.setEnabled(true);
				womanbtn.setVisibility(View.VISIBLE);
		}
		
		 if(bookcount >= 3)
			safebook.setImageResource(R.drawable.book_safe_blood_states);
		 
		 if(headopen == HEADOPEN) {
			 womanbtn.setImageResource(R.drawable.woman_head_states);
		 }
		 
		 if(heartopen == HEARTOPEN) {
			 womanbtn.setImageResource(R.drawable.woman_heart_states);
		 }
		 
		 if(safestate == HEADGET ) {	
         	womanbtn.setEnabled(false);
         	womanbtn.setVisibility(View.INVISIBLE);
		 }
		 
		 if(safestate == HEARTGET) {
	         womanbtn.setEnabled(false);
	         womanbtn.setVisibility(View.INVISIBLE);
		 }
		 
		 if(cellClick == 1) {
			 if(clockstate == CLOCK) {
					clock.setImageResource(R.drawable.clock3_states);
				 } else
			 clock.setImageResource(R.drawable.clock2_states);
		 }  
	}

	public void gameOver() {
		chance--;
		mDriver.writeLed(chance);
		mDriver.setText(1, "-----F-A-I-L-----");
		mDriver.setText(2, "-----------------");
		if(chance == 0) {
			mDriver.setText(1, "---You are Die---");
			mDriver.setText(2, "-----------------");
			finish();
		}
	}
	
	public void success() {
		mDriver.setText(1, "--S-U-C-C-E-S-S-");
		mDriver.setText(2, "----------------");
	}
	
//-----------------------------------------Dialog-----------------------------------------------------------------------------------------------------------------------
	
	public void imagedialog(int imgsource) {
		imagedialog = new Dialog(this) {
			 @Override
			  public boolean onTouchEvent(MotionEvent event) {
			    // Tap anywhere to close dialog.
				 if(imageSafe == 0) {
			    this.dismiss();
				 }
		
			    return true;
			  }
		};
		imagedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		imagedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		imagedialog.setContentView(R.layout.custom_dial);
		imagedialog.setCanceledOnTouchOutside(false);
		
		  ImageView iv = (ImageView) imagedialog.findViewById(R.id.image);
		  iv.setImageResource(imgsource);	  
		  imagedialog.show();
	}
	
	public void nomaldialog(String str) {
		nomaldialog = new Dialog(this) {
			 @Override
			  public boolean onTouchEvent(MotionEvent event) {
			    // Tap anywhere to close dialog.
				 if(normalpench == 1) {
					 penchstate = PENCHGET;
					 imagedialog(R.drawable.pench_extends);
					 normalpench = 0;
				 }
				 if(normaldiarybook == 1) {
					 imagedialog(R.drawable.lock_diary);
					 normaldiarybook = 0;
				 }
				 if(normaldiaryopen == 1) {
					 drawstate = DRAWUPSIDEDOWN;
					 imagedialog(R.drawable.open_diary);
					 normaldiaryopen = 0;
				 }
			    this.dismiss();
			    return true;
			  }
		};
		nomaldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		nomaldialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
		nomaldialog.setContentView(R.layout.night_dial);
		
		TextView txt = (TextView)nomaldialog.findViewById(R.id.dial_txt);
		txt.setText(str);
		
		  
		nomaldialog.show();
	}
	
	public void clockdialog() {
		clockdialog = new Dialog(this);
		clockdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		clockdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
		clockdialog.setContentView(R.layout.clock_dial);
		clockdialog.setCanceledOnTouchOutside(false);
		n1 = (EditText)clockdialog.findViewById(R.id.clock_num1);
		n2 = (EditText)clockdialog.findViewById(R.id.clock_num2);
		n3 = (EditText)clockdialog.findViewById(R.id.clock_num3);
		n4 = (EditText)clockdialog.findViewById(R.id.clock_num4);
		
		
		ImageButton checkbtn = (ImageButton)clockdialog.findViewById(R.id.clockCheckBtn);

		checkbtn.setOnClickListener(mClick);
		
		clockdialog.show();
	}
	
	public void radiodialog() {
		radiodialog = new Dialog(this);
		radiodialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		radiodialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
		radiodialog.setContentView(R.layout.radio_dial);
		
		
		ImageButton radioBtn = (ImageButton)radiodialog.findViewById(R.id.radioBtn);

		radioBtn.setOnClickListener(mClick);
		
		radiodialog.show();
	}
	
	public void headsafedialog() {
		headsafedialog = new Dialog(this);
		headsafedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		headsafedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		headsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.head_safe_no_input);
		headsafedialog.setContentView(R.layout.head_safe_dial);
		
		
		ImageButton headBtn = (ImageButton)headsafedialog.findViewById(R.id.headBtn);

		headBtn.setOnClickListener(mClick);
		
		headsafedialog.show();
	}
	
	public void heartsafedialog() {
		heartsafedialog = new Dialog(this);
		heartsafedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		heartsafedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		heartsafedialog.getWindow().setBackgroundDrawableResource(R.drawable.heart_safe_no_input);
		heartsafedialog.setContentView(R.layout.heart_safe_dial);
		
		
		ImageButton heartBtn = (ImageButton)heartsafedialog.findViewById(R.id.heartBtn);

		heartBtn.setOnClickListener(mClick);
		
		heartsafedialog.show();
	}

	public void selectdialog(String str) {
		selectdialog = new Dialog(this);
		selectdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		selectdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
		selectdialog.setContentView(R.layout.select_dial);
		
		TextView txt = (TextView)selectdialog.findViewById(R.id.selectTxt);
		txt.setText(str);
		
		ImageButton yes = (ImageButton)selectdialog.findViewById(R.id.yesBtn);
		ImageButton no = (ImageButton)selectdialog.findViewById(R.id.noBtn);
		
		yes.setOnClickListener(mClick);
		no.setOnClickListener(mClick);
		selectdialog.show();
	}
	
	public void tvdial() {
		nightdialog = new Dialog(this);
		nightdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		nightdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//dialog.getWindow().setBackgroundDrawableResource(R.drawable.tv_anime);
		nightdialog.setContentView(R.layout.tv_dial);
		 

		  
		img = (ImageView)nightdialog.findViewById(R.id.tv_quiz);
		img.setImageResource(R.drawable.tv_blue);
		
		timer_5 = new CountDownTimer(1000, 1000) {  
	            @Override  
	            public void onFinish() { 
	            	
	            	int data = mDriver.readBtn();
	            	if((data&4) >0 ) tvcount++;
	            	Log.i("tv:", tvcount+"ㅇㅇ");
	                if(tvcount == 5) {
	                	mDriver.setText(1, "-----7-2-9-4-----");
						mDriver.setText(2, "-----------------");
	                }
	                else {
	                	gameOver();
	                }
	                tvcount = 0;
	                nightdialog.dismiss();
	            }  
	      
	           @Override  
	            public void onTick(long millisUntilFinished) {  
	            	//img.setImageResource(R.drawable.tv_blue);
	            }  
	        };  
	        //타이머 시작
	    	timer_4 = new CountDownTimer(1000, 1000) {  
	            @Override  
	            public void onFinish() { 
	            	img.setImageResource(R.drawable.tv_green);
	            	int data = mDriver.readBtn();
	            	if((data&1) >0 ) tvcount++;
	            	Log.i("tv:", tvcount+"ㅇ");
	                timer_5.start();
	            }  
	      
	            @Override  
	            public void onTick(long millisUntilFinished) {  
	            	//img.setImageResource(R.drawable.tv_green);
	            }  
	        };  
	        
	    	timer_3 = new CountDownTimer(1000, 1000) {  
	            @Override  
	            public void onFinish() { 
	            	img.setImageResource(R.drawable.tv_red);
	            	int data = mDriver.readBtn();
	            	if((data&4) >0 ) tvcount++;
	            	Log.i("tv:", tvcount+"ㅇ");
	                timer_4.start();
	            }  
	      
	            @Override  
	            public void onTick(long millisUntilFinished) {  
	            }  
	        };  
	        
	        
	    	timer_2 = new CountDownTimer(1000, 1000) {  
	            @Override  
	            public void onFinish() { 
	            	img.setImageResource(R.drawable.tv_green);
	            	int data = mDriver.readBtn();
	            	if((data&1) >0 ) tvcount++;
	            	Log.i("tv:", tvcount+"ㅇ");
	                timer_3.start();
	            }  
	      
	            @Override  
	            public void onTick(long millisUntilFinished) {  
	            	//img.setImageResource(R.drawable.tv_green);
	            }  
	        };  
	        
			timer_1 = new CountDownTimer(1000, 1000) {  
	            @Override  
	            public void onFinish() { 
	            	img.setImageResource(R.drawable.tv_red);
	            	int data = mDriver.readBtn();
	            	if((data&2) >0 ) tvcount++;
	            	Log.i("tv:", tvcount+"ㅇ");
	                timer_2.start();
	            }  
	      
	            @Override  
	            public void onTick(long millisUntilFinished) {  
	            	//img.setImageResource(R.drawable.tv_green);
	            }  
	        };  
	        
	        nightdialog.show();  
	        timer_1.start();
	        
	        
	        
		
	}
	
	//-----------------------------------------DialogEnd---------------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mDriver.closeBtn();
		mDriver.closeBuz();
		mDriver.closeEE();
		mDriver.closeLcd();
		mDriver.closeLed();
		mDriver.closeSev();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(mDriver.openBuz("/dev/sm9exynos5_buzzer") < 0) {
			Toast.makeText(this, "Driver Buzzer Open Failed", Toast.LENGTH_LONG).show();
		}
		if(mDriver.openLed("/dev/sm9exynos5_led") < 0) {
			Toast.makeText(this, "Driver LED Open Failed", Toast.LENGTH_LONG).show();
		}
		if(mDriver.openBtn("/dev/sm9exynos5_switch") < 0) {
			Toast.makeText(MainActivity.this, "Driver Switch Open Failed", Toast.LENGTH_LONG).show();
		}
		if(mDriver.openSev("/dev/sm9exynos5_segment")<0) {
			Toast.makeText(MainActivity.this, "Open error", Toast.LENGTH_LONG).show();
		}
		if(mDriver.openEE("/dev/sm9exynos5_eeprom")<0) {
			Toast.makeText(MainActivity.this, "Open error", Toast.LENGTH_LONG).show();
		}
		if(mDriver.openLcd("/dev/sm9exynos5_textlcd")<0){
			Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
		}
		mDriver.writeLed(4);
		mDriver.setText(1, "Escape in 10 min ");
		mDriver.setText(2, "at this Room");
		back.setBackgroundResource(R.drawable.wall);
		imagedialog(R.drawable.preview);
		super.onResume();
	}
	
	//-----------------------------------------------------------------ViewPager----------------------------------------------------------------
	private class BkPagerAdapter extends PagerAdapter{
    	private LayoutInflater mInflater;
    	
    	public BkPagerAdapter(Context con) {
			super();
			mInflater = LayoutInflater.from(con);
		}
    	
    	@Override public int getCount() { return 3; }	
    	
    	//뷰페이저에서 사용할 뷰객체 생성/등록
    	@Override public Object instantiateItem(View pager, int position) {
    		View v = null;
    		if(position==0){
    			v = mInflater.inflate(R.layout.activity_room, null);
    			safebook = (ImageButton)v.findViewById(R.id.bookbtn);
    			clock = (ImageButton)v.findViewById(R.id.clockbtn);
    			
    			v.findViewById(R.id.clockbtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.bedbtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.bookbtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.safeheadbtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.tvbtn).setOnClickListener(mButtonClick);

    			
    		}
    		else if(position==1){
    			v = mInflater.inflate(R.layout.activity_living, null);
    			doorbtn = (ImageButton)v.findViewById(R.id.doorBtn);
    			v.findViewById(R.id.doll1Btn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.doll2Btn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.doll3Btn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.doll4Btn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.doorBtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.photoBtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.lightBtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.deskBtn).setOnClickListener(mButtonClick);
    			
    			
    		}
    		else if(position ==2){
    			v = mInflater.inflate(R.layout.activity_toilet, null);			
    			toilbtn = (ImageButton)v.findViewById(R.id.toilbtn);
    			toilbook = (ImageButton)v.findViewById(R.id.shelfbtn);
    			mirrorbtn =(ImageButton)v.findViewById(R.id.mirrorbtn); 
    			womanbtn =(ImageButton)v.findViewById(R.id.womanbtn); 
    			womanbtn.setEnabled(false);
    			womanbtn.setVisibility(View.INVISIBLE);
    			v.findViewById(R.id.washbtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.womanbtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.toilbtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.shelfbtn).setOnClickListener(mButtonClick);
    			v.findViewById(R.id.mirrorbtn).setOnClickListener(mButtonClick);
    			
       	
    		}
    		
    		((ViewPager)pager).addView(v, 0);
    		return v; 
    	}
    	
    	//뷰 객체 삭제.
		@Override public void destroyItem(View pager, int position, Object view) {
			((ViewPager)pager).removeView((View)view);
		}

		// instantiateItem메소드에서 생성한 객체를 이용할 것인지
		@Override public boolean isViewFromObject(View view, Object obj) { return view == obj; }
		
		
		@Override public void finishUpdate(View arg0) {}
		@Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
		@Override public Parcelable saveState() { return null; }
		@Override public void startUpdate(View arg0) {}
    }
	}

	   
	


