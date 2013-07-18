package com.research.aserver;

//import android.util.Log;

/**
 * IPSODAのサーボモータを管理します。 サーボモータは上から順に、0-4番となっています。 位置は0-200の整数で指定し、中心が100となります。
 * 
 * @author kitade
 * 
 */
public class MultiServoState extends Thread {
	TwoWaySerialComm serialCom;
	int currentPos[];

	/**
	 * コンストラクタ
	 * 
	 * @param input_sc
	 *            bluetoothComを入力
	 */
	public MultiServoState(TwoWaySerialComm input_sc) {
		synchronized (this) {
			serialCom = input_sc;
			initServo();
		}
		System.out.println("[MultiServoState] initialized!");
	}

	/**
	 * サーボモータを初期化（100の位置）します
	 */
	public void initServo() {
		currentPos = new int[5];
		synchronized (this) {
			// 100を中心に定めた
			serialCom.sendMessage("100 100 100 100 100");
			currentPos[0] = 100;
			currentPos[1] = 100;
			currentPos[2] = 100;
			currentPos[3] = 100;
			currentPos[4] = 100;
		}
	}


	/**
	 * 現在のサーボモータ位置を返します
	 * 
	 * @param input_servoNo
	 *            サーボモータ番号
	 * @return サーボモータの位置
	 */
	public int getCurrentPos(int input_servoNo) {
		return currentPos[input_servoNo];
	}

