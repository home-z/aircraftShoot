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

//��������
public class ShootGame extends JPanel {
	public static final int WIDTH = 400; //���ڿ�
	public static final int HEIGHT = 654; //���ڸ�
	
	public static BufferedImage background; //����ͼ
	public static BufferedImage start;      //����ͼ
	public static BufferedImage pause;      //��ͣͼ
	public static BufferedImage gameover;   //��Ϸ����ͼ
	public static BufferedImage airplane;   //�л�
	public static BufferedImage bee;        //С�۷�
	public static BufferedImage bullet;     //�ӵ�
	public static BufferedImage hero0;      //Ӣ�ۻ�0
	public static BufferedImage hero1;      //Ӣ�ۻ�1
	
	public static final int START = 0;    //����״̬
	public static final int RUNNING = 1;  //����״̬
	public static final int PAUSE = 2;    //��ͣ״̬
	public static final int GAME_OVER = 3;//��Ϸ����״̬
	private int state = 0; //��ǰ״̬
	
	private Hero hero = new Hero();      //Ӣ�ۻ�����
	private Bullet[] bullets = {};       //�ӵ�����
	private FlyingObject[] flyings = {}; //����(�л�+С�۷�)����
	
	private Timer timer; //��ʱ��
	private int intervel = 10; //���ʱ��(�Ժ���Ϊ��λ)
	
	static{ //��ʼ����̬��Դ
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
	
	/** ������ɵ���(�л�+С�۷�)���� */
	public static FlyingObject nextOne(){
		Random rand = new Random(); //���������
		int type = rand.nextInt(20); //����0��19֮��������
		if(type == 0){ //Ϊ0ʱ����С�۷����
			return new Bee();
		}else{ //Ϊ1��19ʱ���صл�����
			return new Airplane();
		}
	}
	
	int flyEnteredIndex = 0; //�����볡����
	/** �����볡 */
	public void enterAction(){ //10������һ��
		flyEnteredIndex++; //ÿ10������1
		if(flyEnteredIndex%40==0){ //400(10*40)������һ��
			FlyingObject obj = nextOne(); //��ȡ���˶���
			flyings = Arrays.copyOf(flyings,flyings.length+1); //����һ������
			flyings[flyings.length-1] = obj; //�����˶�����ӵ���������һ��Ԫ��λ��
		}
	}
	
	/** ��������һ�� */
	public void stepAction(){ //10������һ��
		hero.step(); //Ӣ�ۻ���һ��
		for(int i=0;i<flyings.length;i++){ //�������е���
			flyings[i].step(); //������һ��
		}
		for(int i=0;i<bullets.length;i++){ //���������ӵ�
			bullets[i].step(); //�ӵ���һ��
		}
	}
	
	int shootIndex = 0; //�������
	/** �ӵ��볡 */
	public void shootAction(){ //10������һ��
		shootIndex++; //ÿ10������1
		if(shootIndex%30==0){ //300(10*30)������һ��
			Bullet[] bs = hero.shoot(); //��ȡ�ӵ�����
			bullets = Arrays.copyOf(bullets,bullets.length+bs.length); //����(bs�м���Ԫ�ؾ����󼸸�����)
			System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length); //�����׷��
		}
	}
	
	/** ɾ��Խ��ķ����� */
	public void outOfBoundsAction(){
		int index = 0; //1.��Խ����˸���  2.��Խ����������±�
		FlyingObject[] flyingLives = new FlyingObject[flyings.length]; //��Խ���������
		for(int i=0;i<flyings.length;i++){ //�������е���
			FlyingObject f = flyings[i]; //��ȡÿһ������
			if(!f.outOfBounds()){ //��Խ��
				flyingLives[index] = f; //����Խ�������ӵ���Խ�����������
				index++; //1.��Խ����˸�����1  2.��Խ����������±���1
			}
		}
		flyings = Arrays.copyOf(flyingLives, index); //����Խ��������鸴�Ƶ�flyings�У�index��Ϊflyings����Ԫ�صĸ���
		
		index = 0; //1.��Խ���ӵ�����   2.��Խ���ӵ������±�
		Bullet[] bulletLives = new Bullet[bullets.length]; //��Խ���ӵ�����
		for(int i=0;i<bullets.length;i++){ //���������ӵ�
			Bullet b = bullets[i]; //��ȡÿһ���ӵ�
			if(!b.outOfBounds()){ //��Խ��
				bulletLives[index] = b; //����Խ���ӵ���ӵ���Խ���ӵ�������
				index++; //1.��Խ���ӵ�������1 2.��Խ���ӵ������±���1
			}
		}
		bullets = Arrays.copyOf(bulletLives, index); //����Խ���ӵ����鸴�Ƶ�bullets�У�index��Ϊbullets����Ԫ�صĸ���
	}
	
