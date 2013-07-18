package com.research.aserver;

//import android.util.Log;

/**
 * IPSODA�̃T�[�{���[�^���Ǘ����܂��B �T�[�{���[�^�͏ォ�珇�ɁA0-4�ԂƂȂ��Ă��܂��B �ʒu��0-200�̐����Ŏw�肵�A���S��100�ƂȂ�܂��B
 * 
 * @author kitade
 * 
 */
public class MultiServoState extends Thread {
	TwoWaySerialComm serialCom;
	int currentPos[];

	/**
	 * �R���X�g���N�^
	 * 
	 * @param input_sc
	 *            bluetoothCom�����
	 */
	public MultiServoState(TwoWaySerialComm input_sc) {
		synchronized (this) {
			serialCom = input_sc;
			initServo();
		}
		System.out.println("[MultiServoState] initialized!");
	}

	/**
	 * �T�[�{���[�^���������i100�̈ʒu�j���܂�
	 */
	public void initServo() {
		currentPos = new int[5];
		synchronized (this) {
			// 100�𒆐S�ɒ�߂�
			serialCom.sendMessage("100 100 100 100 100");
			currentPos[0] = 100;
			currentPos[1] = 100;
			currentPos[2] = 100;
			currentPos[3] = 100;
			currentPos[4] = 100;
		}
	}


	/**
	 * ���݂̃T�[�{���[�^�ʒu��Ԃ��܂�
	 * 
	 * @param input_servoNo
	 *            �T�[�{���[�^�ԍ�
	 * @return �T�[�{���[�^�̈ʒu
	 */
	public int getCurrentPos(int input_servoNo) {
		return currentPos[input_servoNo];
	}

	/**
	 * �T�[�{���[�^�̐��l���w�肵�ē������܂�
	 * 
	 * @param targetPos0
	 *            0�ԃT�[�{�̈ʒu
	 * @param targetPos1
	 *            1�ԃT�[�{�̈ʒu
	 * @param targetPos2
	 *            2�ԃT�[�{�̈ʒu
	 * @param targetPos3
	 *            3�ԃT�[�{�̈ʒu
	 * @param targetPos4
	 *            4�ԃT�[�{�̈ʒu
	 * @param speed
	 *            ���쑬�x
	 * @throws InterruptedException
	 */
	public void runServo(final int targetPos0, final int targetPos1,
			final int targetPos2, final int targetPos3, final int targetPos4,
			final int speed) throws InterruptedException {
		this.setPriority(Thread.MAX_PRIORITY);
		/**
		 * ���̏������u���b�N���Ȃ��悤�ɂ��邽�߁A�����N���X�ɂăX���b�h�𗘗p���Ă��܂��B
		 */
		(new Runnable() {
			public void run() {
				synchronized (this) {
					System.out.println("runServo[" + targetPos0 + ", "
							+ targetPos1 + ", " + targetPos2 + ", " + targetPos3
							+ "," + targetPos4 + "] :" + ", " + speed);
					// sendPos0-4�́A���ۂɃ��[�^�֑��M����l�ł��B
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
						 * interval�ɂ��Ԉ����������s���܂�
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
							 * ���[�^�ʂ̏���
							 */
							// if (currentPos[0] == targetPos0 - 1) {
							// 0�ԃT�[�{
							if (targetPos0 > currentPos[0]) {
								sendPos0++;
							} else if (targetPos0 < currentPos[0]) {
								sendPos0--;
							}
							// 1�ԃT�[�{
							if (targetPos1 > currentPos[1]) {
								sendPos1++;
							} else if (targetPos1 < currentPos[1]) {
								sendPos1--;
							}
							// 2�ԃT�[�{
							if (targetPos2 > currentPos[2]) {
								sendPos2++;
							} else if (targetPos2 < currentPos[2]) {
								sendPos2--;
							}
							// 3�ԃT�[�{
							if (targetPos3 > currentPos[3]) {
								sendPos3++;
							} else if (targetPos3 < currentPos[3]) {
								sendPos3--;
							}
							// 4�ԃT�[�{
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
							 * �Ԉ�������
							 */
							if ((count % interval) == 0) {
								// ���M
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
							// �Y�ꂸ��
							count++;
						}
						// �ȉ����[�v�O
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
	 * ����o���̃W�F�X�`�����s���܂��B
	 * 
	 * @param level
	 *            ����o���̓x�����B0-100�̐����ł��B
	 * @param speed
	 *            ����o�����s�����x�B1-5�̐����ł��B
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
	 * �������݂̃W�F�X�`�����s���܂��B
	 * 
	 * @param level
	 *            �������݂̓x�����B0-100�̐����ł��B
	 * @param speed
	 *            �������݂��s�����x�B0-100�̐����ł��B
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
	 * �����V�̃W�F�X�`�����s���܂��B
	 * 
	 * @param level
	 *            �����V�̐[���B0-125�̐����ł��B
	 * @param speed
	 *            �������݂��s�����x�B0-100�̐����ł��B
	 * @param holdTime
	 *            �����V���ێ����鎞��(ms)�B
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
	 * ��ʂ��V�F�C�N���܂��B
	 * 
	 * @param level
	 *            �V�F�C�N�̐[����0-100�̐[���Ŏw�肵�܂��B
	 * @param speed
	 *            �����̃X�s�[�h���w�肵�܂��B
	 * @param repeat
	 *            �J��Ԃ��񐔂��w�肵�܂��B
	 * @param interval
	 *            �J��Ԃ��̊Ԃ̎��Ԃ��w�肵�܂�(ms)�B
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
	 * �������܂��B
	 * 
	 * @param speed
	 *            ��������X�s�[�h���w�肵�܂��B
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
	 * �T�[�{�̈ʒu���A�}�C�R�����ŉ��ߏo����`�ɕϊ�
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
