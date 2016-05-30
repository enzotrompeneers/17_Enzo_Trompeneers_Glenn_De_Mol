package hellotvxlet;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;
import org.dvb.event.UserEvent;
import org.havi.ui.*;
import javax.tv.xlet.*;
import org.bluray.ui.event.HRcEvent;
import org.dvb.event.EventManager;
import org.dvb.event.UserEventListener;
import org.dvb.event.UserEventRepository;
import org.havi.ui.HComponent;
import org.havi.ui.HScene;
import org.havi.ui.HSceneFactory;
import org.dvb.ui.DVBColor;
import org.havi.ui.event.HActionListener;

public class HelloTVXlet extends HComponent implements Xlet, UserEventListener, HActionListener{
	private XletContext context;
	private HScene scene;
	private Image img1, img2, img3, img4, img5, img6, img7, img8, img9;
        private Image[] aImages = {img1, img2, img3, img4, img5, img6, img7, img8, img9};
        private String[] aNaamImages = {"1.jpeg", "2.jpeg", "3.jpeg", "4.jpeg", "5.jpeg", "6.jpeg", "7.jpeg", "8.jpeg", "9.jpeg"};
        private boolean isComplete = false;
        private int checkControle = 0;
        private int [][] positieImages = new int[3][3];
        private int rows = 3;
        public HTextButton ShuffleButton;
        public int counter = 0;
        Singleton obj = Singleton.getInstance(); //Get the only object available
        
	public void initXlet(XletContext context) throws XletStateChangeException {
            OriginalState();
            this.context = context;
            HSceneFactory factory = HSceneFactory.getInstance();
            scene = factory.getBestScene(null);
            Rectangle rect = scene.getBounds();
            setBounds(rect);
            setVisible(true);
            scene.setBackground(new DVBColor(255,255,255,255));
            scene.setBackgroundMode(HVisible.BACKGROUND_FILL);
            try {
                MediaTracker tracker = new MediaTracker(this);
                for (int i=0;i<9;i++)
                {
                    aImages[i] = this.getToolkit().getImage(aNaamImages[i]);
                    tracker.addImage(aImages[i], 0);
                }
                tracker.waitForAll();
            }
            catch (InterruptedException e) {
            }
            scene.setVisible(false);
            scene.add(this);
	}
	public void startXlet() throws XletStateChangeException {
            HTextButton shuffleButton = new HTextButton("Shuffle PRESS 1", 130,530, 250, 40 );
            shuffleButton.setBackground(new DVBColor(255, 50, 0, 255));
            shuffleButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
            shuffleButton.addHActionListener(this);

            HTextButton originalButton = new HTextButton("Original PRESS 2", 400 ,530, 250, 40 );
            originalButton.setBackground(new DVBColor(255, 50, 0, 255));
            originalButton.setBackgroundMode(HVisible.BACKGROUND_FILL);
            originalButton.addHActionListener(this);


            scene.add(shuffleButton);
            scene.add(originalButton);
            scene.setVisible(true);
            requestFocus();

            // database van alle key arrows
            UserEventRepository userEvents = new UserEventRepository("rep1");
            userEvents.addAllArrowKeys();
            userEvents.addAllNumericKeys();
            EventManager.getInstance().addUserEventListener(this, userEvents);
              
	}
	public void pauseXlet() {
		scene.setVisible(false);
	}
	public void destroyXlet(boolean unconditional) {
	}
	public void ShuffleImages()
        {
            int amountOfShuffling = 30;
            Random rnd = new Random();
            
            for (int i=1;i<amountOfShuffling;i++)
            {
                if (rnd.nextBoolean())
                {//x
                    if (rnd.nextBoolean()) SlideImage(-1,0, false, false); else SlideImage(1,0, false, false);
                }
                else
                {//y
                    if (rnd.nextBoolean()) SlideImage(0,-1, false, false); else SlideImage(0,1, false, false);
                }
            }
        }
        public void paint(Graphics graphics) 
        {
                int xPos = 0;
                int yPos = 50;
                int moveImage = 158;
                
                for (int i=0;i<rows;i++)
                {
                    xPos = 130;
                    for (int j=0; j<rows;j++)
                    {
                        int Nbr= positieImages[j][i];
                        graphics.drawImage(aImages[Nbr-1], xPos, yPos, null);
                        xPos+= moveImage;
                        
                    }
                    yPos += moveImage;
                }
	}
        public boolean ControlImages()
        {
            checkControle = 0;
            int isTrue = 0;
            for (int i=0;i<rows;i++)
            {
                for (int j=0;j<rows;j++)
                {
                    checkControle++;
                    if (positieImages[j][i] == checkControle)
                    {
                        isTrue++;
                    }
                }
            }
            if (isTrue==9) // als alle afbeeldingen juist staan
            {
                isComplete = true;
            }
               return isComplete;
        }
        public void SlideImage(int x, int y, boolean dontShuffle, boolean isComplete) 
        {
                // lokaliseer i (witte afbeelding)
                int coordX = 0;
                int coordY = 0;
                for (int i=0;i<positieImages[0].length;i++)
                {
                    for (int j=0;j<positieImages[1].length;j++)
                    {
                        if (positieImages[i][j] == 9)
                        {
                            coordX = i;
                            coordY = j;
                            break;
                        }
                    }
                }

                if ((coordX+x>2)|| (coordX+x<0)) return;
                if ((coordY+y>2)|| (coordY+y<0)) return;


                positieImages[coordX][coordY]=positieImages[coordX+x][coordY+y];
                positieImages[coordX+x][coordY+y]=9;

                this.repaint();
                if (dontShuffle)
                {
                    if (ControlImages())
                    {
                        HTextButton solved = new HTextButton("Puzzle Solved", 130 ,250, 475, 50 );
                        solved.setBackground(new DVBColor(0, 200, 50, 255));
                        solved.setBackgroundMode(HVisible.BACKGROUND_FILL);
                        solved.addHActionListener(this);


                        scene.add(solved);
                        scene.popToFront(solved);
                        scene.repaint();

                        obj.MessageWin();
                        isComplete = true;


                        //System.exit(y);

                    }
                return;
                }
        }
        public void OriginalState()
        {
            checkControle = 0;
            for (int i=0;i<rows;i++)
            {
                for (int j=0;j<rows;j++)
                {
                    checkControle++;
                    positieImages[j][i] = checkControle;
                }
            }
        }
	public void userEventReceived(UserEvent e)
        {
            if (!isComplete)
            {
                if (e.getType()==HRcEvent.KEY_PRESSED)
                {
                    counter++;
                    System.out.println(counter);
                    switch (e.getCode())
                    {
                        case HRcEvent.VK_UP:
                            obj.MessageUp();
                            SlideImage(0,1, true, false);
                            break;
                        case HRcEvent.VK_DOWN:
                            obj.MessageDown();
                            SlideImage(0,-1, true, false);
                            break;
                        case HRcEvent.VK_LEFT:
                            obj.MessageLeft();
                            SlideImage(1,0, true, false);
                            break;
                        case HRcEvent.VK_RIGHT:
                            obj.MessageRight();
                            SlideImage(-1,0, true, false);
                            break;
                        case HRcEvent.VK_1:
                            ShuffleImages();
                            break;
                        case HRcEvent.VK_2:
                            OriginalState();
                            scene.repaint();
                            break;
                    }
                } 
            }
        }
    public void actionPerformed(ActionEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}