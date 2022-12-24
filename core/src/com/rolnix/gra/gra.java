package com.rolnix.gra;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class gra extends ApplicationAdapter {

	public enum State{ RUNNING, PAUSE, START, ACHIEVEMENT, KONIEC }
	State stan = State.START;

	private FileHandle zapis;

	private OrthographicCamera kamerka;
	private OrthographicCamera hudKamerka;

	private SpriteBatch zadanie;
	private Rectangle ty;
	private Texture fota;
	private Texture cosTekstura;
	private Texture przyciemnienie;
	private Texture pociskTextura;
	private Texture tlo;
	private Array<Rectangle> kilkaCos;
	private Array<Rectangle> kilkaPocisk;

	private BitmapFont fps;
	private BitmapFont koniec;
	private BitmapFont czaser;
	private BitmapFont punkter;
	private BitmapFont tryber;
	private BitmapFont wybieracz;
	private BitmapFont wersjaTekst;
	private BitmapFont twurca;
	private BitmapFont pauza;
	private BitmapFont achTekst1;
	private BitmapFont achTekst2;
	private BitmapFont achTekst3;
	private BitmapFont achTekst4;
	private BitmapFont info;
	private BitmapFont wyjatek;

	private FreeTypeFontGenerator generator;

	private String trybSlowo;
	private String wyjatekTekst;

	private boolean ach1 = false;
	private boolean ach2 = false;
	private boolean ach3 = false;
	private boolean ach4 = false;

	private Sound wybor;
	private Sound umarcie;
	private Sound strzal;
	private Music tloMuzyka;

	private ParticleEffect wybuchCos;

	private long tak;
	private long czasOdOstatnieCos;
	private long czasOdOstatnieAmmo;
	private long czasTeraz;
	private long poczatekCzas;
	private long licznik;
	private int punkty;
	private int mnoznik;
	private int szypkosc;
	private int predkoscStrzalu;
	private int ammo;
	private byte tryb;

	@Override
	public void create () {
		fota = new Texture(Gdx.files.internal("textures/fota.png"));
		cosTekstura = new Texture(Gdx.files.internal("textures/cos.png"));
		przyciemnienie = new Texture(Gdx.files.internal("textures/przyciemnienie.png"));
		pociskTextura = new Texture(Gdx.files.internal("textures/pocisk.png"));
		tlo = new Texture(Gdx.files.internal("textures/tlo.png"));

		wybieracz = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		czaser = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		tryber = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		fps = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		punkter = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		wersjaTekst = new BitmapFont(Gdx.files.internal("font/male.fnt"));
		twurca = new BitmapFont(Gdx.files.internal("font/male.fnt"));
		koniec = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		pauza = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		achTekst1 = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		achTekst2 = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		achTekst3 = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		achTekst4 = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));
		info = new BitmapFont(Gdx.files.internal("font/czcionka.fnt"));

		generator = new FreeTypeFontGenerator(Gdx.files.internal("font/czcionka.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 28;
		parameter.color = Color.RED;
		wyjatek = generator.generateFont(parameter);

		koniec.getData().setScale(3.0f);
		pauza.getData().setScale(3.0f);
		achTekst1.getData().setScale(1.5f);
		achTekst2.getData().setScale(1.5f);
		achTekst3.getData().setScale(1.5f);
		achTekst4.getData().setScale(1.5f);

		zapis = Gdx.files.local("./save.rff");
		boolean czyIstniejeZapis = Gdx.files.local("./save.rff").exists();

		wybor = Gdx.audio.newSound(Gdx.files.internal("sounds/klikniecie.wav"));
		umarcie = Gdx.audio.newSound(Gdx.files.internal("sounds/umarcie.wav"));
		strzal = Gdx.audio.newSound(Gdx.files.internal("sounds/strzal.wav"));
		tloMuzyka = Gdx.audio.newMusic(Gdx.files.internal("sounds/muzyka.wav"));

		wybuchCos = new ParticleEffect();
		wybuchCos.load(Gdx.files.internal("particles/wybuchCos"), Gdx.files.internal("particles"));

		tloMuzyka.setLooping(true);
		tloMuzyka.setVolume(0.4f);

		kamerka = new OrthographicCamera();
		kamerka.setToOrtho(false, 1366, 768);

		hudKamerka = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		hudKamerka.position.set(hudKamerka.viewportWidth / 2.0f, hudKamerka.viewportHeight / 2.0f, 1.0f);

		zadanie = new SpriteBatch();

		wyjatekTekst = "";

		czasTeraz = 0;
		mnoznik = 1000000;
		tak = 1000000000;
		trybSlowo = "";

		predkoscStrzalu = 500;
		ammo = 1;
		punkty = 0;

		ty = new Rectangle();
		ty.x = (1366 / 2f) - (80f / 2f);
		ty.y = 20;
		ty.width = 80;
		ty.height = 80 - 28;

		kilkaCos = new Array<>();
		kilkaPocisk = new Array<>();

		if (czyIstniejeZapis) wczytaj();
	}



	private void spawnCos() {
		Rectangle cos = new Rectangle();
		cos.x = MathUtils.random(0, 1366 - 64);
		cos.y = 768 + 64;
		cos.width = 64;
		cos.height = 64;
		kilkaCos.add(cos);
		czasOdOstatnieCos = TimeUtils.nanoTime();
	}

	private void spawnPocisk() {
		Rectangle pocisk = new Rectangle();
		pocisk.x = ty.x + 40;
		pocisk.y = ty.y + 26;
		pocisk.width = 32;
		pocisk.height = 32;
		kilkaPocisk.add(pocisk);
		strzal.play();
	}

	@Override
	public void render () {
		switch (stan) {
			case RUNNING:
				update();
				break;
			case START:
				menu();
				break;
			case ACHIEVEMENT:
				achivementyPetla();
				break;
			case PAUSE:
				pauzaPetla();
				break;
			case KONIEC:
				koniecPetla();
				break;
		} draw();
	}

	public void update() {
		//bindy i movement
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) ty.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) ty.x += 200 * Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyJustPressed(Input.Keys.X) && ammo > 0) { spawnPocisk(); ammo--; }

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && stan == State.RUNNING) pauzaRaz();

		//blokuje zeby gracz nie wyszedl za ekran
		if (ty.x < 0) ty.x = 0;
		if (ty.x > 1366 - 80) ty.x = 1366 - 80;

		//spawnuje cos
		if (TimeUtils.nanoTime() - czasOdOstatnieCos > tak) spawnCos();

		//dodaje ammo co 15 sek
		if (TimeUtils.millis()  - czasOdOstatnieAmmo > 15000) {
			ammo++;
			czasOdOstatnieAmmo = TimeUtils.millis();
		}

		//poziomy trudnosci
		tak -= mnoznik;
		switch (tryb) {
			//latwy
			case 0:
				szypkosc = 200;
				if (tak < 750000000) mnoznik = 200000;
				if (tak < 333333333) { mnoznik = 300000; szypkosc = 215; }
				if (tak < 250000000) { mnoznik = 0;  szypkosc = 225; }
				if (punkty == 150) ach1 = true;
				break;

			//sredni
			case 1:
				szypkosc = 225;
				if (tak < 850000000) mnoznik = 200000;
				if (tak < 666666667) { mnoznik = 300000; szypkosc = 240; }
				if (tak < 400000000) { mnoznik = 400000; szypkosc = 250; }
				if (tak < 150000000) mnoznik = 0;
				if (punkty == 175) ach2 = true;
				break;

			//trudny
			case 2:
				szypkosc = 250;
				if (tak < 850000000) mnoznik = 250000;
				if (tak < 666666667) { mnoznik = 350000; szypkosc = 265; }
				if (tak < 400000000) { mnoznik = 450000; szypkosc = 275;}
				if (tak < 100000000) mnoznik = 0;
				if (punkty == 200) ach3 = true;
				break;
		}

		if (punkty == 200 && ach1 && ach2 && ach3) ach4 = true;

		//liczy czas
		czasTeraz = TimeUtils.millis();
		licznik = (czasTeraz / 1000) - (poczatekCzas / 1000);

		//sprawdza kolizje i niszczy cos
		for (Iterator<Rectangle> iter = new Array.ArrayIterator<>(kilkaCos).iterator(); iter.hasNext();) {
			Rectangle cos = iter.next();
			cos.y -= szypkosc * Gdx.graphics.getDeltaTime();
			if (cos.y + 64 < 0) { iter.remove(); punkty++; }
			if (cos.overlaps(ty)) { fota = new Texture(Gdx.files.internal("textures/ded.png")); koniecRaz(); }
		}

		for (Iterator<Rectangle> iter = new Array.ArrayIterator<>(kilkaPocisk).iterator(); iter.hasNext();) {
			Rectangle pocisk = iter.next();
			pocisk.y += predkoscStrzalu * Gdx.graphics.getDeltaTime();
			if (pocisk.y > 512) { iter.remove(); }
			for (Iterator<Rectangle> iter2 = new Array.ArrayIterator<>(kilkaCos).iterator(); iter2.hasNext();) {
				Rectangle cos = iter2.next();
				if (pocisk.overlaps(cos)) {
					iter2.remove(); iter.remove();
					wybuchCos.getEmitters().first().setPosition(cos.x, cos.y);
					wybuchCos.start();
				}
			}
		}

		wybuchCos.update(Gdx.graphics.getDeltaTime());

		//zapisuje
		try {
			zapis.writeString(ach1 + "/" + ach2 + "/" + ach3 + "/" + ach4, false);
		} catch (Exception e) {
			wyjatekTekst = "BLAD ZAPISU: " + System.lineSeparator() + e;
			System.out.println(wyjatekTekst);
		}
	}

	public void draw() {
		ScreenUtils.clear(0, 0, 0, 0);

		kamerka.update();
		zadanie.setProjectionMatrix(kamerka.combined);

		zadanie.begin();

		zadanie.draw(tlo, 0, 0);

		switch (stan) {
			case RUNNING:
				for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
				for (Rectangle pocisk: new Array.ArrayIterator<>(kilkaPocisk)) zadanie.draw(pociskTextura, pocisk.x, pocisk.y);
				info.draw(zadanie, "AMMO: " + ammo, 1366.f / 2, hudKamerka.viewportHeight - 1);
				zadanie.draw(fota, ty.x, ty.y);
				wybuchCos.draw(zadanie);
				break;

			case START:
				wybieracz.draw(zadanie,
						"F1 - LATWY" + System.lineSeparator() + "F2 - SREDNI" + System.lineSeparator() + "F3 - TRUDNY" + System.lineSeparator() + "F4 - ACHIEVEMENTY",
						hudKamerka.viewportWidth / 2.0f, hudKamerka.viewportHeight / 2.0f);
				break;

			case ACHIEVEMENT:
				info.draw(zadanie, "ESC - wyjscie", 2.0f, hudKamerka.viewportHeight - 35);
				if (ach1) { achTekst1.draw(zadanie, "150 PUNKTOW NA LATWYM",
						(hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) + 120); }
				if (ach2) { achTekst2.draw(zadanie, "175 PUNKTOW NA SREDNIM",
						(hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) + 60); }
				if (ach3) { achTekst3.draw(zadanie, "200 PUNKTOW NA TRUDNYM",
						(hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f)); }
				if (ach4) { achTekst4.draw(zadanie, "WSZYSTKIE ACHIEVEMENTY + 200 NA TRUDNYM ZNOWU",
						(hudKamerka.viewportWidth / 2.0f) - 500, (hudKamerka.viewportHeight / 2.0f) - 60); }
				break;

			case PAUSE:
				for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
				for (Rectangle cos: new Array.ArrayIterator<>(kilkaPocisk)) zadanie.draw(pociskTextura, cos.x, cos.y);
				zadanie.draw(fota, ty.x, ty.y);
				zadanie.draw(przyciemnienie, 0, 0);
				pauza.draw(zadanie, "PAUZA", (hudKamerka.viewportWidth - 100) / 2.0f, hudKamerka.viewportHeight / 2.0f);
				info.draw(zadanie, "BACKSPACE - RESET",  2.0f, hudKamerka.viewportHeight - 35);
				break;

			case KONIEC:
				for (Rectangle cos: new Array.ArrayIterator<>(kilkaCos)) zadanie.draw(cosTekstura, cos.x, cos.y);
				for (Rectangle cos: new Array.ArrayIterator<>(kilkaPocisk)) zadanie.draw(pociskTextura, cos.x, cos.y);
				zadanie.draw(fota, ty.x, ty.y);
				koniec.draw(zadanie, "UMARES", (hudKamerka.viewportWidth - 100) / 2.0f, hudKamerka.viewportHeight / 2.0f);
				info.draw(zadanie, "BACKSPACE - RESET",  2.0f, hudKamerka.viewportHeight - 35);
				break;
		}
		//zawsze
		fps.draw(zadanie, "" + Gdx.graphics.getFramesPerSecond(), 2.0f, hudKamerka.viewportHeight - 2);

		punkter.draw(zadanie, "PUNKTY: " + punkty, 2.0f, 25.0f);
		czaser.draw(zadanie, "CZAS: " + licznik, 2.0f, 50.0f);

		wersjaTekst.draw(zadanie, "v1.5", hudKamerka.viewportWidth - 50, 30);
		twurca.draw(zadanie, "copyright by rolnix", hudKamerka.viewportWidth - 244, 15);

		tryber.draw(zadanie, trybSlowo, hudKamerka.viewportWidth - 120, hudKamerka.viewportHeight - 2);

		wyjatek.draw(zadanie, wyjatekTekst, 2.0f, hudKamerka.viewportHeight - 2);

		zadanie.end();
	}

	public void menu() {
		if (Gdx.input.isKeyPressed(Input.Keys.F1)) {
			rozpocznij((byte) 0, "LATWY"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F2)) {
			rozpocznij((byte) 1, "SREDNI"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F3)) {
			rozpocznij((byte) 2, "TRUDNY"); }
		if (Gdx.input.isKeyPressed(Input.Keys.F4)) {achievementRaz(); }
	}

	public void rozpocznij(byte wybrane, String trybNapis) {
		poczatekCzas = TimeUtils.millis();
		czasOdOstatnieAmmo = TimeUtils.millis();
		tryb = wybrane;
		trybSlowo = trybNapis;
		stan = State.RUNNING;
		wybor.play();
		tloMuzyka.play();
	}

	//pauza
	public void pauzaPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			stan = State.RUNNING;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
			create();
			tloMuzyka.stop();
			stan = State.START;
		}
	}
	public void pauzaRaz() {
		stan = State.PAUSE;
	}

	//achievementy
	public void achivementyPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			stan = State.START;
			wybor.play();
		}
	}
	public void achievementRaz() {
		stan = State.ACHIEVEMENT;
		wybor.play();
	}

	//umarcie

	public void koniecPetla() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
			create();
			tloMuzyka.stop();
			stan = State.START;
		}
	}
	public void koniecRaz() {
		stan = State.KONIEC;
		tloMuzyka.stop();
		umarcie.play();
	}

	public void wczytaj() {
		String wczytane = zapis.readString();
		String[] gotowyOdczyt = wczytane.split("/");
		ach1 = Boolean.parseBoolean(gotowyOdczyt[0]);
		ach2 = Boolean.parseBoolean(gotowyOdczyt[1]);
		ach3 = Boolean.parseBoolean(gotowyOdczyt[2]);
		ach4 = Boolean.parseBoolean(gotowyOdczyt[3]);
	}

	@Override
	public void dispose () {
		zadanie.dispose();
		cosTekstura.dispose();
		fota.dispose();
		przyciemnienie.dispose();
		pociskTextura.dispose();

		wybuchCos.dispose();

		wybieracz.dispose();
		czaser.dispose();
		tryber.dispose();
		fps.dispose();
		punkter.dispose();
		wersjaTekst.dispose();
		twurca.dispose();
		koniec.dispose();
		pauza.dispose();
		achTekst1.dispose();
		achTekst2.dispose();
		achTekst3.dispose();
		achTekst4.dispose();
		info.dispose();

		generator.dispose();

		wybor.dispose();
		umarcie.dispose();
		tloMuzyka.dispose();
	}
}