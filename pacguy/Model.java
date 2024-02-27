package pacguy;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener {

	private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;
    private boolean startGame = true;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 6;
    private int timerSeconds = 10;

    private int N_GHOSTS = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image heart, ghost, yellow_ghost,blue_ghost,cyan_ghost,pink_ghost,purple_ghost,orange_ghost;
    private Image up, down, left, right;
    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;
    LocalStorage localStorage = new LocalStorage("data.properties");
    

    private final short levelData[] = {
        	19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
        };


    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;
    private Timer countdownTimer;;
    
    public Model() {
    	
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame("normal");
        
    }
    
    
    private void loadImages() {
    	down = new ImageIcon("images/down.gif").getImage();
    	up = new ImageIcon("images/up.gif").getImage();
    	left = new ImageIcon("images/left.gif").getImage();
    	right = new ImageIcon("images/right.gif").getImage();
        ghost = new ImageIcon("images/ghost.gif").getImage();
        heart = new ImageIcon("images/heart.png").getImage();
        yellow_ghost = new ImageIcon("images/yellow.gif").getImage();
        blue_ghost = new ImageIcon("images/blue.gif").getImage();
        cyan_ghost = new ImageIcon("images/cyan.gif").getImage();
        purple_ghost = new ImageIcon("images/purple.gif").getImage();
        pink_ghost = new ImageIcon("images/pink.gif").getImage();
        orange_ghost = new ImageIcon("images/orange.gif").getImage();
        
    }
       private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, this);
        timer.start();
    }

    private void playGame(Graphics2D g2d) {

        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {
       
        String difficulty = "Select Difficulty ";
        String easy = "1. Easy ";
        String medium = "2. Medium ";
        String hard = "3. Hard";
        
        g2d.setColor(Color.yellow);
        g2d.drawString(difficulty, (SCREEN_SIZE)/3, 174);
        g2d.drawString(easy, (SCREEN_SIZE)/3, 196);
        g2d.drawString(medium, (SCREEN_SIZE)/3, 220);
        g2d.drawString(hard, (SCREEN_SIZE)/3, 244);
        
        String start = "WELCOME TO PACGUY";
   
        g2d.setColor(Color.yellow);
        Font font = new Font("Arial", Font.BOLD, 23);
        g2d.setFont(font);
        g2d.drawString(start, (SCREEN_SIZE) / 7, 130);
    }

    
    private void showGameOverScreen(Graphics2D g2d) {
    	
    	String startAgain = "Highest Score: " + localStorage.loadData("Score");
        g2d.setColor(Color.yellow);
        g2d.drawString(startAgain, (SCREEN_SIZE)/3, 270);
    	 
    	String start = "Game Over";
    	
        g2d.setColor(Color.red);
        Font font = new Font("Arial", Font.BOLD, 36);
        g2d.setFont(font);
        g2d.drawString(start, (SCREEN_SIZE)/4, 170);
        
    }


    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);
        
        String t = "Time: " + timerSeconds;
        g.drawString(t, SCREEN_SIZE / 2 , SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    private void checkMaze() {

        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {
    	startGame=false;
    	lives--;

        if (lives == 0) {
            inGame = false;
            int previousScore = localStorage.loadData("Score");
            
            if (score > previousScore) {
                localStorage.saveData("Score", score);
                System.out.println("New high score saved: " + score);
            } else {
                System.out.println("No new high score.");
            }
            
        }

        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;
        String[] ghost_arr = {"yellow","blue","cyan","purple","orange","pink"};
        

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1,ghost_arr[i]);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y,String ghost_color) {
    	
    	if (ghost_color == "yellow") {
    		g2d.drawImage(yellow_ghost, x, y, this);
    	}
    	else if (ghost_color == "blue") {
    		g2d.drawImage(blue_ghost, x, y, this);
        }
    	else if (ghost_color == "purple") {
    		g2d.drawImage(purple_ghost, x, y, this);
        }
    	else if (ghost_color == "orange") {
    		g2d.drawImage(orange_ghost, x, y, this);
        }
    	else if (ghost_color == "pink") {
    		g2d.drawImage(pink_ghost, x, y, this);
        }
    	else {
    		g2d.drawImage(cyan_ghost, x, y, this);
        }
    	
    }
    
    private void movePacman() {

        int pos;
        short ch;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        } 
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {

        if (req_dx == -1) {
        	g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
        	g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
        	g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
        	g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));
                
                if ((levelData[i] == 0)) { 
                	g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                 }

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
               }

                i++;
            }
        }
    }

    private void initGame(String difficulty) {
        lives = 3;
        score = 0;

        switch (difficulty) {
            case "easy":
                level(4, 2, 30);
                break;
            case "medium":
                level(5, 3, 20);
                break;
            case "hard":
                level(6, 3, 10);
                break;
            default:
            	level(6,3,10);
                break;
        }
    }

    
    private void level(Integer ghost, Integer speed, Integer timer) {
    	initLevel();
        N_GHOSTS = ghost;
        currentSpeed = speed;
        timerSeconds = timer;
        
    }

    private void initLevel() {
    	
        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {

    	int dx = 1;
        int random;

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE; //start position
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;  //start position
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;	//reset direction move
        pacmand_y = 0;
        req_dx = 0;		
        req_dy = 0;
        dying = false;
    }

 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
            
        } else if (startGame){
            showIntroScreen(g2d);
        }else {
        	
        	showGameOverScreen(g2d);
        	countdownTimer.stop();
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    //controls
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                	inGame = false;
                }
            } else {
//                if (key == KeyEvent.VK_SPACE) {
//                	countdownTimer = new Timer(1000, new CountdownTimerListener());
//                    countdownTimer.start();
//                    inGame = true;
//                    initGame("normal");
//                   
//                }
                if (key == KeyEvent.VK_1) {
             
                    countdownTimer = new Timer(1000, new CountdownTimerListener());
                    countdownTimer.start();
                    inGame = true;
                    initGame("easy");
                    
                } else if (key == KeyEvent.VK_2) {
                    
                    countdownTimer = new Timer(1000, new CountdownTimerListener());
                    countdownTimer.start();
                    inGame = true;
                    initGame("medium");
                   
                } else if (key == KeyEvent.VK_3) {
                   
                    countdownTimer = new Timer(1000, new CountdownTimerListener());
                    countdownTimer.start();
                    inGame = true;
                    initGame("hard");
                   
                }
            }
        }
    }

	
    @Override
    public void actionPerformed(ActionEvent e) {
    	repaint();
    }
    
    private class CountdownTimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            timerSeconds--; // Decrement timer
            if (timerSeconds <= 0) {
            	startGame = false;
                inGame = false;
                
               
                int previousScore = localStorage.loadData("Score");
         
                if (score > previousScore) {
                    localStorage.saveData("Score", score);
                    System.out.println("New high score saved: " + score);
                } else {
                    System.out.println("No new high score.");
                }
                
                countdownTimer.stop();
            }
        }
    }
		
	}