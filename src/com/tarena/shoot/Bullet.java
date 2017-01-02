package com.tarena.shoot;
/** 子弹: 是飞行物 */
public class Bullet extends FlyingObject {
	private int speed = 3; //子弹走步的步数
	/** 构造方法  x/y参数是因为子弹的坐标随机英雄机动 */
	public Bullet(int x,int y){
		image = ShootGame.bullet; //图片
		width = image.getWidth(); //宽
		height = image.getHeight(); //高
		this.x = x; //x坐标:随着英雄机动
		this.y = y; //y坐标:随着英雄机动
	}
	
	/** 重写step() */
	public void step(){
		y -= speed; //y减(向上)
	}

	/** 重写outOfBounds() */
	public boolean outOfBounds(){
		return this.y<-this.height; //子弹的y小于负的子弹的高，即为越界
	}
}
