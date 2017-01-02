package com.tarena.shoot;
import java.util.Random;
/** 小蜜蜂: 是飞行物，也是奖励 */
public class Bee extends FlyingObject implements Award{
	private int xSpeed = 1; //x坐标走步步数
	private int ySpeed = 2; //y坐标走步步数
	private int awardType;  //奖励的类型
	/** 构造方法 */
	public Bee(){
		image = ShootGame.bee; //图片
		width = image.getWidth();   //宽
		height = image.getHeight(); //高
		Random rand = new Random(); //随机数对象
		x = rand.nextInt(ShootGame.WIDTH-this.width); //x:0到屏幕宽-蜜蜂宽之间的随机数
		y = -this.height; //y:负的蜜蜂的高
		awardType = rand.nextInt(2); //随机生成奖励类型 0代表火力值  1代表命
	}
	
	/** 重写getType() */
	public int getType(){
		return awardType;
	}
	
	/** 重写step() */
	public void step(){
		if(x>=ShootGame.WIDTH-this.width){
			xSpeed = -1; //最右边时减(向左)
		}
		if(x<=0){
			xSpeed = 1; //最左边时加(加右)
		}
		x += xSpeed; //x变(或加或减)
		y += ySpeed; //y加(向下)
	}

	/** 重写outOfBounds() */
	public boolean outOfBounds(){
		return this.y>ShootGame.HEIGHT; //蜜蜂的y大于屏幕的高为越界
	}
}















