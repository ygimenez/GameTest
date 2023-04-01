package com.kuuhaku.view;

import com.kuuhaku.AssetManager;
import com.kuuhaku.Renderer;
import com.kuuhaku.Utils;
import com.kuuhaku.entities.Ship;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.enemies.Invader;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.ui.ButtonElement;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.Closeable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class GameRuntime extends KeyAdapter implements IMenu, Closeable {
	private final Set<Entity> entities = new HashSet<>();
	private final Set<Entity> queue = new HashSet<>();
	private final boolean[] keyState = new boolean[256];
	private final Semaphore lock = new Semaphore(1);
	private final Renderer renderer;
	private final Ship player;

	private final Semaphore spawnLimit = new Semaphore(10);

	private int round = 1;
	private int score = 0;
	private boolean gameover = false;
	private long lastSimu, lastSpawn;
	private ButtonElement back;

	public GameRuntime(Renderer renderer) {
		this.renderer = renderer;
		this.player = new Ship(this);
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

				try {
					Thread.sleep((long) simrate, (int) (1_000_000 * (simrate - (long) simrate)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		simulation.setDaemon(true);
		simulation.start();

		Clip cue = AssetManager.getAudio("theme");
		if (cue != null) {
			FloatControl gain = (FloatControl) cue.getControl(FloatControl.Type.MASTER_GAIN);
			gain.setValue(20f * (float) Math.log10(0.25));

			cue.loop(Clip.LOOP_CONTINUOUSLY);
		}

		back = new ButtonElement(renderer)
				.setSize(150, 50)
				.setText("BACK");

		back.addListener(e -> {
			from.switchTo(this);
			back.dispose();

			if (cue != null) {
				cue.close();
			}
		});

		renderer.render(this::render);
	}

	public Rectangle getBounds() {
		Rectangle r = new Rectangle(renderer.getBounds());
		r.grow(0, 100);
		r.translate(0, -50);

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

	public synchronized void spawn(Entity entity) {
		if (gameover) return;
		queue.add(entity);
	}

	public void process() {
		try {
			lock.acquire();
			for (Entity entity : getEntities()) {
				if (entity instanceof IDynamic d) {
					d.update();
				}
			}

			if (System.currentTimeMillis() - lastSpawn > 1000 + 4000 / getRound()) {
				if (spawnLimit.tryAcquire()) {
					spawn(new Invader(this));
				}

				lastSpawn = System.currentTimeMillis();
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

			long lastFrame = renderer.getFrameTime();
			g2d.drawString("FPS: " + (curr == lastFrame ? "---" : (1000 / (curr - lastFrame))), 10, 20);
			g2d.drawString("UPS: " + (curr == lastSimu ? "---" : (1000 / (curr - lastSimu))), 10, 40);
			g2d.drawString("Entities: " + entities.size(), 10, 60);
			g2d.drawString("Audio cues: " + AssetManager.getAudioInstances(), 10, 80);

			g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 20));
			g2d.drawString("HP: " + player.getHp(), 10, renderer.getHeight() - 50);
			g2d.drawString("Round: " + getRound(), 10, renderer.getHeight() - 30);
			g2d.drawString("Score: " + score, 10, renderer.getHeight() - 10);

			if (gameover) {
				g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 40));
				Utils.drawAlignedString(g2d, "GAME OVER", renderer.getWidth() / 2, renderer.getHeight() / 2, Utils.ALIGN_CENTER);
				back.render(g2d, renderer.getWidth() / 2 - back.getWidth() / 2, renderer.getHeight() / 2 + 50);
			}

			for (Entity entity : getEntities()) {
				AffineTransform tr = new AffineTransform();
				tr.translate(entity.getX(), entity.getY());
				tr.rotate(entity.getBounds().getAngle(), entity.getWidth() / 2d, entity.getHeight());

				g2d.drawImage(entity.getSprite(), tr, null);
			}
			lock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyState[e.getKeyCode()] = true;
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
	}

	public int getRound() {
		int round = 1 + score / 1000;
		if (round != this.round) {
			this.round = round;
			spawnLimit.release();
		}

		return this.round;
	}

	@Override
	public void close() {
		gameover = true;
	}
}