	int score = 0; //�÷�
	/** �����ӵ������е���ײ */
	public void bangAction(){
		for(int i=0;i<bullets.length;i++){ //���������ӵ�
			bang(bullets[i]); //1���ӵ������е���ײ
		}
	}
	/** 1���ӵ������е���ײ */
	public void bang(Bullet b){
		int index = -1; //��ײ��������
		for(int i=0;i<flyings.length;i++){ //�������е���
			if(flyings[i].shootBy(b)){ //�ж��Ƿ�ײ����
				index = i; //��¼��ײ��������
				break; //���ӵ��������������ײ
			}
		}
		if(index!=-1){ //ײ����
			FlyingObject one = flyings[index]; //��ȡ��ײ�ĵ��˶���
			if(one instanceof Enemy){  //�ǵ���
				Enemy e = (Enemy)one;  //ǿתΪ����
				score += e.getScore(); //�÷�
			}
			if(one instanceof Award){  //�ǽ���
				Award a = (Award)one;  //ǿתΪ����
				int type = a.getType();//��ȡ��������
				switch(type){ //���ݽ�����������ͬ����
				case Award.DOUBLE_FIRE:  //����Ϊ����ֵ
					hero.addDoubleFire();//Ӣ�ۻ�������ֵ
					break;
				case Award.LIFE:   //����Ϊ��
					hero.addLife();//Ӣ�ۻ�����
					break;
				}
			}
			
			//������ײ�ĵ�����flyings�е����һ��Ԫ��
			FlyingObject t = flyings[index];
			flyings[index] = flyings[flyings.length-1];
			flyings[flyings.length-1] = t;
			//����--�������һ������(����ײ�Ķ���)
			flyings = Arrays.copyOf(flyings, flyings.length-1);
			
		}
	}
	
	/** �����Ϸ�Ƿ���� */
	public void checkGameOverAction(){
		if(isGameOver()){ //��Ϸ����ʱ�ı䵱ǰ״̬Ϊ��Ϸ����״̬
			state = GAME_OVER;
		}
	}
	/** �ж���Ϸ�Ƿ���� true��ʾ��Ϸ���� */
	public boolean isGameOver(){
		for(int i=0;i<flyings.length;i++){ //�������е���
			if(hero.hit(flyings[i])){ //�ж�Ӣ�ۻ��Ƿ������ײ����
				hero.subtractLife();   //Ӣ�ۻ�����
				hero.setDoubleFire(0); //Ӣ�ۻ�����ֵ����
				
				//����ײ������flyings�����һ��Ԫ�ض��󽻻�
				FlyingObject t = flyings[i];
				flyings[i] = flyings[flyings.length-1];
				flyings[flyings.length-1] = t;
				//����--�������һ��Ԫ��(��:��ײ�ĵ��˶���)
				flyings = Arrays.copyOf(flyings,flyings.length-1);
			}
		}
		
		return hero.getLife()<=0; //Ӣ�ۻ�����С�ڵ���0����Ϊ��Ϸ����
	}
	
