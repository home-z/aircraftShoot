package com.tarena.shoot;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;

//主程序类
public class ShootGame extends JPanel {
	public static final int WIDTH = 400; //窗口宽
	public static final int HEIGHT = 654; //窗口高
	
	public static BufferedImage background; //背景图
	public static BufferedImage start;      //启动图
	public static BufferedImage pause;      //暂停图
	public static BufferedImage gameover;   //游戏结束图
	public static BufferedImage airplane;   //敌机
	public static BufferedImage bee;        //小蜜蜂
	public static BufferedImage bullet;     //子弹
	public static BufferedImage hero0;      //英雄机0
	public static BufferedImage hero1;      //英雄机1
	
	public static final int START = 0;    //启动状态
	public static final int RUNNING = 1;  //运行状态
	public static final int PAUSE = 2;    //暂停状态
	public static final int GAME_OVER = 3;//游戏结束状态
	private int state = 0; //当前状态
	
	private Hero hero = new Hero();      //英雄机对象
	private Bullet[] bullets = {};       //子弹数组
	private FlyingObject[] flyings = {}; //敌人(敌机+小蜜蜂)数组
	
	private Timer timer; //定时器
	private int intervel = 10; //间隔时间(以毫秒为单位)
	
	static{ //初始化静态资源
		try{
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** 随机生成敌人(敌机+小蜜蜂)对象 */
	public static FlyingObject nextOne(){
		Random rand = new Random(); //随机数对象
		int type = rand.nextInt(20); //生成0到19之间的随机数
		if(type == 0){ //为0时返回小蜜蜂对象
			return new Bee();
		}else{ //为1到19时返回敌机对象
			return new Airplane();
		}
	}
	
	int flyEnteredIndex = 0; //敌人入场计数
	/** 敌人入场 */
	public void enterAction(){ //10毫秒走一次
		flyEnteredIndex++; //每10毫秒增1
		if(flyEnteredIndex%40==0){ //400(10*40)毫秒走一次
			FlyingObject obj = nextOne(); //获取敌人对象
			flyings = Arrays.copyOf(flyings,flyings.length+1); //扩大一个容量
			flyings[flyings.length-1] = obj; //将敌人对象添加到数组的最后一个元素位置
		}
	}
	
	/** 飞行物走一步 */
	public void stepAction(){ //10毫秒走一次
		hero.step(); //英雄机走一步
		for(int i=0;i<flyings.length;i++){ //遍历所有敌人
			flyings[i].step(); //敌人走一步
		}
		for(int i=0;i<bullets.length;i++){ //遍历所有子弹
			bullets[i].step(); //子弹走一步
		}
	}
	
	int shootIndex = 0; //射击计数
	/** 子弹入场 */
	public void shootAction(){ //10毫秒走一次
		shootIndex++; //每10毫秒增1
		if(shootIndex%30==0){ //300(10*30)毫秒走一次
			Bullet[] bs = hero.shoot(); //获取子弹对象
			bullets = Arrays.copyOf(bullets,bullets.length+bs.length); //扩容(bs有几个元素就扩大几个容量)
			System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length); //数组的追加
		}
	}
	
	/** 删除越界的飞行物 */
	public void outOfBoundsAction(){
		int index = 0; //1.不越界敌人个数  2.不越界敌人数组下标
		FlyingObject[] flyingLives = new FlyingObject[flyings.length]; //不越界敌人数组
		for(int i=0;i<flyings.length;i++){ //遍历所有敌人
			FlyingObject f = flyings[i]; //获取每一个敌人
			if(!f.outOfBounds()){ //不越界
				flyingLives[index] = f; //将不越界敌人添加到不越界敌人数组中
				index++; //1.不越界敌人个数增1  2.不越界敌人数组下标增1
			}
		}
		flyings = Arrays.copyOf(flyingLives, index); //将不越界敌人数组复制到flyings中，index即为flyings数组元素的个数
		
		index = 0; //1.不越界子弹个数   2.不越界子弹数组下标
		Bullet[] bulletLives = new Bullet[bullets.length]; //不越界子弹数组
		for(int i=0;i<bullets.length;i++){ //遍历所有子弹
			Bullet b = bullets[i]; //获取每一个子弹
			if(!b.outOfBounds()){ //不越界
				bulletLives[index] = b; //将不越界子弹添加到不越界子弹数组中
				index++; //1.不越界子弹个数增1 2.不越界子弹数组下标增1
			}
		}
		bullets = Arrays.copyOf(bulletLives, index); //将不越界子弹数组复制到bullets中，index即为bullets数组元素的个数
	}
	
	int score = 0; //得分
	/** 所有子弹与所有敌人撞 */
	public void bangAction(){
		for(int i=0;i<bullets.length;i++){ //遍历所有子弹
			bang(bullets[i]); //1个子弹与所有敌人撞
		}
	}
	/** 1个子弹与所有敌人撞 */
	public void bang(Bullet b){
		int index = -1; //被撞敌人索引
		for(int i=0;i<flyings.length;i++){ //遍历所有敌人
			if(flyings[i].shootBy(b)){ //判断是否撞上了
				index = i; //记录被撞敌人索引
				break; //该子弹不再与其余敌人撞
			}
		}
		if(index!=-1){ //撞上了
			FlyingObject one = flyings[index]; //获取被撞的敌人对象
			if(one instanceof Enemy){  //是敌人
				Enemy e = (Enemy)one;  //强转为敌人
				score += e.getScore(); //得分
			}
			if(one instanceof Award){  //是奖励
				Award a = (Award)one;  //强转为奖励
				int type = a.getType();//获取奖励类型
				switch(type){ //根据奖励类型做不同操作
				case Award.DOUBLE_FIRE:  //奖励为火力值
					hero.addDoubleFire();//英雄机增火力值
					break;
				case Award.LIFE:   //奖励为命
					hero.addLife();//英雄机增命
					break;
				}
			}
			
			//交换被撞的敌人与flyings中的最后一个元素
			FlyingObject t = flyings[index];
			flyings[index] = flyings[flyings.length-1];
			flyings[flyings.length-1] = t;
			//缩容--缩掉最后一个对象(即被撞的对象)
			flyings = Arrays.copyOf(flyings, flyings.length-1);
			
		}
	}
	
	/** 检测游戏是否结束 */
	public void checkGameOverAction(){
		if(isGameOver()){ //游戏结束时改变当前状态为游戏结束状态
			state = GAME_OVER;
		}
	}
	/** 判断游戏是否结束 true表示游戏结束 */
	public boolean isGameOver(){
		for(int i=0;i<flyings.length;i++){ //遍历所有敌人
			if(hero.hit(flyings[i])){ //判断英雄机是否与敌人撞上了
				hero.subtractLife();   //英雄机减命
				hero.setDoubleFire(0); //英雄机火力值清零
				
				//将被撞敌人与flyings中最后一个元素对象交换
				FlyingObject t = flyings[i];
				flyings[i] = flyings[flyings.length-1];
				flyings[flyings.length-1] = t;
				//缩容--缩掉最后一个元素(即:被撞的敌人对象)
				flyings = Arrays.copyOf(flyings,flyings.length-1);
			}
		}
		
		return hero.getLife()<=0; //英雄机的命小于等于0，即为游戏结束
	}
	
	/** 启动执行代码 */
	public void action(){
		//鼠标适配器对象---侦听器
		MouseAdapter l = new MouseAdapter(){
			/** 鼠标移动事件 */
			public void mouseMoved(MouseEvent e){
				if(state == RUNNING){ //运行状态时执行
					int x = e.getX(); //鼠标的x坐标
					int y = e.getY(); //鼠标的y坐标
					hero.moveTo(x, y); //英雄机随着鼠标移动
				}
			}
			/** 鼠标点击事件 */
			public void mouseClicked(MouseEvent e){
				switch(state){ //根据当前状态做不同操作
				case START: //启动状态时变为运行状态
					state = RUNNING;
					break;
				case GAME_OVER: //游戏结束状态时变为启动状态
					hero = new Hero(); //清理现场
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					score = 0;
					state = START;
					break;
				}
			}
			/** 鼠标移入事件 */
			public void mouseEntered(MouseEvent e){
				if(state == PAUSE){ //暂停状态时改为运行状态
					state = RUNNING;
				}
			}
			/** 鼠标移出事件 */
			public void mouseExited(MouseEvent e){
				if(state == RUNNING){ //运行状态时改为暂停状态
					state = PAUSE;
				}
			}
			
		};
		this.addMouseListener(l); //处理鼠标操作事件
		this.addMouseMotionListener(l); //处理鼠标滑动事件
		
		timer = new Timer(); //创建定时器对象
		timer.schedule(new TimerTask(){
			public void run(){ //定时干的那个事-每10毫秒走一次
				if(state == RUNNING){ //运行状态下执行
					enterAction(); //敌人入场
					stepAction();  //飞行物走一步
					shootAction(); //子弹入场
					outOfBoundsAction(); //删除越界的飞行物
					bangAction();  //子弹与敌人撞
					checkGameOverAction(); //检测游戏是否结束
				}
				repaint(); //重画，调用paint()方法
			}
		},intervel,intervel);
	}
	
	/** 重写paint() g:画笔*/
 	public void paint(Graphics g){
		g.drawImage(background,0,0,null); //画背景图
		paintHero(g); //画英雄机对象
		paintFlyingObjects(g); //画敌人对象s
		paintBullets(g); //画子弹对象s
		paintScore(g); //画分和画命
		paintState(g); //画状态
	}
 	/** 画状态 */
 	public void paintState(Graphics g){
 		switch(state){ //根据当前状态画不同的图
 		case START: //启动状态时画启动图
 			g.drawImage(start,0,0,null);
 			break;
 		case PAUSE: //暂停状态时画暂停图
 			g.drawImage(pause,0,0,null);
 			break;
 		case GAME_OVER: //游戏结束状态时画游戏结束图
 			g.drawImage(gameover,0,0,null);
 			break;
 		}
 	}
 	/** 画分和画命 */
 	public void paintScore(Graphics g){
 		g.setColor(new Color(0xFF0000)); //设置颜色--纯红
 		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24)); //设置字体样式(字体:SANS_SERIF,样式:加粗 字号:24)
 		g.drawString("SCORE: "+score,15,25); //画分
 		g.drawString("LIFE: "+hero.getLife(),15,50); //画命
 	}
	/** 画英雄机对象 */
	public void paintHero(Graphics g){
		g.drawImage(hero.image,hero.x,hero.y,null); //画英雄机对象
	}
	/** 画敌人对象s */
	public void paintFlyingObjects(Graphics g){
		for(int i=0;i<flyings.length;i++){ //遍历所有敌人(敌机+小蜜蜂)
			FlyingObject f = flyings[i]; //获取每一个敌人(敌机+小蜜蜂)
			g.drawImage(f.image,f.x,f.y,null); //画敌人对象(敌机+小蜜蜂)
		}
	}
	/** 画子弹对象s */
	public void paintBullets(Graphics g){
		for(int i=0;i<bullets.length;i++){ //遍历子弹数组
			Bullet b = bullets[i]; //获取每一个子弹
			g.drawImage(b.image,b.x,b.y,null); //画子弹对象
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Fly"); //窗口对象
		ShootGame game = new ShootGame(); //面板对象
		frame.add(game); //将面板添加到窗口中
		
		frame.setSize(WIDTH, HEIGHT); //设置窗口大小
		frame.setAlwaysOnTop(true); //设置一直在最上面
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置默认关闭操作(窗口关闭时退出程序)
		frame.setLocationRelativeTo(null); //设置窗口初始位置(居中)
		frame.setVisible(true); //1.设置窗口可见  2.尽快调用paint()
		
		game.action(); //启动执行
	}
}











