package com.tarena.shoot;
import java.util.Random;
/** С�۷�: �Ƿ����Ҳ�ǽ��� */
public class Bee extends FlyingObject implements Award{
	private int xSpeed = 1; //x�����߲�����
	private int ySpeed = 2; //y�����߲�����
	private int awardType;  //����������
	/** ���췽�� */
	public Bee(){
		image = ShootGame.bee; //ͼƬ
		width = image.getWidth();   //��
		height = image.getHeight(); //��
		Random rand = new Random(); //���������
		x = rand.nextInt(ShootGame.WIDTH-this.width); //x:0����Ļ��-�۷��֮��������
		y = -this.height; //y:�����۷�ĸ�
		awardType = rand.nextInt(2); //������ɽ������� 0�������ֵ  1������
	}
	
	/** ��дgetType() */
	public int getType(){
		return awardType;
	}
	
	/** ��дstep() */
	public void step(){
		if(x>=ShootGame.WIDTH-this.width){
			xSpeed = -1; //���ұ�ʱ��(����)
		}
		if(x<=0){
			xSpeed = 1; //�����ʱ��(����)
		}
		x += xSpeed; //x��(��ӻ��)
		y += ySpeed; //y��(����)
	}

	/** ��дoutOfBounds() */
	public boolean outOfBounds(){
		return this.y>ShootGame.HEIGHT; //�۷��y������Ļ�ĸ�ΪԽ��
	}
}