	/**
	 * サーボモータの数値を指定して動かします
	 * 
	 * @param targetPos0
	 *            0番サーボの位置
	 * @param targetPos1
	 *            1番サーボの位置
	 * @param targetPos2
	 *            2番サーボの位置
	 * @param targetPos3
	 *            3番サーボの位置
	 * @param targetPos4
	 *            4番サーボの位置
	 * @param speed
	 *            動作速度
	 * @throws InterruptedException
	 */
	public void runServo(final int targetPos0, final int targetPos1,
			final int targetPos2, final int targetPos3, final int targetPos4,
			final int speed) throws InterruptedException {
		this.setPriority(Thread.MAX_PRIORITY);
		/**
		 * 他の処理をブロックしないようにするため、匿名クラスにてスレッドを利用しています。
		 */
		(new Runnable() {
			public void run() {
				synchronized (this) {
					System.out.println("runServo[" + targetPos0 + ", "
							+ targetPos1 + ", " + targetPos2 + ", " + targetPos3
							+ "," + targetPos4 + "] :" + ", " + speed);
					// sendPos0-4は、実際にモータへ送信する値です。
					int sendPos0 = currentPos[0];
					int sendPos1 = currentPos[1];
					int sendPos2 = currentPos[2];
					int sendPos3 = currentPos[3];
					int sendPos4 = currentPos[4];

					int sleepTime = 7;
					if (targetPos0 > -1 && targetPos0 < 201 && targetPos1 > -1
							&& targetPos1 < 201 && targetPos2 > -1
							&& targetPos2 < 201 && targetPos3 > -1
							&& targetPos3 < 201 && targetPos4 > -1
							&& targetPos4 < 201 && speed > 0 && speed < 6) {
						// System.out.println("currentPos : " + currentPos);
						// System.out.println("targetPos : " + targetPos);
						// System.out.println("speed : " + speed);
						/**
						 * intervalにより間引き調整を行います
						 */
						int interval = 1;
						if (speed > 4) {
							interval = 100;
							sleepTime = 0;
						} else if (speed > 3) {
							interval = 29;
							sleepTime = 1;
						} else if (speed > 2) {
							interval = 17;
							sleepTime = 2;
						} else if (speed > 1) {
							interval = 13;
							sleepTime = 3;
						} else if (speed > 0) {
							interval = 8;
							sleepTime = 4;
						}

						int count = interval;
						while (currentPos[0] != targetPos0
								|| currentPos[1] != targetPos1
								|| currentPos[2] != targetPos2
								|| currentPos[3] != targetPos3
								|| currentPos[4] != targetPos4) {
							try {
								if (speed != 100) {
									sleep(sleepTime);
								}
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							/**
							 * モータ別の処理
							 */
							// if (currentPos[0] == targetPos0 - 1) {
							// 0番サーボ
							if (targetPos0 > currentPos[0]) {
								sendPos0++;
							} else if (targetPos0 < currentPos[0]) {
								sendPos0--;
							}
							// 1番サーボ
							if (targetPos1 > currentPos[1]) {
								sendPos1++;
							} else if (targetPos1 < currentPos[1]) {
								sendPos1--;
							}
							// 2番サーボ
							if (targetPos2 > currentPos[2]) {
								sendPos2++;
							} else if (targetPos2 < currentPos[2]) {
								sendPos2--;
							}
							// 3番サーボ
							if (targetPos3 > currentPos[3]) {
								sendPos3++;
							} else if (targetPos3 < currentPos[3]) {
								sendPos3--;
							}
							// 4番サーボ
							if (targetPos4 > currentPos[4]) {
								sendPos4++;
							} else if (targetPos4 < currentPos[4]) {
								sendPos4--;
							}
							currentPos[0] = sendPos0;
							currentPos[1] = sendPos1;
							currentPos[2] = sendPos2;
							currentPos[3] = sendPos3;
							currentPos[4] = sendPos4;
							/**
							 * 間引き処理
							 */
							if ((count % interval) == 0) {
								// 送信
								serialCom.sendMessage(changeFormat(sendPos0)
										+ " " + changeFormat(sendPos1) + " "
										+ changeFormat(sendPos2) + " "
										+ changeFormat(sendPos3) + " "
										+ changeFormat(sendPos4));
								//Log test
								/*Log.v("bluetest",changeFormat(sendPos0)
										+ " " + changeFormat(sendPos1) + " "
										+ changeFormat(sendPos2) + " "
										+ changeFormat(sendPos3) + " "
										+ changeFormat(sendPos4));*/
							}
							// 忘れずに
							count++;
						}
						// 以下ループ外
						currentPos[0] = targetPos0;
						currentPos[1] = targetPos1;
						currentPos[2] = targetPos2;
						currentPos[3] = targetPos3;
						currentPos[4] = targetPos4;
						serialCom.sendMessage(changeFormat(targetPos0) + " "
								+ changeFormat(targetPos1) + " "
								+ changeFormat(targetPos2) + " "
								+ changeFormat(targetPos3) + " "
								+ changeFormat(targetPos4));
					}
				}
			}
		}).run();
	}

	/**
	 * せり出しのジェスチャを行います。
	 * 
	 * @param level
	 *            せり出しの度合い。0-100の整数です。
	 * @param speed
	 *            せり出しを行う速度。1-5の整数です。
	 */
	public void attentionGesture(int level, int speed) {
		try {
			if (level > -1 && level < 101 && speed < 6 && speed > 0) {
				runServo(currentPos[0], currentPos[1], 100 - level,
						100 + level, currentPos[4], speed);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 引き込みのジェスチャを行います。
	 * 
	 * @param level
	 *            引き込みの度合い。0-100の整数です。
	 * @param speed
	 *            引きこみを行う速度。0-100の整数です。
	 */
	public void pullGesture(int level, int speed) {
		try {
			if (level > -1 && level < 101 && speed < 6 && speed > 1) {
				runServo(currentPos[0], currentPos[1], 100 + level,
						100 - level, currentPos[4], speed);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * お辞儀のジェスチャを行います。
	 * 
	 * @param level
	 *            お辞儀の深さ。0-125の整数です。
	 * @param speed
	 *            引きこみを行う速度。0-100の整数です。
	 * @param holdTime
	 *            お辞儀を維持する時間(ms)。
	 */
	public void bowGesture(int level, int speed, int holdTime) {
		try {
			if (level > -1 && level < 101 && speed < 6 && speed > 1
					&& holdTime > -1) {
				runServo(currentPos[0], currentPos[1], 200 - level,
						200 - level, currentPos[4], speed);
				sleep(holdTime);
				runServo(currentPos[0], currentPos[1], 100, 100, currentPos[4],
						speed);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 画面をシェイクします。
	 * 
	 * @param level
	 *            シェイクの深さを0-100の深さで指定します。
	 * @param speed
	 *            動きのスピードを指定します。
	 * @param repeat
	 *            繰り返し回数を指定します。
	 * @param interval
	 *            繰り返しの間の時間を指定します(ms)。
	 */
	public void shakeGesture(int level, int speed, int repeat, int interval) {
		try {
			if (level > -1 && level < 101 && speed < 101 && speed > 0
					&& interval > -1) {
				for (int i = 0; i < repeat; i++) {
					runServo(100 + level, currentPos[1], currentPos[2],
							currentPos[3], currentPos[4], speed);
					sleep(150);
					runServo(100 - level, currentPos[1], currentPos[2],
							currentPos[3], currentPos[4], speed);
					sleep(150);
					runServo(100, currentPos[1], currentPos[2], currentPos[3],
							currentPos[4], speed);
					sleep(interval);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 直立します。
	 * 
	 * @param speed
	 *            直立するスピードを指定します。
	 */
	public void standUp(int speed) {
		try {
			runServo(currentPos[0], currentPos[1], 100, 100, currentPos[4],
					speed);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * サーボの位置を、マイコン側で解釈出来る形に変換
	 * 
	 * @param num
	 * @return
	 */
	private String changeFormat(int num) {
		String result;
		num = Math.abs(num);
		if (num < 10) {
			result = "00" + num;
		} else if (num < 100) {
			result = "0" + num;
		} else {
			result = "" + num;
		}
		return result;
	}
}