	/** ����ִ�д��� */
	public void action(){
		//�������������---������
		MouseAdapter l = new MouseAdapter(){
			/** ����ƶ��¼� */
			public void mouseMoved(MouseEvent e){
				if(state == RUNNING){ //����״̬ʱִ��
					int x = e.getX(); //����x����
					int y = e.getY(); //����y����
					hero.moveTo(x, y); //Ӣ�ۻ���������ƶ�
				}
			}
			/** ������¼� */
			public void mouseClicked(MouseEvent e){
				switch(state){ //���ݵ�ǰ״̬����ͬ����
				case START: //����״̬ʱ��Ϊ����״̬
					state = RUNNING;
					break;
				case GAME_OVER: //��Ϸ����״̬ʱ��Ϊ����״̬
					hero = new Hero(); //�����ֳ�
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					score = 0;
					state = START;
					break;
				}
			}
			/** ��������¼� */
			public void mouseEntered(MouseEvent e){
				if(state == PAUSE){ //��ͣ״̬ʱ��Ϊ����״̬
					state = RUNNING;
				}
			}
			/** ����Ƴ��¼� */
			public void mouseExited(MouseEvent e){
				if(state == RUNNING){ //����״̬ʱ��Ϊ��ͣ״̬
					state = PAUSE;
				}
			}
			
		};
		this.addMouseListener(l); //�����������¼�
		this.addMouseMotionListener(l); //������껬���¼�
		
		timer = new Timer(); //������ʱ������
		timer.schedule(new TimerTask(){
			public void run(){ //��ʱ�ɵ��Ǹ���-ÿ10������һ��
				if(state == RUNNING){ //����״̬��ִ��
					enterAction(); //�����볡
					stepAction();  //��������һ��
					shootAction(); //�ӵ��볡
					outOfBoundsAction(); //ɾ��Խ��ķ�����
					bangAction();  //�ӵ������ײ
					checkGameOverAction(); //�����Ϸ�Ƿ����
				}
				repaint(); //�ػ�������paint()����
			}
		},intervel,intervel);
	}
	
	/** ��дpaint() g:����*/
 	public void paint(Graphics g){
		g.drawImage(background,0,0,null); //������ͼ
		paintHero(g); //��Ӣ�ۻ�����
		paintFlyingObjects(g); //�����˶���s
		paintBullets(g); //���ӵ�����s
		paintScore(g); //���ֺͻ���
		paintState(g); //��״̬
	}
 	/** ��״̬ */
 	public void paintState(Graphics g){
 		switch(state){ //���ݵ�ǰ״̬����ͬ��ͼ
 		case START: //����״̬ʱ������ͼ
 			g.drawImage(start,0,0,null);
 			break;
 		case PAUSE: //��ͣ״̬ʱ����ͣͼ
 			g.drawImage(pause,0,0,null);
 			break;
 		case GAME_OVER: //��Ϸ����״̬ʱ����Ϸ����ͼ
 			g.drawImage(gameover,0,0,null);
 			break;
 		}
 	}
 	/** ���ֺͻ��� */
 	public void paintScore(Graphics g){
 		g.setColor(new Color(0xFF0000)); //������ɫ--����
 		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,24)); //����������ʽ(����:SANS_SERIF,��ʽ:�Ӵ� �ֺ�:24)
 		g.drawString("SCORE: "+score,15,25); //����
 		g.drawString("LIFE: "+hero.getLife(),15,50); //����
 	}
	/** ��Ӣ�ۻ����� */
	public void paintHero(Graphics g){
		g.drawImage(hero.image,hero.x,hero.y,null); //��Ӣ�ۻ�����
	}
	/** �����˶���s */
	public void paintFlyingObjects(Graphics g){
		for(int i=0;i<flyings.length;i++){ //�������е���(�л�+С�۷�)
			FlyingObject f = flyings[i]; //��ȡÿһ������(�л�+С�۷�)
			g.drawImage(f.image,f.x,f.y,null); //�����˶���(�л�+С�۷�)
		}
	}
	/** ���ӵ�����s */
	public void paintBullets(Graphics g){
		for(int i=0;i<bullets.length;i++){ //�����ӵ�����
			Bullet b = bullets[i]; //��ȡÿһ���ӵ�
			g.drawImage(b.image,b.x,b.y,null); //���ӵ�����
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Fly"); //���ڶ���
		ShootGame game = new ShootGame(); //������
		frame.add(game); //�������ӵ�������
		
		frame.setSize(WIDTH, HEIGHT); //���ô��ڴ�С
		frame.setAlwaysOnTop(true); //����һֱ��������
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //����Ĭ�Ϲرղ���(���ڹر�ʱ�˳�����)
		frame.setLocationRelativeTo(null); //���ô��ڳ�ʼλ��(����)
		frame.setVisible(true); //1.���ô��ڿɼ�  2.�������paint()
		
		game.action(); //����ִ��
	}
}











