package com.kuuhaku.view;

import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.Renderer;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.entities.Ship;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.enemies.Invader;
import com.kuuhaku.entities.enemies.Mothership;
import com.kuuhaku.enums.SoundType;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.interfaces.ITrackable;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.ui.ButtonElement;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class GameRuntime extends KeyAdapter implements IMenu {
	private final Set<Enemy> enemies = new HashSet<>();
	private final Set<Entity> entities = new HashSet<>();
	private final Set<Entity> queue = new HashSet<>();
	private final boolean[] keyState = new boolean[256];
	private final Semaphore lock = new Semaphore(1);
	private final Renderer renderer;
	private final Ship player;

	private final Semaphore spawnLimit = new Semaphore(10);

	private int round = 1;
	private int score = 0;
	private int difficulty = 0;
	private boolean paused, gameover;
	private long tick, lastSimu, lastSpawn;
	private ButtonElement back;
	private Clip theme, bossTheme;

	private static int highscore;

	public GameRuntime(Renderer renderer) {
		this.renderer = renderer;
		this.player = new Ship(this);

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

		entities.add(player);
		Thread simulation = new Thread(() -> {
			double simrate = renderer.getFramerate() / 4;
			while (!Thread.interrupted()) {
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

		back = new ButtonElement(renderer)
				.setSize(200, 50)
				.setText("MAIN MENU");

		back.addListener(e -> {
			from.switchTo(null);
			back.dispose();
			entities.clear();
			simulation.interrupt();

			if (theme != null) {
				theme.close();
			}

			if (bossTheme != null) {
				bossTheme.close();
			}
		});

		renderer.render(this::render);
	}

	public Rectangle getBounds() {
		Rectangle r = new Rectangle(renderer.getBounds());
		r.grow(150, 150);
		r.translate(-75, -100);

		return r;
	}

	public Rectangle getSafeArea() {
		return renderer.getBounds();
	}

	public synchronized Set<Entity> getEntities() {
		Iterator<Entity> it = entities.iterator();
		while (it.hasNext()) {
			Entity entity = it.next();
			if (entity.getHp() <= 0 || !entity.getBounds().intersect(getBounds())) {
				entity.destroy();
				it.remove();
			}
		}

		entities.addAll(queue);
		queue.clear();

		return entities;
	}

	public Set<Entity> getReadOnlyEntities() {
		return Set.copyOf(entities);
	}

	public synchronized void spawn(Entity... entity) {
		if (gameover) return;
		queue.addAll(Arrays.asList(entity));
	}

	public void process() {
		try {
			if (paused) return;

			tick++;
			lock.acquire();
			for (Entity entity : getEntities()) {
				if (entity instanceof IDynamic d) {
					d.update();
				}
			}

			if (entities.stream().noneMatch(e -> e instanceof Enemy i && i.isBoss())) {
				if (tick - lastSpawn > 250 + 1000 / getRound() - Math.min(100, getTick() / 5000)) {
					if (spawnLimit.tryAcquire()) {
						List<Enemy> pool = enemies.stream()
								.filter(e -> e.getPoints() <= score - difficulty)
								.filter(e -> e.isBoss() == (round % 10 == 0))
								.toList();

						Enemy chosen = new Mothership(this);
						if (!pool.isEmpty()) {
							try {
								chosen = pool.get(ThreadLocalRandom.current().nextInt(pool.size())).getClass()
										.getConstructor(GameRuntime.class)
										.newInstance(this);
								difficulty += chosen.getPoints();
							} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
								e.printStackTrace();
							}
						} else {
							chosen = new Invader(this);
						}

						spawn(chosen);
						if (chosen.isBoss()) {
							Utils.transition(theme, bossTheme);
						} else {
							Utils.transition(bossTheme, theme);
						}
					}

					lastSpawn = tick;
				}
			}

			lock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void render(Graphics2D g2d) {
		try {
			lock.acquire();

			g2d.setColor(Color.BLACK);
			g2d.fill(renderer.getBounds());

			long curr = System.currentTimeMillis();
			g2d.setColor(Color.WHITE);
			g2d.setFont(renderer.getFont());

			long frameTime = renderer.getFrameTime();
			g2d.drawString("FPS: " + (frameTime == 0 ? "---" : (1000 / frameTime)), 10, 20);
			g2d.drawString("UPS: " + (curr == lastSimu ? "---" : (1000 / (curr - lastSimu))), 10, 40);
			g2d.drawString("Entities: " + entities.size(), 10, 60);
			g2d.drawString("Audio cues: " + AssetManager.getAudioInstances(), 10, 80);
			g2d.drawString("Difficulty: " + tick / 1000, 10, 120);

			g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 20));
			g2d.drawString("HP: " + player.getHp(), 10, renderer.getHeight() - 70);
			g2d.drawString("Round: " + getRound(), 10, renderer.getHeight() - 50);
			g2d.drawString("Score: " + score, 10, renderer.getHeight() - 30);
			g2d.drawString("Highscore: " + highscore, 10, renderer.getHeight() - 10);

			back.setDisabled(!(gameover || paused));

			if (gameover) {
				g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 40));
				Utils.drawAlignedString(g2d, "GAME OVER", renderer.getWidth() / 2, renderer.getHeight() / 3, Utils.ALIGN_CENTER);
				back.render(g2d, renderer.getWidth() / 2 - back.getWidth() / 2, renderer.getHeight() / 3 + 50);
			} else if (paused) {
				g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 40));
				Utils.drawAlignedString(g2d, "PAUSED", renderer.getWidth() / 2, renderer.getHeight() / 3, Utils.ALIGN_CENTER);
				back.render(g2d, renderer.getWidth() / 2 - back.getWidth() / 2, renderer.getHeight() / 3 + 50);
			}

			Rectangle safe = getSafeArea();
			g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 30));
			for (Entity entity : getEntities()) {
				if (!entity.getBounds().intersect(safe) && !entity.isCullable()) {
					if (entity instanceof ITrackable) {
						if (entity.getY() < safe.y) {
							Utils.drawAlignedString(g2d, "^",
									entity.getX() + entity.getWidth() / 2,
									20,
									Utils.ALIGN_CENTER, Utils.ALIGN_BOTTOM
							);
						} else if (entity.getX() < safe.x) {
							Utils.drawAlignedString(g2d, "<",
									20,
									entity.getY() + entity.getHeight() / 2,
									Utils.ALIGN_RIGHT, Utils.ALIGN_CENTER
							);
						} else if (entity.getX() > safe.x + safe.width) {
							Utils.drawAlignedString(g2d, ">",
									safe.width - 20,
									entity.getY() + entity.getHeight() / 2,
									Utils.ALIGN_RIGHT, Utils.ALIGN_CENTER
							);
						}
					}
				} else {
					entity.setCullable(true);

					AffineTransform tr = new AffineTransform();
					tr.translate(entity.getX(), entity.getY());
					tr.rotate(entity.getBounds().getAngle(), entity.getWidth() / 2d, entity.getHeight());

					g2d.drawImage(entity.getSprite(), tr, null);
				}
			}
			lock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyState[e.getKeyCode()] = true;

		if (!gameover && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			paused = !paused;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyState[e.getKeyCode()] = false;
	}

	public boolean keyState(int code) {
		return keyState[code];
	}

	public int keyValue(int code) {
		return keyState[code] ? 1 : 0;
	}

	public Semaphore getSpawnLimit() {
		return spawnLimit;
	}

	public void addScore(int score) {
		this.score += score;
		highscore = Math.max(highscore, this.score);
	}

	public int getRound() {
		int round = 1 + score / 1000;
		if (round != this.round) {
			this.round = round;
			spawnLimit.release(3);
		}

		return this.round;
	}

	public Ship getPlayer() {
		return player;
	}

	public long getTick() {
		return tick;
	}

	public void releaseDifficulty(int difficulty) {
		this.difficulty -= difficulty;
	}

	public void close() {
		gameover = true;
	}
}
