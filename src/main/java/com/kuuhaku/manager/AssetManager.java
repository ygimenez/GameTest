package com.kuuhaku.manager;

import com.kuuhaku.enums.SoundType;
import com.kuuhaku.utils.Utils;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class AssetManager {
	private static final Map<String, BufferedImage> sprite = new HashMap<>();
	private static final Map<String, File> audio = new HashMap<>();
	private static final AtomicInteger audioInstances = new AtomicInteger();

	static {
		URL spriteFolder = AssetManager.class.getClassLoader().getResource("sprite");
		if (spriteFolder != null) {
			try (Stream<Path> visitor = Files.walk(Path.of(spriteFolder.toURI()))) {
				visitor.filter(p -> p.getFileName().toString().endsWith(".png"))
						.forEach(p -> {
							try {
								sprite.put(p.getFileName().toString().split("\\.")[0], ImageIO.read(p.toFile()));
							} catch (IOException ignore) {
							}
						});
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		}

		URL audioFolder = AssetManager.class.getClassLoader().getResource("audio");
		if (audioFolder != null) {
			try (Stream<Path> visitor = Files.walk(Path.of(audioFolder.toURI()))) {
				visitor.filter(p -> p.getFileName().toString().endsWith(".wav"))
						.forEach(p -> {
							audio.put(p.getFileName().toString().split("\\.")[0], p.toFile());
						});
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized static BufferedImage getSprite(String name) {
		return sprite.get(name);
	}

	public synchronized static Clip getAudio(String name) {
		File f = audio.get(name);
		if (f == null) return null;

		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.addLineListener(e -> {
				if (e.getType() == LineEvent.Type.STOP) {
					try {
						clip.close();
						ais.close();
						audioInstances.decrementAndGet();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});

			clip.open(ais);
			audioInstances.incrementAndGet();

			FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gain.setValue(Utils.toDecibels(SoundType.EFFECT, 0.3f));

			return clip;
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void playCue(String file) {
		Clip cue = getAudio(file);
		if (cue != null) {
			cue.start();
		}
	}

	public static int getAudioInstances() {
		return audioInstances.get();
	}
}
