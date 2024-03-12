package com.kuuhaku.manager;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public abstract class AssetManager {
	private static final Map<String, BufferedImage> sprite = new HashMap<>();
	private static final Map<String, Path> audio = new HashMap<>();
	private static final AtomicInteger audioInstances = new AtomicInteger();

	static {
		try {
			URL resources = AssetManager.class.getResource("/assets");

			if (resources != null) {
				URI uri = resources.toURI();

				if (uri.getScheme().equals("jar")) {
					try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
						loadAssets(fs.getPath("/assets"));
					}
				} else {
					loadAssets(Path.of(uri));
				}
			}
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadAssets(Path root) {
		try {
			Path sprites = root.resolve("sprite");
			if (Files.isDirectory(sprites)) {
				try (Stream<Path> visitor = Files.walk(sprites)) {
					visitor.filter(p -> p.getFileName().toString().endsWith(".png"))
							.forEach(p -> {
								try (InputStream is = Files.newInputStream(p)) {
									sprite.put(p.getFileName().toString().split("\\.")[0], ImageIO.read(is));
								} catch (IOException ignore) {
								}
							});
				}
			}

			Path audios = root.resolve("audio");
			if (Files.isDirectory(audios)) {
				try (Stream<Path> visitor = Files.walk(audios)) {
					visitor.filter(p -> p.getFileName().toString().endsWith(".wav"))
							.forEach(p -> {
								try (InputStream is = Files.newInputStream(p)) {
									audio.put(p.getFileName().toString().split("\\.")[0], p);
								} catch (IOException ignore) {
								}
							});
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized static BufferedImage getSprite(String name) {
		if (name == null) return null;
		return sprite.get(name);
	}

//	public synchronized static Clip getAudio(String name) {
//		URL url = audio.get(name);
//		if (url == null) return null;
//
//		try {
//			AudioInputStream ais = AudioSystem.getAudioInputStream(url);
//			Clip clip = AudioSystem.getClip(null);
//			clip.addLineListener(e -> {
//				if (e.getType() == LineEvent.Type.STOP) {
//					try {
//						clip.close();
//						ais.close();
//						audioInstances.decrementAndGet();
//					} catch (IOException ex) {
//						ex.printStackTrace();
//					}
//				}
//			});
//
//			clip.open(ais);
//			audioInstances.incrementAndGet();
//
////			FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
////			gain.setValue(Utils.toDecibels(SoundType.EFFECT, 0.3f));
//
//			return clip;
//		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	public synchronized static Clip getAudio(String name) {
		if (true) return null;
//		File f = audio.get(name);
//		if (f == null) return null;
//
//		try (AudioInputStream ais = AudioSystem.getAudioInputStream(f)) {
//			Clip clip = AudioSystem.getClip(null);
//			clip.open(ais);
//			clip.setFramePosition(0);
//			clip.start();
//		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
//			e.printStackTrace();
//		}

		return null;
	}

	public static void playCue(String file) {
		if (true) return;
		Clip cue = getAudio(file);
		if (cue != null) {
			cue.stop();
			cue.start();
		}
	}

	public static int getAudioInstances() {
		return audioInstances.get();
	}
}
