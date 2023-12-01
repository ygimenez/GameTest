package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Boss;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.decoration.Wind;
import com.kuuhaku.entities.enemies.Invader;
import com.kuuhaku.enums.SoundType;
import com.kuuhaku.interfaces.*;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.ui.Button;
import com.kuuhaku.utils.Interp;
import com.kuuhaku.utils.Utils;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class GameRuntime extends KeyAdapter implements IMenu {
	private final float UPS = 200;
	public final boolean DEBUG = false;

	private final Set<Enemy> enemies = new HashSet<>();
	private final Set<Entity> entities = new HashSet<>();
	private final Set<Entity> queue = new HashSet<>();
	private final List<Player> players = new ArrayList<>();
	private final boolean[] keyState = new boolean[256];
	private final Semaphore lock = new Semaphore(1);
	private final Renderer renderer;
	private final Class<? extends Enemy> training;

	private final Semaphore spawnLimit = new Semaphore(10);
	private final Interp barFill = new Interp(this, 0, 1000, 250, 0);
	private final Interp warnTrans = new Interp(this, 0, 150, 100, 3);

	private int score;
	private int entCount, partCount;
	private boolean paused, gameover, closed;
	private long tick, lastSimu, lastSpawn, lastCull;
	private Button back;
	private Clip theme, bossTheme;
	private Boss boss;

	private Color background = Color.BLACK;
	private Color foreground = Color.WHITE;

	private static int highscore;

	public GameRuntime(Renderer renderer) {
		this.renderer = renderer;
		this.training = null;
	}

	public GameRuntime(Renderer renderer, Class<? extends Enemy> training) {
		this.renderer = renderer;
		this.training = training;
		if (training != null) {
			tick = 100000;
		}

		players.add(new Player(this));
		for (Class<?> klass : Utils.getAnnotatedClasses(Managed.class, "com.kuuhaku.entities.enemies")) {
			try {
				Enemy e = (Enemy) klass.getConstructor(GameRuntime.class).newInstance(this);
				enemies.add(e);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
					 NoSuchMethodException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void switchTo(IMenu from) {
		renderer.addKeyListener(this);
		spawn(players);

		float simrate = 1000f / UPS;
		Thread simulation = new Thread(() -> {
			while (!closed) {
				process();
				lastSimu = System.currentTimeMillis();

				Utils.sleep((long) simrate, (int) (1_000_000 * (simrate - (long) simrate)));
			}
		});
		simulation.setDaemon(true);
		simulation.start();

		theme = AssetManager.getAudio("theme");
		if (theme != null) {
			FloatControl gain = (FloatControl) theme.getControl(FloatControl.Type.MASTER_GAIN);
			gain.setValue(Utils.toDecibels(SoundType.MUSIC, 0.25f));
			theme.loop(Clip.LOOP_CONTINUOUSLY);
		}

		bossTheme = AssetManager.getAudio("boss_theme");
		if (bossTheme != null) {
			FloatControl gain = (FloatControl) bossTheme.getControl(FloatControl.Type.MASTER_GAIN);
			gain.setValue(Utils.toDecibels(0));
			bossTheme.loop(Clip.LOOP_CONTINUOUSLY);
		}

		back = new Button(renderer)
				.setSize(200, 50)
				.setValue("MAIN MENU")
				.setDisabled(true);

		back.addListener(e -> {
			from.switchTo(null);
			back.dispose();
			closed = true;

			try {
				simulation.join();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}

			entities.clear();

			if (theme != null) {
				theme.close();
			}

			if (bossTheme != null) {
				bossTheme.close();
			}
		});

		renderer.render(this::render);
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public Rectangle getBounds() {
		Rectangle r = new Rectangle(getSafeArea());
		r.grow(150, 150);
		r.translate(0, -25);

		return r;
	}

	public Rectangle getSafeArea() {
		return new Rectangle(renderer.getResolution().getBounds().width, renderer.getHeight());
	}

	public synchronized Set<Entity> getEntities() {
		if (lastCull != tick) {
			entCount = partCount = 0;

			Iterator<Entity> it = entities.iterator();
			while (it.hasNext()) {
				Entity entity = it.next();
				if (entity.toBeRemoved()) {
					if (entity instanceof IDamageable d && d.getHp() <= 0) {
						entity.onDestroy();
					}

					entity.dispose();
					it.remove();
				}

				if (entity instanceof IParticle) {
					partCount++;
				} else {
					entCount++;
				}
			}

			entities.addAll(queue);
			queue.clear();

			lastCull = tick;
		}

		return entities;
	}

	public synchronized void spawn(Entity... entities) {
		spawn(List.of(entities));
	}

	public synchronized void spawn(Collection<? extends Entity> entities) {
		if (gameover) return;
		queue.addAll(entities);
	}

	public void process() {
		try {
			if (paused) return;

			tick++;
			long threat = score + tick / 100;
			if (threat > 0 && threat % 1000 == 0) {
				spawnLimit.release(3);
			}

			lock.acquire();
			for (Entity entity : getEntities()) {
				if (entity instanceof IDynamic d) {
					d.update();
					entity.calculateCoords();

					if (entity instanceof Enemy e) {
						threat -= e.getCost();
					}
				}
			}
			lock.release();

			if (tick % 50 == 0) {
				spawn(new Wind(getPlayer1()));
			}

			long spawnPool = threat;
			if (entities.stream().noneMatch(e -> e instanceof Boss)) {
				if (tick - lastSpawn > 250 + 1000 / getLevel() - Math.min(100, getTick() / 5000)) {
					if (spawnLimit.tryAcquire()) {
						Enemy chosen = new Invader(this);
						if (isTraining()) {
							try {
								chosen = training.getConstructor(GameRuntime.class).newInstance(this);
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
									 NoSuchMethodException e) {
								e.printStackTrace();
							}
						} else {
							List<Enemy> pool = enemies.stream()
									.filter(e -> e.getCost() <= spawnPool)
									.filter(e -> e instanceof Boss == (getLevel() % 10 == 0))
									.toList();

							if (!pool.isEmpty()) {
								try {
									chosen = pool.get(ThreadLocalRandom.current().nextInt(pool.size())).getClass()
											.getConstructor(GameRuntime.class)
											.newInstance(this);
								} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
										 NoSuchMethodException e) {
									e.printStackTrace();
								}
							}
						}

						spawn(chosen);
						if (chosen instanceof Boss b) {
							Utils.transition(theme, bossTheme);
							boss = b;
							barFill.start();
							warnTrans.start();
						} else {
							Utils.transition(bossTheme, theme);
						}
					}

					lastSpawn = tick;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void render(Graphics2D g) {
		try {
			Graphics2D g2d = (Graphics2D) g.create();
			{
				g2d.setColor(background);
				g2d.fill(renderer.getBounds());

				long curr = System.currentTimeMillis();
				back.setColor(foreground);
				g2d.setColor(foreground);
				g2d.setFont(renderer.getFont());

				long frameTime = renderer.getFrameTime();
				long simuTime = curr - lastSimu;

				g2d.drawString("FPS: " + (frameTime == 0 ? "---" : (1000 / frameTime)), 10, 20);
				g2d.drawString("UPS: " + (simuTime == 0 ? "---" : (1000 / simuTime)), 10, 40);
				g2d.drawString("Entities: " + entCount, 10, 60);
				g2d.drawString("Particles: " + partCount, 10, 80);
				g2d.drawString("Audio cues: " + AssetManager.getAudioInstances(), 10, 100);

				back.setDisabled(!(gameover || paused));

				if (!isTraining()) {
					g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 20));
					g2d.drawString("HP: " + getPlayer1().getHp(), 10, renderer.getHeight() - 70);
					g2d.drawString("Level: " + getLevel(), 10, renderer.getHeight() - 50);
					g2d.drawString("Score: " + score, 10, renderer.getHeight() - 30);
					g2d.drawString("Highscore: " + highscore, 10, renderer.getHeight() - 10);

					if (gameover) {
						g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 40));
						Utils.drawAlignedString(g2d, "GAME OVER", renderer.getWidth() / 2, renderer.getHeight() / 3, Utils.ALIGN_CENTER);
						back.render(g2d, renderer.getWidth() / 2 - back.getWidth() / 2, renderer.getHeight() / 3 + 50);
					}
				}

				if (paused) {
					g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 40));
					Utils.drawAlignedString(g2d, "PAUSED", renderer.getWidth() / 2, renderer.getHeight() / 3, Utils.ALIGN_CENTER);
					back.render(g2d, renderer.getWidth() / 2 - back.getWidth() / 2, renderer.getHeight() / 3 + 50);
				}
			}

			{
				Rectangle safe = getSafeArea();

				g2d.translate(renderer.getBounds().width / 2 - safe.width / 2, 0);
				g2d.setStroke(new BasicStroke(1));

				if (DEBUG) {
					Rectangle bounds = getBounds();
					g2d.setColor(Color.RED.darker());
					g2d.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);
				}

				g2d.setColor(foreground);
				g2d.drawRect(-1, -1, safe.width + 1, safe.height + 1);

				g2d.setClip(safe);
				g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 30));

				lock.acquire();
				for (Entity entity : Set.copyOf(getEntities())) {
					float[] pos = entity.getPosition();

					g2d.setColor(foreground);
					if (entity.isVisible()) {
						entity.setCullable(true);

						if (entity instanceof IParticle p) {
							g2d.setColor(new Color(p.getColor(), true));
							g2d.drawLine(
									(int) pos[0], (int) pos[1],
									(int) (pos[0] + entity.getWidth()), (int) (pos[1] + entity.getHeight())
							);
						} else {
							g2d.drawImage(entity.getImage(), entity.getCoordinates().getTransform(), null);

							if (DEBUG) {
								g2d.setColor(Color.RED.darker());
								g2d.draw(entity.getCoordinates().getCollision());
								g2d.setColor(foreground);
							}
						}
					} else if (!entity.isCullable() && entity instanceof ITrackable) {
						if (pos[1] < safe.y) {
							Utils.drawAlignedString(g2d, "^",
									(int) (pos[0] + entity.getWidth() / 2),
									20,
									Utils.ALIGN_CENTER, Utils.ALIGN_BOTTOM
							);

							if (entity instanceof Boss) {
								Utils.drawAlignedString(g2d, "☠",
										(int) (pos[0] + entity.getWidth() / 2),
										50,
										Utils.ALIGN_CENTER, Utils.ALIGN_BOTTOM
								);
							}
						} else if (pos[0] < safe.x) {
							Utils.drawAlignedString(g2d, entity instanceof Boss ? "< ☠" : "<",
									20,
									(int) (pos[1] + entity.getHeight() / 2),
									Utils.ALIGN_RIGHT, Utils.ALIGN_CENTER
							);
						} else if (pos[0] > safe.x + safe.width) {
							Utils.drawAlignedString(g2d, entity instanceof Boss ? "☠ >" : ">",
									safe.width - 20,
									(int) (pos[1] + entity.getHeight() / 2),
									Utils.ALIGN_RIGHT, Utils.ALIGN_CENTER
							);
						} else if (pos[1] > safe.y + safe.height) {
							Utils.drawAlignedString(g2d, "v",
									(int) (pos[0] + entity.getWidth() / 2),
									safe.height - 20,
									Utils.ALIGN_CENTER, Utils.ALIGN_BOTTOM
							);

							if (entity instanceof Boss) {
								Utils.drawAlignedString(g2d, "☠",
										(int) (pos[0] + entity.getWidth() / 2),
										safe.height - 50,
										Utils.ALIGN_CENTER, Utils.ALIGN_BOTTOM
								);
							}
						}
					}
				}
				lock.release();
			}

			if (boss != null) {
				g2d.setColor(foreground);
				Rectangle bounds = getSafeArea();
				if (bounds.width > renderer.getWindow().getBounds().width) {
					bounds = renderer.getWindow().getBounds();
				}

				float fill = barFill.get() / 1000f;

				g2d.drawRect(
						(int) (bounds.width * 0.1f), bounds.height - 50,
						(int) (bounds.width * 0.8f), 30
				);
				g2d.fillRect(
						(int) (bounds.width * 0.1f) + 5, bounds.height - 50 + 5,
						(int) ((bounds.width * (boss.getHp() * 0.8f / boss.getBaseHp()) - 10) * fill), 20
				);

				if (!warnTrans.isStopped()) {
					g2d.setColor(new Color(200, 0, 0, warnTrans.get()));
					g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 80));
					Utils.drawAlignedString(g2d, "⚠ WARNING ⚠",
							bounds.width / 2, bounds.height / 2,
							Utils.ALIGN_CENTER, Utils.ALIGN_CENTER
					);
				}
			}

			g2d.dispose();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() > 255) return;
		keyState[e.getKeyCode()] = true;

		if (!gameover && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			paused = !paused;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() > 255) return;
		keyState[e.getKeyCode()] = false;
	}

	public boolean keyState(int code) {
		if (code > 255) return false;
		return keyState[code];
	}

	public int keyValue(int code) {
		if (code > 255) return 0;
		return keyState[code] ? 1 : 0;
	}

	public Semaphore getSpawnLimit() {
		return spawnLimit;
	}

	public int getScore() {
		return score;
	}

	public void addScore(int score) {
		if (isTraining()) return;

		this.score += score;
		highscore = Math.max(highscore, this.score);
	}

	public int getLevel() {
		return (int) (1 + (score + tick / 20) / 1000);
	}

	public Player getPlayer1() {
		return players.get(0);
	}

	public Player getRandomPlayer() {
		return players.get(Utils.rng().nextInt(players.size()));
	}

	public Color getBackground() {
		return background;
	}

	public Color getForeground() {
		return foreground;
	}

	public Boss getBoss() {
		return boss;
	}

	public void setBoss(Boss boss) {
		this.boss = boss;
	}

	public long getTick() {
		return tick;
	}

	public int millisToTick(long millis) {
		return (int) (millis / (1000f / UPS));
	}

	public long tickToMillis(int ticks) {
		return (long) (ticks * (1000f / UPS));
	}

	public boolean isTraining() {
		return training != null;
	}

	public boolean isGameover() {
		return gameover;
	}

	public float getFPS() {
		return 1000 / renderer.getFramerate();
	}

	public float getUPS() {
		return UPS;
	}

	public void close() {
		gameover = true;
	}
}
