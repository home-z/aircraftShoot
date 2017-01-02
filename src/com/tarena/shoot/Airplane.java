package com.tarena.shoot;
import java.util.Random;
/** �л�: �Ƿ����Ҳ�ǵ��� */
public class Airplane extends FlyingObject implements Enemy {
	private int speed = 2; //�л��߲��Ĳ���
	/** ���췽�� */
	public Airplane(){
		image = ShootGame.airplane; //ͼƬ
		width = image.getWidth();   //��
		height = image.getHeight(); //��
		Random rand = new Random(); //���������
		x = rand.nextInt(ShootGame.WIDTH-this.width); //x:0����Ļ��-�л���֮��������
		y = -this.height; //y:���ĵл��ĸ�
	}
	
	/** ��дgetScore() */
	public int getScore(){
		return 5; //���һ�����˵�5��
	}

	/** ��дstep() */
	public void step(){
		y += speed; //y+(����)
	}

	/** ��дoutOfBounds() */
	public boolean outOfBounds(){
		return this.y>ShootGame.HEIGHT; //�л���y������Ļ�ĸ�ΪԽ��
	}
	
}





